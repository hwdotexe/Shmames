package com.hadenwatne.shmames.models.game;

import com.hadenwatne.shmames.services.RandomService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class is spaghetti.
public class MiniCactpotGame {
	public static String BuildNewGame() {
		// 1/20 = 0.05 = 5% chance of winning
		boolean isWinnerFirstPrize = RandomService.GetRandom(20) == 0;
		boolean isWinnerSecondPrize = !isWinnerFirstPrize && RandomService.GetRandom(20) == 0;
		List<Integer> numberPool = new ArrayList<>();

		// Populate the number pool.
		for(int i=1; i<10; i++) {
			numberPool.add(i);
		}

		// If the ticket is a winner, organize the numbers in the grid to line up properly.
		if(isWinnerFirstPrize || isWinnerSecondPrize) {
			// For second prize, the winning numbers are 7,8,9
			if(isWinnerSecondPrize) {
				Collections.reverse(numberPool);
			}

			// Scramble the first 3
			int idxA = RandomService.GetRandom(3);
			int idxB = RandomService.GetRandom(3);

			int idxA_real = numberPool.get(idxA);
			int idxB_real = numberPool.get(idxB);

			numberPool.set(idxA, idxB_real);
			numberPool.set(idxB, idxA_real);

			// Remove the first 3
			int poolIdx0 = numberPool.get(0);
			int poolIdx1 = numberPool.get(1);
			int poolIdx2 = numberPool.get(2);

			numberPool.remove(0);
			numberPool.remove(0);
			numberPool.remove(0);

			// Scramble the remaining 6 numbers
			Collections.shuffle(numberPool);

			// Determine the winning pattern.
			int[] winningIndexes = generateWinningPattern();

			// Place the winning numbers in the winning indexes.
			numberPool.add(winningIndexes[0], poolIdx0);
			numberPool.add(winningIndexes[1], poolIdx1);
			numberPool.add(winningIndexes[2], poolIdx2);

			// Run a function to remove extra wins.
			ensureNonWin(numberPool, isWinnerFirstPrize, isWinnerSecondPrize, winningIndexes);
		} else {
			Collections.shuffle(numberPool);

			// Run a function to remove extra wins.
			ensureNonWin(numberPool, false, false, new int[] {});
		}

		// Determine the number that gets revealed for free.
		int revealedIdx = RandomService.GetRandom(9);
		
		// Convert it to a string and send it back.
		StringBuilder cp = new StringBuilder();

		int rowCount = 0;
		for(int i=0; i<numberPool.size(); i++) {
			if(i != revealedIdx) {
				cp.append("||");
			}

			cp.append(mapNumberToCactpotEmoji(numberPool.get(i)));

			if(i != revealedIdx) {
				cp.append("||");
			}

			rowCount++;

			if(rowCount == 3) {
				cp.append(System.lineSeparator());
				rowCount = 0;
			} else {
				cp.append("-");
			}
		}

		// Send it off!
		return cp.toString();
	}

	private static int[] generateWinningPattern() {
		int initialOffset = 0;

		switch(RandomService.GetRandom(3)){
			case 0: // 0 spaces apart (idx 0,1,2)
				// figure out if initial offset is 0, 3, or 6 indexes
				switch(RandomService.GetRandom(3)){
					case 0: // 0
						break;
					case 1: // 4
						initialOffset = 3;
						break;
					case 2: // 7
						initialOffset = 6;
						break;
				}

				return new int[] { initialOffset, initialOffset+1, initialOffset+2 };
			case 1: // 3 spaces apart (idx 0,3,6)
				// figure out if initial offset is 0, 1, or 2
				initialOffset = RandomService.GetRandom(3);

				return new int[] { initialOffset, initialOffset+3, initialOffset+6 };
			case 2: // 4 spaces apart (idx 0,4,8 or idx 2,4,6)
				// figure out if initial offset is 0 & 4 or 2 & 2
				switch(RandomService.GetRandom(2)){
					case 0: // 0 & 4
						return new int[] { 0,4,8 };
					case 1: // 2 & 2
						return new int[] { 2,4,6 };
				}

				break;
		}

		return new int[]{};
	}

