package com.rs.link;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
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
	private ProgressBar progress;
	private MyDialog dialog;
	private ImageView clock;
	private TextView textRefreshNum;
	private TextView textTipNum;
	private TextView gameState;
	private TextView continue_to;
	private TextView explaination;
	private TextView timeUsed;
	private ImageView example;

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

	private Animation scaleOut;
	private Animation transIn;
	private Animation scale;
	private Animation bounce_in;
	private Animation slideUp;

	private AdView adView;
	private String interstitialID;
	private InterstitialAd mInterstitial;
	private AdRequest adRequest;
	private boolean isAdShowed= false;
	// private Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 0:
	// dialog = new MyDialog(WelActivity.this, gameView,
	// "Congratulations��", gameView.getTotalTime()
	// - progress.getProgress());
	// dialog.show();
	// break;
	// case 1:
	// dialog = new MyDialog(WelActivity.this, gameView, "Game Over��",
	// gameView.getTotalTime() - progress.getProgress());
	// dialog.show();
	// break;
	//
	// case 2:
	// dialog = new MyDialog(WelActivity.this, gameView, "Paused��",
	// gameView.getTotalTime() - progress.getProgress());
	// dialog.show();
	// break;
	// }
	// }
	// };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up tracker
		((MyApplication) getApplication())
				.getTracker(MyApplication.TrackerName.APP_TRACKER);

		setContentView(R.layout.welcome);
		// Show ad
		adView = (AdView) findViewById(R.id.ad);
		if (adView != null) {
			AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("5E4CA696BEB736E734DD974DD296F11A").build();
			adView.loadAd(adRequest);
		}

		interstitialID = getResources().getString(R.string.interstitialID);
		mInterstitial = new InterstitialAd(this);
		mInterstitial.setAdUnitId(interstitialID);
		adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("5E4CA696BEB736E734DD974DD296F11A").build();
		mInterstitial.loadAd(adRequest);

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
		progress = (ProgressBar) findViewById(R.id.timer);
		textRefreshNum = (TextView) findViewById(R.id.text_refresh_num);
		textTipNum = (TextView) findViewById(R.id.text_tip_num);
		gameState = (TextView) findViewById(R.id.game_state);
		continue_to = (TextView) findViewById(R.id.continue_to);
		explaination = (TextView) findViewById(R.id.explaination);
		timeUsed = (TextView) findViewById(R.id.time_used);
		example = (ImageView) findViewById(R.id.example);

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

		// ��������豸���豸����
		soundManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		musicVolumn = soundManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		// Animation Effects
		scaleOut = AnimationUtils.loadAnimation(this, R.anim.scale_anim_out);
		transIn = AnimationUtils.loadAnimation(this, R.anim.trans_in);
		scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		bounce_in = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
		slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

		imgTitle.startAnimation(scale);
		btnPlay.startAnimation(scale);
		btnHelp.startAnimation(scale);
		btnRate.startAnimation(scale);
		btnSound.startAnimation(bounce_in);

		SharedPreferences sp = this.getSharedPreferences("settings", 0);
		spEditor = sp.edit();
		hasSound = sp.getBoolean("sound", true);

		player = MediaPlayer.create(this, R.raw.bg);
		player.setLooping(true);// ����ѭ������
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
			showExample();
			break;

		case R.id.rate_btn:
			openRateIntent();
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

	private void showExample() {

		gameState.setText(WelActivity.this.getResources().getString(
				R.string.instruction));
		continue_to.setText(WelActivity.this.getResources().getString(
				R.string.back));

		toggleHomeBtns();
		stateLayout.setVisibility(View.VISIBLE);
		example.setVisibility(View.VISIBLE);
		explaination.setVisibility(View.VISIBLE);
		stateLayout.startAnimation(bounce_in);
		soundLayout.startAnimation(slideUp);
		soundLayout.setVisibility(View.GONE);
		currentState = GameView.HOME;

	}

	private void openRateIntent() {
		String packageName = "com.rs.link";
		Intent rateIntent = new Intent(Intent.ACTION_VIEW);
		// Try Google play
		rateIntent.setData(Uri.parse("market://details?id=" + packageName));
		if (tryStartActivity(rateIntent) == false) {
			// Market (Google play) app seems not installed, let's try to open a
			// webbrowser
			rateIntent.setData(Uri
					.parse("https://play.google.com/store/apps/details?id="
							+ packageName));
			if (tryStartActivity(rateIntent) == false) {
				// Well if this also fails, we have run out of options,inform
				// the user.
				Toast.makeText(
						this,
						"Could not open Google Play, please install Google Play.",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private boolean tryStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	public void doStateLayout() {
		stateLayout.setVisibility(View.GONE);
		if (currentState == GameView.WIN) {
			progress.setMax(gameView.getTotalTime() - 10);
			gameView.startNextPlay();
		} else if (currentState == GameView.LOSE) {
			gameView.startPlay();
		} else if (currentState == GameView.PAUSE) {
			resumeGame();
		} else if (currentState == GameView.HOME) {
			stateLayout.startAnimation(slideUp);
			stateLayout.setVisibility(View.GONE);
			example.setVisibility(View.GONE);
			explaination.setVisibility(View.GONE);
			toggleHomeBtns();
		}

	}

	private void toggleHomeBtns() {
		if (btnPlay.getVisibility() == View.VISIBLE) {
			btnPlay.startAnimation(scaleOut);
			btnHelp.startAnimation(scaleOut);
			btnRate.startAnimation(scaleOut);
			imgTitle.startAnimation(scaleOut);

			btnPlay.setVisibility(View.GONE);
			btnHelp.setVisibility(View.GONE);
			btnRate.setVisibility(View.GONE);
			imgTitle.setVisibility(View.GONE);

		} else {
			btnPlay.setVisibility(View.VISIBLE);
			btnHelp.setVisibility(View.VISIBLE);
			btnRate.setVisibility(View.VISIBLE);
			soundLayout.setVisibility(View.VISIBLE);
			imgTitle.setVisibility(View.VISIBLE);

			btnPlay.startAnimation(scale);
			btnHelp.startAnimation(scale);
			btnRate.startAnimation(scale);
			soundLayout.startAnimation(bounce_in);
			imgTitle.startAnimation(scale);
		}
	}



	public void playClicked() {

		toggleHomeBtns();

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

		toggleHomeBtns();

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
			if (musicVolumn == 0) {
				musicVolumn = 3;
			}
			soundManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					musicVolumn, 0);
			btnSound.setBackgroundResource(R.drawable.sound_fixed47);
		}

		spEditor.commit();
	}

	@Override
	public void onTimer(int leftTime) {
		// Log.i("onTimer", leftTime + "");
		progress.setProgress(leftTime);
	}

	

	public void resumeGame() {
		// stateLayout.startAnimation(slideUp);
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

	private int numOfBackPressed = 0;

	@Override
	public void onBackPressed() {

		// if (stateLayout.getVisibility() == View.GONE && numOfBackPressed ==
		// 0) {
		// // show ad
		// if (mInterstitial != null)
		// if (mInterstitial.isLoaded()) {
		// mInterstitial.show();
		// }
		// numOfBackPressed++;
		// return;
		// }

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

	@Override
	public void onStart() {
		super.onStart();
		// Get an Analytics tracker to report app starts & uncaught exceptions
		// etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);

	}

	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		if(!isAdShowed){
			gameView.setMode(GameView.PAUSE);
		}else{
			isAdShowed = false;
		}
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (adView != null) {
			adView.resume();
		}
		if (currentView == 0) {
			player.start();
		}

	}

	@Override
	protected void onDestroy() {
		// Destroy ads when the view is destroyed
		if (adView != null) {
			adView.destroy();
		}

		soundManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolumn, 0);
		gameView.setMode(GameView.QUIT);
		super.onDestroy();

	}
	
	private void showInterstitial(){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mInterstitial != null) {
					if (mInterstitial.isLoaded()) {
						mInterstitial.show();
					}
					mInterstitial.loadAd(adRequest);
				}
				isAdShowed = true;
			}
		});
	}
	
	@Override
	public void OnStateChanged(int StateMode) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				timeUsed.setVisibility(View.GONE);

				// Log.i("debug", "time used: "
				// + (gameView.getTotalTime() - progress.getProgress()));
				// Show time spent in the game
				timeUsed.setText("Time used: "
						+ (gameView.getTotalTime() - progress.getProgress())
						+ " seconds");
			}
		});

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
					timeUsed.setVisibility(View.VISIBLE);
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
					timeUsed.setVisibility(View.VISIBLE);
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
				gameState.setText(WelActivity.this.getResources().getString(
						R.string.pause));
				continue_to.setText(WelActivity.this.getResources().getString(
						R.string.go));
				stateLayout.setVisibility(View.VISIBLE);
				// stateLayout.startAnimation(bounce_in);
			}
			break;
		case GameView.QUIT:
			player.release();
			gameView.player.release();
			gameView.stopTimer();
			break;
		}
		currentState = StateMode;

//		if (currentState != GameView.PAUSE) {
			showInterstitial();

//		}
	}
	
	public void pauseClicked() {
		gameView.setMode(GameView.PAUSE);
//		showInterstitial();
	}

}