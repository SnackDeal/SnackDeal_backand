-- =====================================================================
-- 개발용 시드 데이터 (팀 공유용).
-- V19 에 이어서 추가로 넣는다. FK 는 ID 하드코딩 대신 이메일/이름 기준
-- 서브쿼리로 참조하므로 auto_increment 값이 달라도 안전하다.
-- 추가 회원의 비밀번호는 기존 user 계정과 동일한 'user1234' 이다.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 회원 (테스트 구매자 8명, 비밀번호 user1234)
-- ---------------------------------------------------------------------
INSERT INTO member (email, password, name, status, created_at, birth, gender, role, phone) VALUES
('buyer01@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '김민준', 'ACTIVE', NOW(), '1992-03-11', 'MALE',   'USER', '010-1000-0001'),
('buyer02@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '이서연', 'ACTIVE', NOW(), '1994-07-22', 'FEMALE', 'USER', '010-1000-0002'),
('buyer03@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '박도윤', 'ACTIVE', NOW(), '1988-11-02', 'MALE',   'USER', '010-1000-0003'),
('buyer04@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '최지우', 'ACTIVE', NOW(), '1997-01-30', 'FEMALE', 'USER', '010-1000-0004'),
('buyer05@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '정하준', 'ACTIVE', NOW(), '1991-09-14', 'MALE',   'USER', '010-1000-0005'),
('buyer06@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '강수아', 'ACTIVE', NOW(), '1996-05-08', 'FEMALE', 'USER', '010-1000-0006'),
('buyer07@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '윤건우', 'ACTIVE', NOW(), '1993-12-19', 'MALE',   'USER', '010-1000-0007'),
('buyer08@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '임채원', 'ACTIVE', NOW(), '1999-02-25', 'FEMALE', 'USER', '010-1000-0008');

-- ---------------------------------------------------------------------
-- 카테고리 (기존 과자/음료/아이스크림 에 4개 추가)
-- ---------------------------------------------------------------------
INSERT INTO category (name, sort_order, created_at) VALUES
('초콜릿', 4, NOW()),
('젤리',   5, NOW()),
('쿠키',   6, NOW()),
('라면',   7, NOW());

-- ---------------------------------------------------------------------
-- 상품 (21개) - category_id 는 이름으로 조회
-- ---------------------------------------------------------------------
INSERT INTO product (name, price, description, status, stock, created_at, category_id) VALUES
('새우칩',         1600, '고소한 새우 스낵',        'ACTIVE',  90, NOW(), (SELECT id FROM category WHERE name='과자')),
('오징어땅콩',     2500, '오징어와 땅콩의 조합',    'ACTIVE',  70, NOW(), (SELECT id FROM category WHERE name='과자')),
('나초칩',         2200, '치즈맛 나초칩',           'ACTIVE',  60, NOW(), (SELECT id FROM category WHERE name='과자')),
('팝콘',           1800, '버터맛 팝콘',             'ACTIVE', 110, NOW(), (SELECT id FROM category WHERE name='과자')),
('프레첼',         2000, '짭짤한 프레첼',           'ACTIVE',  85, NOW(), (SELECT id FROM category WHERE name='과자')),
('사이다',         1800, '청량한 사이다 500ml',     'ACTIVE', 140, NOW(), (SELECT id FROM category WHERE name='음료')),
('오렌지주스',     2500, '100% 오렌지주스',         'ACTIVE',  95, NOW(), (SELECT id FROM category WHERE name='음료')),
('이온음료',       1500, '갈증해소 이온음료',       'ACTIVE', 130, NOW(), (SELECT id FROM category WHERE name='음료')),
('커피우유',       1400, '달콤한 커피우유',         'ACTIVE', 160, NOW(), (SELECT id FROM category WHERE name='음료')),
('생수',            900, '무기질 생수 500ml',       'ACTIVE', 300, NOW(), (SELECT id FROM category WHERE name='음료')),
('초코바',         1200, '초콜릿 아이스바',         'ACTIVE',  75, NOW(), (SELECT id FROM category WHERE name='아이스크림')),
('딸기콘',         1500, '딸기맛 아이스콘',         'ACTIVE',  65, NOW(), (SELECT id FROM category WHERE name='아이스크림')),
('녹차아이스크림', 3200, '진한 녹차 아이스크림',    'ACTIVE',  40, NOW(), (SELECT id FROM category WHERE name='아이스크림')),
('다크초콜릿',     3500, '카카오 72% 다크초콜릿',   'ACTIVE',  55, NOW(), (SELECT id FROM category WHERE name='초콜릿')),
('밀크초콜릿바',   1500, '부드러운 밀크초콜릿',     'ACTIVE', 120, NOW(), (SELECT id FROM category WHERE name='초콜릿')),
('곰젤리',         1800, '과일맛 곰젤리',           'ACTIVE', 100, NOW(), (SELECT id FROM category WHERE name='젤리')),
('콜라젤리',       1700, '콜라맛 젤리',             'ACTIVE',  95, NOW(), (SELECT id FROM category WHERE name='젤리')),
('초코칩쿠키',     2800, '청크 초코칩 쿠키',        'ACTIVE',  60, NOW(), (SELECT id FROM category WHERE name='쿠키')),
('버터쿠키',       2600, '고소한 버터쿠키',         'ACTIVE',  70, NOW(), (SELECT id FROM category WHERE name='쿠키')),
('매운라면',       1300, '얼큰한 매운라면',         'ACTIVE', 200, NOW(), (SELECT id FROM category WHERE name='라면')),
('치즈라면',       1400, '고소한 치즈라면',         'ACTIVE', 180, NOW(), (SELECT id FROM category WHERE name='라면'));

-- ---------------------------------------------------------------------
-- 상품 이미지 (신규 상품 1장씩)
-- ---------------------------------------------------------------------
INSERT INTO product_image (attachment_url, sort_order, product_id) VALUES
('https://placehold.co/400x400?text=shrimp',      0, (SELECT id FROM product WHERE name='새우칩')),
('https://placehold.co/400x400?text=squid',       0, (SELECT id FROM product WHERE name='오징어땅콩')),
('https://placehold.co/400x400?text=nacho',       0, (SELECT id FROM product WHERE name='나초칩')),
('https://placehold.co/400x400?text=popcorn',     0, (SELECT id FROM product WHERE name='팝콘')),
('https://placehold.co/400x400?text=pretzel',     0, (SELECT id FROM product WHERE name='프레첼')),
('https://placehold.co/400x400?text=cider',       0, (SELECT id FROM product WHERE name='사이다')),
('https://placehold.co/400x400?text=orange',      0, (SELECT id FROM product WHERE name='오렌지주스')),
('https://placehold.co/400x400?text=ion',         0, (SELECT id FROM product WHERE name='이온음료')),
('https://placehold.co/400x400?text=coffeemilk',  0, (SELECT id FROM product WHERE name='커피우유')),
('https://placehold.co/400x400?text=water',       0, (SELECT id FROM product WHERE name='생수')),
('https://placehold.co/400x400?text=chocobar',    0, (SELECT id FROM product WHERE name='초코바')),
('https://placehold.co/400x400?text=strawcone',   0, (SELECT id FROM product WHERE name='딸기콘')),
('https://placehold.co/400x400?text=greentea',    0, (SELECT id FROM product WHERE name='녹차아이스크림')),
('https://placehold.co/400x400?text=darkchoco',   0, (SELECT id FROM product WHERE name='다크초콜릿')),
('https://placehold.co/400x400?text=milkchoco',   0, (SELECT id FROM product WHERE name='밀크초콜릿바')),
('https://placehold.co/400x400?text=gummy',       0, (SELECT id FROM product WHERE name='곰젤리')),
('https://placehold.co/400x400?text=colajelly',   0, (SELECT id FROM product WHERE name='콜라젤리')),
('https://placehold.co/400x400?text=chococookie', 0, (SELECT id FROM product WHERE name='초코칩쿠키')),
('https://placehold.co/400x400?text=buttercookie',0, (SELECT id FROM product WHERE name='버터쿠키')),
('https://placehold.co/400x400?text=spicyramen',  0, (SELECT id FROM product WHERE name='매운라면')),
('https://placehold.co/400x400?text=cheeseramen', 0, (SELECT id FROM product WHERE name='치즈라면'));

-- ---------------------------------------------------------------------
-- 쿠폰 이벤트 게시판 (3개)
-- ---------------------------------------------------------------------
INSERT INTO coupon_board (title, content, thumbnail_url, is_active, start_at, end_at, created_at) VALUES
('신규가입 이벤트', '가입만 해도 쿠폰 증정!',       'https://placehold.co/800x300?text=welcome', 1, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), NOW()),
('여름맞이 특가',   '시원한 여름 할인 쿠폰',         'https://placehold.co/800x300?text=summer',  1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
('추석 감사제',     '추석 맞이 감사 쿠폰',           'https://placehold.co/800x300?text=chuseok', 1, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), NOW());

-- ---------------------------------------------------------------------
-- 쿠폰 (6개) - coupon_board_id 는 제목으로 조회
-- ---------------------------------------------------------------------
INSERT INTO coupon (name, discount_type, discount_value, min_order_price, valid_from, valid_until, total_quantity, issued_quantity, issue_type, is_active, created_at, coupon_board_id) VALUES
('신규가입 3천원',  'FIXED',   3000,     0, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 1000, 0, 'EVENT',  1, NOW(), (SELECT id FROM coupon_board WHERE title='신규가입 이벤트')),
('5%할인쿠폰',      'PERCENT',    5, 10000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY),  500, 0, 'EVENT',  1, NOW(), (SELECT id FROM coupon_board WHERE title='여름맞이 특가')),
('10%할인쿠폰',     'PERCENT',   10, 20000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY),  300, 0, 'EVENT',  1, NOW(), (SELECT id FROM coupon_board WHERE title='여름맞이 특가')),
('여름특가 15%',    'PERCENT',   15, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY),  200, 0, 'EVENT',  1, NOW(), (SELECT id FROM coupon_board WHERE title='여름맞이 특가')),
('추석 2천원 할인', 'FIXED',   2000, 15000, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY),  400, 0, 'EVENT',  1, NOW(), (SELECT id FROM coupon_board WHERE title='추석 감사제')),
('가입축하쿠폰',    'FIXED',   1000,     0, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 9999, 0, 'SIGNIN', 1, NOW(), NULL);

-- ---------------------------------------------------------------------
-- 회원 보유 쿠폰 (8개) - member/coupon 은 이메일/이름으로 조회
-- ---------------------------------------------------------------------
INSERT INTO user_coupon (status, issued_at, used_at, member_id, coupon_id) VALUES
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='user@snackdeal.io'),   (SELECT id FROM coupon WHERE name='신규가입 3천원')),
('USED',   NOW(), NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io'),   (SELECT id FROM coupon WHERE name='5%할인쿠폰')),
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='buyer01@snackdeal.io'),(SELECT id FROM coupon WHERE name='10%할인쿠폰')),
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='buyer02@snackdeal.io'),(SELECT id FROM coupon WHERE name='추석 2천원 할인')),
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='buyer03@snackdeal.io'),(SELECT id FROM coupon WHERE name='가입축하쿠폰')),
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='buyer04@snackdeal.io'),(SELECT id FROM coupon WHERE name='여름특가 15%')),
('ACTIVE', NOW(), NULL,  (SELECT id FROM member WHERE email='buyer05@snackdeal.io'),(SELECT id FROM coupon WHERE name='5%할인쿠폰')),
('USED',   NOW(), NOW(), (SELECT id FROM member WHERE email='buyer06@snackdeal.io'),(SELECT id FROM coupon WHERE name='10%할인쿠폰'));

