package com.microsmartgrid.database.dbDataStructures.shellies;

import com.microsmartgrid.database.dbDataStructures.AbstractDevice;

public class shellyplug extends AbstractDevice {

	//inherent vars
	private Float power;
	private Float temperature;
	private long energy;
	private boolean over_temperature;
	private boolean off;

	protected shellyplug() {
	}
}
