# 알림 Front 서버 


## 지원자 정보
- 2595-000159_김진희_서버개발자-AI서비스


## 프로젝트 환경
- Java 17
- Gradle 8.13
- Spring Boot 3.0.4


## 주요 기능
1. **알림 발송 등록 API**
   - **즉시 발송**: 등록 즉시 알림 발송 이벤트 발생  
   - **예약 발송**: 요청받은 시각(yyyyMMddHHmm) 기반 예약 발송 처리  
   - 장애 발생 시, 발송 실패한 알림에 대해 재전송 요청

2. **알림 내역 조회 API**
   - 고객별 최근 3개월간 정상적으로 발송된 알림 내역 조회 (페이징 처리)
   - 빠른 응답을 위해 캐싱(Caffeine) 적용하여 빠른 응답 제공
     
## 시스템 구조
<img width="616" alt="image" src="https://github.com/user-attachments/assets/8fb46cfb-2c49-496f-b924-fedb17b19a9c" />
  
## 디렉토리 구조
```plaintext
com
└── example
    └── notification
        ├── NotificationApplication.java     // 스프링 부트 애플리케이션 진입점
        ├── client                           // 외부 알림 발송 API 연동 (Feign 클라이언트)
        ├── common                           // 공통 예외 및 응답 처리
        ├── config                           // 애플리케이션 설정 (Feign, 캐시, Swagger, 스케줄러, 비동기 등)
        ├── controller                       // 알림 API 엔드포인트 정의
        ├── domain                           // 도메인 관련 클래스 (DTO, Enum 등)
        ├── entity                           // JPA 엔티티 (알림 데이터)
        ├── event                            // 알림 발송 이벤트 및 이벤트 핸들러
        ├── repository                       // 데이터베이스 접근 (JPA Repository)
        ├── scheduler                        // 예약 발송 및 재전송 스케줄러
        └── service                          // 비즈니스 로직 (알림 등록, 조회, 발송 처리)
 ```

## 라이브러리
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Spring Cloud OpenFeign
- Lombok
- Springdoc OpenAPI
- Caffeine
- OkHttp

## API 명세
- **API의 전체 명세와 테스트 인터페이스를 제공합니다.**
   - http://localhost:8080/swagger-ui/index.html


## Executable jar
- **빌드 결과물**
   - https://github.com/kimjinheeee/20250524_2595-000159/tree/main/build/libs
- **실핼 방법**
   - $ java -jar notification-front-server-0.0.1-SNAPSHOT.jar
 
## 결과 화면 - 알림 즉시(IMMEDIATE) 발송
- **20:35**
  <img width="909" alt="Pasted Graphic 6" src="https://github.com/user-attachments/assets/89e760fa-080f-497a-a5a7-d343760e9ef9" />

- **20:36, 20:37**
  <img width="890" alt="Pasted Graphic 7" src="https://github.com/user-attachments/assets/ea751d47-3ea4-43ab-9a98-38246487a747" />

- **20:38**
  <img width="921" alt="image" src="https://github.com/user-attachments/assets/8d6d495b-9bad-4ddf-8f31-e995e7bd5bff" />



