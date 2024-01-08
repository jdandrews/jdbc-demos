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
    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_WHITE_FG = "\u001B[37m";
    public static final String ANSI_BLACK_BG = "\u001B[40m";

    public static final String ANSI_BOLD_WHITE_ON_BLACK = ANSI_BOLD + ANSI_WHITE_FG + ANSI_BLACK_BG;

    // prevents Oracle ojdbc11's SAX parser from being picked up by the logging framework, causing an exception.
    // workaround for this issue: https://jira.qos.ch/browse/LOGBACK-1577
    static {
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, GeneralSecurityException, ClassNotFoundException, SQLException {
        log.info("Attempting to connect to an Oracle 23c instance through an SSH tunnel.");

        try (Tunnel tunnel = new Tunnel()) {
            tunnel.start();

            try (Connection conn = new OracleDbDriver().getConnection();
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery("select 'Hello from Oracle 23c'")) {
                while (rs.next()) {
                    log.info(ANSI_BOLD_WHITE_ON_BLACK + "   " + rs.getString(1) + "   " + ANSI_RESET);
                }
            }
        }
    }
}
