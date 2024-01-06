package com.jrandrews.jdbcdemo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    // prevents Oracle ojdbc11's SAX parser from being picked up by the logging framework, causing an exception.
    // workaround for this issue: https://jira.qos.ch/browse/LOGBACK-1577 
    static {
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, GeneralSecurityException, ClassNotFoundException, SQLException {
        try (Tunnel tunnel = new Tunnel()) {
            tunnel.start();

            try (OracleDbDriver driver = new OracleDbDriver()) {
                Connection conn = driver.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select 'Hello'");
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
                rs.close();
                statement.close();
                conn.close();
            }
        }
    }

    private static void pressEnterToExit() {
        System.out.println("press <enter> to exit (heh).");
        for (int i = 0, c = 0; c != 10; ++i) {
            boolean done = false;
            // This could be done by delegating the whole outer loop to thread, and this to a different thread, and synchronizing,
            // but I think this approach is a little more transparent.
            while (!done) {
                try {
                    if (System.in.available() > 0)
                        c = System.in.read();
                } catch (IOException e) {
                    // ignored
                }

                sleep(5);

                if (c == 10) {
                    done = true;
                }
            }
        }
    }

    private static void sleep(long n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // no-op
        }
    }
}
