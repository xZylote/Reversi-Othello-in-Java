
package reversi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Luca Bosch
 */
public class JavaFXStatic extends Application {
	Board board = new Board(); /*
								 * This is the instance of board the actual game runs on.
								 */
	JavaFXInteractive interaction = new JavaFXInteractive(board);

	@Override
	public void start(Stage primaryStage) throws Exception {
		final Pane root = new Pane(); /*
										 * The room where all shapes and objects are located on. I chose the standard
										 * pane since it was sufficient for what I wanted to create and it's relatively
										 * easy to edit locations etc.
										 */
		final Rectangle background = new Rectangle(); // Green background
		background.setX(0);
		background.setY(0);
		background.setWidth(400);
		background.setHeight(400);
		background.setFill(Color.GREEN);
		root.getChildren().add(background);
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				// 60 'forest green' circles, 2 black and 2 white ones.
				final Circle circle = new Circle();
				circle.setCenterX(25 + (c * 50));
				circle.setCenterY(25 + (r * 50));
				circle.setRadius(20);
				circle.setFill(Color.FORESTGREEN);
				if ((((r * 8) + c + 1) == 28) || (((r * 8) + c + 1) == 37)) {
					circle.setFill(Color.BLACK);
				}
				if ((((r * 8) + c + 1) == 29) || (((r * 8) + c + 1) == 36)) {
					circle.setFill(Color.WHITE);
				}
				circle.setId("" + ((r * 8) + c + 1)); /*
														 * Each circle is given an ID ranging from 1 to 64 so it can be
														 * identified later on.
														 */
				root.getChildren().add(circle);
				circle.setOnMouseClicked(
						new JavaFXInteractive(circle, board)); /*
																 * We handle all actions that will happen if a circle is
																 * clicked in another class.
																 */

			}
		}
		JavaFXInteractive.setList(root.getChildren()); /*
														 * The list where all graphic elements are stored in is given to
														 * the other class since it is needed to be able to change the
														 * properties of the circles on click.
														 */
		for (int i = 0; i < 7; i++) {
			final Rectangle x = new Rectangle(); // Horizontal separators
			x.setX(0);
			x.setY(49 + (i * 50));
			x.setWidth(400);
			x.setHeight(2);
			x.setFill(Color.GREY);
			root.getChildren().add(x);
		}

		for (int i = 0; i < 7; i++) {
			final Rectangle y = new Rectangle(); // Vertical separators
			y.setX(49 + (i * 50));
			y.setY(0);
			y.setWidth(2);
			y.setHeight(400);
			y.setFill(Color.GREY);
			root.getChildren().add(y);
		}

		final RadioButton radioButton1 = new RadioButton("One Player");
		/*
		 * Two radio buttons to be able to select single- or multiplayer. Multiplayer is
		 * selected by default.
		 */
		radioButton1.setUserData(1);
		// The user data is needed to actually identify the radio button that is active.
		radioButton1.setLayoutX(420);
		radioButton1.setLayoutY(150);

		final RadioButton radioButton2 = new RadioButton("Two Players");
		radioButton2.setUserData(2);
		radioButton2.setSelected(true);
		radioButton2.setLayoutX(420);
		radioButton2.setLayoutY(170);

		final ToggleGroup toggleGroup = new ToggleGroup();
		// You can only select one radio button of the same toggle group at a time.
		radioButton1.setToggleGroup(toggleGroup);
		radioButton2.setToggleGroup(toggleGroup);
		/*
		 * I will be using lambda expressions for all the buttons since I thought it to
		 * be the best looking option and it can easily be organized compared to
		 * creating separate classes for every button or using anonymous ones.
		 */
		toggleGroup.selectedToggleProperty().addListener((ChangeListener<Toggle>) (ov, old_toggle, new_toggle) -> {
			if (toggleGroup.getSelectedToggle() != null) {
				board.setPlayers((int) toggleGroup.getSelectedToggle().getUserData());
			}
		});
		root.getChildren().addAll(radioButton1, radioButton2);

		final Button button1 = new Button("Show rules");
		button1.setLayoutX(420);
		button1.setLayoutY(350);
		button1.setOnAction(event -> {
			final Pane root2 = new Pane();

			final Text rules = new Text(board.getRules());
			rules.setFont(new Font("Arial", 14));
			rules.setX(20);
			rules.setY(20);
			root2.getChildren().add(rules);

			final Scene scene = new Scene(root2, 740, 160);

			final Stage stage = new Stage();
			stage.setTitle("Rules");
			stage.setScene(scene);
			stage.show();
		});
		root.getChildren().add(button1);

		final Button button2 = new Button("Pass");
		button2.setLayoutX(420);
		button2.setLayoutY(320);
		button2.setOnAction(event -> {
			board.pass();
			if ((board.getPlayers() == 1) && board.passIsValid()) {
				final AI ai = new AI(board);
				ai.bestMove();
			}
			interaction.refreshBoard();
		});
		root.getChildren().add(button2);

		final Button button3 = new Button("Restart");
		button3.setLayoutX(420);
		button3.setLayoutY(290);
		button3.setOnAction(event -> {
			board.clear();
			interaction.refreshBoard();
			radioButton1.setDisable(false);
			radioButton2.setDisable(false);
			radioButton2.setSelected(true);
		});
		root.getChildren().add(button3);

		final Button button4 = new Button("Show highscores");
		button4.setLayoutX(420);
		button4.setLayoutY(260);
		button4.setOnAction(event -> {
			/*
			 * Highscores from the .txt file are read, separated and added to a list, which
			 * is then sorted and the scores are shown beginning with the highest one. (In
			 * my project I decided that the AI can set new highscores and the multiplayer
			 * highscores are also counted in, even though that might not make most sense
			 * since you can cheat very easily)
			 */
			boolean gamePlayed = true;
			/*
			 * Will be set to false if no game has been played yet and therefore the file
			 * highscores.txt doesn't exist.
			 */
			final Pane root3 = new Pane();
			String text = "";
			BufferedReader in;
			String word[];
			final ArrayList<Integer> sortedScores = new ArrayList<>();
			try {
				in = new BufferedReader(new FileReader("src" + File.separator + "highscores.txt"));
				String line;
				while ((line = in.readLine()) != null) {
					word = line.split(";");
					/*
					 * Different scores are split with a ';' so we can now process each score
					 * separately.
					 */
					for (final String token : word) {
						sortedScores.add(Integer.parseInt(token));
						// Every score is added to a list as an integer.
					}
					sortedScores.sort(null);
					for (int i = 0; i < sortedScores.size(); i++) {
						text = text + "\n" + sortedScores.get(sortedScores.size() - i - 1);
						// We start with the last score since the list is sorted ascendingly.
					}
				}
			} catch (final IOException e) {
				final Pane root4 = new Pane();

				final Text noGames = new Text(
						"At least one game has to be played before you can look at the highscores");
				noGames.setFont(new Font("Arial", 14));
				noGames.setX(20);
				noGames.setY(20);
				root4.getChildren().add(noGames);

				final Scene scene = new Scene(root4, 490, 40);

				final Stage stage = new Stage();
				stage.setTitle("Stop!");
				stage.setScene(scene);
				stage.setAlwaysOnTop(true);
				stage.setResizable(false);
				stage.show();
				gamePlayed = false;
			}
			if (gamePlayed) {
				final Text highscore = new Text(text);
				highscore.setFont(new Font("Arial", 28));
				highscore.setFill(Color.BLACK);
				highscore.setX(50);
				highscore.setY(10);
				root3.getChildren().add(highscore);

				final Scene scene = new Scene(root3, 130, 295);

				final Stage stage = new Stage();
				stage.setMinWidth(140);
				stage.setTitle("Highscores");
				stage.setScene(scene);
				stage.setAlwaysOnTop(true);
				stage.show();
			}
		});
		root.getChildren().add(button4);
		final Label label = new Label(
				"Score:" + "\n" + board.showPoints()); /*
														 * Initial score (will be changed outside of this class).
														 */
		label.setLayoutX(410);
		label.setLayoutY(20);
		root.getChildren().add(label);

		final Scene scene = new Scene(root, 540, 390);

		primaryStage.setTitle("Reversi (Othello)"); /*
													 * This is going to be the title of the window the game runs in
													 */
		primaryStage.setMinHeight(440);
		primaryStage.setMinWidth(545);
		primaryStage.setScene(scene);
		primaryStage.show(); // Important so the window actually shows up.
		// JavaFXInteractive.nameDialog(); // Opens another window where you can enter
		// the names of the players.
	}
}