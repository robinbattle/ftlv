import processing.core.*;
import processing.xml.*;
import sun.awt.windows.ThemeReader;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.*;
import java.util.regex.*;

/**
 * Reference: http://www.learningprocessing.com Example 15-7: Displaying the
 * pixels of an image
 * 
 * http://www.processing.org/tutorials/pixels/
 * 
 * Video sample: https://www.youtube.com/watch?v=hLX3k-9tozk
 */
public class FactoredTimeLapseVideoViewer extends PApplet {

	final int screenWidth = 640;
	final int screenHeight = 360;

	boolean fixedImage = true;

	int currentFrame = 0;
	int numFrames = 0;
	int mPixels_size = screenWidth * screenHeight;

	boolean pause = false;

	boolean sun_only = false;
	boolean sky_only = false;
	boolean sky_and_sun_shadow = true;
	boolean sun_shadow_only = false;

	float[][] mPixels;
	int[] initImage;

	PImage img;

	String videoFileFolder, shadowOutputFolder, matrixOutputFolder,
			fixedImageOutputFolder;

	/* Store all the shadow pixels of each frame */
	TreeMap<Integer, boolean[]> shadowPixels = new TreeMap<Integer, boolean[]>();

	/* Store the median of 20% smallest intensity */
	HashMap<Integer, Float> pixelsMedian = new HashMap<Integer, Float>();
	Matrix W1, H1, W2, H2, W3, H3, W4, H4, W5, H5, W6, H6;

	public FactoredTimeLapseVideoViewer() {


		videoFileFolder = new VideoFolderSelector().select();
		if (videoFileFolder == null) {
			System.exit(0);
		}
		System.out.println(videoFileFolder);

		shadowOutputFolder = videoFileFolder + "/shadow/";
		matrixOutputFolder = videoFileFolder + "/matrix/";
		fixedImageOutputFolder = videoFileFolder + "/fixedImage/";

		File f = new File(shadowOutputFolder);
		numFrames = f.listFiles().length;
		mPixels = new float[numFrames][mPixels_size];

		initImage = new int[mPixels_size];

	}

	void loadLocalImage(String fileName) {
		loadPixels();

		// String fileName = dataFolder + indexString + ".jpg";
		System.out.println("load: " + fileName);

		img = loadImage(fileName);
		img.resize(screenWidth, screenHeight);

		// We must also call loadPixels() on the PImage since we are going
		// to read its pixels.
		img.loadPixels();
	}

	void loadFixedImage() {
		loadPixels();

		System.out.println("load: " + fixedImageOutputFolder + "Sky.jpg");// -fixed

		img = loadImage(fixedImageOutputFolder + "Sky.jpg");
		img.resize(screenWidth, screenHeight);

		// We must also call loadPixels() on the PImage since we are going
		// to read its pixels.
		img.loadPixels();

		PImage newImage = createImage(screenWidth, screenHeight, RGB);
		for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {

			float r = red(img.pixels[pixelIndex]);
			float g = green(img.pixels[pixelIndex]);
			float b = blue(img.pixels[pixelIndex]);

			W1.set(pixelIndex, 0, r / H1.get(0));
			W2.set(pixelIndex, 0, g / H2.get(0));
			W3.set(pixelIndex, 0, b / H3.get(0));

		}

		writeMatrixintoFile("W1", W1);
		writeMatrixintoFile("W2", W2);
		writeMatrixintoFile("W3", W3);

		// -----------------------------------------------------------------------
		loadPixels();

		System.out.println("load: " + fixedImageOutputFolder + "Sun.jpg");// -fixed

		img = loadImage(fixedImageOutputFolder + "Sun.jpg");
		img.resize(screenWidth, screenHeight);

		// We must also call loadPixels() on the PImage since we are going
		// to read its pixels.
		img.loadPixels();

		newImage = createImage(screenWidth, screenHeight, RGB);
		for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {

			float r = red(img.pixels[pixelIndex]);
			float g = green(img.pixels[pixelIndex]);
			float b = blue(img.pixels[pixelIndex]);

			W4.set(pixelIndex, 0, r / H4.get(0));
			W5.set(pixelIndex, 0, g / H5.get(0));
			W6.set(pixelIndex, 0, b / H6.get(0));

		}

		writeMatrixintoFile("W4", W4);
		writeMatrixintoFile("W5", W5);
		writeMatrixintoFile("W6", W6);

	}

