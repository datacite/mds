package org.datacite.mds.service.userdetails;

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

public class BaseMasterUserDetailsServiceImplTest {

    BaseMasterUserDetailsService service = new DummyMasterUserDetailsService();

    UserDetails user;
    UserDetails masterUser;

    @Before
    public void init() {
        Collection<GrantedAuthority> authoritiesMasterUser = new ArrayList<GrantedAuthority>();
        authoritiesMasterUser.add(new GrantedAuthorityImpl("ROLE_ALLOCATOR"));
        Collection<GrantedAuthority> authoritiesUser = new ArrayList<GrantedAuthority>();
        authoritiesUser.add(new GrantedAuthorityImpl("ROLE_DATACENTRE"));

        user = new User("USER", "pw_USER", true, true, true, true, authoritiesUser);
        masterUser = new User("MASTER", "pw_MASTER", true, true, true, true, authoritiesMasterUser);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testWrongUserName() {
        service.loadUserByUsername(masterUser.getUsername());
    }

    @Test
    public void test() {
        mockUserDetailsService();
        UserDetails userDetails = service.loadUserByUsername(user.getUsername());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(masterUser.getPassword(), userDetails.getPassword());
        assertEquals(user.getAuthorities(), userDetails.getAuthorities());
    }

    private void mockUserDetailsService() {
        UserDetailsService userDetailsService = EasyMock.createMock(UserDetailsService.class);
        service.setUserDetailsService(userDetailsService);
        EasyMock.expect(userDetailsService.loadUserByUsername(masterUser.getUsername())).andReturn(masterUser);
        EasyMock.expect(userDetailsService.loadUserByUsername(user.getUsername())).andReturn(user);
        EasyMock.replay(userDetailsService);
    }

    private class DummyMasterUserDetailsService extends BaseMasterUserDetailsService {
        @Override
        public String getMasterUsername(String username) {
            if (username.equals(user.getUsername()))
                return masterUser.getUsername();
            else
                throw new UsernameNotFoundException("user not found");
        }

    }
}
