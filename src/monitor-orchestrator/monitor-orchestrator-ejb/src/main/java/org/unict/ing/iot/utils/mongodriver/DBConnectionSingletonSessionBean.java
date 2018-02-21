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

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import javax.ejb.Singleton;

/**
 * A singleton that is responsible for a single connection to the mongo database
 */
@Singleton
public class DBConnectionSingletonSessionBean implements DBConnectionSingletonSessionBeanLocal {

    private MongoClient     mongo;
    private MongoCredential credential;
    private DB   database;

    private Storage storage;
    
    @Override
    public DB getDatabase () {
        if (database == null) {
            // Creating a Mongo client
            mongo = new MongoClient("mongodb", 27017);
            // Creating Credentials (not needed?)
            // User, Db, Password
            //credential = MongoCredential.createCredential("", "", "".toCharArray());
            System.out.println("Connected to the database successfully");
            // Accessing the database
            database = mongo.getDB("myDb");
            storage = new MongoDBStorage(database);
                    
        }
        return database;
    }
    
    @Override
    public Storage getStorage() {
        getDatabase();
        return storage;
    }
}
