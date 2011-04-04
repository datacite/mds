package org.datacite.mds.web.ui;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class UiUtilsTest {

    private Allocator admin;
    private Allocator dev;
    private Allocator allocator;
    private Datacentre datacentre;
    private Allocator allocator2;
    private HttpSession session;

    @Before
    public void init() {
        admin = TestUtils.createAdmin("ADMIN");
        dev = TestUtils.createDev("DEV");
        allocator = TestUtils.createAllocator("AL");
        datacentre = TestUtils.createDatacentre("AL.DC", allocator);
        allocator2 = TestUtils.createAllocator("AL2");
        TestUtils.persist(admin, dev, allocator, allocator2, datacentre);
        session = new MockHttpSession();
    }

    @Test
    public void testRefreshSymbolsForSwitchUser() {
        assertEquals(",ADMIN,AL,AL2", getSymbolsFor(dev));
        assertEquals(",AL,AL2", getSymbolsFor(admin));
        assertEquals(",AL.DC", getSymbolsFor(allocator));
        assertEquals("", getSymbolsFor(allocator2));
        assertEquals("", getSymbolsFor(datacentre));
    }

    @SuppressWarnings("unchecked")
    private String getSymbolsFor(AllocatorOrDatacentre user) {
        TestUtils.login(user);
        UiUtils.refreshSymbolsForSwitchUser(session);
        Collection<String> symbols = (Collection<String>) session.getAttribute("symbols");
        return StringUtils.join(symbols, ",");
    }

    @Test
    public void callConstructor() {
        TestUtils.callConstructor(UiUtils.class);
    }
}
