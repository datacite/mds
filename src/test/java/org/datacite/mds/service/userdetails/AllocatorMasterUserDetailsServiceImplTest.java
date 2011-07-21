package org.datacite.mds.service.userdetails;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AllocatorMasterUserDetailsServiceImplTest {

    AllocatorMasterUserDetailsServiceImpl service = new AllocatorMasterUserDetailsServiceImpl();
    
    @Test
    public void test() {
        assertEquals("AL",service.getMasterUsername("AL.DC"));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testAllocatorSymbol() {
        service.getMasterUsername("AL");
    }
}
