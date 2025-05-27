drop database m8eat;
create database if not exists m8eat;
use m8eat;


drop table IF EXISTS users_health_info; 
drop table IF EXISTS users; 

drop database m8eat;

drop table users_health_info;
drop table coach_user_map;
drop table diets_food;
drop table diets;
drop table users;

-- 사용자 정보
CREATE TABLE IF NOT EXISTS users (
    user_no INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    id VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    profile_image_path VARCHAR(255),
    coach_no INT,
    CONSTRAINT fk_coach FOREIGN KEY (coach_no) REFERENCES users(user_no) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS coach_user_map (
    coach_no INT NOT NULL,
    user_no INT NOT NULL,
    PRIMARY KEY (coach_no, user_no),
    FOREIGN KEY (coach_no) REFERENCES users(user_no) ON DELETE CASCADE,
    FOREIGN KEY (user_no) REFERENCES users(user_no) ON DELETE CASCADE
);


-- 1. 코치 등록 (user_no = 1)
INSERT INTO users (name, id, password, role, profile_image_path)
VALUES ('김코치', 'coachkim', 'coach1234', 'coach', 'images/coachkim.jpg');

-- 2. 일반 회원 등록 (user_no = 2)
INSERT INTO users (name, id, password, role, profile_image_path, coach_no)
VALUES ('이회원', 'userlee', 'userlee123', 'user', 'images/userlee.jpg', 1);

-- 3. 일반 회원 등록 (user_no = 3)
INSERT INTO users (name, id, password, role, profile_image_path, coach_no)
VALUES ('박회원', 'userpark', 'userpark123', 'user', 'images/userpark.jpg', 1);

-- 4. 다른 코치 등록 (user_no = 4)
INSERT INTO users (name, id, password, role, profile_image_path)
VALUES ('최코치', 'coachchoi', 'choi1234', 'coach', 'images/coachchoi.jpg');

-- 5. 일반 회원 등록 (user_no = 5)
INSERT INTO users (name, id, password, role, profile_image_path, coach_no)
VALUES ('정회원', 'userjung', 'userjung123', 'user', 'images/userjung.jpg', 4);

-- 6. coach_user_map 관계 설정 (코치가 담당하는 회원 등록)
-- 김코치 (1번)가 이회원(2번), 박회원(3번)을 담당
INSERT INTO coach_user_map (coach_no, user_no) VALUES (1, 2);
INSERT INTO coach_user_map (coach_no, user_no) VALUES (1, 3);

-- 최코치 (4번)가 정회원(5번)을 담당
INSERT INTO coach_user_map (coach_no, user_no) VALUES (4, 5);

select * from coach_user_map;

INSERT INTO users (name, id, password, role, coach_no)
VALUES ('김회원', 'kim', 'kim', 'user', 12),
 ('박회원', 'park', 'park', 'user', 12);




select * from users;

update users
set coach_no=12
where user_no=13;

update users_health_info
set height=170, weight=60, illness='', allergy='견과류', purpose='건강 유지'
where user_no=12;

update users_health_info
set height=165, weight=48, illness='관절염', allergy='밀', purpose='근육 증가'
where user_no=13;

select * from users_health_info;

delete from users where user_no=8;
-- 사용자 건강 정보
CREATE TABLE if not exists users_health_info (
    info_no INT PRIMARY KEY AUTO_INCREMENT,
    user_no INT not null,
    height double,
    weight double,
    illness TEXT,
    allergy TEXT,
    purpose TEXT,
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

select * from users_health_info;

-- 식단



drop table diets; 
drop table diets_food;
CREATE TABLE if not exists diets (
    diet_no INT PRIMARY KEY AUTO_INCREMENT,
    user_no INT not null,
    meal_date datetime,
    reg_date TIMESTAMP default now(),
    meal_type VARCHAR(10) not null,
    file_path VARCHAR(255),
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO diets (user_no, reg_date, meal_type, file_path) VALUES
(13, '2025-05-01 08:00:00', '아침', 'uploads/breakfast_1.jpg'),
(13, '2025-05-01 12:30:00', '점심', 'uploads/lunch_1.jpg'),
(13, '2025-05-01 19:00:00', '저녁', 'uploads/dinner_1.jpg'),
(13, '2025-05-02 08:15:00', '아침', 'uploads/breakfast_2.jpg'),
(13, '2025-05-02 13:00:00', '점심', 'uploads/lunch_2.jpg'),
(13, '2025-05-02 19:10:00', '저녁', 'uploads/dinner_2.jpg'),
(13, '2025-05-03 07:50:00', '아침', 'uploads/breakfast_3.jpg'),
(13, '2025-05-03 12:40:00', '점심', 'uploads/lunch_3.jpg'),
(13, '2025-05-03 18:55:00', '저녁', 'uploads/dinner_3.jpg'),
(13, '2025-05-04 08:20:00', '아침', 'uploads/breakfast_4.jpg');

select * from users;
select * from diets;
select * from diets_food;

drop table diets_food;
drop table diets;


-- 음식 데이터
CREATE TABLE food (
    food_id INT PRIMARY KEY,
    calories INT,
    name_ko VARCHAR(100) NOT NULL,
    protein DOUBLE,
    fat DOUBLE,
    carbohydrate DOUBLE,
    sugar DOUBLE,
    cholesterol DOUBLE
);

drop table food;
select * from food;
select * from users;

select * from food where name_ko LIKE '%샐러드%';
select * from food; 

        INSERT INTO users_health_info (user_no, height, weight, illness, allergy, purpose)
          values (2, 0, 0, "", "", "");


-- 식단 음식 구성
CREATE TABLE IF NOT EXISTS diets_food (
    no INT PRIMARY KEY AUTO_INCREMENT,
    diet_no INT NOT NULL,
    food_id INT NOT NULL,
    food_name VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL, -- 섭취량 (g)
    calorie INT NOT NULL, -- 계산된 kcal
    protein DOUBLE NOT NULL,
    fat DOUBLE NOT NULL,
    carbohydrate DOUBLE NOT NULL,
    sugar DOUBLE NOT NULL,
    cholesterol DOUBLE NOT NULL,
    FOREIGN KEY (diet_no) REFERENCES diets(diet_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food(food_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
select * from diets_food;
select * from diets;

delete from diets 
where diet_no >=40;


 INSERT INTO diets_food (
  diet_no, food_id, food_name, amount, calorie,
  protein, fat, carbohydrate, sugar, cholesterol
) VALUES
-- 아침 식단 (diet_no = 1)
(21, 102, '계란 프라이', 50, 75, 6.3, 5.5, 0.5, 0.1, 186),
(21, 102, '밥', 200, 312, 6, 1, 69, 0.1, 0),
(21, 102, '김치', 50, 15, 1.1, 0.2, 3, 1, 0),

-- 점심 식단 (diet_no = 2)
(22, 102, '닭가슴살', 150, 165, 31, 3.6, 0, 0, 85),
(22, 102, '고구마', 100, 86, 1.6, 0.1, 20, 4.2, 0),
(22, 102, '샐러드', 80, 30, 1, 2, 4, 1, 0),

-- 저녁 식단 (diet_no = 3)
(24, 102, '연어구이', 120, 206, 22, 13, 0, 0, 55),
(24, 102, '현미밥', 180, 270, 5, 2, 55, 0.2, 0),
(24, 102, '브로콜리', 50, 17, 1.9, 0.2, 3.3, 0.5, 0);

select * from users;

select coach_no
 from users;


CREATE TABLE chat_room (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user1 VARCHAR(50) NOT NULL,
  user2 VARCHAR(50) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_user_pair (user1, user2) -- 정렬된 순서로 삽입해야 함!
);

CREATE TABLE chat_message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
  sender VARCHAR(50) NOT NULL,
  content TEXT NOT NULL,
  sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES chat_room(id)
);

select * from chat_message;

select * from users;



-- 게시판
CREATE TABLE if not exists board (
    board_no INT PRIMARY KEY AUTO_INCREMENT,
    user_no INT not null,
    title VARCHAR(255) not null,
    content VARCHAR(2000) not null,
    view_cnt INT default 0,
    reg_date TIMESTAMP default now(),
    file_path VARCHAR(255),
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


        SELECT board_no as boardNo, user_no as userNo, title, content, view_cnt as viewCnt,
        reg_date as regDate, file_path as filePath
        FROM board;

SELECT board_no as boardNo, user_no as userNo, title, content, view_cnt as viewCnt,
reg_date as regDate, file_path as filePath
FROM board;



INSERT INTO board (user_no, title, content, file_path)
VALUES
(1, '첫 번째 게시글', '이것은 첫 번째 게시글의 내용입니다.', '/uploads/image1.png'),
(2, '두 번째 게시글', '이것은 두 번째 게시글의 내용입니다.', '/uploads/doc1.pdf'),
(1, '세 번째 게시글', '파일 없이 텍스트만 있는 게시글입니다.', NULL),
(2, '네 번째 게시글', '사진과 함께하는 게시글입니다.', '/uploads/photo.jpg');

select * from board;

-- 게시글 좋아요
CREATE TABLE if not exists boards_like (
    like_no INT PRIMARY KEY AUTO_INCREMENT,
    board_no INT not null,
    user_no INT not null,
    FOREIGN KEY (board_no) REFERENCES boards(board_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
drop table boards_like;
-- 게시글 댓글
CREATE TABLE if not exists boards_comment (
    comment_no INT PRIMARY KEY AUTO_INCREMENT,
    board_no INT not null,
    user_no INT not null,
    content VARCHAR(1000) not null,
    created_at TIMESTAMP default now(),
    updated_at TIMESTAMP,
    FOREIGN KEY (board_no) REFERENCES board(board_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



LOAD DATA LOCAL INFILE '/Users/jang-ayoung/Downloads/Cleaned_Food_Data.csv'
REPLACE INTO TABLE food
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(food_id, calories, name_ko, protein, fat, carbohydrate, sugar, cholesterol);


SHOW VARIABLES LIKE 'local_infile';