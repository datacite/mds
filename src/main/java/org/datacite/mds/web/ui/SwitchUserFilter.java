package org.datacite.mds.web.ui;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.DomainUtils;
import org.datacite.mds.util.SecurityUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SwitchUserFilter extends org.springframework.security.web.authentication.switchuser.SwitchUserFilter {

    private static Logger log = Logger.getLogger(SwitchUserFilter.class);

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        String targetUsername = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        AllocatorOrDatacentre targetUser = DomainUtils.findAllocatorOrDatacentreBySymbol(targetUsername);
        if (targetUser == null)
            throw new UsernameNotFoundException("user '" + targetUsername + "' not found");

        try {
            AllocatorOrDatacentre currentUser = SecurityUtils.getCurrentAllocatorOrDatacentre();
            if (!SecurityUtils.isUserSuperiorTo(currentUser, targetUser))
                throw new AuthenticationServiceException("You are not allowed to switch to the specified user");
        } catch (SecurityException e) {
            throw new UsernameNotFoundException("user not found", e);
        }

        return super.attemptSwitchUser(request);
    }
}
