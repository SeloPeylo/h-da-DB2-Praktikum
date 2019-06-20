-- Practice 5 - Native SQL test sample 2 - Player names who played in timeframe
-- 'Ausgabe aller Spieler(Spielername),die in einem bestimmten Zeitraum gespielt hatten.'
SELECT player.name, MAX(game.startdatetime) AS lastStarted, MAX(game.enddatetime) AS lastEnded
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
WHERE game.startdatetime >= '2019-06-16 16:55:03.381' AND game.enddatetime <= '2019-06-16 16:55:23.386'
GROUP BY player.name
ORDER BY lastStarted DESC, lastEnded DESC;

-- Practice 5 - Native SQL test sample 2 - Player names who played in timeframe
SELECT game.id, game.startdatetime, game.enddatetime, 
	sum(case when questionasked.selectedanswer=question.correctanswer then 1 else 0 end) AS correctAnswered,
	COUNT(question.id) AS question_count
FROM master_data_knowledge_test.game 
	INNER JOIN master_data_knowledge_test.questionasked ON game.id = questionasked.game_id
	INNER JOIN master_data_knowledge_test.question ON question.id = questionasked.question_id
WHERE game.player_id = 1
GROUP BY game.id
ORDER BY game.id ASC;

-- Practice 5 - Native SQL test sample 3 - Player and count of their games
-- 'Ausgabe aller Spieler mit Anzahl der gespielten Spiele, nach Anzahl absteigend geordnet.'
SELECT player.id, player.name, COUNT(game.id) AS game_count
FROM master_data_knowledge_test.player
	INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
GROUP BY player.id
ORDER BY game_count DESC;

-- Practice 5 - Native SQL test sample 4 - Player and count of their games
-- Ausgabe der am meisten gefragten Kategorie, oder alternativ, die Beliebtheit der Kategorien nach Anzahl der Auswahl absteigend sortiert.
SELECT category.id, category.name, COUNT(game_category.gameid) AS countOfGames
FROM master_data_knowledge_test.category
	INNER JOIN master_data_knowledge_test.game_category ON category.id = game_category.categoryid
GROUP BY category.id
ORDER BY countOfGames DESC;
