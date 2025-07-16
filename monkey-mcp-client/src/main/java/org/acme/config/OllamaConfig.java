package org.acme.config;

import java.time.Duration;

/**
 * Configuration class for Ollama client settings
 */
public class OllamaConfig {
    
    // Default configuration values
    public static final String DEFAULT_BASE_URL = "http://localhost:11434";
    public static final String DEFAULT_MODEL = "llama3.2";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(10);
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final double DEFAULT_TEMPERATURE = 0.7;
    
    /**
     * Get the Ollama base URL from system properties or default
     */
    public static String getBaseUrl() {
        return System.getProperty("ollama.baseUrl", DEFAULT_BASE_URL);
    }
    
    /**
     * Get the Ollama model name from system properties or default
     */
    public static String getModelName() {
        return System.getProperty("ollama.model", DEFAULT_MODEL);
    }
    
    /**
     * Get the timeout duration from system properties or default
     */
    public static Duration getTimeout() {
        String timeoutStr = System.getProperty("ollama.timeout");
        if (timeoutStr != null) {
            try {
                return Duration.ofSeconds(Long.parseLong(timeoutStr));
            } catch (NumberFormatException e) {
                System.err.println("Invalid timeout value: " + timeoutStr + ". Using default.");
            }
        }
        return DEFAULT_TIMEOUT;
    }
    
    /**
     * Get the maximum number of retries from system properties or default
     */
    public static int getMaxRetries() {
        String retriesStr = System.getProperty("ollama.maxRetries");
        if (retriesStr != null) {
            try {
                return Integer.parseInt(retriesStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid max retries value: " + retriesStr + ". Using default.");
            }
        }
        return DEFAULT_MAX_RETRIES;
    }
    
    /**
     * Get the temperature value from system properties or default
     */
    public static double getTemperature() {
        String tempStr = System.getProperty("ollama.temperature");
        if (tempStr != null) {
            try {
                return Double.parseDouble(tempStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid temperature value: " + tempStr + ". Using default.");
            }
        }
        return DEFAULT_TEMPERATURE;
    }
}
