/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.ev.charging;

import org.matsim.contrib.ev.data.Charger;
import org.matsim.contrib.ev.data.ElectricVehicle;
import org.matsim.core.api.experimental.events.EventsManager;

import java.util.Collection;

public interface ChargingLogic {
	static interface Factory {
		ChargingLogic create(Charger charger);
	}

	void addVehicle(ElectricVehicle ev, double now);

	void addVehicle(ElectricVehicle ev, ChargingListener chargingListener, double now);

	void removeVehicle(ElectricVehicle ev, double now);

	void chargeVehicles(double chargePeriod, double now);

	void initEventsHandling(EventsManager eventsManager);

	Collection<ElectricVehicle> getPluggedVehicles();

	Collection<ElectricVehicle> getQueuedVehicles();

	ChargingStrategy getChargingStrategy();
}
