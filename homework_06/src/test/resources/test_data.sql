insert into authors (id, surname, author_name, patronymic) values (1, 'Бабаев', 'Сидор', 'Алиханович');
insert into authors (id, surname, author_name, patronymic) values (2, 'Ю', 'Такеда', 'Ноунеймович');
insert into authors (id, surname, author_name, patronymic) values (3, 'Саб', 'Зиро', null);

insert into genres (id, genre_name) values (1, 'Ненаучная фантастика');
insert into genres (id, genre_name) values (2, 'Научная фантастика');
insert into genres (id, genre_name) values (3, 'Фэнтези');

insert into books (id, author_id, genre_id, book_name, description) values (1, 1, 3, 'Новые берега', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.');
insert into books (id, author_id, genre_id, book_name, description) values (2, 2, 1, 'На перекрестке миров', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tristique nec est eget scelerisque. In semper sollicitudin fermentum. Ut laoreet enim sit amet arcu cursus accumsan. In in neque justo.Vestibulum eu vehicula enim. Nulla non quam dui. Sed sed est et eros vulputate molestie sit amet et lorem. Quisque cursus eu nibh scelerisque molestie.');
insert into books (id, author_id, genre_id, book_name, description) values (3, 1, 2, 'Осколки любви', 'Sed volutpat magna nunc, ut vehicula elit facilisis eget. Morbi eu arcu cursus tortor cursus elementum. Proin vitae ultricies ipsum.');
insert into books (id, author_id, genre_id, book_name, description) values (4, 3, 1, 'На краю обрыва', 'liquam erat volutpat. Vivamus rutrum egestas massa eu rutrum. Nullam erat mauris, tristique nec odio sed, bibendum luctus augue. Nulla et purus ut sem iaculis tristique at eu nisi.');
insert into books (id, author_id, genre_id, book_name, description) values (5, 1, 2, 'Скрытый мир', 'Maecenas tristique massa quis pharetra interdum. Aliquam erat volutpat. Nulla mattis scelerisque ante, nec consectetur nulla vehicula id.');
insert into books (id, author_id, genre_id, book_name, description) values (6, 2, 3, 'Дотронуться до неба', 'Vivamus sodales sem a lacus ultricies, at scelerisque ante consectetur. Pellentesque faucibus laoreet odio.');


