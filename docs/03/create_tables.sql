-- =================================================================================
-- Drop Sequences & Tables (Sequences are automatically dropped with tables)
-- =================================================================================

-- Drop Constrains
-- /ALTER TABLE master_data_knowledge_test.game DROP CONSTRAINT FK_game_PLAYER_ID;
-- /ALTER TABLE master_data_knowledge_test.question DROP CONSTRAINT FK_question_CATEGORY_ID;
-- /ALTER TABLE master_data_knowledge_test.questionasked DROP CONSTRAINT FK_questionasked_QUESTION_ID;
-- /ALTER TABLE master_data_knowledge_test.questionasked DROP CONSTRAINT FK_questionasked_GAME_ID;
-- /ALTER TABLE master_data_knowledge_test.game_category DROP CONSTRAINT FK_game_category_gameId;
-- /ALTER TABLE master_data_knowledge_test.game_category DROP CONSTRAINT FK_game_category_categoryId;
-- /ALTER TABLE master_data_knowledge_test.game_category DROP CONSTRAINT UNQ_game_category_0;

-- Drop Tables
-- /DROP TABLE master_data_knowledge_test.category CASCADE;
-- /DROP TABLE master_data_knowledge_test.game CASCADE;
-- /DROP TABLE master_data_knowledge_test.player CASCADE;
-- /DROP TABLE master_data_knowledge_test.question CASCADE;
-- /DROP TABLE master_data_knowledge_test.questionasked CASCADE;
-- /DROP TABLE master_data_knowledge_test.game_category CASCADE;

-- Drop Sequences - not needed, will be dropped with tables
-- \DROP SEQUENCE master_data_knowledge_test.category_id_seq;
-- \DROP SEQUENCE master_data_knowledge_test.game_id_seq;
-- \DROP SEQUENCE master_data_knowledge_test.player_id_seq;
-- \DROP SEQUENCE master_data_knowledge_test.questionasked_id_seq;

-- =================================================================================
-- Create Sequences & Tables (Sequences are automatically created with tables)
-- =================================================================================

-- Create Tables
CREATE TABLE master_data_knowledge_test.category (ID  SERIAL NOT NULL, NAME VARCHAR(255) UNIQUE, PRIMARY KEY (ID))
CREATE TABLE master_data_knowledge_test.game (ID INTEGER NOT NULL, ENDDATETIME TIMESTAMP, MAXQUESTIONS INTEGER, STARTDATETIME TIMESTAMP, PLAYER_ID INTEGER, PRIMARY KEY (ID))
CREATE TABLE master_data_knowledge_test.player (ID INTEGER NOT NULL, NAME VARCHAR(255) UNIQUE, PRIMARY KEY (ID))
CREATE TABLE master_data_knowledge_test.question (ID INTEGER NOT NULL, ANSWERS1 VARCHAR(255), ANSWERS2 VARCHAR(255), ANSWERS3 VARCHAR(255), ANSWERS4 VARCHAR(255), CORRECTANSWER INTEGER, QUESTIONTEXT VARCHAR(255), CATEGORY_ID INTEGER, PRIMARY KEY (ID))
CREATE TABLE master_data_knowledge_test.questionasked (ID INTEGER NOT NULL, SELECTEDANSWER INTEGER, GAME_ID INTEGER, QUESTION_ID INTEGER, PRIMARY KEY (ID))
CREATE TABLE master_data_knowledge_test.game_category (categoryId INTEGER NOT NULL, gameId INTEGER NOT NULL, PRIMARY KEY (categoryId, gameId))

-- Create Constrains
ALTER TABLE master_data_knowledge_test.game_category ADD CONSTRAINT UNQ_game_category_0 UNIQUE (gameId, categoryId);
ALTER TABLE master_data_knowledge_test.game ADD CONSTRAINT FK_game_PLAYER_ID FOREIGN KEY (PLAYER_ID) REFERENCES master_data_knowledge_test.player (ID);
ALTER TABLE master_data_knowledge_test.question ADD CONSTRAINT FK_question_CATEGORY_ID FOREIGN KEY (CATEGORY_ID) REFERENCES master_data_knowledge_test.category (ID);
ALTER TABLE master_data_knowledge_test.questionasked ADD CONSTRAINT FK_questionasked_QUESTION_ID FOREIGN KEY (QUESTION_ID) REFERENCES master_data_knowledge_test.question (ID);
ALTER TABLE master_data_knowledge_test.questionasked ADD CONSTRAINT FK_questionasked_GAME_ID FOREIGN KEY (GAME_ID) REFERENCES master_data_knowledge_test.game (ID);
ALTER TABLE master_data_knowledge_test.game_category ADD CONSTRAINT FK_game_category_gameId FOREIGN KEY (gameId) REFERENCES master_data_knowledge_test.game (ID);
ALTER TABLE master_data_knowledge_test.game_category ADD CONSTRAINT FK_game_category_categoryId FOREIGN KEY (categoryId) REFERENCES master_data_knowledge_test.category (ID);

-- Alter owner of tables
ALTER TABLE master_data_knowledge_test.category OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.game OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.player OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.question OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.questionasked OWNER to jpauser;
ALTER TABLE master_data_knowledge_test.game_category OWNER to jpauser;




