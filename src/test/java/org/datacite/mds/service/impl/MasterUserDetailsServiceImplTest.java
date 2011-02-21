package org.datacite.mds.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MasterUserDetailsServiceImplTest {

    MasterUserDetailsServiceImpl masterUserDetailsService = new MasterUserDetailsServiceImpl();

    UserDetails allocator;
    UserDetails datacentre;

    @Before
    public void init() {
        Collection<GrantedAuthority> authoritiesAllocator = new ArrayList<GrantedAuthority>();
        authoritiesAllocator.add(new GrantedAuthorityImpl("ROLE_ALLOCATOR"));
        Collection<GrantedAuthority> authoritiesDatacentre = new ArrayList<GrantedAuthority>();
        authoritiesDatacentre.add(new GrantedAuthorityImpl("ROLE_DATACENTRE"));

        allocator = new User("AL", "pw_AL", true, true, true, true, authoritiesAllocator);
        datacentre = new User("AL.DC", "pw_DC", true, true, true, true, authoritiesDatacentre);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testAllocatorSymbol() {
        masterUserDetailsService.loadUserByUsername(allocator.getUsername());
    }

    @Test
    public void testDatacentreSymbol() {
        mockUserDetailsService();
        UserDetails userDetails = masterUserDetailsService.loadUserByUsername(datacentre.getUsername());
        assertEquals(datacentre.getUsername(), userDetails.getUsername());
        assertEquals(allocator.getPassword(), userDetails.getPassword());
        assertEquals(datacentre.getAuthorities(), userDetails.getAuthorities());
    }
    
    private void mockUserDetailsService() {
        UserDetailsService userDetailsService = EasyMock.createMock(UserDetailsService.class);
        masterUserDetailsService.setUserDetailsService(userDetailsService);
        EasyMock.expect(userDetailsService.loadUserByUsername(allocator.getUsername())).andReturn(allocator);
        EasyMock.expect(userDetailsService.loadUserByUsername(datacentre.getUsername())).andReturn(datacentre);
        EasyMock.replay(userDetailsService);
    }
}
