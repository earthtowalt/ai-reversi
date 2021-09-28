// ModelAI Assignment - Reversi
// Topic: Adversarial  Search
// File: ReversiMinimaxBrain.java
// Authors: Carly Bernstein and Jack Walton

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

public class ReversiMinimaxBrain implements Agent<Reversi.Player> {

    public Action choose(Reversi.Player actor, Set<Action> options) {
        // If all we can do is pass, go for it.
        if (options.size() == 1) {
            return first(options);
        }

        // Otherwise, greedily select the move that
        // captures the most enemy pieces in the next turn.
        Reversi.Move best = (Reversi.Move)firstOfType(Reversi.Move.class, options);
        int bestScore = Integer.MIN_VALUE;
        for(Action a : options) {
            if (!(a instanceof Reversi.Move)) { continue; }
            Reversi.Move thisMove = (Reversi.Move)a;
            int thisScore = minimax(actor, thisMove);
            if (thisScore > bestScore) {
                best = thisMove;
                bestScore = thisScore;
            }
        }
        return best;
    }

    private int minimax(Reversi.Player actor, Reversi.Move move) {
        
        return 0;
    }

    private int evaluate(Action option) {

        return 0;
    }
}
