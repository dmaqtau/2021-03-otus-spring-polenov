alter table if exists book_comments drop constraint CONSTRAINT_2F;

DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS book_comments;

CREATE TABLE authors(
    id bigint auto_increment PRIMARY KEY,
    surname varchar(255) NOT NULL,
    author_name varchar(255) NOT NULL,
    patronymic varchar(255)
);

CREATE TABLE genres(
   id bigint auto_increment PRIMARY KEY,
   genre_name varchar(255) NOT NULL
);

CREATE TABLE books(
      id bigint auto_increment PRIMARY KEY,
      author_id bigint NOT NULL,
      genre_id bigint,
      book_name varchar(255) NOT NULL,
      description varchar
);

CREATE TABLE book_comments(
     id bigint auto_increment PRIMARY KEY,
     book_id bigint,
     user_login varchar(50) NOT NULL,
     comment varchar(1000)
);

alter table books add foreign key (author_id) references authors(id);
alter table books add foreign key (genre_id) references genres(id);
alter table book_comments add foreign key (book_id) references books(id) on delete cascade;
