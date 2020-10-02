/*
 * Copyright 2018 TFI Systems

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.galfins.gnss_compare.Constellations;

import android.location.GnssMeasurementsEvent;
import android.location.Location;

import com.galfins.gnss_compare.Corrections.Correction;
import com.galfins.gogpsextracts.Coordinates;
import com.galfins.gogpsextracts.Time;

import java.util.ArrayList;
import java.util.List;

public class GpsL1L5Constellation extends Constellation {

    private GpsConstellation gpsL1Constellation = new GpsConstellation();
    private GpsL5Constellation gpsL5Constellation = new GpsL5Constellation();

    private static final String NAME = "GPS L1 + L5";

    /**
     * List holding observed satellites
     */
    protected List<SatelliteParameters> observedSatellites = new ArrayList<>();
    private List<SatelliteParameters> unusedSatellites = new ArrayList<>();

    @Override
    public Coordinates getRxPos() {
        synchronized (this) {
            return gpsL1Constellation.getRxPos();
        }
    }

    @Override
    public void setRxPos(Coordinates rxPos) {
        synchronized (this) {
            gpsL1Constellation.setRxPos(rxPos);
            gpsL5Constellation.setRxPos(rxPos);
        }
    }

    @Override
    public SatelliteParameters getSatellite(int index) {
        synchronized (this) {
            return observedSatellites.get(index);
        }
    }

    @Override
    public List<SatelliteParameters> getSatellites() {
        synchronized (this) {
            return observedSatellites;
        }
    }

    @Override
    public List<SatelliteParameters> getUnusedSatellites() {
        return unusedSatellites;
    }

    @Override
    public int getVisibleConstellationSize() {
        synchronized (this) {
            return observedSatellites.size()+unusedSatellites.size();
        }
    }

    @Override
    public int getUsedConstellationSize() {
        synchronized (this) {
            return observedSatellites.size();
        }
    }

    @Override
    public void calculateSatPosition(Location location, Coordinates position) {
        synchronized (this) {
            gpsL1Constellation.calculateSatPosition(location, position);
            gpsL5Constellation.calculateSatPosition(location, position);

            observedSatellites.clear();
            unusedSatellites.clear();

            for (int i=0; i<gpsL1Constellation.getUsedConstellationSize(); i++){
                observedSatellites.add(gpsL1Constellation.getSatellite(i));
            }

            for (int i=0; i<gpsL5Constellation.getUsedConstellationSize(); i++){
                observedSatellites.add(gpsL5Constellation.getSatellite(i));
            }

            unusedSatellites.addAll(gpsL1Constellation.getUnusedSatellites());
            unusedSatellites.addAll(gpsL5Constellation.getUnusedSatellites());
        }
    }

    public void updateMeasurements(GnssMeasurementsEvent event) {
        synchronized (this) {

            gpsL5Constellation.updateMeasurements(event);
            gpsL1Constellation.updateMeasurements(event);

        }
    }

    @Override
    public double getSatelliteSignalStrength(int index) {
        synchronized (this) {
            return observedSatellites.get(index).getSignalStrength();
        }
    }

    @Override
    public int getConstellationId() {
        synchronized (this) {
            return Constellation.CONSTELLATION_GPS_L1L5;
        }
    }

    @Override
    public void addCorrections(ArrayList<Correction> corrections) {
        synchronized (this) {
            gpsL1Constellation.addCorrections(corrections);
            gpsL5Constellation.addCorrections(corrections);
        }
    }

    @Override
    public Time getTime() {
        synchronized (this) {
            return gpsL1Constellation.getTime();
        }
    }

    @Override
    public String getName() {
        synchronized (this) {
            return NAME;
        }
    }

    public static void registerClass() {
        register(
                NAME,
                GpsL1L5Constellation.class);
    }
}
