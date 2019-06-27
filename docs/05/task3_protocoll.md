# Praktikum 5 - Aufgabe 3

## Indices

    -- Show Indices
    SELECT *
    FROM pg_indexes
    WHERE schemaname = 'master_data_knowledge_test';


| Schema | Tabelle | Index | Tablespace | indexof |
|--------|---------|-------|------------|---------|
| master_data_knowledge_test | category | category_name_key  | null | CREATE UNIQUE INDEX category_name_key ON master_data_knowledge_test.category USING btree (name)  |
| master_data_knowledge_test | category | category_pkey  | null | CREATE UNIQUE INDEX category_pkey ON master_data_knowledge_test.category USING btree (id)  |
| master_data_knowledge_test | game | game_pkey  | null | CREATE UNIQUE INDEX game_pkey ON master_data_knowledge_test.game USING btree (id)  |
| master_data_knowledge_test | player | player_name_key  | null | CREATE UNIQUE INDEX player_name_key ON master_data_knowledge_test.player USING btree (name)  |
| master_data_knowledge_test | player | player_pkey  | null | CREATE UNIQUE INDEX player_pkey ON master_data_knowledge_test.player USING btree (id)  |
| master_data_knowledge_test | question | question_pkey  | null | CREATE UNIQUE INDEX question_pkey ON master_data_knowledge_test.question USING btree (id)  |
| master_data_knowledge_test | questionasked  | questionasked_pkey | null | CREATE UNIQUE INDEX questionasked_pkey ON master_data_knowledge_test.questionasked USING btree (id)  |
| master_data_knowledge_test | game_category  | unq_game_category_0  | null | CREATE UNIQUE INDEX unq_game_category_0 ON master_data_knowledge_test.game_category USING btree (gameid, categoryid) |
| master_data_knowledge_test | game_category  | game_category_pkey | null | CREATE UNIQUE INDEX game_category_pkey ON master_data_knowledge_test.game_category USING btree (categoryid, gameid)  |

# Tabellen Statistiken
    -- Show count statistics
    SELECT relname, n_live_tup
    FROM pg_stat_user_tables;

| relname | n_live_tup |
|---------|-----------:|
| player | 10000 |
| category | 51 |
| questionasked | 5127942 |
| question | 200 |
| game_category | 3501252 |
| game | 1000000 |


    -- Count of Player, Games and Askedquestions
    SELECT 
	(SELECT COUNT(player.id) FROM master_data_knowledge_test.player) AS player_count,
	(SELECT COUNT(game.id) FROM master_data_knowledge_test.game) AS game_count,
	(SELECT COUNT(questionasked.id) FROM master_data_knowledge_test.questionasked) AS askedQuestions_count;

| player_count | game_count | askedquestion_count | 
|-------------:|-----------:|--------------------:|
|10000 | 1000000 | 5127942|

# Erstellung von Indices
## Optimierungen - Neue Indices

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


## Query 1
Vorher:  
* Planning Time: 0.211 ms
* Execution Time: 1784.087 ms

Nachher:  
* Planning Time: 0.467 ms
* Execution Time: 1767.881 ms

## Query 2
Vorher:  
* Planning Time: 0.453 ms
* Execution Time: 346.246 ms

Nachher:  
* Planning Time: 0.462 ms
* Execution Time: 0.059 ms

## Query 3
Vorher:  
* Planning Time: 0.237 ms
* Execution Time: 1506.696 ms

Nachher:  
* Planning Time: 0.323 ms
* Execution Time: 1461.244 ms

## Query 4
Vorher:  
* Planning Time: 0.721 ms
* Execution Time: 9648.261 ms

Nachher:  
* Planning Time: 0.678 ms
* Execution Time: 9546.708 ms