DROP TABLE IF EXISTS books;
CREATE TABLE books(id bigint auto_increment PRIMARY KEY, author_id bigint NOT NULL, genre_id bigint, book_name varchar(255) NOT NULL, description varchar);

DROP TABLE IF EXISTS authors;
CREATE TABLE authors(id bigint auto_increment PRIMARY KEY, surname varchar(255) NOT NULL, author_name varchar(255) NOT NULL, patronymic varchar(255));

DROP TABLE IF EXISTS genres;
CREATE TABLE genres(id bigint auto_increment PRIMARY KEY, genre_name varchar(255) NOT NULL);

 alter table books add foreign key (author_id) references authors(id);
 alter table books add foreign key (genre_id) references genres(id);