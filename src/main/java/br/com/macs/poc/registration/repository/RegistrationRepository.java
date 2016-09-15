package br.com.macs.poc.registration.repository;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.macs.poc.registration.controller.dto.AssetDto;


@Repository
public class RegistrationRepository {

	private static final String DATA_FILE_NAME = "assets-repository.json";
	private static final Logger logger = LoggerFactory.getLogger(RegistrationRepository.class);

	//TODO do not access the collection directly to avoid null pointers problems
	private static Map<String, AssetDto> registeredAssets;

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
	
	
	@PostConstruct
	public void init() {
		registeredAssets = new HashMap<String, AssetDto>();

		logger.debug("Trying to read persisted data");

		ObjectMapper mapper = new ObjectMapper();
		try {
			String retrievedData = readDataFromFile();

			if (!retrievedData.isEmpty()) {
				registeredAssets = mapper.readValue(retrievedData, new TypeReference<HashMap<String, AssetDto>>() {
				});
				logger.info(String.format("%d asset(s) retrieved from repository", registeredAssets.size()));
			} else {
				logger.info("No data found in the repository");
			}

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("Registration service initialized");
	}
	
	
	private String readDataFromFile() {
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
	
	
	
	private void writeDataToFile(String content) {
		logger.debug("Trying to write data into the file: " + dataFile.getAbsolutePath());
		FileWriter fw;
		try {
			fw = new FileWriter(dataFile.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(content);
	        bw.append(System.lineSeparator());
			bw.close();

			logger.info(String.format("%d bytes of data stored into the file %s", content.length(), dataFile.getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	private void doPersistData() {
		// TODO do some concurrency control
		// to be simple, I think of using queues to read and write

		// TODO do some hash check to avoid unnecessary write

		ObjectMapper mapper = new ObjectMapper();
		final StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, registeredAssets);

			writeDataToFile(sw.toString());
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public List<AssetDto> getAssets() {
		return new ArrayList<AssetDto>(registeredAssets.values());
	}



	public AssetDto getAssetById(String id) {
		return registeredAssets.get(id);
	}



	//TODO return true if it was successfully
	public void putAsset(String id, AssetDto putAsset) {
		registeredAssets.put(id, putAsset);
		doPersistData();
	}



	public AssetDto removeAssetById(String id) {
		AssetDto assetRemoved = registeredAssets.remove(id);
		if (assetRemoved != null) {
			doPersistData();
		}
		return assetRemoved;
	}
	
}
