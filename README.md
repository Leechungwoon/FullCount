# ⚾ Full Count

> KBO 야구 티켓 예매 플랫폼  
> *3볼 2스트라이크처럼, 단 한 장의 티켓을 향한 긴장감*

---

## 프로젝트 소개

**Full Count**는 KBO 야구 경기 좌석을 예매할 수 있는 티켓팅 플랫폼입니다.  
동시에 같은 좌석을 예매하려는 다수의 요청을 안전하게 처리하기 위해  
**낙관적 락(Optimistic Lock)**을 적용하고 JMeter로 성능을 측정했습니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5, Spring Security |
| ORM | Spring Data JPA |
| Database | MySQL 8 |

| Auth | JWT (Access Token + Refresh Token) |
| Infra | AWS EC2, Docker |
| Load Test | JMeter |

---

## 핵심 기능

### 1. JWT 인증
- Spring Security 기반 Access Token + Refresh Token 구현
- Redis를 이용한 Refresh Token 저장 및 관리
- `@AuthenticationPrincipal`로 토큰에서 사용자 정보 추출

### 2. 경기 / 구역 / 좌석 조회
- 경기 일정, 구장 구역, 잔여 좌석을 계층 구조로 제공
- `GAME → SECTION → SEAT` 단계별 조회 API

### 3. 좌석 예매 (동시성 제어 핵심)
- 동시에 여러 사용자가 같은 좌석 예매 시 **중복 예매 방지**
- 낙관적 락(Optimistic Lock)을 적용하여 동시 요청 처리

### 4. 결제
- 예매와 연동된 결제 도메인 설계
- 결제 상태 관리 (PENDING → COMPLETED / CANCELLED)

### 5. 예매 만료 스케줄러
- 결제 미완료 예매를 주기적으로 감지하여 자동 만료 처리
- `@Scheduled`를 사용한 배치성 처리 구현

---

## 동시성 제어 (핵심 차별화 포인트)

좌석 예매 특성상 **동시 요청이 몰릴 때 하나의 좌석이 중복 예매되지 않아야 합니다.**

### 낙관적 락 (Optimistic Lock)

- `GAME_SEAT` 테이블에 `@Version` 필드를 적용
- 두 사용자가 동시에 같은 좌석을 예매할 경우, 먼저 커밋된 요청만 성공하고 나머지는 `OptimisticLockException` 발생
- 야구 티켓팅 특성상 **좌석별 동시 충돌 빈도가 낮아** 비관적 락보다 성능 오버헤드가 적은 낙관적 락을 선택

```java
@Entity
public class GameSeat {
    @Version
    private Long version;
    ...
}
```

### JMeter 성능 테스트

> 동시 사용자 N명이 동일 좌석에 요청하는 시나리오

| 항목 | 결과 |
|------|------|
| 중복 예매 발생 | 0건 |

> 📌 테스트 결과는 배포 후 실제 측정값으로 업데이트 예정

---

## ERD

**주요 테이블 (10개)**

| 테이블 | 설명 |
|--------|------|
| USER | 회원 정보 |
| STADIUM | 구장 |
| TEAM | 구단 |
| SECTION | 구장 내 구역 |
| SEAT | 좌석 |
| GAME | 경기 일정 |
| GAME_SEAT | 경기별 좌석 상태 (`@Version` 낙관적 락 적용) |
| RESERVATION | 예매 |
| RESERVATION_SEAT | 예매-좌석 매핑 |
| PAYMENT | 결제 |

---

## API 명세 요약

| 도메인 | 메서드 | URL | 설명 |
|--------|--------|-----|------|
| Auth | POST | `/api/auth/sign-up` | 회원가입 |
| Auth | POST | `/api/auth/sign-in` | 로그인 |
| Auth | POST | `/api/auth/refresh` | 토큰 재발급 |
| Game | GET | `/api/games` | 경기 목록 조회 |
| Game | GET | `/api/games/{gameId}/sections` | 구역 조회 |
| Seat | GET | `/api/games/{gameId}/sections/{sectionId}/seats` | 좌석 조회 |
| Reservation | POST | `/api/reservations` | 예매 생성 |
| Reservation | GET | `/api/reservations/{reservationId}` | 예매 조회 |
| Reservation | DELETE | `/api/reservations/{reservationId}` | 예매 취소 |
| Payment | POST | `/api/payments/{reservationId}` | 결제 승인 |
| Payment | POST | `/api/payments/{paymentId}/cancel` | 결제 취소 |

---

## 패키지 구조

```
com.example.FullCount2
├── domain
│   ├── auth          # JWT, Spring Security
│   ├── user          # 회원
│   ├── game          # 경기
│   ├── stadium       # 구장
│   ├── section       # 구역
│   ├── seat          # 좌석
│   ├── reservation   # 예매 + 동시성 제어
│   └── payment       # 결제
└── global
    ├── common        # 공통 응답, 예외
    ├── config        # Security, Redis, JPA 설정
    └── scheduler     # 예매 만료 처리
```

---

## 브랜치 전략

```
main
└── dev
    └── feat/{기능명}-이니셜
```

---

## 로컬 실행 방법

```bash
# 1. 레포지토리 클론
git clone https://github.com/{your-username}/full-count.git

# 2. 환경 변수 설정
# src/main/resources/application.yml에 DB, Redis, JWT 설정 입력

# 3. 실행
./gradlew bootRun
```

**필요한 환경**
- Java 17
- MySQL 8
- Redis

---

## 트러블슈팅 기록

주요 이슈와 해결 과정은 [Notion 개발 일지](#)에서 확인할 수 있습니다.

| 문제 | 원인 | 해결 |
|------|------|------|
| Hibernate Dialect 자동 감지 오류 | Spring Boot 3.x Hibernate 7 변경사항 | `database-platform` 명시적 설정 |
| MySQL 파일 소유권 문제 | sudo로 MySQL 실행 | `chown`으로 소유권 변경 후 일반 유저 실행 |
| Spring Security 403 오류 | SecurityConfig 경로 설정 누락 | permitAll 경로 추가 |

---

## 개발자

| 이름 | 역할 | GitHub |
|------|------|--------|
| 이청운 | Backend | [@icheong-un](#) |
