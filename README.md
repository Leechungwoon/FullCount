# ⚾ Full Count — KBO 야구 티켓 예매 플랫폼

> KBO 실제 경기 일정 기반, 좌석 선점 → 결제 완료까지 동시성을 제어하는 티켓 예매 백엔드

<br>

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트 유형 | 개인 프로젝트 |
| 개발 기간 | 2026.04 ~ 진행 중 (고도화 예정) |
| 주요 도전 과제 | 동시 좌석 선점 충돌 방지, 단일 트랜잭션 결제/예매 처리, 문자열 기반 경기 검색 |
| 기술 스택 | Java, Spring Boot, MySQL, Spring Data JPA, Spring Security, JWT, Toss Payments |

<br>

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java |
| Framework | Spring Boot |
| ORM | Spring Data JPA |
| DB | MySQL 8.4 |
| 인증 | Spring Security, JWT |
| 결제 | Toss Payments (샌드박스) |
| 테스트 | Apache JMeter 5.6.3, Postman |

<br>

## ✅ 핵심 성과 요약

- 100명 동시 좌석 선점 요청 → 1건만 성공, 중복 예매 0건 검증
- JMeter 테스트에서 완전 동시 환경(Ramp-up 0초)에서도 동일 결과 확인
- 응답시간 21ms → 109ms 증가 분석을 통해 낙관적 락의 한계 도출
- Toss Payments 샌드박스 연동 후 결제-예매 상태 일관성 검증

<br>

## 📊 JMeter 동시성 테스트 결과

**테스트 목적:** 100명이 동시에 같은 좌석을 선점 시도할 때 낙관적 락 정상 동작 검증

**API:** `POST /reservation/held?gameSeatId={id}`

### 시나리오 1 — 순차 투입 (Ramp-up: 1초)

| 항목 | 결과 |
|------|------|
| 총 요청 수 | 100 |
| **성공** | **1건 (1%)** |
| **실패** | **99건 (99%)** |
| 평균 응답시간 | 21ms |
| 최소 응답시간 | 4ms |
| 최대 응답시간 | 159ms |
| Throughput | 100/sec |

### 시나리오 2 — 완전 동시 투입 (Ramp-up: 0초)

| 항목 | 결과 |
|------|------|
| 총 요청 수 | 100 |
| **성공** | **1건 (1%)** |
| **실패** | **99건 (99%)** |
| 평균 응답시간 | 109ms |
| 최소 응답시간 | 75ms |
| 최대 응답시간 | 145ms |
| Throughput | 675.7/sec |

**분석**
- 두 시나리오 모두 정확히 **1명만 선점 성공** → 낙관적 락 정확하게 동작
- 완전 동시 환경에서 응답시간 **21ms → 109ms (약 5배 증가)**
  - 충돌 증가 → 롤백/예외 처리 증가 → 응답시간 상승
  - 이것이 낙관적 락의 한계이며 고트래픽 환경의 개선 포인트

**고도화 방향**
- Redis 분산 락으로 전환 → 좌석 선점 요청을 직렬화해 충돌 원천 차단

<br>

## 🏗 핵심 기능

### 1. 동시성 제어 — 낙관적 락 (@Version)

동시에 여러 사용자가 같은 좌석을 요청할 때 단 1명만 성공하도록 제어

```java
@Entity
public class GameSeat {

    @Version
    private Integer version;  // JPA 낙관적 락 적용

    private SeatStatus status; // AVAILABLE → HELD → CONFIRMED
}
```

**동작 흐름**
```
1. 100명이 동시에 같은 좌석 조회 (version = 0)
2. 가장 먼저 커밋한 1명 → version = 1 업데이트 성공
3. 나머지 99명 → version 불일치 → OptimisticLockException
   → "다른 사용자가 먼저 선점한 좌석입니다" 반환
```

**선택 이유**
- 선점과 결제가 분리된 구조 → 선점 순간에만 동시성 제어 필요
- `@Version` 하나로 JPA 표준 방식 적용 가능
- 현재 트래픽 규모에서 적합 (고트래픽 시 Redis 분산 락 전환 예정)

#### 보조 기능 — 좌석 자동 만료 (@Scheduled)

선점(HELD) 후 결제 미완료 좌석을 1분 주기로 스캔해 자동 원상복구

```java
@Scheduled(fixedRate = 60000)
public void expireHeldSeats() {
    LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(10);
    reservationRepository
        .findByStatusAndCreatedAtBefore(ReservationStatus.HELD, expiredBefore)
        .forEach(r -> {
            r.updateStatus(ReservationStatus.CANCELLED);
            r.getGameSeat().updateStatus(SeatStatus.AVAILABLE);
        });
}
```

