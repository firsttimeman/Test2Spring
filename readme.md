# Blog Search API & Concurrency Control

Spring Boot 기반 블로그 검색 API 프로젝트입니다.  
외부 블로그 API(Kakao, Naver)를 연동하고 인기 검색어 집계 기능을 구현하면서  
동시 요청 환경에서 발생하는 **Lost Update 문제를 해결하는 것을 목표로 진행한 프로젝트입니다.**

---

# 프로젝트 개요

블로그 검색 서비스 구현 과정에서 다음 두 가지 문제를 해결하는 것을 목표로 했습니다.

1. 동일 키워드 동시 검색 시 발생하는 **Lost Update 문제**
2. 외부 API 장애 발생 시 서비스 중단 문제

이를 해결하기 위해 다음 기술을 적용했습니다.

- Redis 기반 **분산 락**
- **Resilience4j CircuitBreaker**
- **k6 부하 테스트**

---

# 기술 스택

## Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Redis (Redisson)
- MySQL

## Testing

- k6 (Load Testing)

## Infra

- Docker
- Docker Compose

---

# 시스템 구조

Client  
↓  
Spring Boot API  
↓  
MySQL / Redis

---

# 주요 기능

## 블로그 검색 API

- Kakao / Naver API 연동
- 검색 결과 조회

## 인기 검색어 집계

- 검색 시 키워드 count 증가
- 인기 검색어 TOP10 조회

## 외부 API 장애 대응

- Resilience4j CircuitBreaker 적용
- Kakao API 장애 발생 시 Naver API fallback

---

# 동시성 문제

동일 키워드에 대한 동시 검색 요청이 발생할 경우  
다음과 같은 **Lost Update 문제**가 발생했습니다.

Thread A: count = 100 읽음  
Thread B: count = 100 읽음

Thread A: count = 101 저장  
Thread B: count = 101 저장

→ 실제 요청 2건이지만 결과는 +1만 반영

즉 동시 요청 환경에서 인기 검색어 집계 데이터의 **정합성이 깨지는 문제**가 발생했습니다.

---

# 해결 과정

## Redis 분산 락 적용

동일 키워드 요청을 직렬화하기 위해 **Redisson 기반 분산 락을 적용**했습니다.

처리 흐름

Lock 획득  
→ 키워드 조회  
→ count 증가  
→ Lock 해제

이를 통해 동시 요청 환경에서도 데이터 정합성을 보장할 수 있도록 구현했습니다.

---

## Spring AOP Proxy 문제 해결

초기 구현에서는 같은 클래스 내부에서 메서드를 호출하면서  
Spring AOP Proxy가 적용되지 않는 문제가 발생했습니다.

Service.methodA()  
→ Service.methodB()

이 경우 AOP가 적용되지 않아 분산락이 동작하지 않았습니다.

이를 해결하기 위해 **Lock 전용 서비스 클래스를 분리**했습니다.

KeywordService  
↓  
KeywordLockService (@DistributeLock)

이를 통해 분산락이 정상적으로 적용되도록 구조를 개선했습니다.

---

# 부하 테스트

동시 요청 환경을 검증하기 위해 **k6 기반 부하 테스트를 진행했습니다.**

테스트 조건

VUs: 100  
Iterations: 5000  
Keyword: "spring"

테스트 결과

5000 requests  
keyword count = 5000

동시 요청 환경에서도 **데이터 유실 없이 정확한 카운트 증가**가 이루어지는 것을 확인했습니다.

---

# 실행 방법

## 1 프로젝트 빌드

./gradlew bootJar

## 2 Docker 실행

docker compose up -d --build

## 3 부하 테스트 실행

k6 run test.js

---

# 프로젝트를 통해 배운 점

- 동시 요청 환경에서 발생하는 **Lost Update 문제 분석**
- Redis 기반 **분산 락 설계 및 구현**
- Spring **AOP Proxy 동작 방식 이해**
- k6 기반 **부하 테스트를 통한 동시성 검증**