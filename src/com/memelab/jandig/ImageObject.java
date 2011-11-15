package com.memelab.jandig;

import java.nio.FloatBuffer;

import java.io.BufferedReader;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.sample.SimpleBox;
import edu.dhbw.andar.util.GraphicsUtil;

/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class ImageObject extends ARObject {

	/**
	 */
	private SimpleImage simg;

	private InputStream textureStream;
	private int[] textures = new int[1];

	public ImageObject(String name, InputStream is, double markerWidth) {
		this(name, "barcode.patt", markerWidth, new double[]{0,0});
		textureStream = is;
	}

	public ImageObject(String name, String patternName, InputStream is, double markerWidth) {
		this(name, patternName, markerWidth, new double[]{0,0});
		textureStream = is;
	}

	public ImageObject(String name, String patternName, InputStream is, double markerWidth, double[] markerCenter) {
		this(name, patternName, markerWidth, markerCenter);
		textureStream = is;
	}

	public ImageObject(String name, String patternName, double markerWidth, double[] markerCenter) {
		super(name, patternName, markerWidth, markerCenter);
		textureStream = null;
	}


	/**
	 * Everything drawn here will be drawn directly onto the marker,
	 * as the corresponding translation matrix will already be applied.
	 */
	@Override
	public final void draw(GL10 gl) {
		super.draw(gl);

		// bind texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		//draw the surface
		simg.draw(gl);
	}
	@Override
	public void init(GL10 gl) {
		// can use this space to read-in texture...
		if(textureStream != null) {
			Bitmap bm = BitmapFactory.decodeStream(textureStream);

			// init a simple image with height taken from bitmap
			simg = new SimpleImage(bm.getWidth(), bm.getHeight());

			// generate one texture pointer
			gl.glGenTextures(1, textures, 0);
			// ...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

			// create nearest filtered texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			// Use Android GLUtils to specify a two-dimensional texture image from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);

			// Clean up
			bm.recycle();
		}
	}
}
