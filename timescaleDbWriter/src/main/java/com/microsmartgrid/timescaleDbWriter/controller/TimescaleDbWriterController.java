package com.microsmartgrid.timescaleDbWriter.controller;

import com.microsmartgrid.database.model.DaiSmartGrid.Readings;
import com.microsmartgrid.database.model.DeviceInformation;
import com.microsmartgrid.database.repository.DeviceInformationRepository;
import com.microsmartgrid.database.service.DaiSmartGrid.ReadingsService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
// TODO: Access noch richtig einstellen. Momentan kann jeder darauf zugreifen.
@CrossOrigin(origins = "*", allowedHeaders = "*")
@EnableDiscoveryClient
public class TimescaleDbWriterController {

	@Autowired
	private DeviceInformationRepository deviceInfoRepository;
	@Autowired
	private ReadingsService readingsService;

	/**
	 * Save or update DeviceInformation to a Device
	 *
	 * @param deviceInfo DeviceInformation object to save
	 * @return saved Object
	 */
	@PutMapping("/device")
	public DeviceInformation saveDeviceInfo(DeviceInformation deviceInfo) {
		return deviceInfoRepository.save(deviceInfo);
	}


	@PostMapping("/")
	@ExceptionHandler({IOException.class})
	public Readings writeReadingToDatabase(@RequestParam("topic") String topic, @RequestParam("json") String json) throws IOException, NotFoundException {
		return readingsService.insertReading(topic, json);
	}
}
