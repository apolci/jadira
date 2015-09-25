/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.dateandtime.threeten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.ext.oracle.OracleConnection;
import org.hibernate.engine.jdbc.spi.JdbcWrapper;
import org.hibernate.jdbc.Work;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalDateTimeJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.shared.dbunit.RuntimeDatabaseUnitException;
import org.jadira.usertype.dateandtime.threeten.testmodel.LocalDateTimeJdk8;
import org.jadira.usertype.dateandtime.threeten.testmodel.LocalDateTimeJdk8JavaZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentLocalDateTime extends DatabaseCapable {

    private static final LocalDateTime[] localDateTimes = new LocalDateTime[] { LocalDateTime.of(2004, 2, 25, 12, 11, 10), LocalDateTime.of(1980, 3, 11, 13, 12, 11) };

    private static final org.joda.time.LocalDateTime[] jodaLocalDateTimes = new org.joda.time.LocalDateTime[] {
        new org.joda.time.LocalDateTime(2004, 2, 25, 12, 11, 10),
        new org.joda.time.LocalDateTime(1980, 3, 11, 13, 12, 11) };

    private static EntityManagerFactory factory;

	private static TimeZone defaultTimezone;

	private static String userTimezone;

    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
        defaultTimezone = TimeZone.getDefault();
        userTimezone = System.getProperty("user.timezone");
    }

    @AfterClass
    public static void tearDown() {
    	TimeZone.setDefault(defaultTimezone);
    	System.setProperty("user.timezone", userTimezone);
    	factory.close();
    }
    
    @After
    public void cleanTables() {
		EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		manager.createNativeQuery("delete " + LocalDateTimeJdk8JavaZone.class.getAnnotation(Table.class).name()).executeUpdate();
		manager.getTransaction().commit();
    }

    @Test
    public void testPersist() {
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8 item = new LocalDateTimeJdk8();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8 item = manager.find(LocalDateTimeJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable(manager, LocalDateTimeJdk8.class.getAnnotation(Table.class).name());

        manager.close();
    }
    
	protected void clearTable(final EntityManager manager, final String tableName) throws RuntimeDatabaseUnitException {

		manager.createNativeQuery("delete table " + tableName);

	}

    
    
    @Test
    public void testPersistWithTimezoneZurich() {
    	System.setProperty("user.timezone", "Europe/Zurich");
    	TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zurich"));
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = new LocalDateTimeJdk8JavaZone();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = manager.find(LocalDateTimeJdk8JavaZone.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable(manager, LocalDateTimeJdk8JavaZone.class.getAnnotation(Table.class).name());

        manager.close();
    }
    
    
    @Test
    public void testPersistWithTimezone1() {
    	System.setProperty("user.timezone", "+1");
    	TimeZone.setDefault(TimeZone.getTimeZone("+1"));
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = new LocalDateTimeJdk8JavaZone();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = manager.find(LocalDateTimeJdk8JavaZone.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable(manager, LocalDateTimeJdk8JavaZone.class.getAnnotation(Table.class).name());

        manager.close();
    }
    
    @Test
    public void testPersistWithTimezoneZ() {
    	System.setProperty("user.timezone", "Z");
    	TimeZone.setDefault(TimeZone.getTimeZone("Z"));
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = new LocalDateTimeJdk8JavaZone();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8JavaZone item = manager.find(LocalDateTimeJdk8JavaZone.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable(manager, LocalDateTimeJdk8JavaZone.class.getAnnotation(Table.class).name());

        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localDateTimes.length; i++) {
            manager.remove(manager.find(LocalDateTimeJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJoda item = new LocalDateTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(jodaLocalDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJdk8 item = manager.find(LocalDateTimeJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }
        manager.close();
    }

    @Test @Ignore // Nanos are not properly supported by JodaTime type
    public void testNanosWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localDateTimes.length; i++) {
            manager.remove(manager.find(LocalDateTimeJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        LocalDateTimeJdk8 item = new LocalDateTimeJdk8();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalDateTime(LocalDateTime.of(2010, 8, 1, 10, 10, 10, 111444444));

        manager.persist(item);
        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        LocalDateTimeJoda jodaItem = manager.find(LocalDateTimeJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.LocalDateTime(2010, 8, 1, 10, 10, 10, 111), jodaItem.getLocalDateTime());

        manager.close();

        manager = factory.createEntityManager();

        item = manager.find(LocalDateTimeJdk8.class, Long.valueOf(1));

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(LocalDateTime.of(2010, 8, 1, 10, 10, 10, 111444444), item.getLocalDateTime());

        manager.close();
    }
}
