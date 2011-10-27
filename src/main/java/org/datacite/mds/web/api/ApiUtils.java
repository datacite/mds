package org.datacite.mds.web.api;

import org.apache.commons.lang.BooleanUtils;

public class ApiUtils {
    public static String makeResponseMessage(String message, Boolean testMode) {
        if (BooleanUtils.isTrue(testMode))
            message += " (only test mode!)";
        return message;
    }
}