	/**
	 * Write the shadow into the file.
	 * 
	 * @param frameIndex
	 * @param array
	 */
	void writeShadowintoFile(int frameIndex, boolean[] array) {
		try {
			FileOutputStream outStream = new FileOutputStream(
					shadowOutputFolder + frameIndex);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outStream);
			objectOutputStream.writeObject(array);
			outStream.close();
			System.out.println("successful(shadow)");

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the Shadow from data file.
	 */
	void readShadowFromFile() {
		FileInputStream freader;
		try {
			File dir = new File(shadowOutputFolder);
			File files[] = dir.listFiles();
			for (File file : files) {
				freader = new FileInputStream(file);
				ObjectInputStream objectInputStream = new ObjectInputStream(
						freader);
				boolean[] array = (boolean[]) objectInputStream.readObject();
				shadowPixels.put(
						Integer.valueOf(file.toString().substring(
								file.toString().lastIndexOf("\\") + 1)), array);
				objectInputStream.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write the Matrix into the file.
	 * 
	 * @param frameIndex
	 * @param array
	 */
	void writeMatrixintoFile(String name, Matrix matrix) {
		try {
			FileOutputStream outStream = new FileOutputStream(
					matrixOutputFolder + name);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outStream);
			objectOutputStream.writeObject(matrix);
			outStream.close();
			System.out.println("successful(matrix)");

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the Matrix from data file.
	 * 
	 * @return
	 */
	Matrix readMatrixFromFile(String name) {
		FileInputStream freader;
		Matrix matrix = null;
		try {
			freader = new FileInputStream(matrixOutputFolder + name);
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			matrix = (Matrix) objectInputStream.readObject();

			objectInputStream.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matrix;
	}

	public void setup() {

		size(screenWidth, screenHeight);
		frameRate(10);

		if (frame != null) {
			frame.setResizable(true);
		}

		File f = new File(shadowOutputFolder + 1);
		System.out.println(f.exists());

		if (f.exists()) {
			System.out.println("Loaded from output.txt");
			readShadowFromFile();

			W1 = readMatrixFromFile("W1");
			H1 = readMatrixFromFile("H1");
			W2 = readMatrixFromFile("W2");
			H2 = readMatrixFromFile("H2");
			W3 = readMatrixFromFile("W3");
			H3 = readMatrixFromFile("H3");

			W4 = readMatrixFromFile("W4");
			H4 = readMatrixFromFile("H4");
			W5 = readMatrixFromFile("W5");
			H5 = readMatrixFromFile("H5");
			W6 = readMatrixFromFile("W6");
			H6 = readMatrixFromFile("H6");

		}

		// loadLocalImage("13");

		loadFixedImage();

		System.out.println("setup done");
	}

	public void mousePressed() {
		noLoop();
	}

	public void mouseReleased() {
		loop();
	}

	public void keyPressed() {

		switch (key) {
		case 'p':
			noLoop();
			break;
		case 'l':
			System.out.println("l");
			break;
		default:
			break;
		}

	}

	public void keyReleased() {
		switch (key) {
		case 'p':
			loop();
			break;

		default:
			break;
		}
	}

	public void draw() {

		if (currentFrame >= numFrames - 1) {
			currentFrame = 0;
		} else {
			currentFrame++;
		}

		for (int i = 0; i < mPixels_size; i++) {

			if ((shadowPixels.get(currentFrame)[i])) {
				pixels[i] = color(W1.get(i) * H1.get(currentFrame), W2.get(i)
						* H2.get(currentFrame),
						W3.get(i) * H3.get(currentFrame));
			} else {
				pixels[i] = color(W1.get(i) * H1.get(currentFrame), W2.get(i)
						* H2.get(currentFrame),
						W3.get(i) * H3.get(currentFrame))
						+ color(W4.get(i) * H4.get(currentFrame), W5.get(i)
								* H5.get(currentFrame),
								W6.get(i) * H6.get(currentFrame));
			}

			// backup();

		}

		updatePixels();

	}

	/*
	 * This is only for future reference
	 */
	void backup(int i) {

		if (sky_and_sun_shadow) {
			// sky + shadow*sun
			if ((shadowPixels.get(currentFrame)[i])) {
				pixels[i] = color(W1.get(i) * H1.get(currentFrame), W2.get(i)
						* H2.get(currentFrame),
						W3.get(i) * H3.get(currentFrame));
			} else {
				pixels[i] = color(W1.get(i) * H1.get(currentFrame), W2.get(i)
						* H2.get(currentFrame),
						W3.get(i) * H3.get(currentFrame))
						+ color(W4.get(i) * H4.get(currentFrame), W5.get(i)
								* H5.get(currentFrame),
								W6.get(i) * H6.get(currentFrame));
			}
		}

		if (sun_shadow_only) {
			// sun + shadow
			if (!(shadowPixels.get(currentFrame)[i])) {
				pixels[i] = color(W4.get(i) * H4.get(currentFrame), W5.get(i)
						* H5.get(currentFrame),
						W6.get(i) * H6.get(currentFrame));
			} else {
				pixels[i] = color(0);
			}
		}

		if (sun_only) {
			// sun + shadow
			pixels[i] = color(W4.get(i) * H4.get(currentFrame),
					W5.get(i) * H5.get(currentFrame),
					W6.get(i) * H6.get(currentFrame));
		}

		if (sky_only) {
			// sun + shadow
			pixels[i] = color(W1.get(i) * H1.get(currentFrame),
					W2.get(i) * H2.get(currentFrame),
					W3.get(i) * H3.get(currentFrame));
		}
	}
}