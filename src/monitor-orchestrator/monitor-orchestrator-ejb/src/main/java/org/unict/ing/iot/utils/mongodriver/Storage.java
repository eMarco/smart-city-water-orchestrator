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
package org.unict.ing.iot.utils.mongodriver;

import java.util.List;
import org.bson.types.ObjectId;
import org.unict.ing.iot.utils.model.GenericValue;
/**
 *
 */
public interface Storage {

    public List<GenericValue> find(ObjectId _id);
    public List<GenericValue> findByClassName(String name);
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, String value);
    public List<GenericValue> findLastTanks();
    public void remove(ObjectId _id);
    public void update(GenericValue elem);
    public void insert(GenericValue elem);

    // Not used (TODO delete)
    public void findOrInsert(GenericValue elem);
    public void updateOrInsert(GenericValue elem);
    
}
