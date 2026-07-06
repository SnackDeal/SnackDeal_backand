# /cs/qna/faq

개발순서: 2차
기능: 문의하기 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%AC%B8%EC%9D%98%ED%95%98%EA%B8%B0%207f1bdfb849fa83c997e7015eb095c2c4.md)
기능설명: FAQ 목록
도메인: user/cs
메서드: GET
사용자/관리자: 사용자

자주 묻는 질문 목록. 로그인 없이 조회 가능. 문의하기 폼 진입 전에 노출.

### Response

---

**`200`** 

```json
{
  "faqs": [
    {
      "id": 1,
      "type": "SHIPPING",
      "title": "배송은 얼마나 걸리나요?",
      "content": "결제 완료 후 평균 2~3일 이내 도착합니다..."
    },
    {
      "id": 2,
      "type": "PRODUCT",
      "title": "유통기한은 어떻게 되나요?",
      "content": "제조일로부터 6개월이며..."
    }
  ]
}
```

FAQ는 관리자 화면에서 관리 (3차) 또는 코드에 고정 (MVP는 고정 추천)