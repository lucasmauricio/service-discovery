package br.com.macs.poc.registration.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.macs.poc.registration.controller.dto.AssetDto;
import br.com.macs.poc.registration.repository.RegistrationRepository;

@RestController
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	private static Map<String, AssetDto> registeredAssets;

	@Autowired
	private RegistrationRepository registrationRepository;

	@PostConstruct
	public void init() {
		registeredAssets = new HashMap<String, AssetDto>();

		logger.debug("Trying to read persisted data");

		ObjectMapper mapper = new ObjectMapper();
		try {
			String retrievedData = registrationRepository.readData();

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

	private void persistData() {
		// TODO do some concurrency control
		// to be simple, I think of using queues to read and write

		// TODO do some hash check to avoid unnecessary write

		ObjectMapper mapper = new ObjectMapper();
		final StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, registeredAssets);

			registrationRepository.writeData(sw.toString());
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

	@RequestMapping(value = "/assets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AssetDto>> getRegisteredItens() {
		logger.info("GET	/assets -> " + registeredAssets);
		return new ResponseEntity<List<AssetDto>>(new ArrayList<AssetDto>(registeredAssets.values()), HttpStatus.OK);
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AssetDto> getRegisteredItem(@PathVariable String id) {
		logger.info("GET	/asset/" + id);
		AssetDto assetFound = registeredAssets.get(id);
		if (assetFound == null) {
			logger.info("   -> 404 (asset not found)");
			return new ResponseEntity<AssetDto>(HttpStatus.NOT_FOUND);
		} else {
			logger.info("   -> " + assetFound);
			return new ResponseEntity<AssetDto>(assetFound, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registerItem(@RequestBody @Valid AssetDto putAsset, @PathVariable String id) {
		logger.info("PUT	/asset/" + id + putAsset);
		putAsset.setId(id);
		registeredAssets.put(id, putAsset);
		persistData();
		// TODO put some header data (i.e. the new item address)
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AssetDto> deleteRegisteredItem(@PathVariable String id) {
		logger.info("DELETE	/asset/" + id);
		AssetDto assetToDel = registeredAssets.remove(id);
		if (assetToDel == null) {
			logger.info("   -> 404 (asset not found)");
			return new ResponseEntity<AssetDto>(HttpStatus.NOT_FOUND);
		} else {
			logger.info("   -> asset deleted: " + assetToDel);
			persistData();
			return new ResponseEntity<AssetDto>(HttpStatus.OK);
		}
	}
}
