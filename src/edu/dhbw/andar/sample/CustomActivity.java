package edu.dhbw.andar.sample;

import android.os.Bundle;
import android.util.Log;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
//import edu.dhbw.andarmodelviewer.R;
import edu.dhbw.andobjviewer.graphics.LightingRenderer;
import edu.dhbw.andobjviewer.graphics.Model3D;
import edu.dhbw.andobjviewer.models.Model;
import edu.dhbw.andobjviewer.parser.ObjParser;
import edu.dhbw.andobjviewer.parser.ParseException;
import edu.dhbw.andobjviewer.parser.Util;
import edu.dhbw.andobjviewer.util.AssetsFileUtil;
import edu.dhbw.andobjviewer.util.BaseFileUtil;
import edu.dhbw.andobjviewer.util.SDCardFileUtil;


/**
 * Example of an application that makes use of the AndAR toolkit.
 * @author Tobi
 *
 */
public class CustomActivity extends AndARActivity {

	/* tgh: making it compatible with augmentedModelViewer */
	public static final boolean DEBUG = false;

	private static final boolean USE3DMODEL = false;
	
	//CustomObject someObject;
	ARToolkit artoolkit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(USE3DMODEL){
			createFromModel3D(savedInstanceState);
		}
		else{
			createFromCustomObjects(savedInstanceState);
		}
	}

	// function to register CustomObject objects onto the ARToolkit
	private void createFromCustomObjects(Bundle savedInstanceState){
		CustomObject myObject;

		super.onCreate(savedInstanceState);
		CustomRenderer renderer = new CustomRenderer();//optional, may be set to null
		super.setNonARRenderer(renderer);//or might be omited

		/* tgh: associate different color cubes to different .patt files */
		try {
			//register a object for each marker type
			artoolkit = super.getArtoolkit();
			myObject = new CustomObject("test0", "ap0_16x16.patt", 80.0, new double[]{0,0}, new float[]{255,0,0});
			artoolkit.registerARObject(myObject);
			myObject = new CustomObject("test1", "ap1_16x16.patt", 80.0, new double[]{0,0}, new float[]{0,255,0});
			artoolkit.registerARObject(myObject);
			myObject = new CustomObject("test2", "ap2_16x16.patt", 80.0, new double[]{0,0}, new float[]{0,0,255});
			artoolkit.registerARObject(myObject);
		} 
		catch (AndARException ex){
			//handle the exception, that means: show the user what happened
			System.out.println("");
		}		
	}


	// function to register Model3D objects onto the ARToolkit
	private void createFromModel3D(Bundle savedInstanceState) {
		/* tgh: Model3D... wooohhooooooo!! */
		Model model;
		Model3D model3d;

		super.onCreate(savedInstanceState);
		CustomRenderer renderer = new CustomRenderer();//optional, may be set to null
		super.setNonARRenderer(renderer);//or might be omited

		/* tgh: get every file in assets/objModels that ends in .obj 
		        then create a model3d and associate it with the correct .patt */
		try{
			artoolkit = super.getArtoolkit();
			final String[] allObjFilesInAssets = getAssets().list("objModels");
			for(int i=0; i<allObjFilesInAssets.length; i++) {
				if(allObjFilesInAssets[i].endsWith(".obj")){
					String baseName = allObjFilesInAssets[i].substring(0,allObjFilesInAssets[i].lastIndexOf("."));
					//System.out.println("-------"+baseName);
					String modelFileName = allObjFilesInAssets[i];
					BaseFileUtil fileUtil = new AssetsFileUtil(getAssets());
					fileUtil.setBaseFolder("objModels/");
					ObjParser parser = new ObjParser(fileUtil);
					if(fileUtil != null) {
						BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
						if(fileReader != null) {
							model = parser.parse(modelFileName, fileReader);
							/* add a check here.... */
							if(Arrays.asList(getAssets().list("")).contains(baseName+".patt")){
								model3d = new Model3D(model,baseName+".patt");
								artoolkit.registerARObject(model3d);
								System.out.println("-------"+baseName);
							}
							else{
								// default
								model3d = new Model3D(model);
								artoolkit.registerARObject(model3d);								
							}
							System.out.println("cretaed model3d for: "+ allObjFilesInAssets[i]);
						}
					}
				}
			}
		}
		catch(Exception e){
			//handle the exception, that means: show the user what happened
			System.out.println("Some exception somewhere was thrown");
			System.out.println("string: "+e.toString());
			System.out.println("message: "+e.getMessage());
			if(e.getCause() != null) {
				System.out.println("cause msg"+e.getCause().getMessage());			
			}

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
