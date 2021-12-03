package com.hadenwatne.shmames.models;

import com.hadenwatne.shmames.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// This class is spaghetti.
public class MiniCactpotGame {
	public static String BuildNewGame() {
		// 1/20 = 0.05 = 5% chance of winning
		boolean isWinnerFirstPrize = Utils.getRandom(20) == 0;
		boolean isWinnerSecondPrize = !isWinnerFirstPrize && Utils.getRandom(20) == 0;
		List<Integer> numberPool = new ArrayList<>();

		for(int i=1; i<10; i++) {
			numberPool.add(i);
		}

		if(isWinnerFirstPrize || isWinnerSecondPrize) {
			if(isWinnerSecondPrize) {
				Collections.reverse(numberPool);
			}

			// Scramble the first 3
			int idxA = Utils.getRandom(3);
			int idxB = Utils.getRandom(3);

			int idxA_real = numberPool.get(idxA);
			int idxB_real = numberPool.get(idxB);

			numberPool.set(idxA, idxB_real);
			numberPool.set(idxB, idxA_real);

			// Scramble the rest.
			int poolIdx0 = numberPool.get(0);
			int poolIdx1 = numberPool.get(1);
			int poolIdx2 = numberPool.get(2);

			numberPool.remove(0);
			numberPool.remove(0);
			numberPool.remove(0);

			Collections.shuffle(numberPool);

			int initialOffset = 0;
			switch(Utils.getRandom(3)){
				case 0: // 0 spaces apart (idx 0,1,2)
					// figure out if initial offset is 0, 3, or 6 indexes
					switch(Utils.getRandom(3)){
						case 0: // 0
							initialOffset = 0;
							break;
						case 1: // 4
							initialOffset = 3;
							break;
						case 2: // 7
							initialOffset = 6;
							break;
					}

					// Place numbers according to the pattern and offset.
					numberPool.add(initialOffset, poolIdx0);
					numberPool.add(initialOffset + 1, poolIdx1);
					numberPool.add(initialOffset + 2, poolIdx2);

					break;
				case 1: // 3 spaces apart (idx 0,3,6)
					// figure out if initial offset is 0, 1, or 2
					initialOffset = Utils.getRandom(3);

					// Place numbers according to the pattern and offset.
					numberPool.add(initialOffset, poolIdx0);
					numberPool.add(initialOffset + 3, poolIdx1);
					numberPool.add(initialOffset + 6, poolIdx2);

					break;
				case 2: // 4 spaces apart (idx 0,4,8 or idx 2,4,6)
					// TODO: figure out if initial offset is 0 & 4 or 2 & 2
					switch(Utils.getRandom(2)){
						case 0: // 0 & 4
							// Place numbers according to the pattern and offset.
							numberPool.add(0, poolIdx0);
							numberPool.add(4, poolIdx1);
							numberPool.add(8, poolIdx2);

							break;
						case 1: // 2 & 2
							// Place numbers according to the pattern and offset.
							numberPool.add(2, poolIdx0);
							numberPool.add(4, poolIdx1);
							numberPool.add(6, poolIdx2);
							break;
					}

					break;
			}
		} else {
			Collections.shuffle(numberPool);
			// Can still win by accident
		}

		// Determine the number that gets revealed for free.
		int revealedIdx = Utils.getRandom(10);
		
		// Convert it to a string and send it back.
		StringBuilder cp = new StringBuilder();

		int rowCount = 0;
		for(int i=0; i<numberPool.size(); i++) {
			if(i != revealedIdx) {
				cp.append("||");
			}

			cp.append(mapNumberToMinesweeperEmoji(numberPool.get(i)));

			if(i != revealedIdx) {
				cp.append("||");
			}

			rowCount++;

			if(rowCount == 3) {
				cp.append("\n");
				rowCount = 0;
			}
		}
		
		return cp.toString();
	}

	/**
	 * Maps an integer to an emoji representation.
	 * @param i The number to map.
	 * @return A Discord emoji String name.
	 */
	private static String mapNumberToMinesweeperEmoji(int i) {
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