	private static void ensureNonWin(List<Integer> numberPool, boolean isWinnerFirstPrize, boolean isWinnerSecondPrize, int[] winningIndexes) {
		// Ensure that the ticket is not a winner per each prize.
		List<Integer> indexesToCheck = new ArrayList<>();

		for(int i=0; i<9; i++) {
			if(!arrayContains(winningIndexes, i)) {
				indexesToCheck.add(i);
			}
		}

		if(isWinnerSecondPrize) {
			// Check that 1,2,3 don't line up at all.
			for(int indexToCheck : indexesToCheck) {
				int[] accidentalWins = detectWinningPattern(numberPool, indexToCheck, true);

				if(accidentalWins.length > 0) {
					if(accidentalWins[0] <= 3) {
						// Swap the lowest index with another number 5 spaces higher
						int swapA = numberPool.get(accidentalWins[0]);
						int swapB = numberPool.get(accidentalWins[0] + 5);

						numberPool.set(accidentalWins[0], swapB);
						numberPool.set(accidentalWins[0] + 5, swapA);
					} else if (accidentalWins[2] >= 5){
						// Swap the highest index with another number 5 spaces lower
						int swapA = numberPool.get(accidentalWins[2]);
						int swapB = numberPool.get(accidentalWins[2] - 5);

						numberPool.set(accidentalWins[2], swapB);
						numberPool.set(accidentalWins[2] - 5, swapA);
					}

					break;
				}
			}
		} else {
			// We want to check both.
			for(int indexToCheck : indexesToCheck) {
				int[] accidentalWinsFirstPrize = detectWinningPattern(numberPool, indexToCheck, true);
				int[] accidentalWinsSecondPrize = detectWinningPattern(numberPool, indexToCheck, false);

				if(accidentalWinsFirstPrize.length > 0) {
					if(accidentalWinsFirstPrize[0] <= 3) {
						// Swap the lowest index with another number 5 spaces higher
						int swapA = numberPool.get(accidentalWinsFirstPrize[0]);
						int swapB = numberPool.get(accidentalWinsFirstPrize[0] + 5);

						numberPool.set(accidentalWinsFirstPrize[0], swapB);
						numberPool.set(accidentalWinsFirstPrize[0] + 5, swapA);
					} else if (accidentalWinsFirstPrize[2] >= 5){
						// Swap the highest index with another number 5 spaces lower
						int swapA = numberPool.get(accidentalWinsFirstPrize[2]);
						int swapB = numberPool.get(accidentalWinsFirstPrize[2] - 5);

						numberPool.set(accidentalWinsFirstPrize[2], swapB);
						numberPool.set(accidentalWinsFirstPrize[2] - 5, swapA);
					}

					break;
				}

				if(accidentalWinsSecondPrize.length > 0) {
					if(accidentalWinsSecondPrize[0] <= 3) {
						// Swap the lowest index with another number 5 spaces higher
						int swapA = numberPool.get(accidentalWinsSecondPrize[0]);
						int swapB = numberPool.get(accidentalWinsSecondPrize[0] + 5);

						numberPool.set(accidentalWinsSecondPrize[0], swapB);
						numberPool.set(accidentalWinsSecondPrize[0] + 5, swapA);
					} else if (accidentalWinsSecondPrize[2] >= 5){
						// Swap the highest index with another number 5 spaces lower
						int swapA = numberPool.get(accidentalWinsSecondPrize[2]);
						int swapB = numberPool.get(accidentalWinsSecondPrize[2] - 5);

						numberPool.set(accidentalWinsSecondPrize[2], swapB);
						numberPool.set(accidentalWinsSecondPrize[2] - 5, swapA);
					}

					break;
				}
			}
		}
	}

	private static int[] detectWinningPattern(List<Integer> numberPool, int startIndex, boolean checkFirstPrize) {
		List<int[]> winningPatterns = new ArrayList<>();

		switch(startIndex) {
			case 0:
				winningPatterns.add(new int[] {0,1,2});
				winningPatterns.add(new int[] {0,3,6});
				winningPatterns.add(new int[] {0,4,8});

				break;
			case 1:
				winningPatterns.add(new int[] {0,1,2});
				winningPatterns.add(new int[] {1,4,7});

				break;
			case 2:
				winningPatterns.add(new int[] {0,1,2});
				winningPatterns.add(new int[] {2,5,8});
				winningPatterns.add(new int[] {2,4,6});

				break;
			case 3:
				winningPatterns.add(new int[] {0,3,6});
				winningPatterns.add(new int[] {3,4,5});

				break;
			case 4:
				winningPatterns.add(new int[] {0,4,8});
				winningPatterns.add(new int[] {1,4,7});
				winningPatterns.add(new int[] {2,4,6});
				winningPatterns.add(new int[] {3,4,5});

				break;
			case 5:
				winningPatterns.add(new int[] {2,5,8});
				winningPatterns.add(new int[] {3,4,5});

				break;
			case 6:
				winningPatterns.add(new int[] {0,3,6});
				winningPatterns.add(new int[] {2,4,6});
				winningPatterns.add(new int[] {6,7,8});

				break;
			case 7:
				winningPatterns.add(new int[] {1,5,7});
				winningPatterns.add(new int[] {6,7,8});

				break;
			case 8:
				winningPatterns.add(new int[] {0,4,8});
				winningPatterns.add(new int[] {2,5,8});
				winningPatterns.add(new int[] {6,7,8});

				break;
		}

		boolean wins = true;

		for(int[] pattern : winningPatterns) {
			for(int testIndex : pattern) {
				if(checkFirstPrize) {
					if (numberPool.get(testIndex) > 3) {
						wins = false;
						break;
					}
				} else {
					if (numberPool.get(testIndex) < 7) {
						wins = false;
						break;
					}
				}
			}

			if(wins) {
				return pattern;
			}
		}

		return new int[]{};
	}

	private static boolean arrayContains(int[] array, int test) {
		for(int i : array) {
			if(i == test) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Maps an integer to an emoji representation.
	 * @param i The number to map.
	 * @return A Discord emoji String name.
	 */
	private static String mapNumberToCactpotEmoji(int i) {
		switch(i){
		case 1:
			return ":one:";
		case 2:
			return ":two:";
		case 3:
			return ":three:";
		case 4:
			return ":four:";
		case 5:
			return ":five:";
		case 6:
			return ":six:";
		case 7:
			return ":seven:";
		case 8:
			return ":eight:";
		case 9:
			return ":nine:";
		default:
			return ":small_red_triangle:";
		}
	}
}
