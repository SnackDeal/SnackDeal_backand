# /chatbot/ask

개발순서: 3차
기능: 고객센터 챗봇페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B3%A0%EA%B0%9D%EC%84%BC%ED%84%B0%20%EC%B1%97%EB%B4%87%ED%8E%98%EC%9D%B4%EC%A7%80%20038bdfb849fa83c890e7817a1ebf79c2.md)
기능설명: 챗봇 질문
도메인: user/cs
메서드: POST
사용자/관리자: 사용자

### Request

---

#### **`Request Body`**

```json
{ 
  "question": "배송 얼마나 걸려요?" 
}
```

### **Response**

---

`200` AI를 "요약"으로 확정하면 생략

```json
{ "answer": "..." }
```

`503` AI 연결 오류