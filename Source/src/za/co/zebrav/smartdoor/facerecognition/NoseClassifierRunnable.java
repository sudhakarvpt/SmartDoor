package za.co.zebrav.smartdoor.facerecognition;
import java.io.File;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;

public class NoseClassifierRunnable extends ClassifierRunnable
{
	private static final String TAG = "NoseClassifierRunnable";
	private static final String classifierFile = "haarcascade_nose.xml";
	@Override
	protected String getClassifierfile()
	{
		return classifierFile;
	}
	public NoseClassifierRunnable(CvMemStorage storage, File cacheDir,double groupRectangleThreshold)
	{
		super(storage,cacheDir,groupRectangleThreshold);
		
	}
	@Override
	public void run()
	{
		super.run();
		if (totalDetected > 1)
		{
			rectangleGroup();
		}
	}

}
