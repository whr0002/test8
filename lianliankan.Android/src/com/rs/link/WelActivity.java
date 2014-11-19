package com.rs.link;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rs.link.R;
import com.rs.link.views.GameView;
import com.rs.link.views.OnStateListener;
import com.rs.link.views.OnTimerListener;
import com.rs.link.views.OnToolsChangeListener;

public class WelActivity extends Activity implements OnClickListener,
		OnTimerListener, OnStateListener, OnToolsChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnHelp;
	private ImageButton btnRate;
	
	private ImageButton btnRefresh;
	private ImageButton btnTip;
	private ImageButton btnMenu;
	private ImageButton btnSound;
	private ImageButton btnPause;

	private ImageView imgTitle;
	private GameView gameView;
	private SeekBar progress;
	private MyDialog dialog;
	private ImageView clock;
	private TextView textRefreshNum;
	private TextView textTipNum;
	private TextView gameState;
	private TextView continue_to;

	private RelativeLayout timerLayout;
	private RelativeLayout menuLayout;
	private RelativeLayout pauseLayout;
	private RelativeLayout refreshLayout;
	private RelativeLayout tipLayout;
	private RelativeLayout stateLayout;
	private RelativeLayout soundLayout;

	private MediaPlayer player;
	private boolean hasSound;
	private SharedPreferences sp;
	private SharedPreferences.Editor spEditor;

	private int currentView;
	private AudioManager soundManager;
	private int musicVolumn;

	private int currentState;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				dialog = new MyDialog(WelActivity.this, gameView,
						"Congratulations！", gameView.getTotalTime()
								- progress.getProgress());
				dialog.show();
				break;
			case 1:
				dialog = new MyDialog(WelActivity.this, gameView, "Game Over！",
						gameView.getTotalTime() - progress.getProgress());
				dialog.show();
				break;

			case 2:
				dialog = new MyDialog(WelActivity.this, gameView, "Paused！",
						gameView.getTotalTime() - progress.getProgress());
				dialog.show();
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		btnPlay = (ImageButton) findViewById(R.id.play_btn);
		btnHelp = (ImageButton) findViewById(R.id.help_btn);
		btnRate = (ImageButton) findViewById(R.id.rate_btn);
		btnRefresh = (ImageButton) findViewById(R.id.refresh_btn);
		btnTip = (ImageButton) findViewById(R.id.tip_btn);
		btnMenu = (ImageButton) findViewById(R.id.menu_btn);
		btnPause = (ImageButton) findViewById(R.id.pause_btn);
		btnSound = (ImageButton) findViewById(R.id.sound_btn);

		imgTitle = (ImageView) findViewById(R.id.title_img);
		gameView = (GameView) findViewById(R.id.game_view);
		clock = (ImageView) findViewById(R.id.clock);
		progress = (SeekBar) findViewById(R.id.timer);
		textRefreshNum = (TextView) findViewById(R.id.text_refresh_num);
		textTipNum = (TextView) findViewById(R.id.text_tip_num);
		gameState = (TextView) findViewById(R.id.game_state);
		continue_to = (TextView) findViewById(R.id.continue_to);

		timerLayout = (RelativeLayout) findViewById(R.id.timer_layout);
		menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		pauseLayout = (RelativeLayout) findViewById(R.id.pause_layout);
		refreshLayout = (RelativeLayout) findViewById(R.id.refresh_layout);
		tipLayout = (RelativeLayout) findViewById(R.id.tip_layout);
		soundLayout = (RelativeLayout) findViewById(R.id.sound_layout);
		stateLayout = (RelativeLayout) findViewById(R.id.state_layout);
		// XXX
		progress.setMax(gameView.getTotalTime());

		btnPlay.setOnClickListener(this);
		btnHelp.setOnClickListener(this);
		btnRate.setOnClickListener(this);
		
		btnRefresh.setOnClickListener(this);
		btnTip.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		btnPause.setOnClickListener(this);
		btnSound.setOnClickListener(this);

		menuLayout.setOnClickListener(this);
		pauseLayout.setOnClickListener(this);
		refreshLayout.setOnClickListener(this);
		tipLayout.setOnClickListener(this);
		soundLayout.setOnClickListener(this);
		stateLayout.setOnClickListener(this);

		gameView.setOnTimerListener(this);
		gameView.setOnStateListener(this);
		gameView.setOnToolsChangedListener(this);
		GameView.initSound(this);

		// 获得声音设备和设备音量
		soundManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		musicVolumn = soundManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		Animation bounce_in = AnimationUtils.loadAnimation(this,
				R.anim.bounce_in);
		imgTitle.startAnimation(scale);
		btnPlay.startAnimation(scale);
		btnHelp.startAnimation(scale);
		btnRate.startAnimation(scale);
		btnSound.startAnimation(bounce_in);

		SharedPreferences sp = this.getSharedPreferences("settings", 0);
		spEditor = sp.edit();
		hasSound = sp.getBoolean("sound", true);

		player = MediaPlayer.create(this, R.raw.bg);
		player.setLooping(true);// 设置循环播放
		if (hasSound)
			player.start();
		else
			btnSound.setBackgroundResource(R.drawable.no_sound_fixed47);

		currentView = 0;
		currentState = -1;
		// GameView.soundPlay.play(GameView.ID_SOUND_BACK2BG, -1);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.play_btn:
			playClicked();
			break;
			
		case R.id.help_btn:
			// Show instruction
			break;
			
		case R.id.rate_btn:

			break;

		case R.id.refresh_btn:
			refreshClicked();
			break;

		case R.id.refresh_layout:
			refreshClicked();
			break;

		case R.id.tip_btn:
			tipClicked();
			break;

		case R.id.tip_layout:
			tipClicked();
			break;

		case R.id.menu_btn:
			showHomeView();
			break;

		case R.id.menu_layout:
			showHomeView();
			break;

		case R.id.sound_btn:
			toggleSound();
			break;

		case R.id.sound_layout:
			toggleSound();
			break;

		case R.id.pause_btn:
			// Pause, show dialog
			pauseClicked();
			break;

		case R.id.pause_layout:
			// Pause, show dialog
			pauseClicked();
			break;

		case R.id.state_layout:
			doStateLayout();
			break;
		}

	}

	public void doStateLayout() {
		stateLayout.setVisibility(View.GONE);
		if (currentState == GameView.WIN) {
			gameView.startNextPlay();	
		} else if (currentState == GameView.LOSE) {
			gameView.startPlay();
		} else if (currentState == GameView.PAUSE) {
			resumeGame();
		}

	}

	public void pauseClicked() {
		gameView.setMode(GameView.PAUSE);
	}

	public void playClicked() {
		Animation scaleOut = AnimationUtils.loadAnimation(this,
				R.anim.scale_anim_out);
		Animation transIn = AnimationUtils.loadAnimation(this, R.anim.trans_in);
		Animation bounce_in = AnimationUtils.loadAnimation(this,
				R.anim.bounce_in);
		btnPlay.startAnimation(scaleOut);
		btnHelp.startAnimation(scaleOut);
		btnRate.startAnimation(scaleOut);
		
		btnPlay.setVisibility(View.GONE);
		btnHelp.setVisibility(View.GONE);
		btnRate.setVisibility(View.GONE);
		
		imgTitle.setVisibility(View.GONE);
		gameView.setVisibility(View.VISIBLE);

		btnRefresh.setVisibility(View.VISIBLE);
		btnTip.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
		clock.setVisibility(View.VISIBLE);
		textRefreshNum.setVisibility(View.VISIBLE);
		textTipNum.setVisibility(View.VISIBLE);

		timerLayout.setVisibility(View.VISIBLE);
		menuLayout.setVisibility(View.VISIBLE);
		pauseLayout.setVisibility(View.VISIBLE);
		refreshLayout.setVisibility(View.VISIBLE);
		tipLayout.setVisibility(View.VISIBLE);

		timerLayout.startAnimation(bounce_in);
		menuLayout.startAnimation(bounce_in);
		pauseLayout.startAnimation(bounce_in);
		refreshLayout.startAnimation(bounce_in);
		tipLayout.startAnimation(bounce_in);

		btnRefresh.startAnimation(bounce_in);
		btnTip.startAnimation(bounce_in);
		gameView.startAnimation(transIn);
		player.pause();
		gameView.startPlay();

		currentView = 1;
	}

	public void refreshClicked() {
		Animation shake01 = AnimationUtils.loadAnimation(this, R.anim.shake);
		Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
		btnRefresh.startAnimation(rotate);
		gameView.refreshChange();
	}

	public void tipClicked() {
		Animation shake02 = AnimationUtils.loadAnimation(this, R.anim.shake);
		btnTip.startAnimation(shake02);
		gameView.autoClear();
	}

	// Change Views
	public void showHomeView() {
		// Show Home View
		Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
		btnPlay.setVisibility(View.VISIBLE);
		btnHelp.setVisibility(View.VISIBLE);
		btnRate.setVisibility(View.VISIBLE);
		
		btnPlay.startAnimation(scale);
		btnHelp.startAnimation(scale);
		btnRate.startAnimation(scale);

		imgTitle.setVisibility(View.VISIBLE);
		imgTitle.startAnimation(scale);

		timerLayout.startAnimation(slideUp);
		menuLayout.startAnimation(slideUp);
		pauseLayout.startAnimation(slideUp);
		refreshLayout.startAnimation(slideUp);
		tipLayout.startAnimation(slideUp);
		
		gameView.setVisibility(View.GONE);
		btnRefresh.setVisibility(View.GONE);
		btnTip.setVisibility(View.GONE);
		progress.setVisibility(View.GONE);
		clock.setVisibility(View.GONE);
		textRefreshNum.setVisibility(View.GONE);
		textTipNum.setVisibility(View.GONE);
		
		timerLayout.setVisibility(View.GONE);
		menuLayout.setVisibility(View.GONE);
		pauseLayout.setVisibility(View.GONE);
		refreshLayout.setVisibility(View.GONE);
		tipLayout.setVisibility(View.GONE);
		

		
		gameView.pausePlayer();
		gameView.stopTimer();

		currentView = 0;
		if (hasSound)
			player.start();
	}

	public void toggleSound() {
		// Home View
		if (hasSound) {
			hasSound = false;
			spEditor.putBoolean("sound", false);
			if (currentView == 0)
				player.pause();
			else if (currentView == 1)
				gameView.pausePlayer();
			musicVolumn = soundManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			soundManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			btnSound.setBackgroundResource(R.drawable.no_sound_fixed47);

		} else {
			hasSound = true;
			spEditor.putBoolean("sound", true);
			if (currentView == 0)
				player.start();
			else if (currentView == 1)
				gameView.startPlayer();
			soundManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					musicVolumn, 0);
			btnSound.setBackgroundResource(R.drawable.sound_fixed47);
		}

		spEditor.commit();
	}

	@Override
	public void onTimer(int leftTime) {
		Log.i("onTimer", leftTime + "");
		progress.setProgress(leftTime);
	}

	@Override
	public void OnStateChanged(int StateMode) {
		switch (StateMode) {
		case GameView.WIN:
			// handler.sendEmptyMessage(0);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					// stuff that updates ui
					gameState.setText(WelActivity.this.getResources()
							.getString(R.string.win));
					continue_to.setText(WelActivity.this.getResources()
							.getString(R.string.go));
					stateLayout.setVisibility(View.VISIBLE);
				}
			});

			break;
		case GameView.LOSE:
			// handler.sendEmptyMessage(1);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					// stuff that updates ui
					gameState.setText(WelActivity.this.getResources()
							.getString(R.string.lose));
					continue_to.setText(WelActivity.this.getResources()
							.getString(R.string.retry));
					stateLayout.setVisibility(View.VISIBLE);
				}
			});

			break;
		case GameView.PAUSE:
			if (currentView == 0) {
				player.pause();
			} else if (currentView == 1) {
				gameView.player.pause();
				gameView.stopTimer();
				gameState.setText(WelActivity.this.getResources()
						.getString(R.string.pause));
				continue_to.setText(WelActivity.this.getResources()
						.getString(R.string.go));
				stateLayout.setVisibility(View.VISIBLE);
			}
			break;
		case GameView.QUIT:
			player.release();
			gameView.player.release();
			gameView.stopTimer();
			break;
		}
		currentState = StateMode;
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameView.setMode(GameView.PAUSE);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (currentView == 0) {
//			player.start();
//		}

	}

	@Override
	protected void onDestroy() {
		soundManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				musicVolumn, 0);
		gameView.setMode(GameView.QUIT);
		super.onDestroy();
		
	}

	public void resumeGame() {
		stateLayout.setVisibility(View.GONE);
		if (currentView == 0) {
			player.start();
		} else if (currentView == 1) {
			gameView.startPlayer();
			gameView.resumeTimer();
		}
	}

	@Override
	public void onRefreshChanged(int count) {
		textRefreshNum.setText("" + gameView.getRefreshNum());
	}

	@Override
	public void onTipChanged(int count) {
		textTipNum.setText("" + gameView.getTipNum());
	}

	public void quit() {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.buttons_bg20)
				.setTitle(R.string.quit)
				.setMessage(R.string.sure_quit)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								quit();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dialog.show();
	}
}