> 고도화 방향: Redis TTL 이벤트 방식으로 전환 → 만료 순간에만 처리, DB 전체 스캔 제거

<br>

### 2. 결제 및 예매 — 단일 트랜잭션 처리

Toss Payments 승인 완료 후 결제 상태와 예매 상태를 **하나의 트랜잭션**에서 동시 처리
→ 결제는 됐는데 예매가 안 되는 데이터 불일치 상황 방지

```
결제 승인 요청 (Toss API)
    ↓
@Transactional 시작
    ├── Payment 상태: PENDING → COMPLETED
    └── Reservation 상태: HELD → CONFIRMED
    ↓
커밋 (또는 예외 시 전체 롤백)
```

**Toss 샌드박스 검증 결과**

| 테이블 | 변경 전 | 변경 후 |
|--------|---------|---------|
| reservations | `HELD` | `CONFIRMED` |
| payment | `PENDING` | `COMPLETED` |

<br>

### 3. 경기 검색 — 팀명 / 구장명 / 날짜 기반

기존 `findByFilters()`는 ID 값을 직접 입력해야 검색 가능한 구조로 실제 사용 불가
→ 문자열 기반 검색 API로 전환, `JOIN FETCH`로 N+1 문제 동시 해결

```
GET /games/search?team=LG
GET /games/search?stadium=잠실&date=2026-05-24
GET /games/search                ← 파라미터 없으면 전체 조회
```

**설계 결정 — 단일 테이블 유지**
- 검색/쓰기 테이블 분리(CQRS)는 현재 트래픽 규모에서 오버스펙
- 트래픽 증가 시 Elasticsearch 전문 검색 엔진 도입 방향으로 고도화 예정
- 현재는 `LIKE` 검색 + `JOIN FETCH`로 성능 충분

**shortName 포함 검색**
- `name(LG 트윈스)` + `shortName(LG)` 모두 검색 대상에 포함
- 사용자가 약칭으로 검색해도 정상 동작

<br>

## 🗂 ERD (주요 엔티티 관계)

```
User ──< Reservation >── GameSeat ──< Game
                │
                └──< Payment
```

| 엔티티 | 역할 |
|--------|------|
| User | 회원 정보, JWT 인증 |
| Game | KBO 경기 일정 |
| GameSeat | 경기별 좌석, `@Version` 낙관적 락 적용 |
| Reservation | 예매 상태 관리 (HELD / CONFIRMED / CANCELLED) |
| Payment | 결제 상태 관리 (PENDING / COMPLETED) |

<br>

## 🔐 인증 흐름 (JWT + Spring Security)

```
로그인 요청
    → UsernamePasswordAuthenticationFilter
    → AccessToken 발급
    → 이후 요청마다 Authorization 헤더에 AccessToken 포함
    → JwtAuthenticationFilter → SecurityContext 등록
```

<br>

## 📁 패키지 구조

```
src/main/java/com/fullcount/
├── auth/          # JWT 필터, Spring Security 설정
├── user/          # 회원가입, 로그인
├── game/          # KBO 경기 일정 관리
├── seat/          # 좌석 정보, @Version 낙관적 락
├── reservation/   # 선점, 만료(@Scheduled), 상태 관리
└── payment/       # Toss Payments 연동, 단일 트랜잭션 처리
```

<br>

## ⚠️ 트러블슈팅

### 1. `@SpringBootApplication` 패키지 스캔 범위 오류
- **문제:** 엔티티/레포지토리가 스캔되지 않아 빈 등록 실패
- **원인:** 멀티 모듈 구조에서 기본 스캔 범위 밖에 위치
- **해결:** `scanBasePackages`, `@EnableJpaRepositories`, `@EntityScan` 명시적 지정

### 2. 로컬 MySQL 인스턴스 충돌
- **문제:** MySQL 포트 충돌로 애플리케이션 실행 불가
- **원인:** 시스템에 MySQL 인스턴스가 2개 동시 실행
- **해결:** `launchctl unload`로 불필요한 인스턴스 종료 후 재시작

<br>

## 💡 회고 및 개선 방향

| 현재 구현 | 개선 방향 |
|-----------|-----------|
| 낙관적 락 (@Version) | Redis 분산 락으로 고트래픽 대응 |
| @Scheduled 만료 처리 | Redis TTL 기반 이벤트 방식으로 전환 |
| 로컬 실행 | Docker + AWS EC2 배포 |
| LIKE 기반 경기 검색 | Elasticsearch 전문 검색 엔진으로 전환 |
