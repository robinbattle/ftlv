import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
 
public class PropertyFile {
	
	
	public static HashMap<String, String> readParams(String fileName){
		
		HashMap <String, String> parameters = new HashMap <String, String> ();
		
		Properties prop = new Properties();
		OutputStream output = null;
		InputStream input = null;
		
		try {
			//output = new FileOutputStream("config.properties");
			input = new FileInputStream(fileName);
			// load a properties file
			prop.load(input);

			parameters.put("imageSetPath", prop.getProperty("imageSetPath"));
			parameters.put("threshold", prop.getProperty("threshold"));

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
					//output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}
	 
		return parameters;
		
	}
	
	public static void writeParams(HashMap <String, String> parameters, String fileName){
		Properties prop = new Properties();
		OutputStream output = null;
	 
		try {
	 
			output = new FileOutputStream(fileName);
			for(String s : parameters.keySet()){
				prop.setProperty(s, parameters.get(s));
			}
	 
			// save properties to project root folder
			prop.store(output, null);
	 
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}
	}
	

}