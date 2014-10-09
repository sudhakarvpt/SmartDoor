package za.co.zebrav.smartdoor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import za.co.zebrav.voice.VoiceAuthenticator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{
	private ScrollView chooseSettingsLayout;
	private LinearLayout faceSettings;
	private LinearLayout serverSettings;
	private LinearLayout voiceSettings;
	private LinearLayout twitterSettings;
	private View view;
	
	private SharedPreferences settings = null;
	private static String PREFS_NAME;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		view = inflater.inflate(R.layout.settings_layout, container, false);
		PREFS_NAME =  getResources().getString((R.string.settingsFileName));
		settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		
		chooseSettingsLayout = (ScrollView)view.findViewById(R.id.chooseSettings);
		faceSettings = (LinearLayout) view.findViewById(R.id.FaceSettings);
		serverSettings = (LinearLayout) view.findViewById(R.id.ServerSettings);
		voiceSettings = (LinearLayout) view.findViewById(R.id.VoiceSettings);
		twitterSettings = (LinearLayout) view.findViewById(R.id.TwitterSettings);
		
		Button trainSettingsButton = (Button) view.findViewById(R.id.trainingSetButton);
		trainSettingsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	trainSettings();
            }
        });
		
		Button serverSettigsButton = (Button) view.findViewById(R.id.ServerSetButton);
		serverSettigsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	serverSettings();
            }
        });
		
		Button twitterSetButton = (Button)view.findViewById(R.id.twitterSetButton);
		twitterSetButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	twitterSettings();
            }
        });
		
		Button voiceSetButton = (Button)view.findViewById(R.id.voiceSetButton);
		voiceSetButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceSettings();
            }
        });
		
		Button cancelButton1 = (Button)view.findViewById(R.id.cancelButton1);
		cancelButton1.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
            {
				done();
            }
        });
		
		Button cancelButton2 = (Button)view.findViewById(R.id.cancelButton2);
		cancelButton2.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
            {
				done();
            }
        });
		
		return view;
	}
	
	private void done()
	{
		MainActivity m = (MainActivity) getActivity();
		m.switchToLoggedInFrag();
	}
	
	//-------------------------------------------------------------------------------------train settings
	private void trainSettings()
	{
		faceSettings.setVisibility(View.VISIBLE);
		chooseSettingsLayout.setVisibility(View.GONE);
		
		//getPreferences and display current settings
		String trainPhotoNum = settings.getString("face_TrainPhotoNum", "");
		((EditText) view.findViewById(R.id.TrainPhotoNumET)).setText(trainPhotoNum);
		
		String recogPhotoNum = settings.getString("face_RecogPhotoNum", "");
		((EditText) view.findViewById(R.id.RecogPhotoNumET)).setText(recogPhotoNum);
		
		String saveTrainThres = settings.getString("face_recognizerThreshold", "");
		((EditText) view.findViewById(R.id.recognizerThresholdET)).setText(saveTrainThres);
		
		String groupRectangleThreshold = settings.getString("face_GroupRectangleThreshold", "");
		Log.d("missing", "text: " + groupRectangleThreshold);
		((EditText) view.findViewById(R.id.GroupRectangleET)).setText(groupRectangleThreshold);
		
		int imageScale = Integer.parseInt(settings.getString("face_ImageScale", "1"));
		((Spinner) view.findViewById(R.id.ImageScaleSP)).setSelection(imageScale - 1);
		
		int algorithm = Integer.parseInt(settings.getString("face_faceRecognizerAlgorithm", "1"));
		((Spinner) view.findViewById(R.id.faceRecognizerAlgorithmSP)).setSelection(algorithm - 1);
		
		//get device available resolutions
		Camera c = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		Parameters parameters = c.getParameters();
		List<Size> sizes = parameters.getSupportedPictureSizes();
		c.release();
		String[] data = new String[sizes.size()];
		for(int i = 0; i < sizes.size(); i++)
		{
			String temp = sizes.get(i).width + " x " + sizes.get(i).height;
			data[i] = temp;
		}
        final Spinner resolutionSpinner = (Spinner) view.findViewById(R.id.face_resolutionSP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resolutionSpinner.setAdapter(adapter);
        int resIndex = Integer.parseInt(settings.getString("face_resolution", "0"));
		resolutionSpinner.setSelection(resIndex);
		
		String detectEyes = settings.getString("face_detectEyes", "");
		if(detectEyes.equals("true"))
			((CheckBox) view.findViewById(R.id.faceDetectEyes)).setChecked(true);
		else
			((CheckBox) view.findViewById(R.id.faceDetectEyes)).setChecked(false);
		
		String detectNose = settings.getString("face_detectNose", "");
		if(detectNose.equals("true"))
			((CheckBox) view.findViewById(R.id.faceDetectNose)).setChecked(true);
		else
			((CheckBox) view.findViewById(R.id.faceDetectNose)).setChecked(false);
		
		//configure buttons
		Button face_photoNumTrainHelpButton = (Button) view.findViewById(R.id.face_photoNumTrainHelpButton);
		face_photoNumTrainHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_facePhotoNumTrain");
            }
        });
		
		Button face_photoNumTrainHelpVoiceButton = (Button) view.findViewById(R.id.face_photoNumTrainHelpVoiceButton);
		face_photoNumTrainHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_facePhotoNumTrain");
            }
        });
		
		Button face_photoNumRecogHelpButton = (Button) view.findViewById(R.id.face_photoNumRecogHelpButton);
		face_photoNumRecogHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_facePhotoNumRecog");
            }
        });
		
		Button face_photoNumRecogHelpVoiceButton = (Button) view.findViewById(R.id.face_photoNumRecogHelpVoiceButton);
		face_photoNumRecogHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_facePhotoNumRecog");
            }
        });
		
		Button face_recThresholdHelpButton = (Button) view.findViewById(R.id.face_recThresholdHelpButton);
		face_recThresholdHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceThreshold");
            }
        });
		
		Button face_recThresholdHelpVoiceButton = (Button) view.findViewById(R.id.face_recThresholdHelpVoiceButton);
		face_recThresholdHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceThreshold");
            }
        });
		
		Button face_imgScaleHelpButton = (Button) view.findViewById(R.id.face_imgScaleHelpButton);
		face_imgScaleHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceImageScale");
            }
        });
		
		Button face_imgScaleHelpVoiceButton = (Button) view.findViewById(R.id.face_imgScaleHelpVoiceButton);
		face_imgScaleHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceImageScale");
            }
        });
		
		Button face_recAlgorithmHelpButton = (Button) view.findViewById(R.id.face_recAlgorithmHelpButton);
		face_recAlgorithmHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceAlgorithm");
            }
        });
		
		Button face_recAlgorithmHelpVoiceButton = (Button) view.findViewById(R.id.face_recAlgorithmHelpVoiceButton);
		face_recAlgorithmHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceAlgorithm");
            }
        });
		
		Button face_resolutionHelpButton = (Button) view.findViewById(R.id.face_resolutionHelpButton);
		face_resolutionHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceResolution");
            }
        });
		
		Button face_resolutionHelpVoiceButton = (Button) view.findViewById(R.id.face_resolutionHelpVoiceButton);
		face_resolutionHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceResolution");
            }
        });
		
		Button face_groupRectangleHelpButton = (Button) view.findViewById(R.id.face_groupRectangleHelpButton);
		face_groupRectangleHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceGroupRectangle");
            }
        });
		
		Button face_groupRectangleHelpVoiceButton = (Button) view.findViewById(R.id.face_groupRectangleHelpVoiceButton);
		face_groupRectangleHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceGroupRectangle");
            }
        });
		
		Button face_eyeDetectionHelpButton = (Button) view.findViewById(R.id.face_eyeDetectionHelpButton);
		face_eyeDetectionHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceDetectEyes");
            }
        });
		
		Button face_eyeDetectionHelpVoiceButton = (Button) view.findViewById(R.id.face_eyeDetectionHelpVoiceButton);
		face_eyeDetectionHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceDetectEyes");
            }
        });
		
		Button face_noseDetectionHelpButton = (Button) view.findViewById(R.id.face_noseDetectionHelpButton);
		face_noseDetectionHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_faceDetectNose");
            }
        });
		
		Button face_noseDetectionHelpVoiceButton = (Button) view.findViewById(R.id.face_noseDetectionHelpVoiceButton);
		face_noseDetectionHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_faceDetectNose");
            }
        });
		
		
		
		Button saveTrain = (Button) view.findViewById(R.id.saveTrainButton);
		saveTrain.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	saveTrainingData();
            }
        });
	}
	
	private void saveTrainingData()
	{
		if(trainNoneEmpty())
		{
			SharedPreferences.Editor editor = settings.edit();
			
			String trainPhotosNum = ((EditText) view.findViewById(R.id.TrainPhotoNumET)).getText().toString();
		    editor.putString("face_TrainPhotoNum", trainPhotosNum);
		    editor.commit();
		    
		    String recogPhotosNum = ((EditText) view.findViewById(R.id.RecogPhotoNumET)).getText().toString();
		    editor.putString("face_RecogPhotoNum", recogPhotosNum);
		    editor.commit();
		    
		    String groupRecThres = ((EditText) view.findViewById(R.id.GroupRectangleET)).getText().toString();
		    editor.putString("face_GroupRectangleThreshold", groupRecThres);
		    editor.commit();
		    
		    String recognizerThreshold = ((EditText) view.findViewById(R.id.recognizerThresholdET)).getText().toString();
		    editor.putString("face_recognizerThreshold", recognizerThreshold);
		    editor.commit();
		    
		    String imageScale = ((Spinner) view.findViewById(R.id.ImageScaleSP)).getSelectedItem().toString();
		    editor.putString("face_ImageScale", imageScale);
		    editor.commit();
		    
		    String algorithm = ((Spinner) view.findViewById(R.id.faceRecognizerAlgorithmSP)).getSelectedItem().toString();
		    if(algorithm.equals("LBPFace"))
		    	editor.putString("face_faceRecognizerAlgorithm", "1");
		    else if(algorithm.equals("FisherFace"))
		    	editor.putString("face_faceRecognizerAlgorithm", "2");
		    else if(algorithm.equals("EigenFace"))
		    	editor.putString("face_faceRecognizerAlgorithm", "3");
		    editor.commit();
		    
		    int resolutionIndex = ((Spinner) view.findViewById(R.id.face_resolutionSP)).getSelectedItemPosition();
		    editor.putString("face_resolution", resolutionIndex+"");
		    editor.commit();
		    
		    
		    if(((CheckBox) view.findViewById(R.id.faceDetectEyes)).isChecked())
		    	editor.putString("face_detectEyes", "true");
		    else
		    	editor.putString("face_detectEyes", "false");
		    editor.commit();
		    
		    if(((CheckBox) view.findViewById(R.id.faceDetectNose)).isChecked())
		    	editor.putString("face_detectNose", "true");
		    else
		    	editor.putString("face_detectNose", "false");
		    editor.commit();
		    
		    
			Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
	}
	
	private boolean trainNoneEmpty()
	{
		if(((EditText) view.findViewById(R.id.TrainPhotoNumET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.RecogPhotoNumET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.recognizerThresholdET)).getText().toString().equals(""))
			return false;
		else if(((Spinner) view.findViewById(R.id.ImageScaleSP)).getSelectedItem().toString().equals(""))
			return false;
		else if(((Spinner) view.findViewById(R.id.ImageScaleSP)).getSelectedItem().toString().equals(""))
			return false;
		return true;
	}
	
	//-------------------------------------------------------------------------------------server settings
	private void serverSettings()
	{
		serverSettings.setVisibility(View.VISIBLE);
		chooseSettingsLayout.setVisibility(View.GONE);
		
		//getPreferences and display current settings
		String ip = settings.getString("server_IP", "");
		((EditText) view.findViewById(R.id.IP_ET)).setText(ip);
		
		String port = settings.getString("server_Port", "");
		((EditText) view.findViewById(R.id.Port_ET)).setText(port);
		
		//configure buttons
		Button serverIP_HelpButton = (Button) view.findViewById(R.id.server_IPAddressHelpButton);
		serverIP_HelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_serverIPAddress");
            }
        });
		
		Button serverIP_VoiceHelpButton = (Button) view.findViewById(R.id.server_IPAddressHelpVoiceButton);
		serverIP_VoiceHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_serverIPAddress");
            }
        });
		
		Button serverPort_HelpButton = (Button) view.findViewById(R.id.server_PortHelpButton);
		serverPort_HelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_serverIPort");
            }
        });
		
		Button serverPort_VoiceHelpButton = (Button) view.findViewById(R.id.server_IPortHelpVoiceButton);
		serverPort_VoiceHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_serverIPort");
            }
        });
		
		Button saveServerSettings = (Button) view.findViewById(R.id.saveServerSettingsButton);
		saveServerSettings.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	saveServerData();
            }
        });
		
		Button cancelServerSettingsButton = (Button) view.findViewById(R.id.cancelServerSettingsButton);
		cancelServerSettingsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	done();
            }
        });
	}
	
	public void saveServerData()
	{
		if(serverNoneEmpty())
		{
			SharedPreferences.Editor editor = settings.edit();
			
			String ip = ((EditText) view.findViewById(R.id.IP_ET)).getText().toString();
		    editor.putString("server_IP", ip);
		    editor.commit();
		    
		    String port = ((EditText) view.findViewById(R.id.Port_ET)).getText().toString();
		    editor.putString("server_Port", port);
		    editor.commit();
		    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
	}
	
	public boolean serverNoneEmpty()
	{
		if(((EditText) view.findViewById(R.id.IP_ET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.Port_ET)).getText().toString().equals(""))
			return false;
		return true;
	}
	
	//--------------------------------------------------------------------------------------voiceSettings
	private void voiceSettings()
	{
		voiceSettings.setVisibility(View.VISIBLE);
		chooseSettingsLayout.setVisibility(View.GONE);
		
		final EditText autoCalibrateET = (EditText) view.findViewById(R.id.voice_calibration_ET);
		String voice_calibration = settings.getString("voice_Calibration", "");
		autoCalibrateET.setText(voice_calibration);
		
		//configure buttons
		Button autoCalibrateButton = (Button) view.findViewById(R.id.voice_autoCalibrateButton);
		autoCalibrateButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	final ProgressDialog progress = new ProgressDialog(getActivity());
            	progress.setMessage("Wait for auto calibration.");
        	    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	    progress.setIndeterminate(true);
        	    
        	    progress.show();
            	final AtomicInteger value= new AtomicInteger(-1);
            	boolean valueSet = false;
            	
        	    
        	    Thread mThread = new Thread() 
        	    {
        	        @Override
        	        public void run() 
        	        {
        	        	VoiceAuthenticator voiceAuthenticator = new VoiceAuthenticator();
                    	value.compareAndSet(-1, voiceAuthenticator.autoCalibrateActivation());
                    	progress.dismiss();
        	        }
    	        };
    	        mThread.start();
    	        while(!valueSet)
    	        {
    	        	if(value.get() != -1)
    	        	{
    	        		valueSet = true;
    	        		autoCalibrateET.setText(value.get()+"");
    	        	}
    	        }
            }
        });
		
		Button voiceCal_HelpButton = (Button) view.findViewById(R.id.voice_CalibrationHelpButton);
		voiceCal_HelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_voiceCalibration");
            }
        });
		
		Button IP_VoiceHelpButton = (Button) view.findViewById(R.id.voice_CalibrationHelpVoiceButton);
		IP_VoiceHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("IPSetting");
            }
        });
		
		Button saveSettingsButton  = (Button) view.findViewById(R.id.voice_saveSettings);
		saveSettingsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	saveVoiceData();
            }
        });
		
		Button cancelSettingsButton  = (Button) view.findViewById(R.id.voice_cancelSettings);
		cancelSettingsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	done();
            }
        });
		
	}
	
	private void voiceHelp(String settingType)
	{
		String help = "";
		if(settingType.equals("Help_voiceCalibration"))
			help = getResources().getString((R.string.Help_voiceCalibration));
		else if(settingType.equals("Help_serverIPAddress"))
			help = getResources().getString((R.string.Help_serverIPAddress));
		else if(settingType.equals("Help_serverIPort"))
			help = getResources().getString(R.string.Help_serverIPort);
		else if(settingType.equals("Help_facePhotoNumTrain"))
			help = getResources().getString(R.string.Help_facePhotoNumTrain);
		else if(settingType.equals("Help_facePhotoNumRecog"))
			help = getResources().getString(R.string.Help_facePhotoNumRecog);
		else if(settingType.equals("Help_faceThreshold"))
			help = getResources().getString(R.string.Help_faceThreshold);
		else if(settingType.equals("Help_faceImageScale"))
			help = getResources().getString(R.string.Help_faceImageScale);
		else if(settingType.equals("Help_faceResolution"))
			help = getResources().getString(R.string.Help_faceResolution);
		else if(settingType.equals("Help_faceAlgorithm"))
			help = getResources().getString(R.string.Help_faceAlgorithm);
		else if(settingType.equals("Help_faceGroupRectangle"))
			help = getResources().getString(R.string.Help_faceGroupRectangle);
		else if(settingType.equals("Help_faceDetectEyes"))
			help = getResources().getString(R.string.Help_faceDetectEyes);
		else if(settingType.equals("Help_faceDetectNose"))
			help = getResources().getString(R.string.Help_faceDetectNose);
		else if(settingType.equals("Help_twitterKey"))
			help = getResources().getString(R.string.Help_twitterKey);
		else if(settingType.equals("Help_twitterSecret"))
			help = getResources().getString(R.string.Help_twitterSecret);
		else if(settingType.equals("Help_twitterTokenKey"))
			help = getResources().getString(R.string.Help_twitterTokenKey);
		else if(settingType.equals("Help_twitterTokenString"))
			help = getResources().getString(R.string.Help_twitterTokenString);
		
		MainActivity m = (MainActivity) getActivity();
		m.speakOut(help);
	}
	
	private void displayHelp(String settingType)
	{
		String help = "";
		if(settingType.equals("Help_voiceCalibration"))
			help = getResources().getString((R.string.Help_voiceCalibration));
		else if(settingType.equals("Help_serverIPAddress"))
			help = getResources().getString((R.string.Help_serverIPAddress));
		else if(settingType.equals("Help_serverIPort"))
			help = getResources().getString(R.string.Help_serverIPort);
		else if(settingType.equals("Help_facePhotoNumTrain"))
			help = getResources().getString(R.string.Help_facePhotoNumTrain);
		else if(settingType.equals("Help_facePhotoNumRecog"))
			help = getResources().getString(R.string.Help_facePhotoNumRecog);
		else if(settingType.equals("Help_faceThreshold"))
			help = getResources().getString(R.string.Help_faceThreshold);
		else if(settingType.equals("Help_faceImageScale"))
			help = getResources().getString(R.string.Help_faceImageScale);
		else if(settingType.equals("Help_faceResolution"))
			help = getResources().getString(R.string.Help_faceResolution);
		else if(settingType.equals("Help_faceAlgorithm"))
			help = getResources().getString(R.string.Help_faceAlgorithm);
		else if(settingType.equals("Help_faceGroupRectangle"))
			help = getResources().getString(R.string.Help_faceGroupRectangle);
		else if(settingType.equals("Help_faceDetectEyes"))
			help = getResources().getString(R.string.Help_faceDetectEyes);
		else if(settingType.equals("Help_faceDetectNose"))
			help = getResources().getString(R.string.Help_faceDetectNose);
		else if(settingType.equals("Help_twitterKey"))
			help = getResources().getString(R.string.Help_twitterKey);
		else if(settingType.equals("Help_twitterSecret"))
			help = getResources().getString(R.string.Help_twitterSecret);
		else if(settingType.equals("Help_twitterTokenKey"))
			help = getResources().getString(R.string.Help_twitterTokenKey);
		else if(settingType.equals("Help_twitterTokenString"))
			help = getResources().getString(R.string.Help_twitterTokenString);
		
		
		
		
		
		
		
		//AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(help)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void saveVoiceData()
	{
		if(voiceNoneEmpty())
		{
			SharedPreferences.Editor editor = settings.edit();
			
			String voiceCalibration = ((EditText) view.findViewById(R.id.voice_calibration_ET)).getText().toString();
		    editor.putString("voice_Calibration", voiceCalibration);
		    editor.commit();
		    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
		
	}
	
	private boolean voiceNoneEmpty()
	{
		if(((EditText) view.findViewById(R.id.voice_calibration_ET)).getText().toString().equals(""))
			return false;
		return true;
	}
	
	//--------------------------------------------------------------------------------------twitterSettings
	private void twitterSettings()
	{
		twitterSettings.setVisibility(View.VISIBLE);
		chooseSettingsLayout.setVisibility(View.GONE);
		
		//getPreferences and display current settings
		String key = settings.getString("twitter_Key", "");
		((EditText) view.findViewById(R.id.twitter_KeyET)).setText(key);
		
		String secret = settings.getString("twitter_Secret", "");
		((EditText) view.findViewById(R.id.twitter_SecretET)).setText(secret);
		
		String tokenKey = settings.getString("twitter_TokenKey", "");
		((EditText) view.findViewById(R.id.twitter_TokenKeyET)).setText(tokenKey);
		
		String tokenSecret = settings.getString("twitter_TokenSecret", "");
		((EditText) view.findViewById(R.id.twitter_TokenSecretET)).setText(tokenSecret);
		
		//configure buttons
		Button twitterKey_HelpButton = (Button) view.findViewById(R.id.twitter_keyHelpButton);
		twitterKey_HelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_twitterKey");
            }
        });
				
		Button twitter_keyHelpVoiceButton = (Button) view.findViewById(R.id.twitter_keyHelpVoiceButton);
		twitter_keyHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_twitterKey");
            }
        });
		
		Button twitter_SecretHelpButton = (Button) view.findViewById(R.id.twitter_SecretHelpButton);
		twitter_SecretHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_twitterSecret");
            }
        });
				
		Button twitter_SecretHelpVoiceButton = (Button) view.findViewById(R.id.twitter_SecretHelpVoiceButton);
		twitter_SecretHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_twitterSecret");
            }
        });
		
		Button twitter_TokenKeyHelpButton = (Button) view.findViewById(R.id.twitter_TokenKeyHelpButton);
		twitter_TokenKeyHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_twitterTokenKey");
            }
        });
				
		Button twitter_TokenKeyHelpVoiceButton = (Button) view.findViewById(R.id.twitter_TokenKeyHelpVoiceButton);
		twitter_TokenKeyHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_twitterTokenKey");
            }
        });
		
		Button twitter_TokenSecretHelpButton = (Button) view.findViewById(R.id.twitter_TokenSecretHelpButton);
		twitter_TokenSecretHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	displayHelp("Help_twitterTokenString");
            }
        });
				
		Button twitter_TokenSecretHelpVoiceButton = (Button) view.findViewById(R.id.twitter_TokenSecretHelpVoiceButton);
		twitter_TokenSecretHelpVoiceButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	voiceHelp("Help_twitterTokenString");
            }
        });
				
				
				
		Button saveTwitterSettings = (Button) view.findViewById(R.id.saveTwitterButton);
		saveTwitterSettings.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	saveTwitterData();
            }
        });
				
		Button cancelTwitterSettingsButton = (Button) view.findViewById(R.id.cancelButtonTwitter);
		cancelTwitterSettingsButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	done();
            }
        });
		
		Button loadDefaultsTwitterButton = (Button) view.findViewById(R.id.defaultTwitter);
		loadDefaultsTwitterButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	loadTwitterDefaults();
            }
        });
	}
	
	private void saveTwitterData()
	{
		if(twitterNoneEmpty())
		{
			SharedPreferences.Editor editor = settings.edit();
			
			String key = ((EditText) view.findViewById(R.id.twitter_KeyET)).getText().toString();
		    editor.putString("twitter_Key", key);
		    editor.commit();
		    
		    String secret = ((EditText) view.findViewById(R.id.twitter_SecretET)).getText().toString();
		    editor.putString("twitter_Secret", secret);
		    editor.commit();
		    
		    String tokenKey = ((EditText) view.findViewById(R.id.twitter_TokenKeyET)).getText().toString();
		    editor.putString("twitter_TokenKey", tokenKey);
		    editor.commit();
		    
		    String tokenSecret = ((EditText) view.findViewById(R.id.twitter_TokenSecretET)).getText().toString();
		    editor.putString("twitter_TokenSecret", tokenSecret);
		    editor.commit();
		    
		    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
	}
	
	private boolean twitterNoneEmpty()
	{
		if(((EditText) view.findViewById(R.id.twitter_KeyET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.twitter_SecretET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.twitter_TokenKeyET)).getText().toString().equals(""))
			return false;
		else if(((EditText) view.findViewById(R.id.twitter_TokenSecretET)).getText().toString().equals(""))
			return false;
		return true;
	}
	
	private void loadTwitterDefaults()
	{
		String key = getResources().getString(R.string.twitter_Key);
		String secret = getResources().getString(R.string.twitter_Secret);
		String tokenKey = getResources().getString(R.string.twitter_TokenKey);
		String tokenSecret = getResources().getString(R.string.twitter_TokenSecret);
		
		((EditText) view.findViewById(R.id.twitter_KeyET)).setText(key);
		((EditText) view.findViewById(R.id.twitter_SecretET)).setText(secret);
		((EditText) view.findViewById(R.id.twitter_TokenKeyET)).setText(tokenKey);
		((EditText) view.findViewById(R.id.twitter_TokenSecretET)).setText(tokenSecret);
	    
	    
	}
}
