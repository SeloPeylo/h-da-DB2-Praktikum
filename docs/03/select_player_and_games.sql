-- Select Player & Games (Game Data)
SELECT player.id AS player_id, player.name, game.id AS game_id, game.enddatetime, game.maxquestions, game.startdatetime
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
ORDER BY player.id ASC, game.id ASC;

-- Player & count of games
SELECT player.id AS player_id, player.name, COUNT(game.id)
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
GROUP BY player.id
ORDER BY player.id ASC;