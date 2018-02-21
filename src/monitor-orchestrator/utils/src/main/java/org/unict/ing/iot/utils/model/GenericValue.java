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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.bson.types.ObjectId;

/**
 * GenericValue has only a Key, extending this class you can creates your model for several kind of Datas
 * Created to make specific-data-structure agnostic 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public abstract class GenericValue implements Serializable {

    
    protected ObjectId _id;
    
    public GenericValue() {
    }

    public GenericValue(ObjectId _id) {
        this._id = _id;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }
    
}
