package org.datacite.mds.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OaiSourceCollection {
    
    private List<OaiSource> oaiSources = new ArrayList<OaiSource>();

    @XmlElementWrapper
    public List<OaiSource> getOaiSources() {
        return oaiSources;
    }

    public void addOaiSources(List<OaiSource> oaiSources) {
        this.oaiSources.addAll(oaiSources);
    }
    
}
