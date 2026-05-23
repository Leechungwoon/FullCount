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
- 스케줄러 N+1 문제 발견 및 벌크 업데이트 적용 → 쿼리 13번 → 2번으로 감소

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

## 💡 기술적 의사 결정

### 1. 무통장 결제 기반 좌석 선점 구조

**문제:** 결제 완료 전에도 좌석을 임시 확보해야 하는 요구사항 존재

**해결:** `Reservation`과 `GameSeat` 상태를 분리하여 `HELD` 상태 도입

**이유:**
- 결제 지연 상황에서도 좌석 중복 예약 방지
- 결제 완료 여부와 좌석 점유 상태를 독립적으로 관리 가능

**정상 결제 흐름**
```
좌석 선택 → 무통장 선택(HELD 전환) → 좌석 선점 → 입금 대기 → 입금 확인 → 예매 확정
```

**미입금 만료 흐름**
```
좌석 선택 → 무통장 선택(HELD 전환) → 좌석 선점 → 입금 대기
→ 당일 23:59:59까지 미입금 → AVAILABLE 복구 → 다른 사용자 선점 가능
```

---

### 2. 낙관적 락 (@Version) 선택

**문제:** 동일 좌석에 대한 동시 선점 요청 시 충돌 발생

**해결:** `@Version` 기반 낙관적 락 적용

**이유:**
- 선점 순간에만 충돌 발생 → 지속적인 락 불필요
- DB 락을 점유하지 않아 성능에 유리
- 현재 트래픽 규모에서 단순하고 효율적인 방식

```
사용자 A, B가 동시에 같은 좌석 선점 시도
→ A가 먼저 version = 1로 UPDATE 성공
→ B는 version 불일치 → OptimisticLockException
→ B에게 "이미 선점된 좌석입니다" 반환
```

---

### 3. 스케줄러 처리 방식 — 벌크 UPDATE 선택

**문제:** 만료 예매 처리 시 N+1 문제로 쿼리 13번 발생

**해결:** 반복문 제거 후 `@Modifying` 벌크 UPDATE 적용

**이유:**
- 해당 로직은 조회가 아닌 상태 변경(batch) 작업
- 객체를 조회할 필요 없이 DB에서 한 번에 처리 가능
- 쿼리 수를 N → 1로 줄여 성능 개선

---

## 도메인 설계
### 상태 정의

**GameSeat 상태**

| 상태 | 의미 |
|------|------|
| `AVAILABLE` | 예매 가능 |
| `HELD` | 선점됨 (결제 대기 중) |
| `SOLD` | 결제 완료 |

**Reservation 상태**

| 상태 | 의미 |
|------|------|
| `HELD` | 선점 (입금 대기) |
| `CONFIRMED` | 입금 확인 완료 |
| `CANCELLED` | 취소 또는 미입금 만료 |

**Payment 상태**

| 상태 | 의미 |
|------|------|
| `PENDING` | 입금 대기 (무통장 선택 시 생성) |
| `COMPLETED` | 입금 확인 완료 |
| `CANCELED` | 결제 취소 |

---

### DB 저장 시점

**좌석 선점 시**
```
game_seats.status      = 'HELD'       ← 저장 O
reservations.status    = 'HELD'       ← 저장 O
reservations.expiredAt = 23:59:59     ← 저장 O
payments 테이블                       ← 저장 X (아직 생성 안 함)
```

**무통장 선택 시 (createPayment)**
```
payments.status = 'PENDING'           ← 저장 O (입금 대기 상태로 생성)
payments.paidAt = null                ← 입금 확인 전까지 null
```

**입금 확인 시 (confirmPayment — 단일 트랜잭션)**
```
payments.status      PENDING → COMPLETED  ← 업데이트
reservations.status  HELD    → CONFIRMED  ← 업데이트
game_seats.status    HELD    → SOLD       ← 업데이트
```

**미입금 만료 시 (자정 스케줄러)**
```
game_seats.status    = 'AVAILABLE'  ← 업데이트
reservations.status  = 'CANCELLED'  ← 업데이트
```



<br>

## 🏗 핵심 기능

### 1. 동시성 제어 — 낙관적 락 (@Version)

동시에 여러 사용자가 같은 좌석을 요청할 때 단 1명만 성공하도록 제어

