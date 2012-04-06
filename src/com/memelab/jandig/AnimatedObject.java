package com.memelab.jandig;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import javax.microedition.khronos.opengles.GL10;
import com.memelab.jandig.SimpleImage;
import edu.dhbw.andar.ARObject;
import eu.andlabs.animatedgifs.GifDecoder;


/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class AnimatedObject extends ARObject {

	private SimpleImage simg;

	private InputStream textureStream;
	private int[] textures;

	// for gif
	private static final int RESTART_TIME = 2000;  // 2 seconds
	private long  lastTime;
	private int[] frameDelays;
	private int   totalFrames;
	private int   frameCounter;


	public AnimatedObject(String name, InputStream is, double markerWidth) {
		this(name, "barcode.patt", markerWidth, new double[]{0,0});
		textureStream = is;
	}

	public AnimatedObject(String name, String patternName, InputStream is, double markerWidth) {
		this(name, patternName, markerWidth, new double[]{0,0});
		textureStream = is;
	}

	public AnimatedObject(String name, String patternName, InputStream is, double markerWidth, double[] markerCenter) {
		this(name, patternName, markerWidth, markerCenter);
		textureStream = is;
	}

	public AnimatedObject(String name, String patternName, double markerWidth, double[] markerCenter) {
		super(name, patternName, markerWidth, markerCenter);
		textures = null;
		totalFrames = -1;
		frameCounter = -1;
		textureStream = null;
		frameDelays = null;
	}


	/**
	 * Everything drawn here will be drawn directly onto the marker,
	 * as the corresponding translation matrix will already be applied.
	 */
	@Override
	public final void draw(GL10 gl) {
		super.draw(gl);

		// determine which frame to display
		// if it has been a while since we drew this object, start over
		if((System.currentTimeMillis()-lastTime) > RESTART_TIME){
			frameCounter = 0;
			lastTime = System.currentTimeMillis();
		}
		// if we've stayed on this frame longer than its delay
		else if((System.currentTimeMillis()-lastTime) > frameDelays[frameCounter]){
			frameCounter = (frameCounter + 1) % totalFrames;
			lastTime = System.currentTimeMillis();
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[frameCounter]);
		
		//draw the surface
		simg.draw(gl);
	}
	@Override
	public void init(GL10 gl) {
		// can use this space to read-in texture...
		// openGL docs :
		//    http://www.opengl.org/sdk/docs/man/xhtml/glTexImage2D.xml
		//    http://www.opengl.org/sdk/docs/man/xhtml/glGenTextures.xml
		if((textureStream != null) && (textures == null)){
			// new GifDecoder
			GifDecoder mGD = new GifDecoder();
			if(mGD.read(textureStream) != 0){
				// some error
			}
			// peachy, so read first bitmap to get size, etc...
			// init the texture array and other local variables
			totalFrames = mGD.getFrameCount();
			frameCounter = 0;
			lastTime = System.currentTimeMillis();
			textures = new int[totalFrames];
			frameDelays = new int[totalFrames];
			// gl.glGenTextures(number-of-texture-names, array-of-texture-names, offset);
			gl.glGenTextures(totalFrames, textures, 0);

			// Bitmap tbmp = BitmapFactory.decodeStream(textureStream);
			Bitmap tbmp = mGD.getBitmap();
			// for reading the frames into textures
			int potW = java.lang.Integer.highestOneBit(tbmp.getWidth())<<1;
			int potH = java.lang.Integer.highestOneBit(tbmp.getHeight())<<1;
			// init a simple image with height taken from first bitmap
			simg = new SimpleImage(tbmp.getWidth(), tbmp.getHeight(), potW, potH);

			// for each frame
			for(int f=0; f<totalFrames; f++){
				tbmp = mGD.getFrame(f);
				frameDelays[f] = mGD.getDelay(f);
				int[] bgnd = new int[potW*potH];

				// move frame image to a larger image
				tbmp.getPixels(bgnd,0,potW, 0,0, tbmp.getWidth(), tbmp.getHeight());

				// create larger image
				//Bitmap bm = Bitmap.createBitmap(bgnd, potW, potH, tbmp.getConfig());
				Bitmap bm = Bitmap.createBitmap(bgnd, potW, potH, Bitmap.Config.ARGB_8888);
				
				// ...and bind it to our array
				//  bind first texture to GL renderer
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[f]);

				// create nearest filtered texture
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				// from : http://stackoverflow.com/questions/3163862/opengl-translucent-texture-over-other-texture
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

				// Use Android GLUtils to specify a two-dimensional texture image from our bitmap
				//   read bitmap into the last texture that's been bound to the renderer
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);

				// Clean up
				bm.recycle();
			}  // for()

			// Clean up
			tbmp.recycle();
		} // if()
	} // init()
}
