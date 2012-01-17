package org.datacite.mds.util;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final Collection<String> EXPERIMENTS_AVAILABLE = Arrays.asList("media");

    public static String TEST_PREFIX;

    /**
     * Hack to inject static variable needed by some static methods (finders,
     * counts, etc.) in our domain classes
     */
    @Value("${handle.testPrefix}")
    @Autowired(required = true)
    public void setTestPrefix(String testPrefix) {
        TEST_PREFIX = testPrefix;
    }
}
