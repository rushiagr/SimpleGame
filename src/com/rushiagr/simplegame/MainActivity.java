package com.rushiagr.simplegame;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	// Some constants
	public final int UNSELECT_COLOR = 0xFFCCFFFF;
	public final int SELECT_COLOR = 0xFFFF0000;
	public final int TAP_CORRECT_COLOR = 0xFF00FF33;
	public final int TAP_INCORRECT_COLOR = 0xFF000000;
	public final int INITIAL_COUNTDOWN_SECONDS = 5;
	
	public Game game;

	// Display consists of one status strip on top, and
	// blocks arranged in 'row' rows and 'col' columns
	public class Game {
		// Keeps track of IDs of previously and currently selected TextViews
		public TextView statusBar = (TextView) findViewById(R.id.status);
		public int prevId, currId;
		public Random generator;
		public boolean continueGame = true;
		public int[][] grid;
		public double score = 0;
		public Thread thr;
		public int currentIteration;
		public int lastCorrectTap;
		public int tick;
		public int row, col;

		// Constructor
		public Game(int row, int col) {
			this.row = row;
			this.col = col;
			grid = new int[row][col];
			generator = new Random();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					grid[i][j] = getResources().getIdentifier(
							"R.id.grid" + row + col, null, null);
				}
			}
			currId = generator.nextInt(row + col);
			selectNextBlock();
		}

		// Select new block
		public void selectNextBlock() {
			prevId = currId;
			currId = generator.nextInt(row + col);
			while (currId == prevId)
				currId = generator.nextInt(row + col);
		}

		public void setBlockColor(int blockId, int color) {
			findViewById(blockId).setBackgroundColor(color);
		}
		
		// Handles what all to do at the time of a tick
		public Runnable tick() {
			Runnable r = new Runnable() {
				public void run() {
					if(continueGame == true) {
						selectNextBlock();
						setBlockColor(currId, SELECT_COLOR);
						setBlockColor(prevId, UNSELECT_COLOR);
					} else {
						statusBar.setText("wrong!");
					}
				}
			};
			return r;
		}
		
		// Handles what to do when one taps on block whose ID is viewId
		public void onClickId(int viewId) {
			if(viewId == currId) {
				setBlockColor(currId, TAP_CORRECT_COLOR);
			} else {
				setBlockColor(currId, TAP_INCORRECT_COLOR);
				continueGame = false;
			}
		}

		// The main thread of game execution
		public void startGame() {
			Runnable main = new Runnable() {
				public void run(){
					for (int i = 0; i < 200; i++) {
						handler.post(tick());
						if (continueGame == true) {
							try {
								Thread.sleep(1000 * 75 / (75 + i));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else
							break;
					}
					
				}
			};
			Thread t = new Thread(main);
			t.start();
		}
		
	}

	public static Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		game = new Game(3, 3);
		game.startGame();

	}



	public void onClick(View v) {
		game.onClickId(v.getId());
	}

}
