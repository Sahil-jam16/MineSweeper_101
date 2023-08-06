SELECT * FROM jdbc_minesweeper.score_table;

DESC jdbc_minesweeper.score_table;

INSERT INTO Score_table (Name, Score, Time) VALUES ('Alice', 7, '18:00:00');
INSERT INTO score_table(Name,Score,Time) VALUES('Alice', 13,"15:00:00");
INSERT INTO score_table(Name,Score,Time) VALUES('Alice', 13,"20:06:16");
INSERT INTO score_table(Name,Score,Time) VALUES('Alice', 13,"21:43:36");

CREATE TABLE score_table (
  Name VARCHAR(50),
  Score INT,
  Time TIME
);

DELETE from Score_table where Name = 'Test 1';

INSERT INTO Score_table (Name, Score, Time) VALUES ('Alice', 100, '13:30:00');
INSERT INTO Score_table (Name, Score, Time) VALUES ('Alice', 7, '8:17:45');

DESC score_table;

ALTER TABLE Score_table
ADD ID INT AUTO_INCREMENT PRIMARY KEY FIRST;




