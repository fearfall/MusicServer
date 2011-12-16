DROP DATABASE IF EXISTS ms_authorization;
CREATE DATABASE ms_authorization;

USE ms_authorization;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  pwd VARCHAR(50) NOT NULL
);

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  UNIQUE KEY (user_id, role_id),
  INDEX(user_id)
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  role VARCHAR(100) NOT NULL UNIQUE KEY
);
INSERT INTO role(role) VALUES ('authUser');
GRANT ALL PRIVILEGES ON ms_authorization.* TO 'authJaas' IDENTIFIED BY '';