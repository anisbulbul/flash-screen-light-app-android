package com.anrosoft.flashlightpro;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.anrosoft.flashlightpro.R.drawable;

public class FlashLightActivity extends Activity {
	private boolean isFlashOn = false;
	private boolean isFlashOnTimerCheck = false;
	private boolean isSosOn = false;
	private Camera camera;
	private Button button;
	private Button buttonSos;
	private ImageButton buttonMenu;
	private Handler sosTimerHandler = new Handler();
	private Handler flashTimerHandler = new Handler();

	private static final String YOUTUBE_CHANNEL = "UCqZzw1k0zMyGMBIHTjslMyw";
	private static final String YOUTUBE_ID = "2AERMXfPJJc";
	private static final String WEBSITE_APPS = "https://apps.anrosoft.com";
	private static final String PLAYSTORE_DEVELOPER = "https://play.google.com/store/apps/developer?id=AnroSoft";
	private static final String DEVELOPER_MAIL = "anrosoft.office@gmail.com";
	private static final String APP_NAME = "Game Wallpaper";
	private static final String directoryName = "flash_light_pro";
	private static final String fileNameSetting = "flash_light_pro_setting_64523";

	private final static File weatherInfoDirectory = new File(Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ directoryName + "/");
	private final static File weatherSettingInfoFile = new File(
			weatherInfoDirectory, fileNameSetting + ".txt");

	private static final String soundOnOffOptionText[] = { "Sound On",
			"Sound Off" };
	private int currentSoundOnOffIndex = 0;
	private static final String flashTimerOptionText[] = { "NONE",
			"15 Seconds", "30 Seconds", "1 Minuit", "2 Minuits", "5 Minuits",
			"10 Minuits", "20 Minuits", "30 Minuits" };
	private static float flashTimerOptionValues[] = { 24 * 60 * 60, 15, 30, 60,
			120, 300, 600, 1200, 1800 };
	private int currentFlashTimerIndex = 0;
	private static final String _SOSTimerOptionText[] = { "Default",
			"0.25 Seconds", "0.50 Seconds", "1.00 Seconds", "2.00 Seconds",
			"5.00 Seconds" };
	private static float _SOSTimerOptionValues[] = { 0.50f, 0.25f, 0.5f, 1, 2,
			5 };
	private int currentSOSTimerIndex = 0;

