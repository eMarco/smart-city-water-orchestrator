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
public class Electrovalve implements Serializable {
    private float flowRateResistance;

    public Electrovalve(float flowRateResistance) {
        this.flowRateResistance = flowRateResistance;
    }

    public Electrovalve() {
        this(0);
    }

    public float getFlowRateResistance() {
        return flowRateResistance;
    }

    public void setFlowRateResistance(float flowRateResistance) {
        this.flowRateResistance = flowRateResistance;
    }
    
    public void increment(float incrementValue) {
        this.flowRateResistance += incrementValue;
    }
    
    public void decrement(float decrementValue) {
        this.flowRateResistance -= decrementValue;
    }
    
    /**
     * TODO
     */
    public void increment() {
        
    }
    
    /**
     * TODO
     */
    public void decrement() {
        
    }
}