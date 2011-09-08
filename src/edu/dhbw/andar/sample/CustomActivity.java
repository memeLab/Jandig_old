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
			//register a object for each marker type
			artoolkit = super.getArtoolkit();
			someObject = new CustomObject
				("test", "patt.hiro", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			someObject = new CustomObject
			("test", "android.patt", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			someObject = new CustomObject
			("test", "barcode.patt", 80.0, new double[]{0,0});
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
