
package reversi;

import java.util.ArrayList;

/**
 * @author Luca Bosch
 */
public class AI {
	private final ArrayList<Integer> list1 = new ArrayList<>();
	/*
	 * This array lists the values of having a disk of your own color on a certain
	 * field. This array always has a corresponding field in board.boardArray.
	 * Changing the values will change the behavior of the AI, it always tries to
	 * get the greatest amount of points by placing a disk. This amount is
	 * calculated by adding the value of the placed disk to the ones of the disks
	 * which will be flipped to the same color. These values are taken from:
	 * https://de.wikipedia.org/wiki/Computer-Othello#Disk-Square/ (This is not a
	 * very hard to win against AI, but it should match the skills of a player new
	 * to the game)
	 */
	private final int[][] valueArray = new int[][] { { 50, -20, 10, 5, 5, 10, -20, 50 },
			{ -20, 30, 1, 1, 1, 1, 30, -20 }, { 10, 1, 1, 1, 1, 1, 1, 10 }, { 5, 1, 1, 1, 1, 1, 1, 5 },
			{ 5, 1, 1, 1, 1, 1, 1, 5 }, { 10, 1, 1, 1, 1, 1, 1, 10 }, { -20, 30, 1, 1, 1, 1, 30, -20 },
			{ 50, -20, 10, 5, 5, 10, -20, 50 } };
	private final Board board;
	private int ax;
	private int ay;

	/**
	 * @param board
	 *            The board on which turns are supposed to be calculated
	 */
	public AI(Board board) {
		this.board = board;
	}

