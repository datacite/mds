package org.datacite.mds.service.impl;

import static org.junit.Assert.*;

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
