import org.bonitasoft.migration.core.MigrationUtil;

def parameters = Collections.singletonMap(":version", "6.2.0")
MigrationUtil.executeSqlFile(feature, dbVendor, null, parameters, sql, true)