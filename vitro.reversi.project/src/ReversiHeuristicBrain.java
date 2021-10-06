// ModelAI Assignment - Reversi
// Topic: Adversarial  Search
// File: ReversiHeuristicBrain.java
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

public class ReversiHeuristicBrain implements Agent<Reversi.Player> {

    private Reversi model;
    private Reversi.Player actor;
    private Reversi.Player opponent;
    private int depth;

    private int moveCount = 0;
    private int cumulativeMoveCount = 0;

    // Constructor
    public ReversiHeuristicBrain(Reversi model, Reversi.Player actor, int depth) {
        this.model = model;
        this.actor = actor;
        this.opponent = model.createPlayer(actor.team() == Reversi.BLACK ? Reversi.WHITE : Reversi.BLACK);
        this.depth = depth;
    }

    /**
     * Outputs the move that the current player should choose.
     * @param actor
     * @param options
     * @return
     */
    public Action choose(Reversi.Player actor, Set<Action> options) {
        System.out.println("CHOOSE :: team=" + actor.team());
        moveCount = 0;

        // If all we can do is pass, go for it.
        if (options.size() == 1) {
            return first(options);
        }

        // Otherwise, find the best scoring minimax move
        Reversi.Move best = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for(Action a : options) {
            if (!(a instanceof Reversi.Move)) { continue; }
            // from my perspective, what is the value of move a
            int thisScore = minimax(actor, a, alpha, beta, depth);

            if (thisScore > alpha) {
                alpha = thisScore;
                best = (Reversi.Move)a;
            }
        }
        System.out.println("selected alpha (best possible): " + alpha);

        // to find the cumulative move count
//        System.out.println("States Examined: " + moveCount);
//        cumulativeMoveCount += moveCount;
//        System.out.println("Cumulative States Examined: " + cumulativeMoveCount);

        return best;
    }

    private void quietlyApply(Action option) {
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
    }

    /**
     * Using the current player, recursively evaluate the best move using minimax search against the adversary.
     *
     * @param currentActor The current player that will make the next move
     * @param option The new move that a player will make in the game.
     * @return The best score more the given move using minimax search.
     */
    private int minimax(Reversi.Player currentActor, Action option, int alpha, int beta, int depth) {
        moveCount++;
        quietlyApply(option);

        // if the game is over, return the evaluated score
        if (model.done()) {
            int score = evaluate();
            option.undo();
            return score;
        }

        // if reached the max depth, then return the heuristic
        if (depth <= 0) {
            int score = heuristic(currentActor);
            option.undo();
            return score;
        }

        // get opposite player
        Reversi.Player nextActor = currentActor.team() == this.actor.team() ? this.opponent : this.actor;

        // make variable for largest score and best move
        boolean maximize = nextActor.team() == this.actor.team();

        // evaluate all of the new moves the next player could play:
        Set<Action> nextActions = nextActor.actions();
        for (Action a : nextActions) {
            if (!(a instanceof Reversi.Move) && nextActions.size() > 1) { continue; }
            // call minimax, with opposite player, and pass in option
            int score = minimax(nextActor, a, alpha, beta, depth-1);

            // update best score and move if needed
            if (maximize) {
                if (score >= beta) {
                    option.undo();
                    return score;
                } else if (score > alpha) {
                    alpha = score;
                }
            } else {
                if (score <= alpha) {
                    option.undo();
                    return score;
                } else if (score < beta) {
                    beta = score;
                }
            }

        }
        // undo the option
        option.undo();

        // return the score
        return maximize ? alpha : beta;
    }

    private int heuristic(Reversi.Player currentActor) {
        return mobilityHeuristic(currentActor);
    }

    public int mobilityHeuristic(Reversi.Player currentActor) {
        Reversi.Player nextActor = currentActor.team() == this.actor.team() ? this.opponent : this.actor;
        Set<Action> myActions = currentActor.actions();

        int myActionsNumber = myActions.size();
        int bestNextActionsNumber = Integer.MAX_VALUE;

        for (Action a : myActions) {
            if (!(a instanceof Reversi.Move) && myActions.size() > 1) { continue; }
            quietlyApply(a);

            if (nextActor.actions().size() < bestNextActionsNumber) {
                bestNextActionsNumber = nextActor.actions().size();
            }

            a.undo();
        }

        return currentActor.team() == this.actor.team() ?
                myActionsNumber - bestNextActionsNumber :
                bestNextActionsNumber - myActionsNumber;

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
