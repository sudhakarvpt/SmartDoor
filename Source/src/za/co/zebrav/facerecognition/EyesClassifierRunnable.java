package za.co.zebrav.facerecognition;

import java.io.File;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;

public class EyesClassifierRunnable extends ClassifierRunnable
{
	private static final String classifierFile = "haarcascade_eye.xml";
	@Override
	protected String getClassifierfile()
	{
		return classifierFile;
	}
	public EyesClassifierRunnable(CvMemStorage storage, File cacheDir)
	{
		super(storage,cacheDir);
		
	}

}
