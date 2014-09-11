package za.co.zebrav.facerecognition;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.Mat;

import static org.bytedeco.javacpp.opencv_highgui.*;
import za.co.zebrav.smartdoor.database.AddUserActivity;
import za.co.zebrav.smartdoor.database.Db4oAdapter;

import com.db4o.Db4o;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class AddCameraFragment extends Fragment
{
	public AddCameraFragment(Context contex)
	{
		super();
		try
		{
			this.faceView = new AddFaceView(contex);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private long uID;
	private static final String TAG = "AddCameraFragment";
	private FrameLayout layout;
	/**
	 * Stores the camera instance for the class
	 */
	private Camera mCamera;
	/**
	 * Preview object to display camera content
	 */
	private Preview mPreview;

	private Context context = null;
	private AddFaceView faceView;
	private Button addButton;

	/**
	 * Standard on create method
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		context = getActivity().getBaseContext();
		// check that the hardware does indeed have a camera
		checkFrontCamera(context);
		// Create the view with nothing to show
		// This is so that onCreateView has a view to return
		// Then onResume we add the camera to the preview
		layout = new FrameLayout(context);
		mPreview = new Preview(context, faceView);
		layout.addView(mPreview);
		layout.addView(faceView);
		final AddUserActivity activity = (AddUserActivity) getActivity();
		addButton = new Button(context);
		addButton.setBackgroundColor(Color.TRANSPARENT);
		addButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				Mat m = faceView.getFace();
				SaveImage(m,(int) uID + ".png");
				
				activity.switchFragToStep3();
			}
		});
		
		addButton.setText("Tap to capture");
		addButton.setTextSize(30);
		layout.addView(addButton);
	}

	public void SaveImage(Mat mat,String fileName)
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, fileName);

		Boolean bool = null;
		fileName = file.toString();
		bool = imwrite(fileName, mat);

		if (bool == true)
			Log.d(TAG, "SUCCESS writing image to external storage");
		else
			Log.d(TAG, "Fail writing image to external storage");
	}

	/**
	 * Sets the whole preview to a CameraPreview
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Bundle bundle = this.getArguments();
		uID = bundle.getLong("userID", -1);
		return layout;
	}

	/**
	 * Check if this device has a camera
	 */
	private boolean checkFrontCamera(Context context)
	{
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
		{
			// this device has a camera
			return true;
		}
		else
		{
			Toast t = Toast.makeText(context, "No front-facing camera on device", Toast.LENGTH_LONG);
			t.show();
			System.exit(0);
			return false;
		}
	}

	/**
	 * Releases the camera to the OS
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		releaseCamera();
		// TODO: add savePersonRecognizer call once db is fixed.
		// faceView.savePersonRecognizer();
	}

	/**
	 * Acquires the camera from the OS
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		// Create an instance of Camera
		mCamera = getFrontCameraInstance();
		mPreview.setCamera(mCamera);
		// TODO: add loadPersonRecognizer call once db is fixed.
		// faceView.loadPersonRecognizer();
	}

	private void releaseCamera()
	{
		if (mCamera != null)
		{
			Log.d("Camera", "Releasing Camera");
			mCamera.release(); // release the camera for other applications
			mCamera = null;
			mPreview.setCamera(null);
		}
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 * */
	public Camera getFrontCameraInstance()
	{
		Log.d("Camera", "Aquiring Camera");
		Camera c = null;
		try
		{
			// attempt to get a Camera instance
			c = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			// c = Camera.open(CameraInfo.CAMERA_FACING_BACK);

		}
		catch (Exception e)
		{
			Toast t = Toast.makeText(context, "Camera in use", Toast.LENGTH_LONG);
			t.show();
			System.exit(0);
		}
		return c; // returns null if camera is unavailable
	}
}