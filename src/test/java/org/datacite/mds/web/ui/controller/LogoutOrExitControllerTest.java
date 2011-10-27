package org.datacite.mds.web.ui.controller;

import junit.framework.Assert;

import org.datacite.mds.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class LogoutOrExitControllerTest {
    
    LogoutOrExitController controller = new LogoutOrExitController();
    
    @Test
    public void testNonSwitchedUser() {
        TestUtils.setUsernamePassword("foo", "bar");
        String view = controller.logoutOrReturn();
        Assert.assertEquals("redirect:/resources/j_spring_security_logout", view);
    }

    @Test
    public void testSwitchedUser() {
        Authentication origAuthentication = new TestingAuthenticationToken("foo", "bar");
        GrantedAuthority[] swichUserAuthority = { new SwitchUserGrantedAuthority("foobar", origAuthentication) };
        
        Authentication switchedAuthentication = new TestingAuthenticationToken("foo", "bar", swichUserAuthority);
        SecurityContextHolder.getContext().setAuthentication(switchedAuthentication);
        
        String view = controller.logoutOrReturn();
        Assert.assertEquals("redirect:/resources/j_spring_security_exit_user", view);
    }
}
