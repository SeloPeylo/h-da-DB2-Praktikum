
-- Drop Constrains
--ALTER TABLE master_data_knowledge_test.game DROP CONSTRAINT FK_game_PLAYER_ID;
--ALTER TABLE master_data_knowledge_test.question DROP CONSTRAINT FK_question_CATEGORY_ID;
--ALTER TABLE master_data_knowledge_test.questionasked DROP CONSTRAINT FK_questionasked_QUESTION_ID;
--ALTER TABLE master_data_knowledge_test.questionasked DROP CONSTRAINT FK_questionasked_GAME_ID;
--ALTER TABLE master_data_knowledge_test.category_game DROP CONSTRAINT FK_category_game_games_ID;
--ALTER TABLE master_data_knowledge_test.category_game DROP CONSTRAINT FK_category_game_categories_ID;

-- Drop Tables
--DROP TABLE master_data_knowledge_test.category CASCADE;
--DROP TABLE master_data_knowledge_test.game CASCADE;
--DROP TABLE master_data_knowledge_test.player CASCADE;
--DROP TABLE master_data_knowledge_test.question CASCADE;
--DROP TABLE master_data_knowledge_test.questionasked CASCADE;
--DROP TABLE master_data_knowledge_test.category_game CASCADE;
--DROP SEQUENCE master_data_knowledge_test.SEQ_GEN_SEQUENCE;

-- Create Tables
CREATE TABLE master_data_knowledge_test.category (ID INTEGER NOT NULL, NAME VARCHAR(255) UNIQUE, PRIMARY KEY (ID));
CREATE TABLE master_data_knowledge_test.game (ID INTEGER NOT NULL, ENDDATETIME TIMESTAMP, MAXQUESTIONS INTEGER, STARTDATETIME TIMESTAMP, PLAYER_ID INTEGER, PRIMARY KEY (ID));
CREATE TABLE master_data_knowledge_test.player (ID INTEGER NOT NULL, NAME VARCHAR(255) UNIQUE, PRIMARY KEY (ID));
CREATE TABLE master_data_knowledge_test.question (ID INTEGER NOT NULL, ANSWERS1 VARCHAR(255), ANSWERS2 VARCHAR(255), ANSWERS3 VARCHAR(255), ANSWERS4 VARCHAR(255), CORRECTANSWER INTEGER, QUESTIONTEXT VARCHAR(255), CATEGORY_ID INTEGER, PRIMARY KEY (ID));
CREATE TABLE master_data_knowledge_test.questionasked (ID INTEGER NOT NULL, SELECTEDANSWER INTEGER, GAME_ID INTEGER, QUESTION_ID INTEGER, PRIMARY KEY (ID));
CREATE TABLE master_data_knowledge_test.category_game (categories_ID INTEGER NOT NULL, games_ID INTEGER NOT NULL, PRIMARY KEY (categories_ID, games_ID));

-- Create Constrains
ALTER TABLE master_data_knowledge_test.game ADD CONSTRAINT FK_game_PLAYER_ID FOREIGN KEY (PLAYER_ID) REFERENCES master_data_knowledge_test.player (ID);
ALTER TABLE master_data_knowledge_test.question ADD CONSTRAINT FK_question_CATEGORY_ID FOREIGN KEY (CATEGORY_ID) REFERENCES master_data_knowledge_test.category (ID);
ALTER TABLE master_data_knowledge_test.questionasked ADD CONSTRAINT FK_questionasked_QUESTION_ID FOREIGN KEY (QUESTION_ID) REFERENCES master_data_knowledge_test.question (ID);
ALTER TABLE master_data_knowledge_test.questionasked ADD CONSTRAINT FK_questionasked_GAME_ID FOREIGN KEY (GAME_ID) REFERENCES master_data_knowledge_test.game (ID);
ALTER TABLE master_data_knowledge_test.category_game ADD CONSTRAINT FK_category_game_games_ID FOREIGN KEY (games_ID) REFERENCES master_data_knowledge_test.game (ID);
ALTER TABLE master_data_knowledge_test.category_game ADD CONSTRAINT FK_category_game_categories_ID FOREIGN KEY (categories_ID) REFERENCES master_data_knowledge_test.category (ID);

-- Alter owner of tables
ALTER TABLE master_data_knowledge_test.category OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.game OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.player OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.question OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.questionasked OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.category_game OWNER to jpauser;

-- Create generator sequence
--select nextval('SEQ_GEN_SEQUENCE')
CREATE SEQUENCE master_data_knowledge_test.SEQ_GEN_SEQUENCE INCREMENT BY 50 START WITH 50;
ALTER SEQUENCE master_data_knowledge_test.seq_gen_sequence OWNER TO jpauser;