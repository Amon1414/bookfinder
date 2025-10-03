-- 本テーブル作成
DROP TABLE IF EXISTS book;
CREATE TABLE book (
	id integer NOT NULL PRIMARY KEY,
	title varchar NOT NULL UNIQUE,
	price integer NOT NULL,
	is_published boolean NOT NULL,
	created_date_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_date_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- 作者テーブル作成
DROP TABLE IF EXISTS author;
CREATE TABLE author (
	id integer NOT NULL PRIMARY KEY,
	name varchar NOT NULL UNIQUE,
	birth_date date NOT NULL,
	created_date_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_date_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- 作者と本の中間テーブル作成
DROP TABLE IF EXISTS author_book;
CREATE TABLE author_book (
    book_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    PRIMARY KEY (book_id, author_id),
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 外部キー制約に ON DELETE CASCADE を追加
    CONSTRAINT fk_author_book_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    CONSTRAINT fk_author_book_author FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
-- ID用シーケンス作成
CREATE SEQUENCE book_id_seq START 1;
CREATE SEQUENCE author_id_seq START 1;
-- サンブルデータの登録
INSERT INTO author (id, name, birth_date) VALUES(nextval('author_id_seq'), 'Yukio Mishima', '1925-1-14');
INSERT INTO book (id, title, price, is_published) VALUES(nextval('book_id_seq'), '潮騒', 1000, TRUE);
INSERT INTO author_book (book_id, author_id) VALUES (1, 1);
