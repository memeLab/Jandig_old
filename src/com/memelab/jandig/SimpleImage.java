package com.memelab.jandig;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;

public class SimpleImage {
	private FloatBuffer vertices, normals, texture;

	// w,h     := width and height of the image
	// tw, th  := width and height of the texture
	public SimpleImage(int w, int h, int tw, int th) {
		float mind = (w<h)?w:h;
		float verticesf[] = {
				// with scaling!
				-25.0f*w/mind, -25.0f*h/mind,  0.0f,
				+25.0f*w/mind, -25.0f*h/mind,  0.0f,
				-25.0f*w/mind, +25.0f*h/mind,  0.0f,
				+25.0f*w/mind, +25.0f*h/mind,  0.0f
				
		};
		float normalsf[] =  {
				// FRONT
				0.0f, 0.0f,  1.0f,
				0.0f, 0.0f,  1.0f,
				0.0f, 0.0f,  1.0f,
				0.0f, 0.0f,  1.0f
		};
		float texturef[] = {
				0.0f, 1.0f*h/th,
				1.0f*w/tw, 1.0f*h/th,
				0.0f, 0.0f,
				1.0f*w/tw, 0.0f
		};
		
		vertices = GraphicsUtil.makeFloatBuffer(verticesf);
		normals = GraphicsUtil.makeFloatBuffer(normalsf);
		texture = GraphicsUtil.makeFloatBuffer(texturef);
	}


	public final void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDisable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		// from : http://stackoverflow.com/questions/3163862/opengl-translucent-texture-over-other-texture
		gl.glEnable(GL10.GL_BLEND);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glNormalPointer(GL10.GL_FLOAT,0, normals);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_LIGHTING);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}
