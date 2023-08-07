package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigFileReader {
    private static final String propertyFilePath = "src/main/resources/config.properties";

    public static String getProperty(String name) {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream(propertyFilePath), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties.getProperty(name);
    }

}
