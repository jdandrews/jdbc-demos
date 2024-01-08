package com.jrandrews.jdbcdemo;

import java.io.IOException;
import java.io.InputStream;
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
        ORACLE_PASSWORD("oracle_password");

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

    private static Properties properties;

    public Configuration() {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        InputStream fs = this.getClass().getResourceAsStream("/configuration.properties");
        try {
            properties.load(fs);
        } catch (NullPointerException | IOException e) {
            if (fs == null) {
                log.error("configuration.properties not found on the classpath", e);
            } else {
                log.error("unable to read configuration", e);
            }
        }
    }

    public String get(Item item) {
        return properties.getProperty(item.getKey());
    }

    public int getInt(Item item) {
        return Integer.valueOf(get(item));
    }
}
