
package reversi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Luca Bosch
 */

public class Board {
	/*
	 * I want to inform you that this is not the most object-oriented approach
	 * possible, the reason for this being that I begun working on the project very
	 * early on and later on I didn't want to create many, rather useless classes,
	 * that only contain one method just for the sake of it being more
	 * object-oriented. Therefore you can find all data, which you can't see on
	 * screen but is used for calculations, and all methods to interact with it in
	 * here.
	 */

	// We use a 2D Array to serve as our Board for the game.
	private int[][] boardArray = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, -1, 1, 0, 0, 0 }, { 0, 0, 0, 1, -1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
	/*
	 * We need a copy of the board for the AI as a 'sandbox' to calculate all moves
	 * on, without actually changing anything on the real board.
	 */
	private int[][] copiedArray = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, -1, 1, 0, 0, 0 }, { 0, 0, 0, 1, -1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };;
	private boolean twoPlayers = true; // Turns AI on or off.
	private boolean whiteTurn = false; // Black begins.
	private boolean validMove = true; // Will be set to false if a move is considered invalid.
	private boolean validPass = true; // Will be set to false if a player tries to pass when he can't.
	private int foundMoves = 0;
	private int whiteDisks = 2; /*
								 * We start with 2 white disks in the center since we are playing the variant
								 * 'Othello'.
								 */
	private int blackDisks = 2; /*
								 * We start with 2 black disks in the center since we are playing the variant
								 * 'Othello'.
								 */
	private int passCount = 0; // Keeps track of the times the players passed in a row.
	private int e = 0; /*
						 * Keeps track of flips in combination with flipRowArray and flipColumnArray and
						 * tells AI at which position to look for possible flips.
						 */
	private int[] flipRowArray = new int[500]; // Remembers the rows where flips happen.
	private int[] flipColumnArray = new int[500]; // Remembers the columns where flips happen.
	private boolean testTurn = false; // Used by the AI.
	private static String playerName1 = "";
	private static String playerName2 = "";

	/**
	 * Stores the names of the players so they can be used later on to generate the
	 * match history file.
	 *
	 * @param name1 Name of the player who plays black.
	 * @param name2 Name of the player who plays white (can be left blank for
	 *              singleplayer).
	 */
	public static void setName(String name1, String name2) {
		playerName1 = name1;
		playerName2 = name2;
	}

	/**
	 * @param x true for test placements, false for real placements.
	 */
	public void setTest(boolean x) {
		testTurn = x;
	}

	/**
	 * Changes the turn to a certain color.
	 *
	 * @param i 1 for white -1 or any other integer for black.
	 */
	public void setTurn(int i) {
		if (i == 1) {
			whiteTurn = true;
		} else {
			whiteTurn = false;
		}
	}

	/**
	 * @return the number of white disks currently on the board.
	 */
	public int getNumberOfWhiteDisks() {
		return whiteDisks;
	}

	/**
	 * @return the number of black disks currently on the board.
	 */
	public int getNumberOfBlackDisks() {
		return blackDisks;
	}

	/**
	 * @return whether the move is valid or not.
	 */
	public boolean isValid() {
		return validMove;
	}

	/**
	 * @return The current status of the Reversi board as a 2D array.
	 */
	public int[][] getBoard() {
		return boardArray;
	}

	/**
	 * Sets the board to the input state.
	 *
	 * @param x A 2D array.
	 */
	public void setBoard(int[][] x) {
		this.boardArray = x;
	}

	/**
	 * @param x the number of players participating in the game (1 or 2)
	 */
	public void setPlayers(int x) {
		if (x == 1) {
			twoPlayers = false;
		}
		if (x == 2) {
			twoPlayers = true;
		}
	}

	/**
	 * @return the number of players participating in the game (1 or 2)
	 */
	public int getPlayers() {
		if (twoPlayers) {
			return 2;
		} else {
			return 1;
		}

	}

	/**
	 *
	 * @return The array where the x-coordinates of all flips are stored
	 */
	public int[] getFlipRow() {
		return flipRowArray;
	}

	/**
	 *
	 * @return The array where the y-coordinates of all flips are stored
	 */
	public int[] getFlipColumn() {
		return flipColumnArray;
	}

	/**
	 * Resets the two arrays where coordinates of all flips are stored and resets e,
	 * so that new flips will be stored at the beginning of the array again.
	 */
	public void resetFlipArrays() {
		this.flipRowArray = new int[500];
		this.flipColumnArray = new int[500];
		e = 0;
	}

	/**
	 * Stores a copy of boardArray in copiedArray.
	 */
	public void copyCurrent() {
		copiedArray = new int[boardArray.length][];
		for (int i = 0; i < boardArray.length; i++) {
			final int[] aMatrix = boardArray[i];
			final int aLength = aMatrix.length;
			copiedArray[i] = new int[aLength];
			System.arraycopy(aMatrix, 0, copiedArray[i], 0, aLength);
		}
	}

	/**
	 * Overwrites boardArray with copiedArray.
	 */
	private void undo() {
		boardArray = new int[copiedArray.length][];
		for (int i = 0; i < copiedArray.length; i++) {
			final int[] aMatrix = copiedArray[i];
			final int aLength = aMatrix.length;
			boardArray[i] = new int[aLength];
			System.arraycopy(aMatrix, 0, boardArray[i], 0, aLength);
		}
	}

	/**
	 * Places a white disk at the specified position in case the move is valid but
	 * restores the board to the state before the placement took place. Only works
	 * if it's whites turn since the method is only used by the AI.
	 *
	 * @param row    The row (0-7) where a disk should be placed; if the board had
	 *               the coordinates x and y: X.
	 * @param column The column (0-7) where a disk should be placed; if the board
	 *               had the coordinates x and y: Y.
	 */
	public void testPlace(int row, int column) {
		testTurn = true;
		checkMove(row, column);
		if (validMove) {
			placeWhiteDisk(row, column);
			checkAllWhiteFlips(row, column);
		}
		undo();
	}

	/**
	 * @return The games rules as a string.
	 */
	public String getRules() {
		return "Reversi is a strategy board game for two players, played on an 8x8 uncheckered board." + "\n"
				+ "There are sixty-four identical game pieces called disks, which are light on one side and dark on the other."
				+ "\n" + "Players take turns placing disks on the board with their assigned color facing up." + "\n"
				+ "During a play, any disks of the opponent's color that are in a straight line and bounded by the disk just placed"
				+ "\n" + "and another disk of the current player's color are turned over to the current player's color."
				+ "\n"
				+ "The objective of the game is to have the majority of disks turned to display your color when the game ends."
				+ "\n"
				+ "Disks can only be placed vertically, horizontally or diagonally next to an already existing one."
				+ "\n" + "If a player isn't allowed place a disk anywhere, he can pass." + "\n"
				+ "The game ends when either both players pass after each other or when the board is full.";
	}

	/**
	 * Checks whether this move is valid, in case it is, places a white or black
	 * disk based on whose turn it is. Then proceeds to check all the possible flips
	 * and whether the game has ended. Finally displays the points of each color and
	 * changes the turn to the other color.
	 *
	 * @param row    The row (0-7) where a disk should be placed; if the board had
	 *               the coordinates x and y: X.
	 * @param column The column (0-7) where a disk should be placed; if the board
	 *               had the coordinates x and y: Y.
	 */
	public void placeDisk(int row, int column) {
		checkMove(row, column);
		if (validMove) {
			if (whiteTurn) {
				placeWhiteDisk(row, column);
				checkAllWhiteFlips(row, column);
				whiteTurn = false;
				passCount = 0;
				whiteDisks++;
				checkEnd();
			} else {
				placeBlackDisk(row, column);
				checkAllBlackFlips(row, column);
				whiteTurn = true;
				passCount = 0;
				blackDisks++;
				checkEnd();
			}
		}
	}

	/**
	 * Places a disk of the color whose turn it is on the first valid position on
	 * the board.
	 */
	public void placeRandom() {
		boolean done = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				checkMove(i, j);
				if (validMove && !done) {
					placeDisk(i, j);
					done = true;
				}
			}
		}
	}

	/**
	 * @return the current number of white and black disks on the board. Also
	 *         returns whose turn it is.
	 *
	 */
	public String showPoints() {

		if (whiteTurn) {
			return ("White: " + whiteDisks + " - Black: " + blackDisks + "\n" + "It is white's turn");
		} else {
			return ("White: " + whiteDisks + " - Black: " + blackDisks + "\n" + "It is black's turn");
		}
	}

	/**
	 * Places a white disk at the specified position.
	 *
	 * @param row    The row (0-7) where a disk should be placed; if the board had
	 *               the coordinates x and y: X.
	 * @param column The column (0-7) where a disk should be placed; if the board
	 *               had the coordinates x and y: Y.
	 */
	private void placeWhiteDisk(int row, int column) {
		boardArray[row][column] = 1;
	}

	/**
	 * Places a black disk at the specified position.
	 *
	 * @param row    The row (0-7) where a disk should be placed; if the board had
	 *               the coordinates x and y: X.
	 * @param column The column (0-7) where a disk should be placed; if the board
	 *               had the coordinates x and y: Y.
	 */
	private void placeBlackDisk(int row, int column) {
		boardArray[row][column] = -1;

	}

	/**
	 * Changes the color of a disk at the specified position
	 *
	 * @param row    The row (0-7) where a disk should be flipped; if the board had
	 *               the coordinates x and y: X.
	 * @param column The column (0-7) where a disk should be flipped; if the board
	 *               had the coordinates x and y: Y.
	 */
	private void flipDisk(int row, int column) {
		if (boardArray[row][column] == 1) {
			if (testTurn) {
			} else {
				whiteDisks--;
				blackDisks++;
				JavaFXInteractive.flipAnimation((row * 8) + column + 1, 1);
			}
		}
		if (boardArray[row][column] == -1) {
			if (testTurn) {
			} else {
				whiteDisks++;
				blackDisks--;
				JavaFXInteractive.flipAnimation((row * 8) + column + 1, -1);
			}
		}
		boardArray[row][column] = (boardArray[row][column]) * (-1);
	}

	/**
	 * Checks for possible flips to white in all directions from the specified
	 * origin. Uses multiple if functions to avoid looking in directions where there
	 * can't be any flips.
	 *
	 * @param row    The row (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: X.
	 * @param column The column (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: Y.
	 */
	private void checkAllWhiteFlips(int row, int column) {
		if ((column == 0) || (column == 1)) {
			if ((row == 0) || (row == 1)) {
				checkForWhiteFlips(row, column, 0, 1);
				checkForWhiteFlips(row, column, 1, 1);
				checkForWhiteFlips(row, column, 1, 0);
			} else if ((row == 6) || (row == 7)) {
				checkForWhiteFlips(row, column, 0, 1);
				checkForWhiteFlips(row, column, -1, 1);
				checkForWhiteFlips(row, column, -1, 0);
			} else {
				checkForWhiteFlips(row, column, 0, 1);
				checkForWhiteFlips(row, column, -1, 1);
				checkForWhiteFlips(row, column, 1, 1);
				checkForWhiteFlips(row, column, 1, 0);
				checkForWhiteFlips(row, column, -1, 0);
			}
		} else if ((column == 6) || (column == 7)) {
			if ((row == 0) || (row == 1)) {
				checkForWhiteFlips(row, column, 1, 0);
				checkForWhiteFlips(row, column, 0, -1);
				checkForWhiteFlips(row, column, 1, -1);
			} else if ((row == 6) || (row == 7)) {
				checkForWhiteFlips(row, column, 0, -1);
				checkForWhiteFlips(row, column, -1, 0);
				checkForWhiteFlips(row, column, -1, -1);
			} else {
				checkForWhiteFlips(row, column, 1, 0);
				checkForWhiteFlips(row, column, 0, -1);
				checkForWhiteFlips(row, column, -1, 0);
				checkForWhiteFlips(row, column, -1, -1);
				checkForWhiteFlips(row, column, 1, -1);
			}
		} else if ((row == 0) || (row == 1)) {
			checkForWhiteFlips(row, column, 0, 1);
			checkForWhiteFlips(row, column, 1, 1);
			checkForWhiteFlips(row, column, 1, 0);
			checkForWhiteFlips(row, column, 0, -1);
			checkForWhiteFlips(row, column, 1, -1);
		} else if ((row == 6) || (row == 7)) {
			checkForWhiteFlips(row, column, 0, 1);
			checkForWhiteFlips(row, column, -1, 1);
			checkForWhiteFlips(row, column, 0, -1);
			checkForWhiteFlips(row, column, -1, 0);
			checkForWhiteFlips(row, column, -1, -1);
		} else {
			checkForWhiteFlips(row, column, 0, 1);
			checkForWhiteFlips(row, column, 1, 0);
			checkForWhiteFlips(row, column, 0, -1);
			checkForWhiteFlips(row, column, -1, 0);
			checkForWhiteFlips(row, column, 1, 1);
			checkForWhiteFlips(row, column, -1, -1);
			checkForWhiteFlips(row, column, -1, 1);
			checkForWhiteFlips(row, column, 1, -1);

		}
	}

	/**
	 * Checks for possible flips to black in all directions from the specified
	 * origin. Uses multiple if functions to avoid looking in directions where there
	 * can't be any flips.
	 *
	 * @param row    The row (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: X.
	 * @param column The column (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: Y.
	 */
	private void checkAllBlackFlips(int row, int column) {
		if ((column == 0) || (column == 1)) {
			if ((row == 0) || (row == 1)) {
				checkForBlackFlips(row, column, 0, 1);
				checkForBlackFlips(row, column, 1, 1);
				checkForBlackFlips(row, column, 1, 0);
			} else if ((row == 6) || (row == 7)) {
				checkForBlackFlips(row, column, 0, 1);
				checkForBlackFlips(row, column, -1, 1);
				checkForBlackFlips(row, column, -1, 0);
			} else {
				checkForBlackFlips(row, column, 0, 1);
				checkForBlackFlips(row, column, -1, 1);
				checkForBlackFlips(row, column, 1, 1);
				checkForBlackFlips(row, column, 1, 0);
				checkForBlackFlips(row, column, -1, 0);
			}
		} else if ((column == 6) || (column == 7)) {
			if ((row == 0) || (row == 1)) {
				checkForBlackFlips(row, column, 1, 0);
				checkForBlackFlips(row, column, 0, -1);
				checkForBlackFlips(row, column, 1, -1);
			} else if ((row == 6) || (row == 7)) {
				checkForBlackFlips(row, column, 0, -1);
				checkForBlackFlips(row, column, -1, 0);
				checkForBlackFlips(row, column, -1, -1);
			} else {
				checkForBlackFlips(row, column, 1, 0);
				checkForBlackFlips(row, column, 0, -1);
				checkForBlackFlips(row, column, -1, 0);
				checkForBlackFlips(row, column, -1, -1);
				checkForBlackFlips(row, column, 1, -1);
			}
		} else if ((row == 0) || (row == 1)) {
			checkForBlackFlips(row, column, 0, 1);
			checkForBlackFlips(row, column, 1, 1);
			checkForBlackFlips(row, column, 1, 0);
			checkForBlackFlips(row, column, 0, -1);
			checkForBlackFlips(row, column, 1, -1);
		} else if ((row == 6) || (row == 7)) {
			checkForBlackFlips(row, column, 0, 1);
			checkForBlackFlips(row, column, -1, 1);
			checkForBlackFlips(row, column, 0, -1);
			checkForBlackFlips(row, column, -1, 0);
			checkForBlackFlips(row, column, -1, -1);
		} else {
			checkForBlackFlips(row, column, 0, 1);
			checkForBlackFlips(row, column, 1, 0);
			checkForBlackFlips(row, column, 0, -1);
			checkForBlackFlips(row, column, -1, 0);
			checkForBlackFlips(row, column, 1, 1);
			checkForBlackFlips(row, column, -1, -1);
			checkForBlackFlips(row, column, -1, 1);
			checkForBlackFlips(row, column, 1, -1);

		}
	}

	/**
	 * Checks for possible flips to white in a specified direction. For AI: Adds
	 * coordinates of all flips that take place to arrays.
	 *
	 * @param row    The row (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: X.
	 * @param column The column (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: Y.
	 * @param x      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 * @param y      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 */
	private void checkForWhiteFlips(int row, int column, int x, int y) {
		if (boardArray[row][column] == 1) {
			/*
			 * (((0 <= row + 2 * x) && (row + 2 * x <= 7)) && ((0 <= column + 2 y) &&
			 * (column + 2 * y <= 7))) makes sure that elements out of bounds will never be
			 * looked at.
			 *
			 * (boardArray[row + 1 * x][column + 1 * y] == -1 && boardArray[row + 2 *
			 * x][column + 2 * y] == 1) checks whether the length of the line of consecutive
			 * disks of the other color is 1 and whether there is a disk of the own color at
			 * the end of it. If there is exactly 1 disk of the opposite color in between,
			 * it is flipped.
			 *
			 * It might seem like a very long if function, but it was necessary to combine
			 * all conditions into one in order to use else if which accelerates the
			 * process. Combining the two works because of lazy evaluation where statement B
			 * is never looked at if A is wrong in (A && B).
			 */
			if ((((0 <= (row + (2 * x))) && ((row + (2 * x)) <= 7))
					&& ((0 <= (column + (2 * y))) && ((column + (2 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
			}
			/*
			 * This else if makes sure that elements out of bounds will never be looked at
			 * and checks whether the length of the line of consecutive disks of the other
			 * color is 2 and whether there is a disk of the own color at the end of it. If
			 * there are exactly 2 disks of the opposite color in between, they are flipped.
			 */
			else if ((((0 <= (row + (3 * x))) && ((row + (3 * x)) <= 7))
					&& ((0 <= (column + (3 * y))) && ((column + (3 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
				flipDisk((row + (2 * x)), column + (2 * y));
				flipRowArray[e] = (row + (2 * x));
				flipColumnArray[e] = (column + (2 * y));
				e++;
			}
			// etc.
			else if ((((0 <= (row + (4 * x))) && ((row + (4 * x)) <= 7))
					&& ((0 <= (column + (4 * y))) && ((column + (4 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
				flipDisk((row + (2 * x)), column + (2 * y));
				flipRowArray[e] = (row + (2 * x));
				flipColumnArray[e] = (column + (2 * y));
				e++;
				flipDisk((row + (3 * x)), column + (3 * y));
				flipRowArray[e] = (row + (3 * x));
				flipColumnArray[e] = (column + (3 * y));
				e++;
			} else if ((((0 <= (row + (5 * x))) && ((row + (5 * x)) <= 7))
					&& ((0 <= (column + (5 * y))) && ((column + (5 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
				flipDisk((row + (2 * x)), column + (2 * y));
				flipRowArray[e] = (row + (2 * x));
				flipColumnArray[e] = (column + (2 * y));
				e++;
				flipDisk((row + (3 * x)), column + (3 * y));
				flipRowArray[e] = (row + (3 * x));
				flipColumnArray[e] = (column + (3 * y));
				e++;
				flipDisk((row + (4 * x)), column + (4 * y));
				flipRowArray[e] = (row + (4 * x));
				flipColumnArray[e] = (column + (4 * y));
				e++;
			} else if ((((0 <= (row + (6 * x))) && ((row + (6 * x)) <= 7))
					&& ((0 <= (column + (6 * y))) && ((column + (6 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == -1)
							&& (boardArray[row + (6 * x)][column + (6 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
				flipDisk((row + (2 * x)), column + (2 * y));
				flipRowArray[e] = (row + (2 * x));
				flipColumnArray[e] = (column + (2 * y));
				e++;
				flipDisk((row + (3 * x)), column + (3 * y));
				flipRowArray[e] = (row + (3 * x));
				flipColumnArray[e] = (column + (3 * y));
				e++;
				flipDisk((row + (4 * x)), column + (4 * y));
				flipRowArray[e] = (row + (4 * x));
				flipColumnArray[e] = (column + (4 * y));
				e++;
				flipDisk((row + (5 * x)), column + (5 * y));
				flipRowArray[e] = (row + (5 * x));
				flipColumnArray[e] = (column + (5 * y));
				e++;
			} else if ((((0 <= (row + (7 * x))) && ((row + (7 * x)) <= 7))
					&& ((0 <= (column + (7 * y))) && ((column + (7 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == -1)
							&& (boardArray[row + (6 * x)][column + (6 * y)] == -1)
							&& (boardArray[row + (7 * x)][column + (7 * y)] == 1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipRowArray[e] = (row + (1 * x));
				flipColumnArray[e] = (column + (1 * y));
				e++;
				flipDisk((row + (2 * x)), column + (2 * y));
				flipRowArray[e] = (row + (2 * x));
				flipColumnArray[e] = (column + (2 * y));
				e++;
				flipDisk((row + (3 * x)), column + (3 * y));
				flipRowArray[e] = (row + (3 * x));
				flipColumnArray[e] = (column + (3 * y));
				e++;
				flipDisk((row + (4 * x)), column + (4 * y));
				flipRowArray[e] = (row + (4 * x));
				flipColumnArray[e] = (column + (4 * y));
				e++;
				flipDisk((row + (5 * x)), column + (5 * y));
				flipRowArray[e] = (row + (5 * x));
				flipColumnArray[e] = (column + (5 * y));
				e++;
				flipDisk((row + (6 * x)), column + (6 * y));
				flipRowArray[e] = (row + (6 * x));
				flipColumnArray[e] = (column + (6 * y));
				e++;
			}
		}
		/*
		 * This code may seem quite redundant and like it could be written in less
		 * lines, however, performance-wise it was the fastest.
		 */
	}

	/**
	 * Checks for possible flips to black in a specified direction.
	 *
	 * @param row    The row (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: X.
	 * @param column The column (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: Y.
	 * @param x      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 * @param y      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 */
	private void checkForBlackFlips(int row, int column, int x, int y) {
		if (boardArray[row][column] == -1) {
			/*
			 * (((0 <= row + 2 * x) && (row + 2 * x <= 7)) && ((0 <= column + 2 y) &&
			 * (column + 2 * y <= 7))) makes sure that elements out of bounds will never be
			 * looked at.
			 *
			 * (boardArray[row + 1 * x][column + 1 * y] == -1 && boardArray[row + 2 *
			 * x][column + 2 * y] == 1) checks whether the length of the line of consecutive
			 * disks of the other color is 1 and whether there is a disk of the own color at
			 * the end of it. If there is exactly 1 disk of the opposite color in between,
			 * it is flipped.
			 *
			 * It might seem like a very long if function, but it was necessary to combine
			 * all conditions into one in order to use else if which accelerates the
			 * process. Combining the two works because of lazy evaluation where statement B
			 * is never looked at if A is wrong in (A && B).
			 */
			if ((((0 <= (row + (2 * x))) && ((row + (2 * x)) <= 7))
					&& ((0 <= (column + (2 * y))) && ((column + (2 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
			}
			/*
			 * This else if makes sure that elements out of bounds will never be looked at
			 * and checks whether the length of the line of consecutive disks of the other
			 * color is 2 and whether there is a disk of the own color at the end of it. If
			 * there are exactly 2 disks of the opposite color in between, they are flipped.
			 */
			else if ((((0 <= (row + (3 * x))) && ((row + (3 * x)) <= 7))
					&& ((0 <= (column + (3 * y))) && ((column + (3 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipDisk((row + (2 * x)), column + (2 * y));
			}
			// etc.
			else if ((((0 <= (row + (4 * x))) && ((row + (4 * x)) <= 7))
					&& ((0 <= (column + (4 * y))) && ((column + (4 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipDisk((row + (2 * x)), column + (2 * y));
				flipDisk((row + (3 * x)), column + (3 * y));
			} else if ((((0 <= (row + (5 * x))) && ((row + (5 * x)) <= 7))
					&& ((0 <= (column + (5 * y))) && ((column + (5 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipDisk((row + (2 * x)), column + (2 * y));
				flipDisk((row + (3 * x)), column + (3 * y));
				flipDisk((row + (4 * x)), column + (4 * y));
			} else if ((((0 <= (row + (6 * x))) && ((row + (6 * x)) <= 7))
					&& ((0 <= (column + (6 * y))) && ((column + (6 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == 1)
							&& (boardArray[row + (6 * x)][column + (6 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipDisk((row + (2 * x)), column + (2 * y));
				flipDisk((row + (3 * x)), column + (3 * y));
				flipDisk((row + (4 * x)), column + (4 * y));
				flipDisk((row + (5 * x)), column + (5 * y));
			} else if ((((0 <= (row + (7 * x))) && ((row + (7 * x)) <= 7))
					&& ((0 <= (column + (7 * y))) && ((column + (7 * y)) <= 7)))
					&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
							&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
							&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
							&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
							&& (boardArray[row + (5 * x)][column + (5 * y)] == 1)
							&& (boardArray[row + (6 * x)][column + (6 * y)] == 1)
							&& (boardArray[row + (7 * x)][column + (7 * y)] == -1))) {
				flipDisk((row + (1 * x)), column + (1 * y));
				flipDisk((row + (2 * x)), column + (2 * y));
				flipDisk((row + (3 * x)), column + (3 * y));
				flipDisk((row + (4 * x)), column + (4 * y));
				flipDisk((row + (5 * x)), column + (5 * y));
				flipDisk((row + (6 * x)), column + (6 * y));
			}
		}
		/*
		 * This code may seem quite redundant and like it could be written in less
		 * lines, however, performance-wise it was the fastest.
		 *
		 * Also, AI functionality is missing here since AI will always play white.
		 */
	}

	/**
	 * Checks whether a move complies with the rules. The strategy is quite similar
	 * to the method used to check for flips.
	 *
	 * @param row    The row (0-7) where it should be checked whether placing a disk
	 *               there is valid; if the board had the coordinates x and y: X.
	 * @param column The column (0-7) where it should be checked whether placing a
	 *               disk there is valid; if the board had the coordinates x and y:
	 *               Y.
	 */
	private void checkMove(int row, int column) {
		foundMoves = 0;
		if ((column == 0) || (column == 1)) {
			if ((row == 0) || (row == 1)) {
				checkAllMoves(row, column, 0, 1);
				checkAllMoves(row, column, 1, 1);
				checkAllMoves(row, column, 1, 0);
			} else if ((row == 6) || (row == 7)) {
				checkAllMoves(row, column, 0, 1);
				checkAllMoves(row, column, -1, 1);
				checkAllMoves(row, column, -1, 0);
			} else {
				checkAllMoves(row, column, 0, 1);
				checkAllMoves(row, column, -1, 1);
				checkAllMoves(row, column, 1, 1);
				checkAllMoves(row, column, 1, 0);
				checkAllMoves(row, column, -1, 0);
			}
		} else if ((column == 6) || (column == 7)) {
			if ((row == 0) || (row == 1)) {
				checkAllMoves(row, column, 1, 0);
				checkAllMoves(row, column, 0, -1);
				checkAllMoves(row, column, 1, -1);
			} else if ((row == 6) || (row == 7)) {
				checkAllMoves(row, column, 0, -1);
				checkAllMoves(row, column, -1, 0);
				checkAllMoves(row, column, -1, -1);
			} else {
				checkAllMoves(row, column, 1, 0);
				checkAllMoves(row, column, 0, -1);
				checkAllMoves(row, column, -1, 0);
				checkAllMoves(row, column, -1, -1);
				checkAllMoves(row, column, 1, -1);
			}
		} else if ((row == 0) || (row == 1)) {
			checkAllMoves(row, column, 0, 1);
			checkAllMoves(row, column, 1, 1);
			checkAllMoves(row, column, 1, 0);
			checkAllMoves(row, column, 0, -1);
			checkAllMoves(row, column, 1, -1);
		} else if ((row == 6) || (row == 7)) {
			checkAllMoves(row, column, 0, 1);
			checkAllMoves(row, column, -1, 1);
			checkAllMoves(row, column, 0, -1);
			checkAllMoves(row, column, -1, 0);
			checkAllMoves(row, column, -1, -1);
		} else {
			checkAllMoves(row, column, 0, 1);
			checkAllMoves(row, column, 1, 0);
			checkAllMoves(row, column, 0, -1);
			checkAllMoves(row, column, -1, 0);
			checkAllMoves(row, column, 1, 1);
			checkAllMoves(row, column, -1, -1);
			checkAllMoves(row, column, -1, 1);
			checkAllMoves(row, column, 1, -1);
		}
		if (foundMoves > 0) {
			validMove = true;
		} else {
			validMove = false;
		}
		foundMoves = 0;
	}

	/**
	 * @param row    The row (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: X.
	 * @param column The column (0-7) around which flips should be checked; if the
	 *               board had the coordinates x and y: Y.
	 * @param x      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 * @param y      x and y are used to define the direction in which to check for
	 *               flips, with S: x=1, y=0 E: x=0 y=1 N: x=-1, y=0 W: x=0, y=-1
	 *               SE: x=1, y=1 SW: x=1, y=-1 NE: x=-1, y=1 NW: x=-1, y=-1.
	 */
	private void checkAllMoves(int row, int column, int x, int y) {

		if (boardArray[row][column] == 0) {
			if (!whiteTurn) {
				if ((((0 <= (row + (2 * x))) && ((row + (2 * x)) <= 7))
						&& ((0 <= (column + (2 * y))) && ((column + (2 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1))) {
					foundMoves++;
				} else if ((((0 <= (row + (3 * x))) && ((row + (3 * x)) <= 7))
						&& ((0 <= (column + (3 * y))) && ((column + (3 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == -1))) {
					foundMoves++;
				} else if ((((0 <= (row + (4 * x))) && ((row + (4 * x)) <= 7))
						&& ((0 <= (column + (4 * y))) && ((column + (4 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == -1))) {
					foundMoves++;
				} else if ((((0 <= (row + (5 * x))) && ((row + (5 * x)) <= 7))
						&& ((0 <= (column + (5 * y))) && ((column + (5 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == -1))) {
					foundMoves++;
				} else if ((((0 <= (row + (6 * x))) && ((row + (6 * x)) <= 7))
						&& ((0 <= (column + (6 * y))) && ((column + (6 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == 1)
								&& (boardArray[row + (6 * x)][column + (6 * y)] == -1))) {
					foundMoves++;
				} else if ((((0 <= (row + (7 * x))) && ((row + (7 * x)) <= 7))
						&& ((0 <= (column + (7 * y))) && ((column + (7 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == 1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == 1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == 1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == 1)
								&& (boardArray[row + (6 * x)][column + (6 * y)] == 1)
								&& (boardArray[row + (7 * x)][column + (7 * y)] == -1))) {
					foundMoves++;
				}
			} else {
				if ((((0 <= (row + (2 * x))) && ((row + (2 * x)) <= 7))
						&& ((0 <= (column + (2 * y))) && ((column + (2 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == 1))) {
					foundMoves++;
				}

				else if ((((0 <= (row + (3 * x))) && ((row + (3 * x)) <= 7))
						&& ((0 <= (column + (3 * y))) && ((column + (3 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == 1))) {
					foundMoves++;
				} else if ((((0 <= (row + (4 * x))) && ((row + (4 * x)) <= 7))
						&& ((0 <= (column + (4 * y))) && ((column + (4 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == 1))) {
					foundMoves++;
				} else if ((((0 <= (row + (5 * x))) && ((row + (5 * x)) <= 7))
						&& ((0 <= (column + (5 * y))) && ((column + (5 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == 1))) {
					foundMoves++;
				} else if ((((0 <= (row + (6 * x))) && ((row + (6 * x)) <= 7))
						&& ((0 <= (column + (6 * y))) && ((column + (6 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == -1)
								&& (boardArray[row + (6 * x)][column + (6 * y)] == 1))) {
					foundMoves++;
				} else if ((((0 <= (row + (7 * x))) && ((row + (7 * x)) <= 7))
						&& ((0 <= (column + (7 * y))) && ((column + (7 * y)) <= 7)))
						&& ((boardArray[row + (1 * x)][column + (1 * y)] == -1)
								&& (boardArray[row + (2 * x)][column + (2 * y)] == -1)
								&& (boardArray[row + (3 * x)][column + (3 * y)] == -1)
								&& (boardArray[row + (4 * x)][column + (4 * y)] == -1)
								&& (boardArray[row + (5 * x)][column + (5 * y)] == -1)
								&& (boardArray[row + (6 * x)][column + (6 * y)] == -1)
								&& (boardArray[row + (7 * x)][column + (7 * y)] == 1))) {
					foundMoves++;
				}
			}
		}
	}

	/**
	 * Changes turn to the other color. Adds 1 to passCount and checks if the game
	 * has ended.
	 */
	public void pass() {
		if (validatePass()) {

			if (!whiteTurn) {
				whiteTurn = true;
			} else {
				whiteTurn = false;
			}
			passCount++;
			checkEnd();
		} else {
			JavaFXInteractive.wrongPass();
			// Displays a message to the user.
		}
	}

	/**
	 * @return true or false depending on whether there has been found a valid move
	 *         (false) or not (true).
	 */
	public boolean validatePass() {
		validPass = true;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				checkMove(i, j);
				if (validMove && validPass) {
					validPass = false;
				}
			}
		}
		return validPass;
	}

	/**
	 * @return true if the last pass was valid, false if not
	 */
	public boolean passIsValid() {
		return validPass;
	}

	/**
	 * Displays the text 'Game has ended.' if both players passed after each other
	 * or if the sum of black and white disks is 64 and therefore the board is full.
	 */
	private void checkEnd() {
		if (((passCount == 2) || ((blackDisks + whiteDisks) == 64))) {
			System.out.println("Game has ended.");

			try {
				evaluate();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displays the text "White wins!", "Black wins!" or "Draw!" based on whose
	 * color has more disks on the board. Prints the result of the current match to
	 * matchhistory.txt and saves the number of disks of the winner to
	 * highscores.txt.
	 *
	 * @throws IOException when there's something wrong with the files.
	 *
	 */
	private void evaluate() throws IOException {
		String higherScore = "";
		if (blackDisks < whiteDisks) {
			higherScore = whiteDisks + ";";
			System.out.println("White wins!");
		} else if (blackDisks > whiteDisks) {
			higherScore = blackDisks + ";";
			System.out.println("Black wins!");
		} else {
			higherScore = blackDisks + ";";
			System.out.println("Draw!");
		}
		final File highscores = new File("src" + File.separator + "highscores.txt");

		try (FileOutputStream fop = new FileOutputStream(highscores, true)) {

			if (!highscores.exists()) {
				highscores.createNewFile();
			}
			final byte[] contentInBytes = higherScore.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		}
		/*
		 * String result = ""; if (twoPlayers) { result = "White" + " (" + playerName2 +
		 * "): " + whiteDisks + " - Black" + " (" + playerName1 + "): " + blackDisks +
		 * ", "; } else { result = "(AI) White: " + whiteDisks + " - Black" + " (" +
		 * playerName1 + "): " + blackDisks + ", "; } final File matchHistory = new
		 * File("src" + File.separator + "matchhistory.txt");
		 * 
		 * try (FileOutputStream fop = new FileOutputStream(matchHistory, true)) {
		 * 
		 * if (!matchHistory.exists()) { matchHistory.createNewFile(); } final byte[]
		 * contentInBytes = result.getBytes();
		 * 
		 * fop.write(contentInBytes); fop.flush(); fop.close();
		 * 
		 * }
		 */
	}

	/**
	 * Resets all variables so a new game can be started.
	 */
	public void clear() {
		boardArray = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, -1, 1, 0, 0, 0 }, { 0, 0, 0, 1, -1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

		copiedArray = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, -1, 1, 0, 0, 0 }, { 0, 0, 0, 1, -1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
		twoPlayers = true;
		whiteTurn = false;
		validMove = true;
		whiteDisks = 2;
		blackDisks = 2;
		passCount = 0;
		e = 0;
		flipRowArray = new int[500];
		flipColumnArray = new int[500];
		testTurn = false;

	}
}