	@Override
	protected void onStop() {
		super.onStop();

		if (isFlashOn) {
			flashOff();
		}
		if (isSosOn) {
			sosOff();
		}
		if (camera != null) {
			camera.release();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		button = (Button) findViewById(R.id.buttonFlashlight);
		buttonSos = (Button) findViewById(R.id.buttonSos);
		buttonMenu = (ImageButton) findViewById(R.id.imageButton1);
		PackageManager pm = getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			flashNotSupportedAlert();
			return;
		}

		camera = Camera.open();
		final Parameters p = camera.getParameters();

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (isSosOn) {
					isSosOn = false;
					sosOff();
					flashOn();
				} else {
					if (isFlashOn) {
						flashOff();
					} else {
						flashOn();
						flashTimerHandler
								.postDelayed(updateFlashTimerThread, 0);
					}
				}
			}
		});

		buttonSos.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (isSosOn) {
					isSosOn = false;
					sosOff();
					flashOff();
				} else {
					isSosOn = true;
					sosOn();
				}
			}
		});
		buttonMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				openOptionsMenu();
			}
		});

		sosOff();
		flashOn();
		flashTimerHandler.postDelayed(updateFlashTimerThread, 0);

		initStatus();
	}

	public void seeWebSite(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
				.parse(WEBSITE_APPS));
		startActivity(intent);
	}

	public void flashNotSupportedAlert() {
		// device doesn't support flash
		// Show alert message and close the application
		button.setEnabled(false);
		buttonSos.setEnabled(false);
		AlertDialog alert = new AlertDialog.Builder(FlashLightActivity.this)
				.create();
		alert.setTitle("Error");
		alert.setMessage("Sorry, your device doesn't support flash light!");
		alert.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert.show();
	}

	private Runnable updateFlashTimerThread = new Runnable() {
		public void run() {
			float flashTimerInterval = 1000 * flashTimerOptionValues[currentFlashTimerIndex];
			flashTimerHandler.postDelayed(this, (int) flashTimerInterval);
			if (isFlashOnTimerCheck) {
				isFlashOnTimerCheck = false;
			} else {
				flashOff();
			}
		}
	};

	private Runnable updateSOSTimerThread = new Runnable() {
		public void run() {
			if (isFlashOn) {
				flashOff();
			} else {
				flashOn();
			}
			float sosTimeInterval = 1000 * _SOSTimerOptionValues[currentSOSTimerIndex];
			sosTimerHandler.postDelayed(this, (int) sosTimeInterval);
		}
	};

	private void sosOn() {
		Toast.makeText(getApplicationContext(), "SOS ON", Toast.LENGTH_SHORT)
				.show();
		buttonSos.setText("SOS ON");
		sosTimerHandler.postDelayed(updateSOSTimerThread, 0);
	}

	private void sosOff() {
		Toast.makeText(getApplicationContext(), "SOS OFF", Toast.LENGTH_SHORT)
				.show();
		buttonSos.setText("SOS OFF");
		sosTimerHandler.removeCallbacks(updateSOSTimerThread);
	}

	private void flashOn() {
		Toast.makeText(getApplicationContext(),
				flashTimerOptionValues[currentFlashTimerIndex] + " FLASH ON",
				Toast.LENGTH_SHORT).show();
		final Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(p);
		isFlashOn = true;
		isFlashOnTimerCheck = true;
		button.setText("Torch ON");
	}

	private void flashOff() {
		Toast.makeText(getApplicationContext(), "FLASH OFF", Toast.LENGTH_SHORT)
				.show();
		final Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(p);
		isFlashOn = false;
		button.setText("Torch OFF");
		flashTimerHandler.removeCallbacks(updateFlashTimerThread);
	}

	private boolean isFileExist() {
		try {
			if (!weatherInfoDirectory.exists()) {
				weatherInfoDirectory.mkdir();
				weatherSettingInfoFile.createNewFile();
				return false;
			}
			if (!weatherSettingInfoFile.exists()) {
				weatherSettingInfoFile.createNewFile();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "isFileExist : " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	private void initStatus() {
		if (isFileExist()) {
			String statusText = FileUtils.readFileData(getApplicationContext(),
					weatherSettingInfoFile);
			String[] statusInfo = (statusText.trim()).split("d");
			if (statusInfo.length == 3) {
				currentSoundOnOffIndex = Integer.parseInt(statusInfo[0].trim());
				currentFlashTimerIndex = Integer.parseInt(statusInfo[1].trim());
				currentSOSTimerIndex = Integer.parseInt(statusInfo[2].trim());
			}
		}
	}

	private void saveStatus() {
		isFileExist();
		String statusText = currentSoundOnOffIndex + "d\n";
		statusText += currentFlashTimerIndex + "d\n";
		statusText += currentSOSTimerIndex + "d\n";
		FileUtils.writeFileData(getApplicationContext(),
				weatherSettingInfoFile, statusText.trim());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	// SOUND ON/OFF dialog
	private void displaySoundOnOffChooser() {
		AlertDialog.Builder audioChannel = new AlertDialog.Builder(this);
		audioChannel.setIcon(drawable.ic_launcher); // dialog icon
		audioChannel.setTitle("Sound ON/OFF"); // dialog title
		audioChannel.setSingleChoiceItems(soundOnOffOptionText,
				currentSoundOnOffIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentSoundOnOffIndex = which;
						saveStatus();
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	// FLASH TIMER dialog
	private void displayFlashTimerChooser() {
		AlertDialog.Builder audioChannel = new AlertDialog.Builder(this);
		audioChannel.setIcon(drawable.ic_launcher); // dialog icon
		audioChannel.setTitle("Set Flash Timer"); // dialog title
		audioChannel.setSingleChoiceItems(flashTimerOptionText,
				currentFlashTimerIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentFlashTimerIndex = which;
						saveStatus();
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	// FLASH TIMER dialog
	private void displaySOSTimerChooser() {
		AlertDialog.Builder audioChannel = new AlertDialog.Builder(this);
		audioChannel.setIcon(drawable.ic_launcher); // dialog icon
		audioChannel.setTitle("Set SOS Timer"); // dialog title
		audioChannel.setSingleChoiceItems(_SOSTimerOptionText,
				currentSOSTimerIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentSOSTimerIndex = which;
						saveStatus();
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	public void contactOption() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
					+ DEVELOPER_MAIL)));
		} catch (ActivityNotFoundException e) {

		}
	}

	public void showRated() {

		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id="
							+ getPackageName())));
		} catch (ActivityNotFoundException e) {

		}

	}

	public void showShare() {

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, APP_NAME
				+ " https://play.google.com/store/apps/details?id="
				+ getPackageName());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_sound) {
			displaySoundOnOffChooser();
			return true;
		} else if (id == R.id.action_timer_flash) {
			displayFlashTimerChooser();
			return true;
		} else if (id == R.id.action_timer_sos) {
			displaySOSTimerChooser();
			return true;
		} else if (id == R.id.action_contact) {
			contactOption();
			return true;
		} else if (id == R.id.action_share) {
			showShare();
			return true;
		} else if (id == R.id.action_rate) {
			showRated();
			return true;
		} else if (id == R.id.action_exit) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}