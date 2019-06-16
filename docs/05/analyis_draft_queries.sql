-- Player names who played in timeframe
SELECT player.name, MAX(game.startdatetime) AS lastStarted, MAX(game.enddatetime) AS lastEnded
FROM master_data_knowledge_test.player INNER JOIN master_data_knowledge_test.game ON player.id = game.player_id
WHERE game.startdatetime >= '2019-06-16 16:55:03.381' AND game.enddatetime <= '2019-06-16 16:55:23.386'
GROUP BY player.name
ORDER BY lastStarted DESC, lastEnded DESC;