	/**
	 * Calculates the best possible move based on the valueArray inputs. Places a
	 * disk on every possible field and calculates the value of that move. Stores
	 * the value of all moves in a list, then sorts the list so the maximum value
	 * can be extracted. Knowing what the maximum is, the AI, again, places a disk
	 * on every possible field. When it makes a move as valuable as the
	 * maximum-value move stored in the list earlier on, remembers the coordinates
	 * of it. Finally places a disk where the best encountered value was, or if
	 * there hasn't been found a valuable spot, places a disk randomly. Clears the
	 * list and resets the arrays where all possible flips were stored.
	 */
	public void bestMove() {
		final int whiteDisksBefore = board.getNumberOfWhiteDisks();
		int f = 0; // Don't be scared by this!
		int x = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;
		int x5 = 0;
		int x6 = 0;
		int x7 = 0;
		int x8 = 0;
		int x9 = 0;
		int max = 0;
		board.copyCurrent(); /*
								 * Important so none of the testing affects the real game.
								 */
		final boolean noBestMove = false; /*
											 * Will be set to true if no valuable move is found.
											 */
		// Iterates through the 2D array.
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.testPlace(i, j);
				final int[] xArray = board.getFlipRow();
				final int[] yArray = board.getFlipColumn();
				/*
				 * If there were any flips in this test move, they will be detected here. Using
				 * a loop would have been much more elegant, but for some reason it always
				 * returned errors. The amount of if functions determines the amount of flips
				 * the AI will maximally look for. There are only 10 so the corrector's eyes are
				 * not hurt too much. The theoretical maximum is 18, but 10 is more than enough
				 * to make very good moves and will almost never be reached in a normal game and
				 * if it is, it would still be the chosen move in 99% of cases.
				 */
				if ((xArray[f] != 0) && (yArray[f] != 0)) {
					if ((xArray[f + 1] != 0) && (yArray[f + 1] != 0)) {
						x = valueArray[yArray[f + 1]][xArray[f + 1]];
						if ((xArray[f + 2] != 0) && (yArray[f + 2] != 0)) {
							x2 = valueArray[yArray[f + 2]][xArray[f + 2]];
							if ((xArray[f + 3] != 0) && (yArray[f + 3] != 0)) {
								x3 = valueArray[yArray[f + 3]][xArray[f + 3]];
								if ((xArray[f + 4] != 0) && (yArray[f + 4] != 0)) {
									x4 = valueArray[yArray[f + 4]][xArray[f + 4]];
									if ((xArray[f + 5] != 0) && (yArray[f + 5] != 0)) {
										x5 = valueArray[yArray[f + 5]][xArray[f + 5]];
										if ((xArray[f + 6] != 0) && (yArray[f + 6] != 0)) {
											x6 = valueArray[yArray[f + 6]][xArray[f + 6]];
											if ((xArray[f + 7] != 0) && (yArray[f + 7] != 0)) {
												x7 = valueArray[yArray[f + 7]][xArray[f + 7]];
												if ((xArray[f + 8] != 0) && (yArray[f + 8] != 0)) {
													x8 = valueArray[yArray[f + 8]][xArray[f + 8]];
													if ((xArray[f + 9] != 0) && (yArray[f + 9] != 0)) {
														x9 = valueArray[yArray[f + 9]][xArray[f + 9]];
														f++;
													}
													f++;
												}
												f++;
											}
											f++;
										}
										f++;
									}
									f++;
								}
								f++;
							}
							f++;
						}
						f++;
					}
					list1.add((x + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + valueArray[xArray[f]][yArray[f]]
							+ valueArray[i][j]));
					/*
					 * The value of all found flips for each placed disk is added to a list.
					 */
					x = 0;
					x2 = 0;
					x3 = 0;
					x4 = 0;
					x5 = 0;
					x6 = 0;
					x7 = 0;
					x8 = 0;
					x9 = 0;
					f++;
				}
			}
		}
		list1.sort(null); // The list is sorted numerically.
		if (list1.size() > 0) {
			max = (list1.get(list1.size() - 1)); /*
													 * max is the highest value reachable.
													 */
		}
		f = 0;
		x = 0;
		x2 = 0;
		x3 = 0;
		x4 = 0;
		x5 = 0;
		x6 = 0;
		x7 = 0;
		x8 = 0;
		x9 = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.testPlace(i, j);
				final int[] xArray = board.getFlipRow();
				final int[] yArray = board.getFlipColumn();
				// Same here for the loop.
				if ((xArray[f] != 0) && (yArray[f] != 0)) {
					if ((xArray[f + 1] != 0) && (yArray[f + 1] != 0)) {
						x = valueArray[yArray[f + 1]][xArray[f + 1]];
						if ((xArray[f + 2] != 0) && (yArray[f + 2] != 0)) {
							x2 = valueArray[yArray[f + 2]][xArray[f + 2]];
							if ((xArray[f + 3] != 0) && (yArray[f + 3] != 0)) {
								x3 = valueArray[yArray[f + 3]][xArray[f + 3]];
								if ((xArray[f + 4] != 0) && (yArray[f + 4] != 0)) {
									x4 = valueArray[yArray[f + 4]][xArray[f + 4]];
									if ((xArray[f + 5] != 0) && (yArray[f + 5] != 0)) {
										x5 = valueArray[yArray[f + 5]][xArray[f + 5]];
										if ((xArray[f + 6] != 0) && (yArray[f + 6] != 0)) {
											x6 = valueArray[yArray[f + 6]][xArray[f + 6]];
											if ((xArray[f + 7] != 0) && (yArray[f + 7] != 0)) {
												x7 = valueArray[yArray[f + 7]][xArray[f + 7]];
												if ((xArray[f + 8] != 0) && (yArray[f + 8] != 0)) {
													x8 = valueArray[yArray[f + 8]][xArray[f + 8]];
													if ((xArray[f + 9] != 0) && (yArray[f + 9] != 0)) {
														x9 = valueArray[yArray[f + 9]][xArray[f + 9]];
														f++;
													}
													f++;
												}
												f++;
											}
											f++;
										}
										f++;
									}
									f++;
								}
								f++;
							}
							f++;
						}
						f++;
					}
					if (max == ((x + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + valueArray[xArray[f]][yArray[f]]
							+ valueArray[i][j]))) {
						/*
						 * If the disk placement which produced the highest-value flips is found, its
						 * coordinates are stored in ax and ay
						 */
						ax = i;
						ay = j;
					}
					x = 0;
					x2 = 0;
					x3 = 0;
					x4 = 0;
					x5 = 0;
					x6 = 0;
					x7 = 0;
					x8 = 0;
					x9 = 0;
					f++;
				}
			}
		}
		board.setTest(false);
		; /*
			 * This last placement will happen on the real board
			 */
		if (noBestMove) {
			board.placeRandom();
		} else {
			board.placeDisk(ax, ay);
		}
		if (whiteDisksBefore == board.getNumberOfWhiteDisks()) {
			board.placeRandom();
			// If for some reason no disk was placed, we do it here.
		}
		board.resetFlipArrays();
		list1.clear();
		board.setTurn(-1);
	}
}