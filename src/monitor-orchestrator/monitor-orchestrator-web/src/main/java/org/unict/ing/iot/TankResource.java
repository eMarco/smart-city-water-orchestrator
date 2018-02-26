/*
 * Copyright (C) 2018 aleskandro - eMarco
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
package org.unict.ing.iot;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.unict.ing.iot.ejb.ClientSessionBeanRemote;
import org.unict.ing.iot.utils.helper.JsonHelper;

/**
 * REST Web Service
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Path("tank")
@RequestScoped
public class TankResource {

    ClientSessionBeanRemote clientSessionBean = lookupClientSessionBeanRemote();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of
     */
    public TankResource() {
    }

    /**
     * tank/
     *
     * @return | all the data
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value = "/")
    public String getAll() {
        return JsonHelper.writeList(clientSessionBean.findByClassName("org.unict.ing.iot.utils.model.Tank"));
    }

    /**
     * tank/id/
     *
     * @param id
     * @return | an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value = "/{id:([0-9]+)}")
    public String getId(
            @PathParam(value = "id") String id) {
        System.out.println(id);
        return JsonHelper.writeList(clientSessionBean.findByClassNameAndFieldMatch("org.unict.ing.iot.utils.model.Tank", "tankId", Integer.parseInt(id)));
    }

    private ClientSessionBeanRemote lookupClientSessionBeanRemote() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ClientSessionBeanRemote) c.lookup("java:global/monitor-orchestrator-ear-1.0-SNAPSHOT/monitor-orchestrator-ejb-1.0-SNAPSHOT/ClientSessionBean!org.unict.ing.iot.ejb.ClientSessionBeanRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