```java
@Entity
public class GameSeat {

    @Version
    private Integer version;  // JPA 낙관적 락 적용

    private GameSeatStatus status; // AVAILABLE → HELD → SOLD
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

선점(HELD) 후 당일 자정까지 미결제 시 좌석 자동 원상복구

```java
// 매일 자정 실행: 당일 결제 미완료 HELD 예매 일괄 취소
@Scheduled(cron = "0 0 0 * * *")
@Transactional
public void cancelExpiredReservation() {

    LocalDateTime now = LocalDateTime.now();

    // Step 1: 만료 HELD 예매에 연결된 GameSeat 먼저 AVAILABLE 복구
    int restoredSeats = reservationSeatRepository.bulkRestoreExpiredGameSeats(now);
    log.info("좌석 복구 완료: {}건", restoredSeats);

    // Step 2: 만료 HELD 예매 CANCELLED로 일괄 변경
    int cancelledCount = reservationRepository.bulkCancelExpired(now);
    log.info("예매 취소 완료: {}건", cancelledCount);
}
```

> GameSeat 먼저 처리하는 이유: 서브쿼리 조건이 HELD 기준이므로 Reservation 취소 전에 실행해야 대상이 잡힘

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

User ──< Reservation ──< ReservationSeat >── GameSeat ──< Game
                        │
                        └── Payment

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
src/main/java/com/example/FullCount2/
├── auth/          # JWT 필터, Spring Security 설정
├── user/          # 회원가입, 로그인
├── game/          # KBO 경기 일정 관리
├── seat/          # 좌석 정보, @Version 낙관적 락
├── reservation/   # 선점, 만료(@Scheduled), 상태 관리
└── payment/       # Toss Payments 연동, 단일 트랜잭션 처리
```
```
src/main/java/com/example/FullCount2/
├── common/
│   ├── config/
│   │   └── SecurityConfig
│   ├── enums/
│   │   ├── ExceptionCode
│   │   ├── GameSeatStatus
│   │   ├── PaymentMethod
│   │   ├── PaymentStatus
│   │   ├── ReservationStatus
│   │   └── UserRole
│   ├── filter/
│   │   └── JwtFilter
│   ├── global/
│   │   ├── BaseEntity
│   │   ├── CommonResponse
│   │   ├── CustomException
│   │   └── GlobalExceptionHandler
│   └── util/
│       └── JwtUtil
├── domain/
│   ├── auth/  # JWT 필터, Spring Security 설정
│   ├── game/  # KBO 경기 일정 관리
│   ├── payment/  # 무통장 입금, Toss Payments 연동, 단일 트랜잭션 처리
│   ├── reservation/  # 선점, 만료(@Scheduled), 상태 관리
│   ├── seat/   # 좌석 정보, @Version 낙관적 락
│   ├── section/
│   ├── stadium/
│   ├── team/
│   └── user/  # 회원가입, 로그인
└── FullCountApplication
```

<br>

## ⚠️ 트러블슈팅

### 1. 스케줄러 N+1 문제 — 벌크 업데이트 적용

- **문제:** 만료 예매 3건 취소 시 쿼리 13번 발생 (SELECT 7번 + UPDATE 6번)
- **원인:** `cancelExpired()`를 건별 루프 호출 + `GameSeat` LAZY 로딩으로 `.getGameSeat()` 호출마다 SELECT 추가
- **해결:** `@Modifying` 벌크 UPDATE 2번으로 교체
- **채택한 이유:**  데이터를 조회해 응답하는 기능이 아닌 만료 상태를 일괄 변경하는 로직임으로 JOIN FETCH 보다 벌크 UPDATE가 적합하다고 판단

```java
// ReservationSeatRepository — GameSeat 일괄 복구
@Modifying
@Query("""
    UPDATE GameSeat gs SET gs.status = 'AVAILABLE'
    WHERE gs.id IN (
        SELECT rs.gameSeat.id FROM ReservationSeat rs
        WHERE rs.reservation.status = 'HELD'
        AND rs.reservation.expiredAt < :now
    )
""")
int bulkRestoreExpiredGameSeats(@Param("now") LocalDateTime now);

// ReservationRepository — 예매 일괄 취소
@Modifying
@Query("""
    UPDATE Reservation r SET r.status = 'CANCELLED'
    WHERE r.status = 'HELD' AND r.expiredAt < :now
""")
int bulkCancelExpired(@Param("now") LocalDateTime now);
```

| 항목 | 개선 전 | 개선 후 |
|------|---------|---------|
| 쿼리 수 (3건) | 13번 | **2번** |
| 쿼리 수 (N건) | 1 + N×4번 | **2번** |

### 2. Enum ordinal 변환 에러

- **문제:** `@Enumerated(EnumType.STRING)` 추가 후 기존 DB 값과 매핑 실패
- **원인:** `@Enumerated` 어노테이션 없으면 Hibernate가 기본적으로 ordinal(숫자) 방식으로 저장
```
HELD = 0 / CONFIRMED = 1 / CANCELLED = 2
```
`EnumType.STRING` 추가 후 문자열로 읽으려 하는데 DB에는 숫자가 저장되어 있어서 충돌 발생
- **에러:**
```
No enum constant com.example.FullCount2.common.enums.ReservationStatus.0
```
- **해결:**

엔티티 수정
```java
@Column(nullable = false, length = 20)
@Enumerated(EnumType.STRING)
private ReservationStatus status;
```

DB 데이터 마이그레이션
```sql
UPDATE reservations SET status = 'HELD'      WHERE status = '0';
UPDATE reservations SET status = 'CONFIRMED' WHERE status = '1';
UPDATE reservations SET status = 'CANCELLED' WHERE status = '2';
```

> Enum 필드에는 항상 `@Enumerated(EnumType.STRING)` 명시 필요. ordinal 방식은 Enum 순서 변경 시 데이터가 깨지므로 실무에서도 STRING 방식을 권장함

<br>

## 💡 회고 및 개선 방향

| 현재 구현 | 개선 방향 |
|-----------|-----------|
| 낙관적 락 (@Version) | Redis 분산 락으로 고트래픽 대응 |
| @Scheduled 만료 처리 (벌크 업데이트) | Redis TTL 이벤트 방식으로 전환 |
| 로컬 실행 | Docker + AWS EC2 배포 |
| LIKE 기반 경기 검색 | Elasticsearch 전문 검색 엔진으로 전환 |
