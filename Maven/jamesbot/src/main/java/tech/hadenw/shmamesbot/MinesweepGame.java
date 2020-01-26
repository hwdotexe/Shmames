package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinesweepGame {
	public static String BuildNewGame(int size) {
		List<int[]> rows = new ArrayList<int[]>();
		Random r = new Random();
		
		for(int i=0; i<size; i++) {
			rows.add(new int[size]);
		}
		
		// Determine where the bombs are.
		int bombs = size + r.nextInt(6);
		
		for(int i=0; i<bombs; i++) {
			// pick a random row and place a bomb at a random spot
			int rrow = r.nextInt(rows.size());
			int[] row = rows.get(rrow);
			
			// Place a bomb in an empty cell on this row
			int thr = 0;
			while(true) {
				int rcell = r.nextInt(size);
				
				if(row[rcell] != -1) {
					row[rcell] = -1;
					break;
				}else {
					thr += 1;
				}
				
				// If the threshold exceeds 5 attempts, change the row.
				if(thr > 5) {
					rrow = r.nextInt(rows.size());
					row = rows.get(rrow);
					thr = 0;
				}
			}
			
			rows.set(rrow, row);
		}
		
		// For each cell that's not a bomb, place a number corresponding to the bombs in each of the 8 directions surrounding
		for(int i=0; i<rows.size(); i++) {
			int[] row = rows.get(i);
			
			for(int c=0; c<row.length; c++) {
				if(row[c] != -1) {
					// Holy Ternary, Batman!
					int nw = i>0 && c>0 ? rows.get(i-1)[c-1] == -1 ? 1 : 0 : 0;
					int n = i>0 ? rows.get(i-1)[c] == -1 ? 1 : 0 : 0;
					int ne = i>0 && c<(size-1) ? rows.get(i-1)[c+1] == -1 ? 1 : 0 : 0;
					int w = c>0 ? rows.get(i)[c-1] == -1 ? 1 : 0 : 0;
					int e = c<(size-1) ? rows.get(i)[c+1] == -1 ? 1 : 0 : 0;
					int sw = i<(size-1) && c>0 ? rows.get(i+1)[c-1] == -1 ? 1 : 0 : 0;
					int s = i<(size-1) ? rows.get(i+1)[c] == -1 ? 1 : 0 : 0;
					int se = i<(size-1) && c<(size-1) ? rows.get(i+1)[c+1] == -1 ? 1 : 0 : 0;
					int sum = nw + n + ne + w + e + sw + s + se;
					
					row[c] = sum;
				}
			}
			
			rows.set(i, row);
		}
		
		// Convert it to a string and send it back.
		StringBuilder ms = new StringBuilder();
		
		for(int[] row : rows) {
			for(int c : row) {
				ms.append("||"+numberToEmoji(c)+"||");
			}
			
			ms.append("\n");
		}
		
		return ms.toString();
	}
	
	private static String numberToEmoji(int i) {
		switch(i){
		case -1:
			return ":bomb:";
		case 0:
			return ":white_small_square:";
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
