/*
 * Copyright (C) 2018 Marco Grassia <marco.grassia@studium.unict.it>
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
package org.unict.ing.iot.ejb;

import java.util.List;
import javax.ejb.Remote;
import org.unict.ing.iot.utils.model.GenericValue;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Remote
public interface ClientSessionBeanRemote {
    public List<GenericValue> findByClassName(String name);
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, String value);
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, Integer value);

    public void insert(GenericValue value);
}
