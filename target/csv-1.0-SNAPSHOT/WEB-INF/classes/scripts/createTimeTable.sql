CREATE SCHEMA TIMETABLE;


CREATE TABLE job(
  ID BIGINT NOT NULL,
  ROOM VARCHAR(120),
  DATE_TIME VARCHAR(50),
  GROUP_NAME VARCHAR(20),
  DISCIPLINE VARCHAR(100),
  PRIMARY KEY(ID)
);


