package org.datacite.mds.domain;

import static org.junit.Assert.*;

import org.datacite.mds.test.Utils;
import org.junit.Before;
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
        Datacentre datacentre;
        Integer expectedQuota;

        Allocator allocator = Utils.createAllocator("AL");
        allocator.persist();
        datacentre = Utils.createDatacentre("AL.DC", allocator);
        datacentre.setDoiQuotaUsed(42);
        expectedQuota = 43;
        datacentre.persist();
        
        datacentre.incQuotaUsed(true);
        
        assertEquals(expectedQuota, datacentre.getDoiQuotaUsed());

        Datacentre datacentre2 = Datacentre.findDatacentre(datacentre.getId());
        assertEquals(expectedQuota, datacentre2.getDoiQuotaUsed());
    }

/*    @Test
    public void testIsQuotaExceeded() {
        fail("Not yet implemented");
    }
*/
}
