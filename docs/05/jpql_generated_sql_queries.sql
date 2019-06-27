
-- Query 1
SELECT t0.ID, t0.NAME 
FROM master_data_knowledge_test.player t0, master_data_knowledge_test.game t1 
WHERE (((t1.STARTDATETIME >= ?) AND (t1.ENDDATETIME >= ?)) AND (t1.PLAYER_ID = t0.ID)) 
GROUP BY t0.ID, t0.NAME 
ORDER BY MAX(t1.STARTDATETIME) DESC, MAX(t1.ENDDATETIME) DESC;
-- bind => [2019-06-16 16:55:03.0, 2019-06-16 16:55:23.0]


-- Query 2
SELECT t0.ID, t0.ENDDATETIME, t0.MAXQUESTIONS, t0.STARTDATETIME, t0.PLAYER_ID, COUNT(t1.ID), 
	SUM(CASE  WHEN (t2.SELECTEDANSWER = t1.CORRECTANSWER) THEN ? ELSE ? END) 
FROM master_data_knowledge_test.game t0, master_data_knowledge_test.questionasked t2, master_data_knowledge_test.question t1 
WHERE ((t0.PLAYER_ID = ?) AND ((t2.GAME_ID = t0.ID) AND (t2.QUESTION_ID = t1.ID))) 
GROUP BY t0.ID, t0.ENDDATETIME, t0.MAXQUESTIONS, t0.STARTDATETIME, t0.PLAYER_ID 
ORDER BY t0.ID ASC;
-- bind => [1, 0, 1]


-- Query 3
SELECT t0.ID, t0.NAME 
FROM master_data_knowledge_test.player t0, master_data_knowledge_test.game t1 
WHERE (t1.PLAYER_ID = t0.ID) 
GROUP BY t0.ID, t0.NAME 
ORDER BY COUNT(t1.ID) DESC;

SELECT ID, ENDDATETIME, MAXQUESTIONS, STARTDATETIME, PLAYER_ID 
FROM master_data_knowledge_test.game 
WHERE (PLAYER_ID = ?);
-- bind => [47051]


-- Query 4
SELECT t0.ID, t0.NAME 
FROM master_data_knowledge_test.category t0, master_data_knowledge_test.game_category t2, master_data_knowledge_test.game t1 
WHERE ((t2.categoryId = t0.ID) AND (t1.ID = t2.gameId)) 
GROUP BY t0.ID 
ORDER BY COUNT(t1.ID);

-- ============================================================================================================================================
-- Analyze
-- ============================================================================================================================================

-- query1
EXPLAIN ANALYZE 
SELECT t0.ID, t0.NAME    
FROM master_data_knowledge_test.player t0, master_data_knowledge_test.game t1     
WHERE (((t1.STARTDATETIME >= '2019-06-16 16:55:03.0') AND (t1.ENDDATETIME >= '2019-06-16 16:55:23.0')) AND (t1.PLAYER_ID = t0.ID))  
GROUP BY t0.ID, t0.NAME  
ORDER BY MAX(t1.STARTDATETIME) DESC, MAX(t1.ENDDATETIME) DESC; 

-- query 2
EXPLAIN ANALYZE 
SELECT t0.ID, t0.ENDDATETIME, t0.MAXQUESTIONS, t0.STARTDATETIME, t0.PLAYER_ID, COUNT(t1.ID), 
	SUM(CASE  WHEN (t2.SELECTEDANSWER = t1.CORRECTANSWER) THEN 1 ELSE 0 END)  
FROM master_data_knowledge_test.game t0, master_data_knowledge_test.questionasked t2, master_data_knowledge_test.question t1  
WHERE ((t0.PLAYER_ID = 1) AND ((t2.GAME_ID = t0.ID) AND (t2.QUESTION_ID = t1.ID)))  
GROUP BY t0.ID, t0.ENDDATETIME, t0.MAXQUESTIONS, t0.STARTDATETIME, t0.PLAYER_ID  
ORDER BY t0.ID ASC; 

-- query 3
EXPLAIN ANALYZE 
SELECT t0.ID, t0.NAME  
FROM master_data_knowledge_test.player t0, master_data_knowledge_test.game t1  
WHERE (t1.PLAYER_ID = t0.ID)  
GROUP BY t0.ID, t0.NAME  
ORDER BY COUNT(t1.ID) DESC; 

-- query 4
EXPLAIN ANALYZE 
SELECT t0.ID, t0.NAME  
FROM master_data_knowledge_test.category t0, master_data_knowledge_test.game_category t2, master_data_knowledge_test.game t1  
WHERE ((t2.categoryId = t0.ID) AND (t1.ID = t2.gameId))  
GROUP BY t0.ID  
ORDER BY COUNT(t1.ID); 



