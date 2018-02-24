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
public final class Tank extends GenericValue implements Serializable {
    private float capacity;
    private float inputFlowRate;
    private float outputFlowRate;
    private int   tankId;
    
    private final SchmidtTrigger trigger;
    private final Electrovalve   valve;

    public Tank(float capacity, float inputFlowRate, float outputFlowRate, int tankId, SchmidtTrigger trigger, Electrovalve valve) {
        this.capacity       = capacity;
        this.inputFlowRate  = inputFlowRate;
        this.outputFlowRate = outputFlowRate;
        this.trigger        = trigger;
        this.valve          = valve;
        this.tankId         = tankId;
    }

    public Tank(float capacity, float inputFlowRate, float outputFlowRate, int tankId) {
        this(capacity, inputFlowRate, outputFlowRate, tankId, new SchmidtTrigger(), new Electrovalve());
    }

    public Tank() {
        this(0, 0, 0, 0);
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public float getInputFlowRate() {
        return inputFlowRate;
    }

    public void setInputFlowRate(float inputFlowRate) {
        this.inputFlowRate = inputFlowRate;
    }

    public float getOutputFlowRate() {
        return outputFlowRate;
    }

    public void setOutputFlowRate(float outputFlowRate) {
        this.outputFlowRate = outputFlowRate;
    }

    public SchmidtTrigger getTrigger() {
        return trigger;
    }

    public Electrovalve getValve() {
        return valve;
    }

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

}
