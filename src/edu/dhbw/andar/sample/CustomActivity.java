package edu.dhbw.andar.sample;

import android.os.Bundle;
import android.util.Log;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

/**
 * Example of an application that makes use of the AndAR toolkit.
 * @author Tobi
 *
 */
public class CustomActivity extends AndARActivity {

	CustomObject someObject;
	ARToolkit artoolkit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CustomRenderer renderer = new CustomRenderer();//optional, may be set to null
		super.setNonARRenderer(renderer);//or might be omited
		try {
			// actually it's show a cube with different colors when see different markers
			artoolkit = super.getArtoolkit();
			someObject = new CustomObject
				("test", "patt_01.patt", 80.0, new double[]{0,0}, new float[]{0f,0f,0f});
			artoolkit.registerARObject(someObject);
			someObject = new CustomObject
				("test", "patt_02.patt", 80.0, new double[]{0,0}, new float[]{0f,0f,1f});
			artoolkit.registerARObject(someObject);
			someObject = new CustomObject
				("test", "patt_03.patt", 80.0, new double[]{0,0}, new float[]{0f,1f,0f});
			artoolkit.registerARObject(someObject);
			someObject = new CustomObject
				("test", "patt_04.patt", 80.0, new double[]{0,0}, new float[]{1f,0f,0f});
			artoolkit.registerARObject(someObject);		
			someObject = new CustomObject
				("test", "patt_05.patt", 80.0, new double[]{0,0}, new float[]{0f,1f,1f});
			artoolkit.registerARObject(someObject);		
			someObject = new CustomObject
				("test", "patt_06.patt", 80.0, new double[]{0,0}, new float[]{1f,1f,0f});
			artoolkit.registerARObject(someObject);		
			someObject = new CustomObject
				("test", "patt_07.patt", 80.0, new double[]{0,0}, new float[]{1f,0f,1f});
			artoolkit.registerARObject(someObject);		
			someObject = new CustomObject
				("test", "patt_08.patt", 80.0, new double[]{0,0}, new float[]{1f,1f,1f});
			artoolkit.registerARObject(someObject);		
		} catch (AndARException ex){
			//handle the exception, that means: show the user what happened
			System.out.println("");
		}		
		startPreview();
	}

	/**
	 * Inform the user about exceptions that occurred in background threads.
	 * This exception is rather severe and can not be recovered from.
	 * TODO Inform the user and shut down the application.
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e("AndAR EXCEPTION", ex.getMessage());
		finish();
	}
	
}
