/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.unict.ing.iot.SectorResource.class);
        resources.add(org.unict.ing.iot.TankResource.class);
        resources.add(org.unict.ing.iot.TestResource.class);
    }

}
