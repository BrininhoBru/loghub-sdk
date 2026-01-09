package io.loghub.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Model representing SDK information.
 * Contains metadata about the SDK that generated the log event.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SdkInfo {

    @JsonProperty("language")
    private final String language;

    @JsonProperty("version")
    private final String version;

    /**
     * Default constructor for JSON deserialization.
     */
    public SdkInfo() {
        this.language = null;
        this.version = null;
    }

    /**
     * Creates a new SdkInfo instance.
     *
     * @param language the programming language of the SDK
     * @param version  the version of the SDK
     */
    public SdkInfo(String language, String version) {
        this.language = language;
        this.version = version;
    }

    /**
     * Gets the programming language of the SDK.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets the version of the SDK.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SdkInfo sdkInfo = (SdkInfo) o;
        return Objects.equals(language, sdkInfo.language) &&
               Objects.equals(version, sdkInfo.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, version);
    }

    @Override
    public String toString() {
        return "SdkInfo{" +
               "language='" + language + '\'' +
               ", version='" + version + '\'' +
               '}';
    }

    /**
     * Creates a new Builder instance.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating SdkInfo instances.
     */
    public static final class Builder {
        private String language;
        private String version;

        private Builder() {
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public SdkInfo build() {
            return new SdkInfo(language, version);
        }
    }
}

