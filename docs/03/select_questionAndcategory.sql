SELECT category.id, category.name, question.id, answers1, answers2, answers3, answers4, question.correctanswer, questiontext, category_id
	FROM master_data_knowledge_test.category INNER JOIN master_data_knowledge_test.question ON question.category_id = category.id
	ORDER BY category.id ASC, question.id ASC;