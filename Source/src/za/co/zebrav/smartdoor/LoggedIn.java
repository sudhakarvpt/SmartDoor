package za.co.zebrav.smartdoor;

import java.util.Locale;

import za.co.zebrav.smartdoor.database.User;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoggedIn extends Activity implements OnInitListener
{
	private TextToSpeech tts;
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.logged_in);
		super.onCreate(savedInstanceState);
		
		tts = new TextToSpeech(this, this);
		
		//receive user that logged in.
		Bundle bundle = this.getIntent().getExtras();
		
		user = (User) bundle.getSerializable("user");
	}

	@Override
	/**
	 * ShutDown TextToSpeech when activity is destroyed
	 */
	public void onDestroy()
	{	
		if (tts != null)
		{
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	/**
	 * Sets voice pronunciation 
	 */
	public void onInit(int status)
	{
		if (status == TextToSpeech.SUCCESS)
		{
			int result = tts.setLanguage(Locale.UK);

			if (result == TextToSpeech.LANG_MISSING_DATA|| result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Toast.makeText(this, "Language not supported",Toast.LENGTH_LONG).show();
			} 
			else
			{
				//Only at this time will the tts be ready for speaking
				speakOut("Welcome, " + user.getFirstnames() + " " + user.getSurname());
			}
		}
	}

	/**
	 * @param text, The text to be spoken out loud by the device
	 */
	public void speakOut(String text)
	{
		if (text.length() == 0)
		{
			tts.speak("You haven't typed text", TextToSpeech.QUEUE_FLUSH, null);
		} else
		{
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
}
