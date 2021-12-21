
INSERT INTO songs (name,gender,duration,created_at) VALUES('Bohemian Rhapsody','Rock','5.54','2021-10-01');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Stairway to Heaven','Rock','8.02','2021-12-01');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Imagine','Rock','3.07','2021-10-15');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Outside the wall','Rock','1.43','2021-10-21');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Hotel California','Rock','6.31','2021-10-11');
INSERT INTO songs (name,gender,duration,created_at) VALUES('We are the World','Pop','5.50','2021-10-31');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Unintended','Pop','3.57','2021-10-30');
INSERT INTO songs (name,gender,duration,created_at) VALUES('Stand By Me','Pop','5.55','2021-08-01');

INSERT INTO records (name,gender,singer,price,created_at) VALUES('Greatest Hits','Rock','Bon Jovi','6.99','2000-08-01');
INSERT INTO records (name,gender,singer,price,created_at) VALUES('Live at the Music Hall Boston','Rock','Aerosmith','18.99','1978-08-01');
INSERT INTO records (name,gender,singer,price,created_at) VALUES('Justice For All','Rock','Metallica','31.99','1990-09-12');
INSERT INTO records (name,gender,singer,price,created_at) VALUES('Herzeleid','Rock','Rammstein','25.99','2010-02-01');

INSERT INTO `users` (username,password,enabled) VALUES('pablo','$2a$10$oBpvWQKP5NUuurEirQzA5eADATjtLLN6CkfRLOW.Vlh6Z5wjRQvE6',1);
INSERT INTO `users` (username,password,enabled) VALUES('admin','$2a$10$bxyJj93VbbU1uWzlNCfGLupMbkG2XUsjKVbXDuOHxbzZKEDdcrlvy',1);
INSERT INTO `roles` (name) VALUES('ROLE_USER');
INSERT INTO `roles` (name) VALUES('ROLE_ADMIN');

INSERT INTO `users_roles` (user_id,role_id) VALUES(1,1);
INSERT INTO `users_roles` (user_id,role_id) VALUES(2,2);
INSERT INTO `users_roles` (user_id,role_id) VALUES(2,1);