package io.loghub.logger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for retrieving SDK version information.
 * Reads version from the JAR manifest or pom.properties.
 */
public final class SdkVersion {

    private static final String SDK_LANGUAGE = "java";
    private static final String VERSION;

    static {
        VERSION = loadVersion();
    }

    private SdkVersion() {
        // Utility class
    }

    /**
     * Gets the SDK language identifier.
     *
     * @return always "java"
     */
    public static String getLanguage() {
        return SDK_LANGUAGE;
    }

    /**
     * Gets the SDK version.
     *
     * @return the version string, or "unknown" if not found
     */
    public static String getVersion() {
        return VERSION;
    }

    private static String loadVersion() {
        // Try to get version from package implementation version (JAR manifest)
        String version = SdkVersion.class.getPackage().getImplementationVersion();
        if (version != null && !version.isEmpty()) {
            return version;
        }

        // Try to get version from Maven pom.properties
        try (InputStream is = SdkVersion.class.getResourceAsStream(
                "/META-INF/maven/io.loghub/loghub-logger/pom.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                version = props.getProperty("version");
                if (version != null && !version.isEmpty()) {
                    return version;
                }
            }
        } catch (IOException e) {
            // Ignore - fall through to default
        }

        // Try to get from system property (useful for testing)
        version = System.getProperty("loghub.sdk.version");
        if (version != null && !version.isEmpty()) {
            return version;
        }

        return "0.1.0-SNAPSHOT";
    }
}

