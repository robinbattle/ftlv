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
public class FactoredTimeLapseVideo extends PApplet {

	float threshold = 150;
	final int screenWidth = 640;
	final int screenHeight = 360;

	/* Specify which DataSet we are using */
	int datasetIndex = 3;
	boolean useDefalutVal = false;

	int currentFrame = 0;
	int numFrames = 0;
	int mPixels_size = screenWidth * screenHeight;
	float[][] mPixels;
	int[] initImage;

	PImage img;

	String shadowOutputFolder = "../../shadow/";
	String dataFolder = "../../data/tl" + datasetIndex + "/";

	/* Store all the shadow pixels of each frame */
	TreeMap<Integer, boolean[]> shadowPixels = new TreeMap<Integer, boolean[]>();

	/* Store the median of 20% smallest intensity */
	HashMap<Integer, Float> pixelsMedian = new HashMap<Integer, Float>();
	
	File[] files = new File(dataFolder).listFiles();

	public FactoredTimeLapseVideo() {
		File f = new File(dataFolder);
		numFrames = f.listFiles().length;
		mPixels = new float[numFrames][mPixels_size];
		initImage = new int[mPixels_size];
		String filename = "../../generated/output.txt";
		String dataFolder = "../../data/tl" + datasetIndex + "/";
	}

	void loadLocalImage(String fileName) {
		loadPixels();

		//String fileName = dataFolder + indexString + ".jpg";
		System.out.println("load: " + fileName);

		img = loadImage(fileName);
		img.resize(screenWidth, screenHeight);

		// We must also call loadPixels() on the PImage since we are going
		// to read its pixels.
		img.loadPixels();
	}


	void SetUpDefaultPixels() {
		/* <Pixel Index, Intensity TreeMap> */
		HashMap<Integer, TreeMap<String, Float>> tempHashMap = new HashMap<Integer, TreeMap<String, Float>>();
		for (int frameIndex = 1; frameIndex < numFrames + 1; frameIndex++) {
			String frameIndexString = "";

			if (frameIndex < 10)
				frameIndexString = "0" + frameIndex;
			else
				frameIndexString = "" + frameIndex;
			loadLocalImage(files[frameIndex-1].getAbsolutePath());

			for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {
				float r = red(img.pixels[pixelIndex]);
				float g = green(img.pixels[pixelIndex]);
				float b = blue(img.pixels[pixelIndex]);

				float brightness = brightness(img.pixels[pixelIndex]);

				mPixels[frameIndex - 1][pixelIndex] = brightness;

				if (frameIndex == 1){
					TreeMap<String, Float> pixelIntensityMap = new TreeMap<String, Float>();
					pixelIntensityMap.put(frameIndexString, brightness);
					tempHashMap.put(pixelIndex, pixelIntensityMap);
				}
				else
					tempHashMap.get(pixelIndex).put(frameIndexString, brightness);
			}
		}

		// Sort the treeMap.
		int pixelIndex = 0;
		float median = 0;
		for (TreeMap<String, Float> pixelIntensityMap : tempHashMap.values()) {
			List<Map.Entry<String, Float>> arrayList = new ArrayList<Map.Entry<String, Float>>(pixelIntensityMap.entrySet());
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
			medianIndex = (numFrames/5)/2;

			if ((numFrames/5) % 2 == 1)
				median = arrayList.get(medianIndex).getValue();
			else
				median = (arrayList.get(medianIndex).getValue() + arrayList.get(medianIndex - 1).getValue()) / 2;

			pixelsMedian.put(pixelIndex++, median);
		}
	}


	/**
	 * Write the shadow into the file.
	 * @param frameIndex
	 * @param array
	 */
	void writeShadowintoFile(int frameIndex, boolean[] array){
		try {
			FileOutputStream outStream = new FileOutputStream(shadowOutputFolder + frameIndex);  
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);  
			objectOutputStream.writeObject(array);  
			outStream.close();  
			System.out.println("successful");  

		} catch (FileNotFoundException e) {  
			e.printStackTrace();  

		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}

	/**
	 * Read the Shadow from data file.
	 */
	void readShadowFromFile(){
		FileInputStream freader;  
		try {  
			File dir = new File(shadowOutputFolder);
			File files[] = dir.listFiles();
			for (File file : files) {
				freader = new FileInputStream(file);  
				ObjectInputStream objectInputStream = new ObjectInputStream(freader);  
				boolean[] array = (boolean[]) objectInputStream.readObject(); 
				shadowPixels.put(Integer.valueOf(file.toString().substring(file.toString().lastIndexOf("\\") + 1)), array);	
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
	 * Set the Shadow Pixels to true which is less than the threshold
	 * Save them into the files.
	 */
	void setShadowPixels(){
		for (int i = 0; i < numFrames; i++) {
			boolean[] shadowPixelArray = new boolean[mPixels_size]; 
			for (int j = 0; j < mPixels_size; j++) {
				float _threshold = 0;
				if (useDefalutVal)
					_threshold = threshold;
				else {
					float tmp = 1.5f * pixelsMedian.get(j);
					if(tmp > threshold){
						_threshold =  tmp;
					}else{
						_threshold =  threshold;
					}
					
				}
				if (mPixels[i][j] < _threshold){
					shadowPixelArray[j] = true;
				}
				else {
					shadowPixelArray[j] = false;
				}
			}
			shadowPixels.put(i, shadowPixelArray);
			writeShadowintoFile(i , shadowPixelArray);
		}
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
		} else {
			SetUpDefaultPixels();
			setShadowPixels();
		}

		loadLocalImage(files[57].getAbsolutePath());
		System.out.println("setup done");
	}

	public void draw() {

		if (currentFrame >= numFrames - 1) {
			currentFrame = 0;
		} else {
			currentFrame++;
		}

		for (int i = 0; i < mPixels_size; i++) {		
			if (!(shadowPixels.get(currentFrame)[i])) {
				//pixels[i] = color(255);
				pixels[i] = img.pixels[i];
			} else {

				pixels[i] = color(0);
			}
		}
		


		updatePixels();

	}
}
