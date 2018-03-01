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
    private static float STEP = (float) 0.10;

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
        this.flowRateResistance *= (1 + incrementValue);
        if (this.flowRateResistance < 1) {
            this.flowRateResistance = 100;
        }
    }

    public void decrement(float decrementValue) {
        if (decrementValue >= 0)
            this.flowRateResistance *= (1 - decrementValue);
        else
            System.err.println("Decrementing would set a value equal or less than 0");
    }

    public void increment() {
        increment(STEP);
    }

    public void decrement() {
        decrement(STEP);
    }
}
