package org.datacite.mds.web.ui.controller;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.datacite.mds.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
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
        HttpServletRequest request = makeHttpServletRequest(false);
        String view = controller.logoutOrReturn(request);
        Assert.assertEquals("redirect:/resources/j_spring_security_logout", view);
    }
    
    private HttpServletRequest makeHttpServletRequest(boolean sessionIsNew) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = (MockHttpSession) request.getSession();
        session.setNew(sessionIsNew);
        return request;
    }

    @Test
    public void testSwitchedUser() {
        Authentication origAuthentication = new TestingAuthenticationToken("foo", "bar");
        GrantedAuthority[] swichUserAuthority = { new SwitchUserGrantedAuthority("foobar", origAuthentication) };
        
        Authentication switchedAuthentication = new TestingAuthenticationToken("foo", "bar", swichUserAuthority);
        SecurityContextHolder.getContext().setAuthentication(switchedAuthentication);
        
        HttpServletRequest request = makeHttpServletRequest(false);
        String view = controller.logoutOrReturn(request);
        Assert.assertEquals("redirect:/resources/j_spring_security_exit_user", view);
    }
    
    @Test
    public void testNotLoggedIn() {
        TestUtils.logout();
        HttpServletRequest request = makeHttpServletRequest(false);
        String view = controller.logoutOrReturn(request);
        Assert.assertEquals("redirect:/", view);
    }
    
    @Test
    public void testWithNewSession() {
        TestUtils.setUsernamePassword("foo", "bar");
        HttpServletRequest request = makeHttpServletRequest(true);
        String view = controller.logoutOrReturn(request);
        Assert.assertEquals("redirect:/", view);
    }
    
}
