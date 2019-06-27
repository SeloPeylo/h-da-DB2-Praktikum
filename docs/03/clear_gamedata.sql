-- Delete Game Data
DELETE FROM master_data_knowledge_test.questionasked;
DELETE FROM master_data_knowledge_test.game_category;
DELETE FROM master_data_knowledge_test.game;
DELETE FROM master_data_knowledge_test.player;

-- Clear Game data (fast & insecure)
TRUNCATE TABLE master_data_knowledge_test.questionasked CASCADE;
TRUNCATE TABLE master_data_knowledge_test.game_category CASCADE;
TRUNCATE TABLE master_data_knowledge_test.game CASCADE;
TRUNCATE TABLE master_data_knowledge_test.player CASCADE;