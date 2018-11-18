DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id        INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email     VARCHAR(256) NOT NULL UNIQUE,
  password  VARCHAR(256) NOT NULL,
  tempPassword  VARCHAR(256),
  salt      varbinary(1024) NOT NULL,
  firstName VARCHAR(35)  NOT NULL,
  lastName  VARCHAR(35)  NOT NULL,
  authToken VARCHAR(256),
  year      INT,
  major     VARCHAR(256)
);