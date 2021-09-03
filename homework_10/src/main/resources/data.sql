
INSERT INTO authors (id, surname, author_name, patronymic) VALUES (1, 'Бабаев', 'Сидор', 'Алиханович');
INSERT INTO authors (id, surname, author_name, patronymic) VALUES (2, 'Ю', 'Такеда', 'Ноунеймович');
INSERT INTO authors (id, surname, author_name, patronymic) VALUES (3, 'Саб', 'Зиро', 'Тест');

INSERT INTO genres (id, genre_name) VALUES (1, 'Ненаучная фантастика');
INSERT INTO genres (id, genre_name) VALUES (2, 'Научная фантастика');
INSERT INTO genres (id, genre_name) VALUES (3, 'Фэнтези');

INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (1, 1, 3, 'Новые берега', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (2, 2, 1, 'На перекрестке миров', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.Vestibulum eu vehicula enim. Nulla non quam dui. Sed sed est et eros vulputate molestie sit amet et lorem. Quisque cursus eu nibh scelerisque molestie.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (3, 1, 2, 'Осколки любви', 'Sed volutpat magna nunc, ut vehicula elit facilisis eget. Morbi eu arcu cursus tortor cursus elementum. Proin vitae ultricies ipsum.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (4, 3, 1, 'На краю обрыва', 'liquam erat volutpat. Vivamus rutrum egestas massa eu rutrum. Nullam erat mauris, tristique nec odio sed, bibendum luctus augue. Nulla et purus ut sem iaculis tristique at eu nisi.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (5, 1, 2, 'Скрытый мир', 'Maecenas tristique massa quis pharetra interdum. Aliquam erat volutpat. Nulla mattis scelerisque ante, nec consectetur nulla vehicula id.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (6, 2, 3, 'Дотронуться до неба', 'Vivamus sodales sem a lacus ultricies, at scelerisque ante consectetur. Pellentesque faucibus laoreet odio.');

INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (1, 1, 'user1', 'comment1');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (2, 4, 'user2', 'comment2');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (3, 4, 'user2', 'comment3');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (4, 4, 'user2', 'comment4');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (5, 4, 'user2', 'comment5');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (6, 3, 'user3', 'comment6');
INSERT INTO book_comments (id, book_id, user_login, comment) VALUES (7, 3, 'user3', 'comment7');

INSERT INTO library_users (id, user_login, password, is_active) VALUES (1, 'director', '$2a$10$wQcUhlu1q8brE2zFv8fBxu/l/vTXRWrz.qCzPWEMX./6nhHBxsCKe', true);       -- Исходное значение пароля: 'pepper'
INSERT INTO library_users (id, user_login, password, is_active) VALUES (2, 'worker2000', '$2a$10$cOtOBzl57tw8hnWieKdhOOn5O8bE1MLA6J6EtX7Q1hOJFD7ILz/LW', true);     -- Исходное значение пароля: 'pepper1'
INSERT INTO library_users (id, user_login, password, is_active) VALUES (3, 'worker3000', '$2a$10$.KMFPtm9ErpROse9SNtOPuxAQVHPhtZfNuz.kfJUlp0Qc0msKBR9.', false);    -- Исходное значение пароля: 'pepper2'. Юзер неактивный.
