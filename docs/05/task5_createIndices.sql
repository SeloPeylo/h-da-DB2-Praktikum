-- DROP INDEX master_data_knowledge_test.game_fk_player;
-- DROP INDEX master_data_knowledge_test.game_maxquestions;
-- DROP INDEX master_data_knowledge_test.game_startdatetime;
-- DROP INDEX master_data_knowledge_test.game_enddatetime;

-- DROP INDEX master_data_knowledge_test.questionasked_fk_game;
-- DROP INDEX master_data_knowledge_test.questionasked_fk_question;

-- DROP INDEX master_data_knowledge_test.game_category_fk_category;
-- DROP INDEX master_data_knowledge_test.game_category_fk_game;

-- Game
CREATE INDEX game_fk_player 
    ON master_data_knowledge_test.game (player_id);
CREATE INDEX game_maxquestions 
    ON master_data_knowledge_test.game (maxquestions);
CREATE INDEX game_startdatetime 
    ON master_data_knowledge_test.game (startdatetime);
CREATE INDEX game_enddatetime 
    ON master_data_knowledge_test.game (enddatetime);

-- QuestionAsked
CREATE INDEX questionasked_fk_game 
    ON master_data_knowledge_test.questionasked (game_id);
CREATE INDEX questionasked_fk_question 
    ON master_data_knowledge_test.questionasked (question_id);

-- GameCategory
CREATE INDEX game_category_fk_category 
    ON master_data_knowledge_test.game_category (categoryid);
CREATE INDEX game_category_fk_game
    ON master_data_knowledge_test.game_category (gameid);