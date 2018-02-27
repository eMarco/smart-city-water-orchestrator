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

import java.io.Serializable;

/**
 *
 * @author aleskandro
 */
public final class Sector extends GenericValue implements Serializable {
    private float flowRate;
    private float flowRateCounted;
    private int   ownerTankId;
    private int   sectorId;
    private final SchmidtTrigger trigger;

    public int getOwnerTankId() {
        return ownerTankId;
    }

    public float getFlowRateCounted() {
        return flowRateCounted;
    }

    public void setFlowRateCounted(float flowRateCounted) {
        this.flowRateCounted = flowRateCounted;
    }

    public void setOwnerTankId(int ownerTankId) {
        this.ownerTankId = ownerTankId;
    }

    public int getSectorId() {
        return sectorId;
    }

    public void setSectorId(int sectorId) {
        this.sectorId = sectorId;
    }



    public Sector(float flowRate, float flowRateCounted, int tankId, SchmidtTrigger trigger, int sectorId) {
        this.flowRate    = flowRate;
        this.trigger     = trigger;
        this.ownerTankId = tankId;
        this.sectorId    = sectorId;
    }

    public Sector() {
        this(0, 0, 0, new SchmidtTrigger(), 0);
    }


    public float getFlowRate() {
        return flowRate;
    }


    public SchmidtTrigger getTrigger() {
        return trigger;
    }

    public int getTankId() {
        return ownerTankId;
    }

    @Override
    public String toString() {
        return "Sector{" + "flowRate=" + flowRate + ", tankId=" + ownerTankId + ", trigger=" + trigger + '}';
    }

}
