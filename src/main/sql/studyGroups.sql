DROP TABLE IF EXISTS studyGroups;
CREATE TABLE studyGroups (
  id        INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  courseId  INT NOT NULL,
  ownerId   INT NOT NULL,
  capacity  INT,
  size      INT NOT NULL DEFAULT 1,
  location  VARCHAR(128),
  topic     INT,
  professor VARCHAR(128),
  start     TIMESTAMP,
  end       TIMESTAMP,

  FOREIGN KEY (courseId) REFERENCES courses (id),
  FOREIGN KEY (ownerId) REFERENCES users (id)
);