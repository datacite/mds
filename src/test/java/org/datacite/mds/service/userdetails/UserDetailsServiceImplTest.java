package org.datacite.mds.service.userdetails;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.util.DomainUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( { "javax.*", "org.apache.log4j.*" })
@PrepareForTest( { DomainUtils.class })
public class UserDetailsServiceImplTest {
    
    UserDetailsService userDetailsService  = new UserDetailsServiceImpl();

    Allocator user;

    @Before
    public void init() {
        user = TestUtils.createAllocator("DUMMY");
        user.setExperiments(",fOO\n bar");
        PowerMock.mockStatic(DomainUtils.class);
    }
    
    @After
    public void after() {
        PowerMock.verifyAll();
    }

    @Test
    public void testExistingUser() {
        mockFindAllocatorOrDatacentre(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getSymbol());
        assertEquals(user.getSymbol(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(hasRole("ROLE_ALLOCATOR", userDetails));
        assertTrue(hasRole(UserDetailsServiceImpl.ROLE_EXPERIMENT_PREFIX + "FOO", userDetails));
        assertTrue(hasRole(UserDetailsServiceImpl.ROLE_EXPERIMENT_PREFIX + "BAR", userDetails));
    }
    
    private boolean hasRole(String role, UserDetails userDetails) {
        for (GrantedAuthority auth : userDetails.getAuthorities()) 
            if (StringUtils.equals(role, auth.getAuthority()))
                return true;
        return false;
    }

    @Test(expected=UsernameNotFoundException.class)
    public void testNonExistingUser() {
        mockFindAllocatorOrDatacentre(null);
        userDetailsService.loadUserByUsername(user.getSymbol());
    }
    
    @Test
    public void testUserWithNullPassword() {
        user.setPassword(null);
        mockFindAllocatorOrDatacentre(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getSymbol());
        assertFalse(userDetails.isCredentialsNonExpired());
    }
    
    private void mockFindAllocatorOrDatacentre(AllocatorOrDatacentre mockReturn) {
        EasyMock.expect(DomainUtils.findAllocatorOrDatacentreBySymbol(user.getSymbol())).andReturn(mockReturn);
        PowerMock.replay(DomainUtils.class);
    }
}
