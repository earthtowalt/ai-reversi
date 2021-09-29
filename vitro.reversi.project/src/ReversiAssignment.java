// ModelAI Assignment - Reversi
// Topic: Adversarial  Search
// File: Reversi.java
// Authors: ***

import vitro.*;
import vitro.grid.*;

public class ReversiAssignment extends Host {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ReversiAssignment();
	}

	public ReversiAssignment() {

		Reversi model                   = new Reversi(4, 4);
		SequentialController controller = new SequentialController(model);
		ReversiView view                = new ReversiView(model, controller, 640, 480, new ColorScheme());


		Reversi.Player black = model.createPlayer(Reversi.BLACK);
		Reversi.Player white = model.createPlayer(Reversi.WHITE);

		model.actors.add(black);
		model.actors.add(white);

//		controller.bind(black, new ReversiRandomBrain());
//		controller.bind(white, new ReversiGreedyBrain());

		controller.bind(black, new ReversiMinimaxBrain(model, black));
		controller.bind(white, new ReversiMinimaxBrain(model, white));

		show(view);
	}
}
