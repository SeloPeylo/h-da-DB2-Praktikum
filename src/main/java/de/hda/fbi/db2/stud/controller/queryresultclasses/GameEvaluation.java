package de.hda.fbi.db2.stud.controller.queryresultclasses;

import de.hda.fbi.db2.stud.entity.Game;

/**
 * GameEvaluation class.
 *
 * @author Ruben van Laack
 */
public class GameEvaluation {

    private Game game;
    private long questionCount;
    private long countCorrectAnswers;


    public GameEvaluation(Game game, Long questionCount, Long countCorrectAnswers){
        this.game = game;
        this.questionCount = questionCount;
        this.countCorrectAnswers = countCorrectAnswers;
    }

    public double percentageRight() {
        return (100 / questionCount) * countCorrectAnswers;
    }

    // getter
    public Game getGame() {
        return game;
    }

    public long getQuestionCount() {
        return questionCount;
    }

    public long getCountCorrectAnswers() {
        return countCorrectAnswers;
    }
}
