-- Categories & Questions (Master Data)
SELECT category.id AS cat_id, category.name, question.id AS quest_id, answers1, answers2, answers3, answers4, question.correctanswer, questiontext
	FROM master_data_knowledge_test.category INNER JOIN master_data_knowledge_test.question ON question.category_id = category.id
	ORDER BY category.id ASC, question.id ASC;