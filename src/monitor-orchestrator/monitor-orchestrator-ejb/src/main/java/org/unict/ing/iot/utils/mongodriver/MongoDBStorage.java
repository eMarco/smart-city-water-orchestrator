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
import java.util.LinkedList;
import java.util.List;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.unict.ing.iot.utils.helper.JsonHelper;
import org.unict.ing.iot.utils.model.GenericValue;

/**
 * An implementation of the Storage interface for MongoDB
 */
public class MongoDBStorage implements Storage {

    private final DB db;
    private final MongoCollection collection;

    public MongoDBStorage(DB db) {
        // Using a single connection to provide better (query-oriented) scalability
        this.db = db;
        Jongo jongo = new Jongo(db);
        this.collection = jongo.getCollection("waterCollection2555");
    }
    /**
     * Insert
     * @param elem  |
     */
    @Override
    public void insert(GenericValue elem) {
        System.err.println(JsonHelper.write(elem));
        collection.insert(elem);
    }

    @Override
    public List<GenericValue> find(ObjectId _id) {
        List<GenericValue> ret = new LinkedList<>();
        ret.add(collection.findOne(_id).as(GenericValue.class));
        return ret;
    }

    @Override
    public void remove(ObjectId _id) {
        collection.remove(_id);
    }

    @Override
    public void update(GenericValue elem) {
        collection.save(elem);
    }

    @Override
    public void findOrInsert(GenericValue elem) {
        update(elem);
    }

    @Override
    public void updateOrInsert(GenericValue elem) {
        update(elem);
    }

    @Override
    public List<GenericValue> findByClassName(String name) {
        String query = "{ className: {$regex: \".*" + name + ".*\" } }";
        return findBy(query);
    }

    @Override
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, String value) {
        String query = "{ $and: [ {className: {$regex: \".*" + name + ".*\" } }, { \""+ field + "\": \"" + value + "\" } ] }";
        return findBy(query);
    }

    @Override
    public List<GenericValue> findByClassNameAndFieldMatch(String name, String field, Integer value) {
        String query = "{ $and: [ {className: {$regex: \".*" + name + ".*\" } }, { \""+ field + "\": " + value + " } ] }";
        return findBy(query);
    }

    private List<GenericValue> findBy(String query) {
        MongoCursor<GenericValue> iterDoc;
        List<GenericValue> ret = new LinkedList();

        if (query == null) {
            // Put HERE a default query (TODO)
            iterDoc = collection.find().as(GenericValue.class);
        } else {
            iterDoc = collection.find(query).sort("{_id: -1}").as(GenericValue.class);
        }

        iterDoc.forEach(v -> ret.add(v));
        return ret;
    }

    @Override
    public List<GenericValue> findLast(String field, String className) {
        List<Integer> tanksIds = new LinkedList<>();
        List<GenericValue> ret = new LinkedList<>();
        collection.distinct(field).as(Integer.class).forEach((t) -> {
            tanksIds.add(t);
        });

        tanksIds.forEach((tId) -> {
            ret.add(collection.find("{ $and: [ { \"className\": {$regex: \".*"+ className +".*\" } }, {\"" + field +"\": " + tId + "} ] }").sort("{_id: -1}")
                    .as(GenericValue.class).next());// forEach(v -> ret.add(v));
        });

        return ret;
    }

    @Override
    public List<GenericValue> findLastTanks() {
        return findLast("tankId", "Tank");
    }

    @Override
    public List<GenericValue> findLastSectors() {
        return findLast("sectorId", "Sector");
    }
    

}
