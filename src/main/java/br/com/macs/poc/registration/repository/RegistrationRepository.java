package br.com.macs.poc.registration.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationRepository {

	private static final String DATA_FILE_NAME = "assets-repository.json";
	private static final Logger logger = LoggerFactory.getLogger(RegistrationRepository.class);

	private File dataFile;

	
	public RegistrationRepository() {
		try {
			//using data file at application's path
			//TODO use dir separator from system
			dataFile = new File(System.getProperty("user.dir") + "/" + DATA_FILE_NAME);

			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String readData() {
		logger.debug("Trying to read data from the file " + dataFile.getAbsolutePath());
		
	    StringBuilder contents = new StringBuilder();
	    
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(dataFile));
	      try {
	        String line = null; //not declared within while loop
	        /*
	        * readLine is a bit quirky :
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
		logger.info("Data retrieved from the file " + dataFile.getAbsolutePath());
	    return contents.toString();
	}
	
	
	
	public void writeData(String content) {
		logger.debug("Trying to write data into the file: " + dataFile.getAbsolutePath());
		FileWriter fw;
		try {
			fw = new FileWriter(dataFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			logger.info(String.format("%d bytes of data stored into the file %s", content.length(), dataFile.getAbsolutePath()));
//			logger.info("Data stored into the file " + dataFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
