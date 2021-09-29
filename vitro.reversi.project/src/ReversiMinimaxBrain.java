// ModelAI Assignment - Reversi
// Topic: Adversarial  Search
// File: ReversiMinimaxBrain.java
// Authors: Carly Bernstein and Jack Walton

import vitro.*;
import vitro.grid.*;
import vitro.util.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static vitro.util.Groups.*;

public class ReversiMinimaxBrain implements Agent<Reversi.Player> {

    private Reversi model;
    private Reversi.Player actor;
    private Reversi.Player opponent;

    // Constructor
    public ReversiMinimaxBrain(Reversi model, Reversi.Player actor) {
        this.model = model;
        this.actor = actor;
        this.opponent = model.createPlayer(actor.team() == Reversi.BLACK ? Reversi.WHITE : Reversi.BLACK);
    }

    /**
     * Outputs the move that the current player should choose.
     * @param actor
     * @param options
     * @return
     */
    public Action choose(Reversi.Player actor, Set<Action> options) {
        // System.out.println("CHOOSE :: team=" + actor.team());

        // If all we can do is pass, go for it.
        if (options.size() == 1) {
            return first(options);
        }

        // Otherwise, find the best scoring minimax move
        Reversi.Move best = null;
        int bestScore = Integer.MIN_VALUE;
        for(Action a : options) {
            if (!(a instanceof Reversi.Move)) { continue; }
            // from my perspective, what is the value of move a
            int thisScore = minimax(actor, a);
            // System.out.println("\tCHOOSE :: THISSCORE: " + thisScore);
            if (thisScore > bestScore) {
                best = (Reversi.Move)a;
                bestScore = thisScore;
            }
        }
        return best;
    }

    /**
     * Using the current player, recursively evaluate the best move using minimax search against the adversary.
     *
     * @param currentActor The current player that will make the next move
     * @param option The new move that a player will make in the game.
     * @return The best score more the given move using minimax search.
     */
    private int minimax(Reversi.Player currentActor, Action option) {
        // apply the option (don't print out passing moves)
        if (!(option instanceof Reversi.Move)) {
            PrintStream std = System.out;
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            }));
            option.apply();
            System.setOut(std);
        } else {
            option.apply();
        }

        // if the game is over, return the evaluated score
        if (model.done()) {
            int score = evaluate();
            option.undo();
            return score;
        }

        // get opposite player
        Reversi.Player nextActor = (currentActor.team() == this.actor.team() ? this.opponent : this.actor);

        // make variable for largest score and best move
        int bestScore = (nextActor.team() == this.actor.team()) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // evaluate all of the new moves the next player could play:
        Set<Action> nextActions = nextActor.actions();
        for (Action a : nextActions) {
            if (!(a instanceof Reversi.Move) && nextActions.size() > 1) { continue; }
            // call minimax, with opposite player, and pass in option
            int score = minimax(nextActor, a);
            // update best score and move if needed
            if (nextActor.team() == this.actor.team() ? score > bestScore : score < bestScore)
                bestScore = score;

        }
        // undo the option
        option.undo();

        // return the score
        return bestScore;
    }


    /**
     * Evaluates the current number of dots on the board the actor has.
     *
     * @return the score for the player at the current end node.
     */
    private int evaluate() {
        return model.scores().get(this.actor.team())
                - model.scores().get(this.opponent.team());
    }
}
