create database if not exists m8eat;

use m8eat;

<<<<<<< HEAD
drop table IF EXISTS users_health_info; 
drop table IF EXISTS users; 
=======
drop database m8eat;

>>>>>>> d0a9c0514aec37fe596da79675549d3ec0c2ddfe
-- 사용자 정보
-- CREATE TABLE if not exists users (
--     user_no INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(100) not null,
--     id VARCHAR(100) UNIQUE not null,
--     password VARCHAR(255) ,
--     role VARCHAR(20) not null DEFAULT 'user'
-- );

CREATE TABLE IF NOT EXISTS users (
    user_no INT PRIMARY KEY AUTO_INCREMENT,
<<<<<<< HEAD
    name VARCHAR(100) NOT NULL,
    id VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    profile_image VARCHAR(255), -- 프로필 이미지 경로

    coach_no INT, -- 일반 회원인 경우: 담당 코치의 user_no (FK 가능)
    managed_users TEXT, -- 코치인 경우: 담당하는 회원들 (user_no 리스트 형태, 예: "2,4,7")
    
    CONSTRAINT fk_coach FOREIGN KEY (coach_no) REFERENCES users(user_no) ON DELETE SET NULL
=======
    name VARCHAR(100) not null,
    id VARCHAR(100) UNIQUE not null,
    password VARCHAR(255) not null,
    role VARCHAR(20) not null DEFAULT 'user'
>>>>>>> d0a9c0514aec37fe596da79675549d3ec0c2ddfe
);

-- 1. 코치 등록 (user_no = 1)
INSERT INTO users (name, id, password, role, profile_image)
VALUES ('김코치', 'coachkim', 'coach1234', 'coach', 'images/coachkim.jpg');

-- 2. 일반 회원 등록 (user_no = 2)
INSERT INTO users (name, id, password, role, profile_image, coach_no)
VALUES ('이회원', 'userlee', 'userlee123', 'user', 'images/userlee.jpg', 1);

-- 3. 일반 회원 등록 (user_no = 3)
INSERT INTO users (name, id, password, role, profile_image, coach_no)
VALUES ('박회원', 'userpark', 'userpark123', 'user', 'images/userpark.jpg', 1);

-- 4. 코치의 managed_users 업데이트 (user_no = 1 기준)
UPDATE users
SET managed_users = '2,3'
WHERE user_no = 1;


-- INSERT INTO users (name, id, password, role)
-- VALUES
-- ('홍길동', 'hong123', 'password1!', 'user'),
-- ('김철수', 'kimcs', 'passw0rd!', 'user'),
-- ('이영희', 'lee01', 'myp@ssword', 'admin'),
-- ('박민수', 'parkms', 'qwer1234', 'user'),
-- ('최지은', 'choi_je', 'abcd!1234', 'user');

select * from users;

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

-- 코치 선호 태그
CREATE TABLE if not exists coach_prefer (
    user_no INT PRIMARY KEY,
    tags TEXT,
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

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
(1, '2024-05-01 08:00:00', '아침', 'uploads/breakfast_1.jpg'),
(1, '2024-05-01 12:30:00', '점심', 'uploads/lunch_1.jpg'),
(1, '2024-05-01 19:00:00', '저녁', 'uploads/dinner_1.jpg'),
(2, '2024-05-02 08:15:00', '아침', 'uploads/breakfast_2.jpg'),
(2, '2024-05-02 13:00:00', '점심', 'uploads/lunch_2.jpg'),
(2, '2024-05-02 19:10:00', '저녁', 'uploads/dinner_2.jpg'),
(3, '2024-05-03 07:50:00', '아침', 'uploads/breakfast_3.jpg'),
(3, '2024-05-03 12:40:00', '점심', 'uploads/lunch_3.jpg'),
(3, '2024-05-03 18:55:00', '저녁', 'uploads/dinner_3.jpg'),
(1, '2024-05-04 08:20:00', '아침', 'uploads/breakfast_4.jpg');

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
<<<<<<< HEAD
select * from food where name_ko LIKE '%샐러드%';
select * from food; 




=======
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
>>>>>>> d0a9c0514aec37fe596da79675549d3ec0c2ddfe
-- 채팅방
CREATE TABLE if not exists chat_room (
    room_no INT PRIMARY KEY AUTO_INCREMENT,
    room_type TEXT not null,
    created_at TIMESTAMP default now()
);

-- 채팅방 참여자
CREATE TABLE if not exists chat_room_user (
    room_no INT not null,
    user_no INT not null,
    PRIMARY KEY (room_no, user_no),
    FOREIGN KEY (room_no) REFERENCES chat_room(room_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (user_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 채팅 메시지
CREATE TABLE if not exists chat_msg (
    msg_no INT PRIMARY KEY AUTO_INCREMENT,
    room_no INT not null,
    sender_no INT not null,
    content VARCHAR(1000) not null,
    msg_type TEXT not null,
    created_at TIMESTAMP default now(),
    FOREIGN KEY (room_no) REFERENCES chat_room(room_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (sender_no) REFERENCES users(user_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

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

<<<<<<< HEAD
        SELECT board_no as boardNo, user_no as userNo, title, content, view_cnt as viewCnt,
        reg_date as regDate, file_path as filePath
        FROM board;
=======
SELECT board_no as boardNo, user_no as userNo, title, content, view_cnt as viewCnt,
reg_date as regDate, file_path as filePath
FROM board;
>>>>>>> d0a9c0514aec37fe596da79675549d3ec0c2ddfe


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