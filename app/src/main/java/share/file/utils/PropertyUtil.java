package share.file.utils;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    
    public Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = getClass()
                                 .getClassLoader()
                                 .getResourceAsStream("application.properties")
        ) {
            if (input == null)
                throw new RuntimeException("Unable to find application.properties");
            
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading application.properties", e);
        }

        return props;
    }
}