-- ---------------------------------------------------------------------
-- 배송지 (8개)
-- ---------------------------------------------------------------------
INSERT INTO delivery (name, receiver_name, receiver_phone, zipcode, address, detail_address, is_default, created_at, member_id) VALUES
('집',   '테스트유저', '010-1111-2222', '06236', '서울시 강남구 테헤란로 123',    '101동 1001호', 1, NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io')),
('회사', '테스트유저', '010-1111-2222', '04524', '서울시 중구 세종대로 110',      '5층',          0, NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io')),
('집',   '김민준',     '010-1000-0001', '13529', '경기도 성남시 분당구 판교로 200','202호',        1, NOW(), (SELECT id FROM member WHERE email='buyer01@snackdeal.io')),
('집',   '이서연',     '010-1000-0002', '48058', '부산시 해운대구 센텀로 45',     '303호',        1, NOW(), (SELECT id FROM member WHERE email='buyer02@snackdeal.io')),
('집',   '박도윤',     '010-1000-0003', '34126', '대전시 유성구 대학로 99',       '1층',          1, NOW(), (SELECT id FROM member WHERE email='buyer03@snackdeal.io')),
('집',   '최지우',     '010-1000-0004', '61186', '광주시 북구 첨단과기로 123',    '404호',        1, NOW(), (SELECT id FROM member WHERE email='buyer04@snackdeal.io')),
('집',   '정하준',     '010-1000-0005', '41931', '대구시 중구 국채보상로 500',    '5층',          1, NOW(), (SELECT id FROM member WHERE email='buyer05@snackdeal.io')),
('집',   '강수아',     '010-1000-0006', '22382', '인천시 중구 공항로 271',        '606호',        1, NOW(), (SELECT id FROM member WHERE email='buyer06@snackdeal.io'));

-- ---------------------------------------------------------------------
-- 공지사항 (6개)
-- ---------------------------------------------------------------------
INSERT INTO notice (title, content, is_pinned, created_at) VALUES
('서비스 오픈 안내',        'SnackDeal 서비스가 오픈했습니다. 많은 이용 바랍니다.', 1, NOW()),
('개인정보 처리방침 개정',  '개인정보 처리방침이 개정되었습니다.',                  0, NOW()),
('추석 배송 일정 안내',     '추석 연휴 배송 일정을 안내드립니다.',                  1, NOW()),
('앱 업데이트 안내',        '더 나은 서비스를 위해 앱이 업데이트되었습니다.',       0, NOW()),
('여름 특가 이벤트 오픈',   '여름맞이 특가 쿠폰을 지금 받아보세요.',                0, NOW()),
('고객센터 운영시간 변경',  '고객센터 운영시간이 평일 09~18시로 변경됩니다.',       0, NOW());

-- ---------------------------------------------------------------------
-- FAQ (8개)
-- ---------------------------------------------------------------------
INSERT INTO faq (type, title, content, created_at) VALUES
('ORDER',    '주문은 어떻게 취소하나요?',       '배송 준비 전 상태에서 주문 상세페이지에서 취소할 수 있습니다.', NOW()),
('ORDER',    '주문 내역은 어디서 확인하나요?',  '마이페이지 > 주문내역에서 확인할 수 있습니다.',                 NOW()),
('SHIPPING', '배송은 얼마나 걸리나요?',         '결제 완료 후 평균 2~3일 소요됩니다.',                           NOW()),
('SHIPPING', '배송비는 얼마인가요?',            '3만원 이상 구매 시 무료, 미만은 3,000원입니다.',                NOW()),
('PRODUCT',  '상품 재고는 실시간인가요?',       '재고는 실시간으로 반영됩니다.',                                 NOW()),
('PRODUCT',  '유통기한은 어떻게 확인하나요?',   '상품 상세페이지 하단에서 확인할 수 있습니다.',                  NOW()),
('OTHER',    '회원 탈퇴는 어떻게 하나요?',      '마이페이지 > 설정에서 탈퇴할 수 있습니다.',                     NOW()),
('OTHER',    '쿠폰은 중복 사용이 되나요?',      '쿠폰은 주문당 1장만 사용 가능합니다.',                          NOW());

-- ---------------------------------------------------------------------
-- 1:1 문의 (6개) - member 는 이메일로 조회, 답변은 아래에서 연결
-- ---------------------------------------------------------------------
INSERT INTO qna (type, title, content, attachment_url, is_answered, created_at, member_id) VALUES
('ORDER',    '주문 취소 요청합니다',        '주문번호 확인 부탁드립니다.',       NULL, 1, NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io')),
('SHIPPING', '배송이 안 와요',              '3일째 배송중입니다.',               NULL, 1, NOW(), (SELECT id FROM member WHERE email='buyer01@snackdeal.io')),
('PRODUCT',  '상품이 파손되어 왔어요',      '교환 가능한가요?',                  NULL, 1, NOW(), (SELECT id FROM member WHERE email='buyer02@snackdeal.io')),
('PRODUCT',  '유통기한 문의드립니다',       '남은 기한이 궁금합니다.',           NULL, 0, NOW(), (SELECT id FROM member WHERE email='buyer03@snackdeal.io')),
('OTHER',    '쿠폰 적용이 안돼요',          '결제 시 쿠폰이 안 보입니다.',       NULL, 0, NOW(), (SELECT id FROM member WHERE email='buyer04@snackdeal.io')),
('ORDER',    '결제 오류 문의',              '결제가 두 번 된 것 같아요.',        NULL, 0, NOW(), (SELECT id FROM member WHERE email='buyer05@snackdeal.io'));

-- ---------------------------------------------------------------------
-- 문의 답변 (3개, is_answered=1 인 문의에 연결)
-- ---------------------------------------------------------------------
INSERT INTO qna_answer (content, answered_at, qna_id) VALUES
('주문 취소 처리해드렸습니다. 확인 부탁드립니다.', NOW(), (SELECT id FROM qna WHERE title='주문 취소 요청합니다')),
('배송사에 확인 요청했으며 곧 도착 예정입니다.',   NOW(), (SELECT id FROM qna WHERE title='배송이 안 와요')),
('파손 상품은 무상 교환해드립니다. 접수되었습니다.', NOW(), (SELECT id FROM qna WHERE title='상품이 파손되어 왔어요'));

-- ---------------------------------------------------------------------
-- 주문 (6개) - order_number 유니크, member 는 이메일로 조회
-- ---------------------------------------------------------------------
INSERT INTO orders (order_number, product_amount, shipping_fee, discount_amount, final_amount, status, ordered_at, created_at, member_id) VALUES
('ORD-20260701-0001',  4800, 3000,    0,  7800, 'COMPLETED',          DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io')),
('ORD-20260701-0002', 11000,    0, 1000, 10000, 'SHIPPED',            DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), (SELECT id FROM member WHERE email='buyer01@snackdeal.io')),
('ORD-20260702-0003',  5400, 3000,    0,  8400, 'PREPARING_SHIPMENT', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), (SELECT id FROM member WHERE email='buyer02@snackdeal.io')),
('ORD-20260702-0004',  3500, 3000,    0,  6500, 'PAYMENT_COMPLETED',  DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), (SELECT id FROM member WHERE email='buyer03@snackdeal.io')),
('ORD-20260703-0005',  9000,    0,    0,  9000, 'CANCELLED',          DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), (SELECT id FROM member WHERE email='buyer04@snackdeal.io')),
('ORD-20260703-0006',  6200, 3000,    0,  9200, 'COMPLETED',          NOW(),                          NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io'));

-- ---------------------------------------------------------------------
-- 주문 항목 (order_number/상품명으로 조회, product_name 은 스냅샷)
-- ---------------------------------------------------------------------
INSERT INTO order_item (product_name, price, quantity, product_id, order_id) VALUES
('감자칩',   1500, 2, (SELECT id FROM product WHERE name='감자칩'),   (SELECT id FROM orders WHERE order_number='ORD-20260701-0001')),
('콜라',     1800, 1, (SELECT id FROM product WHERE name='콜라'),     (SELECT id FROM orders WHERE order_number='ORD-20260701-0001')),
('팝콘',     1800, 3, (SELECT id FROM product WHERE name='팝콘'),     (SELECT id FROM orders WHERE order_number='ORD-20260701-0002')),
('생수',      900, 6, (SELECT id FROM product WHERE name='생수'),     (SELECT id FROM orders WHERE order_number='ORD-20260701-0002')),
('나초칩',   2200, 1, (SELECT id FROM product WHERE name='나초칩'),   (SELECT id FROM orders WHERE order_number='ORD-20260702-0003')),
('오징어땅콩',2500,1, (SELECT id FROM product WHERE name='오징어땅콩'),(SELECT id FROM orders WHERE order_number='ORD-20260702-0003')),
('다크초콜릿',3500,1, (SELECT id FROM product WHERE name='다크초콜릿'),(SELECT id FROM orders WHERE order_number='ORD-20260702-0004')),
('녹차아이스크림',3200,1,(SELECT id FROM product WHERE name='녹차아이스크림'),(SELECT id FROM orders WHERE order_number='ORD-20260703-0005')),
('밀크초콜릿바',1500,2,(SELECT id FROM product WHERE name='밀크초콜릿바'),(SELECT id FROM orders WHERE order_number='ORD-20260703-0005')),
('초코칩쿠키',2800,1, (SELECT id FROM product WHERE name='초코칩쿠키'),(SELECT id FROM orders WHERE order_number='ORD-20260703-0006')),
('곰젤리',   1800, 1, (SELECT id FROM product WHERE name='곰젤리'),   (SELECT id FROM orders WHERE order_number='ORD-20260703-0006'));

-- ---------------------------------------------------------------------
-- 결제 (주문당 1건)
-- ---------------------------------------------------------------------
INSERT INTO payment (imp_uid, merchant_uid, amount, pay_method, pg_provider, status, receipt_url, paid_at, cancelled_at, created_at, order_id) VALUES
('imp_1001', 'ORD-20260701-0001',  7800, 'card', 'kakaopay', 'PAID',      'https://receipt.example/1', DATE_SUB(NOW(), INTERVAL 5 DAY), NULL,   NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260701-0001')),
('imp_1002', 'ORD-20260701-0002', 10000, 'card', 'tosspay',  'PAID',      'https://receipt.example/2', DATE_SUB(NOW(), INTERVAL 4 DAY), NULL,   NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260701-0002')),
('imp_1003', 'ORD-20260702-0003',  8400, 'card', 'kakaopay', 'PAID',      'https://receipt.example/3', DATE_SUB(NOW(), INTERVAL 3 DAY), NULL,   NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260702-0003')),
('imp_1004', 'ORD-20260702-0004',  6500, 'card', 'nicepay',  'PAID',      'https://receipt.example/4', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL,   NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260702-0004')),
('imp_1005', 'ORD-20260703-0005',  9000, 'card', 'kakaopay', 'CANCELLED', 'https://receipt.example/5', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260703-0005')),
('imp_1006', 'ORD-20260703-0006',  9200, 'card', 'tosspay',  'PAID',      'https://receipt.example/6', NOW(),                          NULL,   NOW(), (SELECT id FROM orders WHERE order_number='ORD-20260703-0006'));

-- ---------------------------------------------------------------------
-- 배송 (주문당 1건)
-- ---------------------------------------------------------------------
INSERT INTO shipping (order_id, receiver_name, receiver_phone, zipcode, address, detail_address, delivery_request, courier, tracking_number, status, shipped_at, delivered_at, created_at) VALUES
((SELECT id FROM orders WHERE order_number='ORD-20260701-0001'), '테스트유저', '010-1111-2222', '06236', '서울시 강남구 테헤란로 123',     '101동 1001호', '문 앞에 놓아주세요',   'CJ대한통운', 'CJ1000000001', 'DELIVERED', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
((SELECT id FROM orders WHERE order_number='ORD-20260701-0002'), '김민준',     '010-1000-0001', '13529', '경기도 성남시 분당구 판교로 200', '202호',        '부재시 경비실',       '한진택배',   'HJ1000000002', 'SHIPPING',  DATE_SUB(NOW(), INTERVAL 2 DAY), NULL,                            NOW()),
((SELECT id FROM orders WHERE order_number='ORD-20260702-0003'), '이서연',     '010-1000-0002', '48058', '부산시 해운대구 센텀로 45',       '303호',        NULL,                   NULL,         NULL,           'PREPARING', NULL,                            NULL,                            NOW()),
((SELECT id FROM orders WHERE order_number='ORD-20260702-0004'), '박도윤',     '010-1000-0003', '34126', '대전시 유성구 대학로 99',         '1층',          '빠른 배송 부탁드려요', NULL,         NULL,           'READY',     NULL,                            NULL,                            NOW()),
((SELECT id FROM orders WHERE order_number='ORD-20260703-0005'), '최지우',     '010-1000-0004', '61186', '광주시 북구 첨단과기로 123',      '404호',        NULL,                   NULL,         NULL,           'READY',     NULL,                            NULL,                            NOW()),
((SELECT id FROM orders WHERE order_number='ORD-20260703-0006'), '테스트유저', '010-1111-2222', '06236', '서울시 강남구 테헤란로 123',     '101동 1001호', NULL,                   'CJ대한통운', 'CJ1000000006', 'DELIVERED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(),                           NOW());

-- ---------------------------------------------------------------------
-- 장바구니 (8개) - (member, product) 조합 유니크
-- ---------------------------------------------------------------------
INSERT INTO cart_item (quantity, created_at, updated_at, member_id, product_id) VALUES
(2, NOW(), NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io'),    (SELECT id FROM product WHERE name='새우칩')),
(1, NOW(), NOW(), (SELECT id FROM member WHERE email='user@snackdeal.io'),    (SELECT id FROM product WHERE name='콜라')),
(3, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer01@snackdeal.io'), (SELECT id FROM product WHERE name='팝콘')),
(6, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer02@snackdeal.io'), (SELECT id FROM product WHERE name='생수')),
(1, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer03@snackdeal.io'), (SELECT id FROM product WHERE name='곰젤리')),
(2, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer04@snackdeal.io'), (SELECT id FROM product WHERE name='매운라면')),
(1, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer05@snackdeal.io'), (SELECT id FROM product WHERE name='초코칩쿠키')),
(1, NOW(), NOW(), (SELECT id FROM member WHERE email='buyer06@snackdeal.io'), (SELECT id FROM product WHERE name='다크초콜릿'));
