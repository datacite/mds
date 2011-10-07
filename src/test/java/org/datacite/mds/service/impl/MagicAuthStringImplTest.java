package org.datacite.mds.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.service.MagicAuthStringService;
import org.datacite.mds.test.TestUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( { "javax.*", "org.apache.log4j.*" })
@PrepareForTest( { MagicAuthStringServiceImpl.class })
public class MagicAuthStringImplTest {
    
    MagicAuthStringService service = new MagicAuthStringServiceImpl();
    
    AllocatorOrDatacentre user = TestUtils.createAllocator("AL");
    
    Date today = new Date();
    Date tomorrow = DateUtils.addDays(today, 1);
    Date yesterday = DateUtils.addDays(today, -1);
    Date anotherDay = DateUtils.addDays(today, 2);
    
    @Test
    public void testGetValidAuthStrings() {
        setDate(today);
        String authToday = service.getCurrentAuthString(user);

        setDate(yesterday);
        String authYesterday = service.getCurrentAuthString(user);
        
        setDate(today);
        Collection<String> validAuthStrings = service.getValidAuthStrings(user);
        
        assertEquals(2, validAuthStrings.size());
        assertTrue(validAuthStrings.contains(authToday));
        assertTrue(validAuthStrings.contains(authYesterday));
    }
    
    @Test
    public void testIsValidAuthString() {
        setDate(today);
        String auth = service.getCurrentAuthString(user);
        
        setDate(today);
        assertTrue(service.isValidAuthString(user, auth));
        
        setDate(tomorrow);
        assertTrue(service.isValidAuthString(user, auth));
        
        setDate(yesterday);
        assertFalse(service.isValidAuthString(user, auth));
        
        setDate(anotherDay);
        assertFalse(service.isValidAuthString(user, auth));
    }
    
    @Test 
    public void testNull() {
        assertNull(service.getCurrentAuthString(null));
        assertFalse(service.isValidAuthString(null, "foobar"));
        assertFalse(service.isValidAuthString(user, null));
        assertTrue(service.getValidAuthStrings(null).isEmpty());
    }
    
    
    @Test
    public void testEmptyVsNullPassword() {
        user.setPassword("");
        String authStringForEmptyPassword = service.getCurrentAuthString(user);
        user.setPassword(null);
        String authStringForNullPassword = service.getCurrentAuthString(user);
        assertEquals(authStringForEmptyPassword, authStringForNullPassword);
    }

    
    @Test
    public void testEmptyBasicAuthString() {
        AllocatorOrDatacentre mockUser = EasyMock.createMock(AllocatorOrDatacentre.class);
        EasyMock.expect(mockUser.getBaseAuthString()).andReturn(null);
        EasyMock.replay(mockUser);
        assertNull(service.getCurrentAuthString(mockUser));
        EasyMock.verify(mockUser);
    }
    
    private void setDate(Date date) {
        try {
            PowerMock.resetAll();
            PowerMock.expectNew(Date.class).andReturn(date);
            PowerMock.replayAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
