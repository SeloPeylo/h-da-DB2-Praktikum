-- Select Player & Games (Game Data)
SELECT player.id AS player_id, player.name, game.id AS game_id, game.enddatetime, game.maxquestions, game.startdatetime
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
ORDER BY player.id ASC, game.id ASC;

-- Player & count of games
SELECT player.id AS player_id, player.name, COUNT(game.id)
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
GROUP BY player.id
ORDER BY player.id ASC;

-- Games & asked Questions
SELECT game.id AS game_id, game.startdatetime, game.enddatetime, game.maxquestions, questionasked.selectedanswer, 
	question.id AS quest_id, question.correctanswer, question.answers1, question.answers2, question.answers3, question.answers4
FROM master_data_knowledge_test.game 
	INNER JOIN master_data_knowledge_test.questionasked ON game.id = questionasked.game_id
	INNER JOIN master_data_knowledge_test.question ON questionasked.question_id = question.id
WHERE game.player_id = 1;

-- Count Player
SELECT 
	(SELECT COUNT(player.id) FROM master_data_knowledge_test.player) AS player_count,
	(SELECT COUNT(game.id) FROM master_data_knowledge_test.game) AS game_count,
	(SELECT COUNT(questionasked.id) FROM master_data_knowledge_test.questionasked) AS askedQuestions_count;
