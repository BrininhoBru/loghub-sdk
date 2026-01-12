package io.loghub.logger.config;

/**
 * Configuration holder for LogHub Logger.
 * Stores application-level settings used for log enrichment.
 */
public final class LogHubConfig {

    private static final LogHubConfig INSTANCE = new LogHubConfig();

    /**
     * Header name for API Key authentication.
     */
    public static final String API_KEY_HEADER = "X-API-KEY";

    private String application = "unknown";
    private String environment = "unknown";
    private String endpoint;
    private String apiKey;
    private int timeoutMs = 5000;
    private int queueCapacity = 1000;
    private int workerThreads = 1;
    private boolean enabled = true;

    private LogHubConfig() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return the configuration instance
     */
    public static LogHubConfig getInstance() {
        return INSTANCE;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "LogHubConfig{" +
               "application='" + application + '\'' +
               ", environment='" + environment + '\'' +
               ", endpoint='" + endpoint + '\'' +
               ", apiKey='" + maskApiKey(apiKey) + '\'' +
               ", timeoutMs=" + timeoutMs +
               ", queueCapacity=" + queueCapacity +
               ", workerThreads=" + workerThreads +
               ", enabled=" + enabled +
               '}';
    }

    /**
     * Masks the API key for secure logging (shows only first 4 chars).
     */
    private String maskApiKey(String key) {
        if (key == null || key.length() <= 4) {
            return "****";
        }
        return key.substring(0, 4) + "****";
    }
}

