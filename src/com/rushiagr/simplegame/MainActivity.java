package com.rushiagr.simplegame;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	public TextView status;
	public int prev_id, current_id;
	public Random gen = new Random();
	public boolean continueGame = true;

	public int UNSELECT_COLOR = 0xFFCCFFFF;
	public int SELECT_COLOR = 0xFFFF0000;
	public int TAP_CORRECT_COLOR = 0xFF00FF33;
	public int TAP_INCORRECT_COLOR = 0xFF000000;
	public int[] grid = new int[9];
	public float score = 0;
	public Thread thr;
	public int currentIteration = 4;
	public int lastCorrectTap;
	public int value;

	public static Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		status = (TextView) findViewById(R.id.status);

		startGame();

	}

	public float scoreForValue(int value) {
		if (value < 5)
			return (float) 0.0;
		return (float) (Math.sqrt((value)) * 3 / 10);
	}

	public class MyRunnable implements Runnable {
		public MyRunnable(int tick) {
			this.tick = tick;
		}
		protected int tick;
		public void setTick(int tick) {
			this.tick = tick;
		}
		public void run() {
			if (value < 5) {
				status.setText(getString(R.string.status) + (5 - value));
			} else {
				if (lastCorrectTap != prev_id) {
					status.setText("Oops!! You missed it! Your score: " + score);
					continueGame = false;

				} else {
					score += scoreForValue(value);
					status.setText(getString(R.string.score) + " " + score);
					current_id = grid[gen.nextInt(9)];
					while (current_id == prev_id)
						current_id = grid[gen.nextInt(9)];
					findViewById(prev_id).setBackgroundColor(UNSELECT_COLOR);
					findViewById(current_id).setBackgroundColor(SELECT_COLOR);
					prev_id = current_id;
					currentIteration = value;
				}
			}

		}
	};

	public void startGame() {
		grid[0] = R.id.grid00;
		grid[1] = R.id.grid01;
		grid[2] = R.id.grid02;
		grid[3] = R.id.grid10;
		grid[4] = R.id.grid11;
		grid[5] = R.id.grid12;
		grid[6] = R.id.grid20;
		grid[7] = R.id.grid21;
		grid[8] = R.id.grid22;

		prev_id = grid[gen.nextInt(9)];
		lastCorrectTap = prev_id;

		Runnable r = new Runnable() {
			public void run() {
				for (int i = 0; i < 200; i++) {
					if (continueGame == true) {
						value = i;
						try {
							Thread.sleep(1000 * 75 / (75 + i));
							if (continueGame == false)
								break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						MyRunnable r = new MyRunnable(value);
						handler.post(r);
					} else
						break;
				}
			}
		};
		thr = new Thread(r);
		thr.start();
	}

	public void onClick(View v) {
		if (v.getId() == current_id) {
			if (continueGame == true) {
				v.setBackgroundColor(TAP_CORRECT_COLOR);
				lastCorrectTap = current_id;
			}
		} else {
			for (int i = 0; i < 9; i++) {
				findViewById(grid[i]).setBackgroundColor(TAP_INCORRECT_COLOR);
				status.setText("Game Over!! Your score is " + score + ".");
				continueGame = false;

			}
		}
	}
}
