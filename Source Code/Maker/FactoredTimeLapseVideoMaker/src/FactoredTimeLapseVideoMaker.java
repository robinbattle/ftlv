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
public class FactoredTimeLapseVideoMaker extends PApplet {

	float threshold = 90;
	final int screenWidth = 640;
	final int screenHeight = 360;

	PFont font;
	String outputName;

	int currentFrame = 0;
	int numFrames = 0;
	int mPixels_size = screenWidth * screenHeight;

	float[][] mPixels;

	PImage img;

	String shadowOutputFolder;
	String matrixOutputFolder;
	String fixedImageOutputFolder;
	String dataFolder;

	/* Store all the shadow pixels of each frame */
	TreeMap<Integer, boolean[]> shadowPixels = new TreeMap<Integer, boolean[]>();

	/* Store the median of 20% smallest intensity */
	HashMap<Integer, Float> pixelsMedian = new HashMap<Integer, Float>();
	Matrix X1, W1, H1, X2, W2, H2, X3, W3, H3, X4, W4, H4, X5, W5, H5, X6, W6,
			H6;

	public FactoredTimeLapseVideoMaker() {
		VideoSetter vs = new VideoSetter();

		while (!vs.done) { // wait until user finish setting
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// parameters
		HashMap<String, String> parameters = PropertyFile
				.readParams("../../config.properties");

		threshold = Integer.valueOf(parameters.get("threshold"));
		dataFolder = parameters.get("imageSetPath");

		outputName = "output " + System.currentTimeMillis();

		new File(dataFolder + "/../" + outputName + "/shadow/").mkdirs();
		new File(dataFolder + "/../" + outputName + "/matrix/").mkdirs();
		new File(dataFolder + "/../" + outputName + "/fixedImage/").mkdirs();

		shadowOutputFolder = dataFolder + "/../" + outputName + "/shadow/";
		matrixOutputFolder = dataFolder + "/../" + outputName + "/matrix/";
		fixedImageOutputFolder = dataFolder + "/../" + outputName
				+ "/fixedImage/";


		File f = new File(dataFolder);
		numFrames = f.listFiles().length;
		mPixels = new float[numFrames][mPixels_size];

		/* Init NMF */
		X1 = new Matrix(mPixels_size, numFrames);
		W1 = new Matrix(mPixels_size, 1);
		H1 = new Matrix(1, numFrames);
		X2 = new Matrix(mPixels_size, numFrames);
		W2 = new Matrix(mPixels_size, 1);
		H2 = new Matrix(1, numFrames);
		X3 = new Matrix(mPixels_size, numFrames);
		W3 = new Matrix(mPixels_size, 1);
		H3 = new Matrix(1, numFrames);

		MatrixUtilities.randomize(X1);
		MatrixUtilities.randomize(W1);
		MatrixUtilities.randomize(H1);
		MatrixUtilities.randomize(X2);
		MatrixUtilities.randomize(W2);
		MatrixUtilities.randomize(H2);
		MatrixUtilities.randomize(X3);
		MatrixUtilities.randomize(W3);
		MatrixUtilities.randomize(H3);

		X4 = new Matrix(mPixels_size, numFrames);
		W4 = new Matrix(mPixels_size, 1);
		H4 = new Matrix(1, numFrames);
		X5 = new Matrix(mPixels_size, numFrames);
		W5 = new Matrix(mPixels_size, 1);
		H5 = new Matrix(1, numFrames);
		X6 = new Matrix(mPixels_size, numFrames);
		W6 = new Matrix(mPixels_size, 1);
		H6 = new Matrix(1, numFrames);

		MatrixUtilities.randomize(X4);
		MatrixUtilities.randomize(W4);
		MatrixUtilities.randomize(H4);
		MatrixUtilities.randomize(X5);
		MatrixUtilities.randomize(W5);
		MatrixUtilities.randomize(H5);
		MatrixUtilities.randomize(X6);
		MatrixUtilities.randomize(W6);
		MatrixUtilities.randomize(H6);

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

		PImage newImage = createImage(640, 360, RGB);
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

		newImage = createImage(640, 360, RGB);
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

	void SetUpDefaultPixels() {
		/* <Pixel Index, Intensity TreeMap> */
		HashMap<Integer, TreeMap<String, Float>> tempHashMap = new HashMap<Integer, TreeMap<String, Float>>();

		File[] files = new File(dataFolder).listFiles();

		for (int frameIndex = 1; frameIndex < numFrames + 1; frameIndex++) {
			String frameIndexString = "";

			if (frameIndex < 10)
				frameIndexString = "0" + frameIndex;
			else
				frameIndexString = "" + frameIndex;

			loadLocalImage(files[frameIndex - 1].getAbsolutePath());

			for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {

				float r = red(img.pixels[pixelIndex]);
				float g = green(img.pixels[pixelIndex]);
				float b = blue(img.pixels[pixelIndex]);

				float brightness = brightness(img.pixels[pixelIndex]);

				mPixels[frameIndex - 1][pixelIndex] = brightness;
				X1.set(pixelIndex, frameIndex - 1, r);
				X2.set(pixelIndex, frameIndex - 1, g);
				X3.set(pixelIndex, frameIndex - 1, b);

				X4.set(pixelIndex, frameIndex - 1, r);
				X5.set(pixelIndex, frameIndex - 1, g);
				X6.set(pixelIndex, frameIndex - 1, b);

				if (frameIndex == 1) {
					TreeMap<String, Float> pixelIntensityMap = new TreeMap<String, Float>();
					pixelIntensityMap.put(frameIndexString, brightness);
					tempHashMap.put(pixelIndex, pixelIntensityMap);
				} else
					tempHashMap.get(pixelIndex).put(frameIndexString,
							brightness);
			}
		}

		// Sort the treeMap.
		int pixelIndex = 0;
		float median = 0;
		for (TreeMap<String, Float> pixelIntensityMap : tempHashMap.values()) {
			List<Map.Entry<String, Float>> arrayList = new ArrayList<Map.Entry<String, Float>>(
					pixelIntensityMap.entrySet());
			Collections.sort(arrayList, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry obj1 = (Map.Entry) o1;
					Map.Entry obj2 = (Map.Entry) o2;
					return ((Float) obj1.getValue()).compareTo((Float) obj2
							.getValue());
				}
			});

			// Calculate median of 20% of smallest intensity pixels.
			int medianIndex = 0;
			medianIndex = (numFrames / 5) / 2;

			if ((numFrames / 5) % 2 == 1)
				median = arrayList.get(medianIndex).getValue();
			else
				median = (arrayList.get(medianIndex).getValue() + arrayList
						.get(medianIndex - 1).getValue()) / 2;

			pixelsMedian.put(pixelIndex++, median);
		}

		// System.out.println(X);

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
			System.out.println("successfully write shadow to "
					+ shadowOutputFolder + frameIndex);

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
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
	 * Set the Shadow Pixels to true which is less than the threshold Save them
	 * into the files.
	 */
	void setShadowPixels() {
		for (int i = 0; i < numFrames; i++) {
			boolean[] shadowPixelArray = new boolean[mPixels_size];
			for (int j = 0; j < mPixels_size; j++) {
				float _threshold = 0;

				if (_threshold > threshold) {
					_threshold = 1.5f * pixelsMedian.get(j);
				} else {
					_threshold = threshold;
				}

				if (mPixels[i][j] < _threshold) {
					shadowPixelArray[j] = true;
				} else {
					shadowPixelArray[j] = false;
				}
			}
			shadowPixels.put(i, shadowPixelArray);
			writeShadowintoFile(i, shadowPixelArray);
		}

		for (int frameIndex = 1; frameIndex < numFrames + 1; frameIndex++) {

			for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {

				if (!(shadowPixels.get(frameIndex - 1)[pixelIndex])) {
					X1.set(pixelIndex, frameIndex - 1, 0);
					X2.set(pixelIndex, frameIndex - 1, 0);
					X3.set(pixelIndex, frameIndex - 1, 0);
				} else {
					X4.set(pixelIndex, frameIndex - 1, 0);
					X5.set(pixelIndex, frameIndex - 1, 0);
					X6.set(pixelIndex, frameIndex - 1, 0);
				}

			}
		}

	}

	void setupNMF() {
		/* NMF */
		NMFCostKL nmfSolver1 = new NMFCostKL(X1, W1, H1, "solver1");
		NMFCostKL nmfSolver2 = new NMFCostKL(X2, W2, H2, "solver2");
		NMFCostKL nmfSolver3 = new NMFCostKL(X3, W3, H3, "solver3");

		NMFCostKL nmfSolver4 = new NMFCostKL(X4, W4, H4, "solver4");
		NMFCostKL nmfSolver5 = new NMFCostKL(X5, W5, H5, "solver5");
		NMFCostKL nmfSolver6 = new NMFCostKL(X6, W6, H6, "solver6");

		int iterations = 5;
		for (int i = 0; i < iterations; i++) {
			System.out.println(i + ": " + W1.get(0));
			nmfSolver1.doLeftUpdate();
			nmfSolver1.doRightUpdate();
			nmfSolver2.doLeftUpdate();
			nmfSolver2.doRightUpdate();
			nmfSolver3.doLeftUpdate();
			nmfSolver3.doRightUpdate();

			nmfSolver4.doLeftUpdate();
			nmfSolver4.doRightUpdate();
			nmfSolver5.doLeftUpdate();
			nmfSolver5.doRightUpdate();
			nmfSolver6.doLeftUpdate();
			nmfSolver6.doRightUpdate();

			System.out.println("Iteration " + i);
		}

		writeMatrixintoFile("W1", W1);
		writeMatrixintoFile("H1", H1);
		writeMatrixintoFile("W2", W2);
		writeMatrixintoFile("H2", H2);
		writeMatrixintoFile("W3", W3);
		writeMatrixintoFile("H3", H3);

		writeMatrixintoFile("W4", W4);
		writeMatrixintoFile("H4", H4);
		writeMatrixintoFile("W5", W5);
		writeMatrixintoFile("H5", H5);
		writeMatrixintoFile("W6", W6);
		writeMatrixintoFile("H6", H6);

		System.out.println("NMF Done");

	}

	public void setup() {
		font = createFont("Arial", 25, true);
		size(screenWidth, screenHeight);
		frameRate(10);

		if (frame != null) {
			frame.setResizable(true);
		}

		File f = new File(shadowOutputFolder + 1);
		System.out.println(f.exists());

		SetUpDefaultPixels();
		setShadowPixels();
		setupNMF();

		outputFixingImage();

		System.out.println("setup done");
	}

	private void outputFixingImage() {
		loadPixels();

		PImage newImageSky = createImage(640, 360, RGB);
		PImage newImageSun = createImage(640, 360, RGB);
		for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {
			newImageSky.pixels[pixelIndex] = color(
					W1.get(pixelIndex) * H1.get(0),
					W2.get(pixelIndex) * H2.get(0),
					W3.get(pixelIndex) * H3.get(0));
			newImageSun.pixels[pixelIndex] = color(
					W4.get(pixelIndex) * H4.get(0),
					W5.get(pixelIndex) * H5.get(0),
					W6.get(pixelIndex) * H6.get(0));
		}
		newImageSky.save(fixedImageOutputFolder + "Sky.jpg");
		System.out.println("save: " + fixedImageOutputFolder + "Sky.jpg");

		newImageSun.save(fixedImageOutputFolder + "Sun.jpg");
		System.out.println("save: " + fixedImageOutputFolder + "Sun.jpg");
	}

	public void draw() {
		background(255);
		textFont(font, 25);
		fill(0);
		text("Video compression is done and saved in \n\"" + outputName + "\"", 10, 100);
	}
}