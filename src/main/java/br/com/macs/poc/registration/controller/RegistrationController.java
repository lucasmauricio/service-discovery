package br.com.macs.poc.registration.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import br.com.macs.poc.registration.controller.dto.AssetDto;
import br.com.macs.poc.registration.repository.RegistrationRepository;

@RestController
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private RegistrationRepository registrationRepository;


	@RequestMapping(value = "/assets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AssetDto>> getRegisteredItens() {
		logger.info("GET	/assets -> " + registrationRepository.getAssets());
		
		return new ResponseEntity<List<AssetDto>>(registrationRepository.getAssets(), HttpStatus.OK);
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AssetDto> getRegisteredItem(@PathVariable String id) {
		logger.info("GET	/asset/" + id);
		
		AssetDto assetFound = registrationRepository.getAssetById(id);
		if (assetFound == null) {
			logger.info("   -> 404 (asset not found)");
			return new ResponseEntity<AssetDto>(HttpStatus.NOT_FOUND);
		} else {
			logger.info("   -> " + assetFound);
			return new ResponseEntity<AssetDto>(assetFound, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registerItem(@RequestBody @Valid AssetDto putAsset, @PathVariable String id, HttpServletRequest request) {
		logger.info("PUT	/asset/" + id + putAsset);
		logger.info("client address :" + request.getRemoteAddr());
		logger.info(" and " + request.getHeader("X-FORWARDED-FOR"));
		logger.info("client host: " + request.getRemoteHost());
		logger.info("client port: " + request.getRemotePort());
		logger.info("client user: " + request.getRemoteUser());
		logger.info(" user agent: " + request.getHeader("user-agent"));
		
		
		putAsset.setId(id);
		registrationRepository.putAsset(id, putAsset);
		// TODO put some header data (i.e. the new item address)
		
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/asset/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AssetDto> deleteRegisteredItem(@PathVariable String id) {
		logger.info("DELETE	/asset/" + id);
		
		AssetDto assetToDel = registrationRepository.removeAssetById(id);
		if (assetToDel == null) {
			logger.info("   -> 404 (asset not found)");
			return new ResponseEntity<AssetDto>(HttpStatus.NOT_FOUND);
		} else {
			logger.info("   -> asset deleted: " + assetToDel);
			return new ResponseEntity<AssetDto>(HttpStatus.OK);
		}
	}
}
