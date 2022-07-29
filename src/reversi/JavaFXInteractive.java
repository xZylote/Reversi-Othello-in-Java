
package reversi;

import java.util.Arrays;

import javafx.animation.FillTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Luca Bosch
 */
public class JavaFXInteractive implements EventHandler<MouseEvent> {
	private Board board = new Board();
	private Circle circle;
	private static ObservableList<Node> circleList;

	/**
	 * Constructor
	 */
	public JavaFXInteractive() {
	}

	/**
	 * @param board The board of the current game.
	 */
	public JavaFXInteractive(Board board) {
		this.board = board;

	}

	/**
	 * @param circle The circle that was clicked on.
	 * @param board  The board of the current game.
	 */
	public JavaFXInteractive(Circle circle, Board board) {
		this.board = board;
		this.circle = circle;
	}

	/**
	 * Animates the transition of color.
	 *
	 * @param diskId Number from 1-64 identifying the circle whose color should be
	 *               changed.
	 * @param color  Current color of this circle (1 = White, -1 = Black).
	 */
	public static void flipAnimation(int diskId, int color) {
		final Circle circle = (Circle) circleList.get(diskId);
		if (color == -1) {
			final FillTransition ft = new FillTransition(Duration.millis(500), circle, Color.BLACK, Color.WHITE);
			ft.setCycleCount(1);
			ft.play();
		} else {
			final FillTransition ft = new FillTransition(Duration.millis(500), circle, Color.WHITE, Color.BLACK);
			ft.setCycleCount(1);
			ft.play();
		}
	}

	/**
	 * Alerts the user if he passes, even though he could make a move.
	 */
	public static void wrongPass() {
		final Pane root = new Pane();

		final Text msg = new Text("You can only pass if there is no valid move.");
		msg.setFont(new Font("Arial", 14));
		msg.setX(10);
		msg.setY(15);
		root.getChildren().add(msg);

		final Scene scene = new Scene(root, 400, 30);

		final Stage stage = new Stage();
		stage.setTitle("Hol' up");
		stage.setScene(scene);
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * @param children The list where all objects in the pane are stored.
	 *                 (Pane.getChildren())
	 */
	public static void setList(ObservableList<Node> children) {
		circleList = children;
	}

	/**
	 * Converts the ID given to the circles to their corresponding x,y position on
	 * the board and places a disk on the clicked circle, then refreshes the
	 * displayed board. If only one player is playing also tells the AI to calculate
	 * its move. After that refreshes the displayed board again.
	 */
	@Override
	public void handle(MouseEvent event) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (Integer.parseInt(circle.getId()) == ((r * 8) + c + 1)) {
					/*
					 * Using our assigned ID's we can find the field that was clicked on and place a
					 * disk there.
					 */
					board.placeDisk(r, c);
				}
			}

		}
		if ((board.getPlayers() == 1) && board.isValid()) {
			final AI ai = new AI(board);
			ai.bestMove();
		}
		refreshBoard();
		circleList.get(79).setDisable(true);
		circleList.get(80).setDisable(true);
		/*
		 * Switching from single- to mulitplayer and vice versa are disabled after the
		 * first turn.
		 */
	}

	/**
	 * Synchronizes the graphical board shown in the application with the one which
	 * is used to make calculations.
	 */
	public void refreshBoard() {
		// /*
		// This is optional, but gives an insight to how the program works.
		for (int i = 0; i < 8; i++) {
			System.out.println(Arrays.toString(board.getBoard()[i]));
		}
		System.out.println("");
		// */
		for (int i = 0; i < 64; i++) {
			/*
			 * We use ((int) (i / 8))][(i % 8)] to convert a number from 0-63 to a
			 * corresponding x,y-position on the 2D array.
			 */
			if (board.getBoard()[(i / 8)][(i % 8)] == 0) {
				((Circle) circleList.get(i + 1))
						.setFill(Color.FORESTGREEN);/*
													 * We set the color of a circle to green if there is no disk on it.
													 */
			}

		}
		for (int i = 0; i < 64; i++) {
			if (board.getBoard()[(i / 8)][(i % 8)] == 1) {
				((Circle) circleList.get(i + 1))
						.setFill(Color.WHITE);/*
												 * We set the color of a circle to white if there is a white disk on it.
												 */
			}
		}
		for (int i = 0; i < 64; i++) {
			if (board.getBoard()[(i / 8)][(i % 8)] == -1) {
				((Circle) circleList.get(i + 1))
						.setFill(Color.BLACK);/*
												 * We set the color of a circle to black if there is a black disk on it.
												 */
			}
		}
		((Label) circleList.get(85)).setText("Score:" + "\n" + board.showPoints()); // Updates the score-label.

	}

	/**
	 * Opens a dialog where the player(s) can enter their name(s).
	 */
	public static void nameDialog() {
		final Pane root = new Pane();
		final Label label1 = new Label("Black:");
		label1.setLayoutX(10);
		label1.setLayoutY(42);

		final Label label2 = new Label("White (optional):");
		label2.setLayoutX(10);
		label2.setLayoutY(82);

		final Label label3 = new Label("Please enter your name(s)");
		label3.setLayoutX(10);
		label3.setLayoutY(12);

		final TextField textField1 = new TextField();
		textField1.setLayoutX(150);
		textField1.setLayoutY(40);

		final TextField textField2 = new TextField();
		textField2.setLayoutX(150);
		textField2.setLayoutY(80);

		final Button button = new Button("Submit");
		button.setLayoutX(325);
		button.setLayoutY(80);

		root.getChildren().addAll(label1, label2, label3, textField1, textField2, button);
		final Scene scene = new Scene(root, 390, 120);

		final Stage stage = new Stage();
		stage.setTitle("Names");
		stage.setScene(scene);
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.show();
		button.setOnAction((event -> {
			Board.setName(textField1.getText(), textField2.getText());
			stage.close();
		}));

	}
}
