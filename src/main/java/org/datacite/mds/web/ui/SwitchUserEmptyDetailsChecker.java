package org.datacite.mds.web.ui;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class SwitchUserEmptyDetailsChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails toCheck) {
        // empty to allow to switch to every user regardless of user status
    }

}
