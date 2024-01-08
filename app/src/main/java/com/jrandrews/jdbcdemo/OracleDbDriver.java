package com.jrandrews.jdbcdemo;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.jrandrews.jdbcdemo.Configuration.Item;

/**
 * 
 * https://docs.oracle.com/en/database/oracle/oracle-database/23/jjdbc/index.html#Oracle%C2%AE-Database
 */
public class OracleDbDriver implements Closeable {
    // jdbc:oracle:driver_type:[username/password]@database_specifier";
    /*
     * private static String DB_URL = "jdbc:oracle:thin:@(description="
     * + "(retry_count=20)(retry_delay=3)"
     * + "(address=(protocol=tcps)(port=1521)(host=localhost))"
     * + "(connect_data=(service_name=db23c02_pdb1.sub12191546560.vcnfordb23c.oraclevcn.com))"
     * + "(security=(ssl_server_dn_match=yes)))";
     */
    private Connection connection = null;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Configuration config = new Configuration();

        if (connection == null) {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//" + config.get(Item.ORACLE_DB_HOST)
                    + ":" + config.get(Item.ORACLE_DB_PORT)
                    + "/" + config.get(Item.ORACLE_DB_SID_OR_NAME),
                    config.get(Item.ORACLE_USERNAME), config.get(Item.ORACLE_PASSWORD));
        }

        return connection;
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                // no action
            } finally {
                connection = null;
            }
        }

    }

}
