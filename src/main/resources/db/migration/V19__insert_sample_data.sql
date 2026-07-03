INSERT INTO member (email, password, name, status, created_at, birth, gender, role, phone)
VALUES ('admin@snackdeal.io', '$2a$10$i8m/KESQugr0BGZ4kR836.PWysUZsB2L8SdKnU0TVblZe9ksvvZTu', '관리자', 'ACTIVE', NOW(), '1990-01-01', 'MALE', 'ADMIN', '010-0000-0000');

INSERT INTO member (email, password, name, status, created_at, birth, gender, role, phone)
VALUES ('user@snackdeal.io', '$2a$10$2yvBdTv2pCD2ZxY7CdnZrOJ6r9Hp7z9.zqoObLTYjGQ/XB8euOHEW', '테스트유저', 'ACTIVE', NOW(), '1995-05-05', 'FEMALE', 'USER', '010-1111-2222');

INSERT INTO category (name, sort_order, created_at)
VALUES ('과자', 1, NOW()), ('음료', 2, NOW()), ('아이스크림', 3, NOW());

INSERT INTO product (name, price, description, status, stock, created_at, category_id)
VALUES
    ('감자칩', 1500, '바삭한 감자칩', 'ACTIVE', 100, NOW(), 1),
    ('초코과자', 2000, '달콤한 초콜릿 과자', 'ACTIVE', 80, NOW(), 1),
    ('콜라', 1800, '탄산음료 500ml', 'ACTIVE', 150, NOW(), 2),
    ('아이스티', 1700, '복숭아 아이스티', 'ACTIVE', 120, NOW(), 2),
    ('바닐라 아이스크림', 3000, '부드러운 바닐라 아이스크림', 'ACTIVE', 50, NOW(), 3);

INSERT INTO product_image (attachment_url, sort_order, product_id)
VALUES
    ('https://placehold.co/400x400?text=potato-chip', 0, 1),
    ('https://placehold.co/400x400?text=choco-snack', 0, 2),
    ('https://placehold.co/400x400?text=cola', 0, 3),
    ('https://placehold.co/400x400?text=ice-tea', 0, 4),
    ('https://placehold.co/400x400?text=ice-cream', 0, 5);
