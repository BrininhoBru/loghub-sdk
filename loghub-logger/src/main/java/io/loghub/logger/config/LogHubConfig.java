package io.loghub.logger.config;

/**
 * Configuration holder for LogHub Logger.
 * Stores application-level settings used for log enrichment.
 */
public final class LogHubConfig {

    private static final LogHubConfig INSTANCE = new LogHubConfig();

    private String application = "unknown";
    private String environment = "unknown";
    private String endpoint;
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
               ", timeoutMs=" + timeoutMs +
               ", queueCapacity=" + queueCapacity +
               ", workerThreads=" + workerThreads +
               ", enabled=" + enabled +
               '}';
    }
}

