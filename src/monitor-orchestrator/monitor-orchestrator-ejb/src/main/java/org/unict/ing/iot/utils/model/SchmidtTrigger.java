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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

/**
 *
 * @author aleskandro
 */
public final class SchmidtTrigger implements Serializable {
    private boolean opened;
    
    /**
     * Close the circuit
     */
    @JsonIgnore
    public void open (){
        this.opened = true;
    }
    
    /**
     * Open the circuit
     */
    @JsonIgnore
    public void close() {
        this.opened = false;
    }

    public SchmidtTrigger(boolean opened) {
        this.opened = opened;
    }

    public SchmidtTrigger() {
        open();
    }

    public boolean isOpened() {
        return opened;
    }
    
}
