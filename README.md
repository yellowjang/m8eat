# 🧠 M8eat 프로젝트 소개
## 🏅SSAFY 관통 프로젝트 우수상 수상

## 📖 목차
1. [팀 구성원 소개](#팀-구성원-소개)
2. [프로젝트 개요](#프로젝트-개요)
3. [추진 계획](#추진-계획)
4. [시장 분석](#시장-분석)
5. [핵심 서비스 소개](#핵심-서비스-소개)
6. [화면 소개](#화면-소개)
7. [개발 결과](#개발-결과)
8. [AI 적용 내용](#ai-적용-내용)

---

## 👥 팀 구성원 소개
| 역할 | 이름 | 담당 업무 |
|------|------|-----------|
| 팀장 | 장아영 | 프로젝트 기획, 백엔드, 디자인 및 피그마 제작, 프론트, OAuth, 캘린더(vue cal), 사용자 인증(spring security), 발표 |
| 팀원 | 김민정 | 프로젝트 기획, 백엔드, 프론트, 이미지 인식 AI, 채팅, 사용자 인증 관리 |

---

## 💡 프로젝트 개요
### 기획 배경
- “음식을 일일이 찾아서 등록하는 게 너무 번거롭다.”
- “코치와의 연락이 부담스러워요.”

### 프로젝트 목적
- 기존 식단 기록 서비스의 번거로움을 줄이기 위해 **이미지 인식 AI** 기술 활용
- 코치와 회원 간 식단 공유를 간편하게 만들고, 불필요한 소통을 줄이는 효율적 방식 도입
- 코치는 일상/업무를 분리하며, 식단 피드백을 서비스 내 채팅 기능으로 전달 가능
- 향후 **코치 매칭 서비스**로의 확장도 고려

---

## 📅 추진 계획
- **05.12~15**: 기획 및 DB 설계, 피그마 제작
- **05.19~25**: 백엔드 개발, 프론트 동시 개발
- **05.25~27**: 개발 점검 및 발표 준비

---

## 📊 시장 분석
| 서비스 | 분석 |
|--------|------|
| **인아웃** | 칼로리 계산 + 다이어트 커뮤니티. 식단을 일일이 등록해야 함 |
| **밀로그** | 이미지 등록으로 식단 기록 가능하지만 영양정보 파악 어려움 |
| **타임스탬프** | 사진과 시간만 기록. 칼로리 계산 불가, 목적 구분 불명확 |

> 📌 결론: 단순 식단 기록에서 **이미지 분석 기반 식단 자동 등록과 공유 기능**으로 차별화된 서비스 제공

---

## 🧩 핵심 서비스 소개

### ✅ AI 이미지 분석 기반 식단 자동 등록
- Google Vision API + GPT + OpenAI 활용
- 이미지에서 음식명 추출 → 한글 번역 → 음식 DB 탐색 → 결과 반영

### ✅ 회원 유형 구분 (코치 / 일반회원)
- 가입 시 역할을 구분해 DB에 저장 (role: `coach`, `member`)
- 역할에 따라 UI와 기능 차별화 → 코치만 피드백/채팅 가능 등

---

## 🖼️ 화면 소개
### 회원가입/로그인
<img width="935" alt="회원가입/로그인" src="https://github.com/user-attachments/assets/cb4c535f-5cc8-490b-bdfb-cb7d4319363f" />

### 회원 유형별 다른 처리
<img width="937" alt="회원 유형별 처리" src="https://github.com/user-attachments/assets/97952cfb-3fb5-46a5-bdf2-0cd5f2d87488" />

### AI 식단 등록(메인 기능)
<img width="937" alt="AI 식단 등록" src="https://github.com/user-attachments/assets/3f3ef01c-a164-41c9-9947-0331594cb201" />

### 식단 분석 결과 (메인 화면)
<img width="942" alt="식단 분석 결과" src="https://github.com/user-attachments/assets/d88465b7-f9ff-4ea9-a7e8-d0af1aac90c0" />

### 마이페이지
<img width="941" alt="마이페이지" src="https://github.com/user-attachments/assets/c337d37f-7b30-4f54-9b29-ef12412dc851" />

### 채팅
<img width="942" alt="채팅" src="https://github.com/user-attachments/assets/ac8d2afa-4ae0-4364-bac5-52016ea8138f" />

---

## 🚧 개발 결과
### 전체 시스템 구성
- 회원가입 (코치/일반회원)
- 로그인 (일반/소셜)
- 메인 기능: 식단 기록, 달력
  - AI 이미지 분석 통한 식단 자동 등록
  - 식단 영양 정보 분석
- 마이페이지 (건강정보 등록, 채팅방 관리)
- 코치 기능: 멤버 관리, 식단 조회/채팅/삭제
- 커뮤니티 게시판

### 개발 환경
- 백엔드: Spring Boot
- 프론트엔드: Vue.js
- 데이터베이스: MySQL

### 협업 도구
- Notion
- GitLab

---

## 🤖 AI 적용 내용

### 🍱 음식 DB 처리 방식
- 식약처 API로 가져온 음식 데이터를 DB에 한 번 저장해 사용
- 이유:
  - 빠른 응답 속도 (API 호출보다 DB 조회가 빠름)
  - 트래픽 비용 없음
  - 자유도 높음 (이름 변환, 사용자 맞춤 필터링 가능)

### 🖼️ 식단 이미지 분석
- Google Vision API 사용 (Object Detection + Label Detection)
- 장점:
  - 위치 정보까지 검출 가능 (반응형 박스)
  - 다양한 음식 라벨 추출 가능 (김치, 햄버거 등)
  - 안정적이고 문서화 잘 되어 있음
- 한계:
  - 정확도가 낮을 수 있음 → GPT를 통해 보완 가능

### 👥 사용자 DB 구조 설계
- 소셜 로그인과 일반 로그인 사용자 모두 **하나의 User 테이블**에서 관리
- 이유:
  - "하나의 사용자" 개념으로 관리 효율화
  - 중복 코드 방지
  - 소셜 계정이 비밀번호 설정 후 일반 로그인으로도 사용 가능

```sql
User (
  id BIGINT PK,
  email VARCHAR UNIQUE,
  password VARCHAR NULL,
  oauth_provider VARCHAR NULL,  -- GOOGLE, KAKAO 등
  oauth_id VARCHAR NULL,
  name VARCHAR,
  role ENUM('coach', 'member'),
  profile_image_url TEXT,
  created_at DATETIME,
  updated_at DATETIME
)
```

---

## 📎 참고 자료
- [Canva 발표자료 보기](https://www.canva.com/design/DAGomJVnzaQ/YkY2A4ycSaN7nEAS6ZUcKg/view)
