package org.bonitasoft.migration.versions.v6_4_0_to_6_4_1

import static org.assertj.core.api.Assertions.*
import static org.bonitasoft.migration.DBUnitHelper.*
import groovy.sql.Sql

import org.dbunit.JdbcDatabaseTester

/**
 * @author Emmanuel Duchastenier
 */
class MigrateDateDataInstancesFromWrongXMLObjectIT extends GroovyTestCase {

    Sql sql
    JdbcDatabaseTester tester

    @Override
    void setUp() {
        sql = createSqlConnection();
        tester = createTester()

        createTables(sql, "data_instance")
    }


    @Override
    void tearDown() {
        tester.onTearDown();

        def String[] tables = [
            "data_instance",
            "arch_data_instance"
        ]
        dropTables(sql, tables)
    }

    void test_migrate_should_move_data_instances_from_xmlobject_to_date_column() throws Exception {
        //given
        def Long dateTime = 1418660268855;
        def xmlDate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><date>2014-12-15 16:17:48.855 UTC</date>"; // equivalent to 1418660268855 in UTC and English locale (XStream defaults)
        def xmlNullDate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><null></null>";

        sql.executeInsert("INSERT INTO data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, CLASSNAME) VALUES (1, 14, null, '" + xmlDate + "', 'SXMLObjectDataInstanceImpl', 'java.util.Date')")
        sql.executeInsert("INSERT INTO data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, CLASSNAME) VALUES (1, 15, null, '" + xmlNullDate + "', 'SXMLObjectDataInstanceImpl', 'java.util.Date')")
        sql.executeInsert("INSERT INTO data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, CLASSNAME) VALUES (1, 16, null, null, 'SXMLObjectDataInstanceImpl', 'java.util.Date')")
        def dateDataInstanceMigration = new MigrateDateDataInstancesFromWrongXMLObject(sql, dbVendor())

        //when
        dateDataInstanceMigration.migrate()

        //then
        def row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM data_instance where tenantid = 1 and ID = 14");
        def String name = row.getProperty("NAME")
        def Long dateAsLong = row.getProperty("LONGVALUE")
        def String dateAsClob = row.getProperty("CLOBVALUE")
        def String dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isEqualTo(dateTime)
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SDateDataInstanceImpl")

        row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM data_instance where tenantid = 1 and ID = 15");
        name = row.getProperty("NAME")
        dateAsLong = row.getProperty("LONGVALUE")
        dateAsClob = row.getProperty("CLOBVALUE")
        dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isNull()
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SDateDataInstanceImpl")

        row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM data_instance where tenantid = 1 and ID = 16");
        name = row.getProperty("NAME")
        dateAsLong = row.getProperty("LONGVALUE")
        dateAsClob = row.getProperty("CLOBVALUE")
        dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isNull()
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SDateDataInstanceImpl")
    }

    void test_migrate_should_move_archived_data_instances_from_xmlobject_to_date_column() throws Exception {
        //given
        def Long dateTime = 1418660268855;
        def xmlDate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><date>2014-12-15 16:17:48.855 UTC</date>"; // equivalent to 1418660268855 in UTC and English locale (XStream defaults)
        def xmlNullDate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><null></null>";

        sql.executeInsert("INSERT INTO arch_data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, ARCHIVEDATE, SOURCEOBJECTID, CLASSNAME) VALUES (1, 211, null, '" + xmlDate + "', 'SAXMLObjectDataInstanceImpl', 123456789, 14, 'java.util.Date')")
        sql.executeInsert("INSERT INTO arch_data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, ARCHIVEDATE, SOURCEOBJECTID, CLASSNAME) VALUES (1, 212, null, '" + xmlNullDate + "', 'SAXMLObjectDataInstanceImpl', 123456789, 14, 'java.util.Date')")
        sql.executeInsert("INSERT INTO arch_data_instance (tenantid, id, LONGVALUE, CLOBVALUE, DISCRIMINANT, ARCHIVEDATE, SOURCEOBJECTID, CLASSNAME) VALUES (1, 213, null, null, 'SAXMLObjectDataInstanceImpl', 123456789, 14, 'java.util.Date')")
        def dateDataInstanceMigration = new MigrateDateDataInstancesFromWrongXMLObject(sql, dbVendor())

        //when
        dateDataInstanceMigration.migrate()

        //then
        def row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM arch_data_instance where tenantid = 1 and ID = 211");
        def String name = row.getProperty("NAME")
        def Long dateAsLong = row.getProperty("LONGVALUE")
        def String dateAsClob = row.getProperty("CLOBVALUE")
        def String dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isEqualTo(dateTime)
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SADateDataInstanceImpl")

        row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM arch_data_instance where tenantid = 1 and ID = 212");
        name = row.getProperty("NAME")
        dateAsLong = row.getProperty("LONGVALUE")
        dateAsClob = row.getProperty("CLOBVALUE")
        dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isNull()
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SADateDataInstanceImpl")

        row = sql.firstRow("SELECT NAME, LONGVALUE, CLOBVALUE, DISCRIMINANT FROM arch_data_instance where tenantid = 1 and ID = 213");
        name = row.getProperty("NAME")
        dateAsLong = row.getProperty("LONGVALUE")
        dateAsClob = row.getProperty("CLOBVALUE")
        dataType = row.getProperty("DISCRIMINANT")
        assertThat(dateAsLong).isNull()
        assertThat(dateAsClob).isNull()
        assertThat(dataType).isEqualTo("SADateDataInstanceImpl")
    }
}
