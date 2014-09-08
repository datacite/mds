package org.datacite.mds.domain;

import static org.junit.Assert.*;

import org.datacite.mds.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional 
public class DatacentreTest {
            
    @Test
    @Rollback
    public void testIncQuotaUsed() {
        Datacentre datacentre = TestUtils.createDefaultDatacentre();
        Integer expectedQuota = 43;

        datacentre.setDoiQuotaUsed(42);
        datacentre.persist();
        
        datacentre.incQuotaUsed(Datacentre.ForceRefresh.YES);
        
        assertEquals(expectedQuota, datacentre.getDoiQuotaUsed());

        Datacentre datacentre2 = Datacentre.findDatacentre(datacentre.getId());
        assertEquals(expectedQuota, datacentre2.getDoiQuotaUsed());
    }

    @Test
    @Rollback
    public void testIsQuotaExceeded() {
        Datacentre datacentre = TestUtils.createDefaultDatacentre();
        datacentre.setDoiQuotaUsed(42);
        datacentre.setDoiQuotaAllowed(43);
        datacentre.persist();
        
        assertFalse(datacentre.isQuotaExceeded());
        
        datacentre.incQuotaUsed(Datacentre.ForceRefresh.YES);

        assertTrue(datacentre.isQuotaExceeded());
        
        datacentre.setDoiQuotaAllowed(-1);
        datacentre.persist();

        assertFalse(datacentre.isQuotaExceeded());
    }

}
