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

import org.bson.types.ObjectId;


/**
 *
 * @author aleskandro
 */
public final class Zone extends GenericValue {
    private final Tank tank;

    public Zone(Tank tank, ObjectId _id) {
        super(_id);
        this.tank = tank;
    }

    public Zone(Tank tank) {
        this.tank = tank;
    }

    public Zone() {
        this(new Tank());
    }

    public Tank getTank() {
        return tank;
    }
    
}