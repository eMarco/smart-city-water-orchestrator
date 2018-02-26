/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import org.unict.ing.iot.ejb.MonitorSessionBeanRemote;
import org.unict.ing.iot.utils.helper.JsonHelper;

/**
 * REST Web Service
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Path("generic")
@RequestScoped
public class GenericResource {
    MonitorSessionBeanRemote monitorSessionBean = lookupMonitorSessionBeanRemote();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.iot.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getXml() {
        //monitorSessionBean.put(new Zone(new Tank()));
        return JsonHelper.writeList(monitorSessionBean.findByClassName("Tank"));        
        
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
   
    @GET
    @Path("/1")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return Float.toString(monitorSessionBean.obtainValue());
    }

    private MonitorSessionBeanRemote lookupMonitorSessionBeanRemote() {
        try {
            javax.naming.Context c = new InitialContext();
            return (MonitorSessionBeanRemote) c.lookup("java:global/monitor-orchestrator-ear-1.0-SNAPSHOT/monitor-orchestrator-ejb-1.0-SNAPSHOT/MonitorSessionBean!org.unict.ing.iot.ejb.MonitorSessionBeanRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
