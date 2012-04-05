package com.memelab.jandig;

//import java.nio.FloatBuffer;
//import java.io.BufferedReader;
//import edu.dhbw.andar.sample.SimpleBox;
//import edu.dhbw.andar.util.GraphicsUtil;

import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import javax.microedition.khronos.opengles.GL10;
import com.memelab.jandig.SimpleImage;
import edu.dhbw.andar.ARObject;


/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class ImageObject extends ARObject {

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
		// openGL docs :
		//    http://www.opengl.org/sdk/docs/man/xhtml/glTexImage2D.xml
		//    http://www.opengl.org/sdk/docs/man/xhtml/glGenTextures.xml
		if(textureStream != null) {
			Bitmap tbmp = BitmapFactory.decodeStream(textureStream);
			// int potW = 1024;
			// int potH = 1024;
			//int potW = (tbmp.getWidth()<256)?256:((tbmp.getWidth()<512)?512:1024);
			//int potH = (tbmp.getHeight()<256)?256:((tbmp.getHeight()<512)?512:1024);
			int potW = java.lang.Integer.highestOneBit(tbmp.getWidth())<<1;
			int potH = java.lang.Integer.highestOneBit(tbmp.getHeight())<<1;
			int[] bgnd = new int[potW*potH];

			tbmp.getPixels(bgnd,0,potW, 0,0, tbmp.getWidth(), tbmp.getHeight());
			Bitmap bm = Bitmap.createBitmap(bgnd, potW, potH, tbmp.getConfig());

			// init a simple image with height taken from bitmap
			simg = new SimpleImage(tbmp.getWidth(), tbmp.getHeight(), potW, potH);

			// generate texture pointers
			// gl.glGenTextures(number-of-texture-names, array-of-texture-names, offset);
			gl.glGenTextures(1, textures, 0);
			// ...and bind it to our array
			//  bind first texture to GL renderer
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

			// create nearest filtered texture
			//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			
			// Use Android GLUtils to specify a two-dimensional texture image from our bitmap
			//   read bitmap into the last texture that's been bound to the renderer
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);
			
			// Clean up
			tbmp.recycle();
			bm.recycle();
		}
	}
}
