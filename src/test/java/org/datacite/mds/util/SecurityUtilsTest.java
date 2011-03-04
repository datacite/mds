package org.datacite.mds.util;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.Utils;
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
        allocator = Utils.createAllocator("AL");
        allocator.persist();
        datacentre = Utils.createDatacentre("AL.DC", allocator);
        datacentre.persist();
    }

    @Test
    public void testNotLoggedIn() {
        Utils.logout();
        Assert.assertFalse(SecurityUtils.isLoggedIn());
        Assert.assertNull(SecurityUtils.getCurrentSymbol());
        Assert.assertNull(SecurityUtils.getCurrentAllocatorOrNull());
        Assert.assertNull(SecurityUtils.getCurrentDatacentreOrNull());
    }

    @Test
    public void testLoggedInAsAllocator() {
        Utils.login(allocator);
        Assert.assertTrue(SecurityUtils.isLoggedIn());
        Assert.assertEquals(allocator.getSymbol(), SecurityUtils.getCurrentSymbol());
        Assert.assertEquals(allocator, SecurityUtils.getCurrentAllocatorOrNull());
        Assert.assertNull(SecurityUtils.getCurrentDatacentreOrNull());
    }

    @Test
    public void testLoggedInAsDatacentre() {
        Utils.login(datacentre);
        Assert.assertTrue(SecurityUtils.isLoggedIn());
        Assert.assertEquals(datacentre.getSymbol(), SecurityUtils.getCurrentSymbol());
        Assert.assertEquals(datacentre, SecurityUtils.getCurrentDatacentreOrNull());
        Assert.assertNull(SecurityUtils.getCurrentAllocatorOrNull());
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
        Utils.login(datacentre);
        SecurityUtils.getCurrentDatacentre();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentDatacentre_NotLoggedIn() throws SecurityException {
        Utils.logout();
        SecurityUtils.getCurrentDatacentre();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentDatacentre_LoggedInAsAllocator() throws SecurityException {
        Utils.login(allocator);
        SecurityUtils.getCurrentDatacentre();
    }

    @Test
    public void testGetCurrentAllocator() throws SecurityException {
        Utils.login(allocator);
        SecurityUtils.getCurrentAllocator();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentAllocator_NotLoggedIn() throws SecurityException {
        Utils.logout();
        SecurityUtils.getCurrentAllocator();
    }

    @Test(expected = SecurityException.class)
    public void testGetCurrentAllocator_LoggedInAsDatacentre() throws SecurityException {
        Utils.login(datacentre);
        SecurityUtils.getCurrentAllocator();
    }


}
