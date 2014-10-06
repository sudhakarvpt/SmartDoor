package za.co.zebrav.smartdoor;

import za.co.zebrav.smartdoor.database.AddUserActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AddVoiceFragment extends VoiceFragment
{
	private static final String LOG_TAG = "AddVoiceFragment";
	private Button btnDone;
	private Button btnTrain;
	private TextView txtCounter;
	private TrainTask trainTask;
	private int trainCounter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreateView");
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);

		float density = getResources().getDisplayMetrics().density;
		int widthDPS = 300;
		int widthPixels = (int) (widthDPS * density);

		int heightDPS = 40;
		int heightPixels = (int) (heightDPS * density);
		
		LayoutParams params = new LayoutParams(widthPixels, heightPixels);
		params.setMargins(0, heightDPS/6, 0, heightDPS/6);
		params.gravity = Gravity.CENTER;
		
		btnTrain = new Button(activity);
		btnTrain.setText("Train");
		btnTrain.setBackground(getResources().getDrawable(R.drawable.button1));
		btnTrain.setLayoutParams(params);

		btnTrain.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				trainVoice();
			}
		});

		btnDone = new Button(activity);
		btnDone.setText("Done");
		btnDone.setBackground(getResources().getDrawable(R.drawable.button1));
		btnDone.setLayoutParams(params);

		btnDone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (activity.getUser() != null)
				{
					activity.getUser().setCodeBook(voiceAuthenticator.getCodeBook());
				}
				else
				{
					Toast.makeText(activity, "user is NULL", Toast.LENGTH_LONG).show();
				}

				((AddUserActivity) activity).doneStepThreeAddUser();
			}
		});
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, heightDPS/6, 0, heightDPS/6);
		params.gravity = Gravity.CENTER;
		
		txtCounter = new TextView(activity);
		txtCounter.setLayoutParams(params);
		txtCounter.setTextSize(heightPixels/6);

		layout.addView(txtCounter);
		layout.addView(btnTrain);
		layout.addView(btnDone);

		trainCounter = 0;
		return layout;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		trainVoice();
		txtCounter.setText("Voice Trained Counter: " + trainCounter);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		if (trainTask != null)
			trainTask.cancel(true);
	}

	/**
	 * Delete old recording and record a new file as wav
	 * 
	 * @param output
	 */
	private void startRecording()
	{
		processingDialog.show();
		soundLevelDialog.show();

		trainTask = new TrainTask();
		trainTask.execute();
	}

	private class TrainTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			Log.i(LOG_TAG, "Training new Voice");
			voiceAuthenticator.startRecording();
			soundLevelDialog.dismiss();

			if (!voiceAuthenticator.train())
			{
				Toast.makeText(activity, "Error Training voice", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG, "Error with training voice, check if activeFile is set");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			processingDialog.dismiss();
			btnDone.setEnabled(true);
			trainCounter++;
			txtCounter.setText("Voice Trained Counter: " + trainCounter);
			Log.i(LOG_TAG, "Done Training Voice");
		}
	}

	private void trainVoice()
	{
		Log.i(LOG_TAG, "Training Voice");
		startRecording();
	}
}
