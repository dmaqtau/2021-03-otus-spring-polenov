DROP TABLE IF EXISTS books cascade;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS book_comments;

DROP TABLE IF EXISTS library_users cascade ;
DROP TABLE IF EXISTS library_user_roles;

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

CREATE TABLE library_users (
    id bigint auto_increment PRIMARY KEY,
    user_login VARCHAR(50),
    password VARCHAR(255),      -- Здесь храним хэшированное значение солёного пароля
    is_active BIT
 );

CREATE TABLE library_user_roles(
    id bigint auto_increment PRIMARY KEY,
    user_id bigint,
    role_name VARCHAR(50)
);

alter table books add foreign key (author_id) references authors(id);
alter table books add foreign key (genre_id) references genres(id);
alter table book_comments add foreign key (book_id) references books(id) on delete cascade;
alter table library_users add constraint login_unique unique(user_login);
alter table library_user_roles add foreign key (user_id) references library_users(id) on delete cascade;
