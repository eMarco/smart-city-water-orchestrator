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
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.mongodriver.DBConnectionSingletonSessionBeanLocal;

/**
 *
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Stateless
public class ClientSessionBean implements ClientSessionBeanRemote {
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @EJB
    private DBConnectionSingletonSessionBeanLocal db;

    @Override
    public List<GenericValue> findByClassName(String name) {
        return this.db.getStorage().findByClassName(name);
    }

    @Override
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, String value) {
        return this.db.getStorage().findByClassNameAndFieldMatch(name, field, value);
    }

    @Override
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, Integer value) {
        return this.db.getStorage().findByClassNameAndFieldMatch(name, field, value);
    }

    @Override
    public List<GenericValue> findByClassNameAndFieldMatch(String name, Map<String, ? extends Object> fields) {
        return this.db.getStorage().findByClassNameAndFieldMatch(name, fields);
    }

    @Override
    public void insert(GenericValue value) {
        this.db.getStorage().insert(value);
    }
}
