// ModelAI Assignment - Reversi
// Topic: Adversarial  Search
// File: ReversiGreedyBrain.java
// Authors: ***

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

public class ReversiRandomBrain implements Agent<Reversi.Player> {

    public Action choose(Reversi.Player actor, Set<Action> options) {

        // pass if needed
        if (options.size() == 1) {
            return first(options);
        }
        
        // otherwise select a move that places a piece
        return options.stream()
                .filter(a -> a instanceof Reversi.Move)
                .findAny()
                .orElse(null);

    }
}
