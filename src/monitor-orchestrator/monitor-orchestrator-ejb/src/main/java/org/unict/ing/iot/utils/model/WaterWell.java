/* 
 * Copyright (C) 2018 aleskandro - eMarco - cursedLondor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.unict.ing.iot.utils.model;

import java.util.LinkedList;
import java.util.List;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 *
 * @author aleskandro
 */
public final class WaterWell extends GenericValue {
    private float flowRate;
    private final List<Zone> zones;

    public WaterWell(float flowRate) {
        this.flowRate = flowRate;
        this.zones = new LinkedList<>();
    }

    public WaterWell(float flowRate, ObjectId _id) {
        super(_id);
        this.flowRate = flowRate;
        this.zones = new LinkedList<>();
    }

    public WaterWell(float flowRate, List<Zone> zones, ObjectId _id) {
        super(_id);
        this.flowRate = flowRate;
        
        this.zones = zones;
    }

    public List<Zone> getZones() {
        return zones;
    }
    
    public float getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(float flowRate) {
        this.flowRate = flowRate;
    }
}
