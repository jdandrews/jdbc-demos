package com.jrandrews.jdbcdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    public enum Item {
        BASTION_PRIVATE_KEY_FILENAME("bastion_private_key_filename"),
        BASTION_HOSTNAME("bastion_hostname"),
        BASTION_USERNAME("bastion_username"),

        TUNNEL_TARGET_HOSTNAME("tunnel_target_hostname"),
        TUNNEL_TARGET_PORT("tunnel_target_port"),
        TUNNEL_LOCAL_PORT("tunnel_local_port"),

        ORACLE_DB_HOST("oracle_db_host"),
        ORACLE_DB_PORT("oracle_db_port"),
        ORACLE_DB_SID_OR_NAME("oracle_db_sid_or_name"),
        ORACLE_USERNAME("oracle_username"),
        ORACLE_PASSWORD("oracle_password")
        ;

        private String key;
        Item(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
    public static final String PRIVATE_KEY_PATH = "";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Properties properties;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/configuration.properties"));
        } catch (IOException e) {
            File f = new File("src/main/resources/configuration.properties");
            log.error("Unable to load configuration from " + f.getAbsolutePath() + ": ", e);
        }
    }

    public String get(Item item) {
        return properties.getProperty(item.getKey());
    }
    
    public int getInt(Item item) {
        return Integer.valueOf(get(item));
    }
}
