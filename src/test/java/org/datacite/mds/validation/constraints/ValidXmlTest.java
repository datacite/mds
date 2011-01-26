package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValidXmlTest extends AbstractContraintsTest {

    @ValidXML
    byte[] xml;

    @Test
    public void test() {
        assertTrue(isValid("<xml/>"));
        assertFalse(isValid("<a></b>"));
    }
    
    boolean isValid(String xml) {
        this.xml = xml.getBytes();
        return super.isValid(this, "xml");
    }

}
