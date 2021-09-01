INSERT INTO authors (id, surname, author_name, patronymic) VALUES (1, 'Бабаев', 'Сидор', 'Алиханович');
INSERT INTO authors (id, surname, author_name, patronymic) VALUES (2, 'Ю', 'Такеда', 'Ноунеймович');
INSERT INTO authors (id, surname, author_name, patronymic) VALUES (3, 'Саб', 'Зиро', null);

INSERT INTO genres (id, genre_name) VALUES (1, 'Ненаучная фантастика');
INSERT INTO genres (id, genre_name) VALUES (2, 'Научная фантастика');
INSERT INTO genres (id, genre_name) VALUES (3, 'Фэнтези');

INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (1, 1, 3, 'Новые берега', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (2, 2, 1, 'На перекрестке миров', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.Vestibulum eu vehicula enim. Nulla non quam dui. Sed sed est et eros vulputate molestie sit amet et lorem. Quisque cursus eu nibh scelerisque molestie.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (3, 1, 2, 'Осколки любви', 'Sed volutpat magna nunc, ut vehicula elit facilisis eget. Morbi eu arcu cursus tortor cursus elementum. Proin vitae ultricies ipsum.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (4, 3, 1, 'На краю обрыва', 'liquam erat volutpat. Vivamus rutrum egestas massa eu rutrum. Nullam erat mauris, tristique nec odio sed, bibendum luctus augue. Nulla et purus ut sem iaculis tristique at eu nisi.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (5, 1, 2, 'Скрытый мир', 'Maecenas tristique massa quis pharetra interdum. Aliquam erat volutpat. Nulla mattis scelerisque ante, nec consectetur nulla vehicula id.');
INSERT INTO books (id, author_id, genre_id, book_name, description) VALUES (6, 2, 3, 'Дотронуться до неба', 'Vivamus sodales sem a lacus ultricies, at scelerisque ante consectetur. Pellentesque faucibus laoreet odio.');
