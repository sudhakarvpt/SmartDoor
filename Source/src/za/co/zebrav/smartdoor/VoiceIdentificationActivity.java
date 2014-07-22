package za.co.zebrav.smartdoor;

import java.io.File;
import java.io.IOException;

import com.bitsinharmony.recognito.Recognito;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class VoiceIdentificationActivity extends Activity
{

	private static final String LOG_TAG = "VoiceIdentificationActivity";
	private static String mFileName = null;

	private RecordButton mRecordButton = null;
	private MediaRecorder mRecorder = null;

	private PlayButton mPlayButton = null;
	private MediaPlayer mPlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_identification);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		LinearLayout ll = new LinearLayout(this);
		mRecordButton = new RecordButton(this);
		ll.addView(mRecordButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		mPlayButton = new PlayButton(this);
		ll.addView(mPlayButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		setContentView(ll);
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mRecorder != null)
		{
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null)
		{
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void onRecord(boolean start)
	{
		if (start)
		{
			startRecording();
		}
		else
		{
			stopRecording();
		}
	}

	private void onPlay(boolean start)
	{
		if (start)
		{
			startPlaying();
		}
		else
		{
			stopPlaying();
		}
	}

	private void startPlaying()
	{
		mPlayer = new MediaPlayer();
		try
		{
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying()
	{
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording()
	{
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

		try
		{
			mRecorder.prepare();
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording()
	{
		Recognito<String> recognito = new Recognito(44100.0f);
		//TODO
//		recognito.createVoicePrint("TEST", new File(mFileName));
		
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	class RecordButton extends Button
	{
		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener()
		{
			public void onClick(View v)
			{
				onRecord(mStartRecording);
				if (mStartRecording)
				{
					setText("Stop recording");
				}
				else
				{
					setText("Start recording");
				}
				mStartRecording = !mStartRecording;
			}
		};

		public RecordButton(Context ctx)
		{
			super(ctx);
			setText("Start recording");
			setOnClickListener(clicker);
		}
	}

	class PlayButton extends Button
	{
		boolean mStartPlaying = true;

		OnClickListener clicker = new OnClickListener()
		{
			public void onClick(View v)
			{
				onPlay(mStartPlaying);
				if (mStartPlaying)
				{
					setText("Stop playing");
				}
				else
				{
					setText("Start playing");
				}
				mStartPlaying = !mStartPlaying;
			}
		};

		public PlayButton(Context ctx)
		{
			super(ctx);
			setText("Start playing");
			setOnClickListener(clicker);
		}
	}

	public VoiceIdentificationActivity()
	{
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/audiorecordtest.3gp";
	}
}