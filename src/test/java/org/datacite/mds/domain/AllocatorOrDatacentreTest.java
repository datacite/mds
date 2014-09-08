package org.datacite.mds.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AllocatorOrDatacentreTest {
    
    AllocatorOrDatacentre user;
    
    public AllocatorOrDatacentreTest(AllocatorOrDatacentre user) {
        this.user = user;
    }

    @Test
    public void testTrimEmail() {
        String email = "foo@example.com"; 
        user.setContactEmail(" " + email + " ");
        assertEquals(email, user.getContactEmail());
    }
    
    @Test
    public void testNameReplaceLinebreak() {
        String name = " Foo\r\nbar\n";
        user.setName(name);
        assertEquals("Foo bar", user.getName());
    }
    
    @Parameters
    public static Collection<Object[]> getUsers() {
        Collection<Object[]> params = new ArrayList<Object[]>();
        params.add(new Object[] { new Datacentre() });
        params.add(new Object[] { new Allocator() });
        return params;
    }

    
}
