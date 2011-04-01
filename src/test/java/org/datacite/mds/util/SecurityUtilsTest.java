package org.datacite.mds.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.constraints.AssertTrue;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class SecurityUtilsTest {
    Allocator allocator;
    Datacentre datacentre;

    @Before
    public void init() {
        allocator = TestUtils.createAllocator("AL");
        allocator.persist();
        datacentre = TestUtils.createDatacentre("AL.DC", allocator);
        datacentre.persist();
    }

    @Test
    public void testNotLoggedIn() {
        TestUtils.logout();
        Assert.assertFalse(SecurityUtils.isLoggedIn());
        Assert.assertFalse(SecurityUtils.isLoggedInAsAllocator());
        Assert.assertFalse(SecurityUtils.isLoggedInAsDatacentre());
    }

    @Test
    public void testLoggedInAsAllocator() {
        TestUtils.login(allocator);
        Assert.assertTrue(SecurityUtils.isLoggedIn());
        Assert.assertTrue(SecurityUtils.isLoggedInAsAllocator());
        Assert.assertFalse(SecurityUtils.isLoggedInAsDatacentre());
    }

    @Test
    public void testLoggedInAsDatacentre() {
        TestUtils.login(datacentre);
        Assert.assertTrue(SecurityUtils.isLoggedIn());
        Assert.assertFalse(SecurityUtils.isLoggedInAsAllocator());
        Assert.assertTrue(SecurityUtils.isLoggedInAsDatacentre());
    }

    @Test(expected = SecurityException.class)
    public void testQuotaExceeded() throws SecurityException {
        checkMockedIsQuotaExceeded(true);
    }

    @Test
    public void testQuotaNotExceeded() throws SecurityException {
        checkMockedIsQuotaExceeded(false);
    }

    private void checkMockedIsQuotaExceeded(boolean exceeded) throws SecurityException {
        Datacentre datacentre = EasyMock.createMock(Datacentre.class);
        EasyMock.expect(datacentre.isQuotaExceeded()).andReturn(exceeded);
        EasyMock.expect(datacentre.getSymbol()).andStubReturn(StringUtils.EMPTY);
        EasyMock.replay(datacentre);
        SecurityUtils.checkQuota(datacentre);
        EasyMock.verify(datacentre);
    }
    
    @Test
    public void testGetCurrentDatacentre() throws SecurityException {
        TestUtils.login(datacentre);
        SecurityUtils.getCurrentDatacentre();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentDatacentre_NotLoggedIn() throws SecurityException {
        TestUtils.logout();
        SecurityUtils.getCurrentDatacentre();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentDatacentre_LoggedInAsAllocator() throws SecurityException {
        TestUtils.login(allocator);
        SecurityUtils.getCurrentDatacentre();
    }

    @Test
    public void testGetCurrentAllocator() throws SecurityException {
        TestUtils.login(allocator);
        SecurityUtils.getCurrentAllocator();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentAllocator_NotLoggedIn() throws SecurityException {
        TestUtils.logout();
        SecurityUtils.getCurrentAllocator();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentAllocator_LoggedInAsDatacentre() throws SecurityException {
        TestUtils.login(datacentre);
        SecurityUtils.getCurrentAllocator();
    }
    
    @Test
    public void testIsUserSuperiorTo() {
        Allocator admin = TestUtils.createAllocator("ADMIN");
        admin.setRoleName("ROLE_ADMIN");
        Allocator dev = TestUtils.createAllocator("DEV");
        dev.setRoleName("ROLE_DEV");
        Allocator allocator = TestUtils.createAllocator("AL");
        Datacentre datacentre = TestUtils.createDatacentre("AL.DC", allocator);
        Allocator allocator2 = TestUtils.createAllocator("AL2");
        
        assertFalse(SecurityUtils.isUserSuperiorTo(dev, dev));
        assertTrue(SecurityUtils.isUserSuperiorTo(dev, admin));
        assertTrue(SecurityUtils.isUserSuperiorTo(dev, allocator));
        assertTrue(SecurityUtils.isUserSuperiorTo(dev, allocator2));
        assertTrue(SecurityUtils.isUserSuperiorTo(dev, datacentre));
        
        assertFalse(SecurityUtils.isUserSuperiorTo(admin, dev));
        assertFalse(SecurityUtils.isUserSuperiorTo(admin, admin));
        assertTrue(SecurityUtils.isUserSuperiorTo(admin, allocator));
        assertTrue(SecurityUtils.isUserSuperiorTo(admin, allocator2));
        assertTrue(SecurityUtils.isUserSuperiorTo(admin, datacentre));

        assertFalse(SecurityUtils.isUserSuperiorTo(allocator, dev));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator, admin));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator, allocator));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator, allocator2));
        assertTrue(SecurityUtils.isUserSuperiorTo(allocator, datacentre));

        assertFalse(SecurityUtils.isUserSuperiorTo(allocator2, dev));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator2, admin));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator2, allocator));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator2, allocator2));
        assertFalse(SecurityUtils.isUserSuperiorTo(allocator2, datacentre));

        assertFalse(SecurityUtils.isUserSuperiorTo(datacentre, dev));
        assertFalse(SecurityUtils.isUserSuperiorTo(datacentre, admin));
        assertFalse(SecurityUtils.isUserSuperiorTo(datacentre, allocator));
        assertFalse(SecurityUtils.isUserSuperiorTo(datacentre, allocator2));
        assertFalse(SecurityUtils.isUserSuperiorTo(datacentre, datacentre));
    }


}
