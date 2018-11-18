DROP TABLE IF EXISTS joinedgroups;
CREATE TABLE joinedgroups (
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  userId INT NOT NULL,
  groupId INT NOT NULL,

  FOREIGN KEY (userId) REFERENCES users(id),
  FOREIGN KEY (groupId) REFERENCES studygroups(id)
);