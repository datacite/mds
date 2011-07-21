package org.datacite.mds.service.userdetails;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.util.DomainUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( { "javax.*", "org.apache.log4j.*" })
@PrepareForTest( { DomainUtils.class })
public class AdminMasterUserDetailsServiceImplTest {

    AdminMasterUserDetailsServiceImpl service = new AdminMasterUserDetailsServiceImpl();
    
    @Test
    public void test() {
        String admin = "ADMIN";
        mockGetAdmin(TestUtils.createAdmin(admin));
        assertEquals(admin,service.getMasterUsername("FOOBAR"));
    }
    
    @Test(expected = UsernameNotFoundException.class)
    public void testAdminNotFound() {
        mockGetAdmin(null);
        service.getMasterUsername("FOOBAR");
    }
    
    void mockGetAdmin(Allocator mockReturn) {
        PowerMock.mockStatic(DomainUtils.class);
        EasyMock.expect(DomainUtils.getAdmin()).andReturn(mockReturn);
        PowerMock.replay(DomainUtils.class);
    }
}
