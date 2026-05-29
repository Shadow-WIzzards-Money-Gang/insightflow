package com.bytestorm.insightflow.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.bytestorm.insightflow.domain.exceptions.ai.ChaveApiNaoEncontradaException;

public class ApplicationProperties {
    
    private static ApplicationProperties instance;
    private final Properties properties = new Properties();

    public ApplicationProperties() {

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new ChaveApiNaoEncontradaException();
            }

            this.properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }

}
