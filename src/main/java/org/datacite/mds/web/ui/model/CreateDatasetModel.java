package org.datacite.mds.web.ui.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.datacite.mds.domain.Datacentre;
import org.hibernate.validator.constraints.NotEmpty;

public class CreateDatasetModel {

    @NotEmpty
    private String doi;
    
    @NotEmpty
    private String url; 

    @NotNull
    private Datacentre datacentre;

    private byte[] xml;
    
    private byte[] xmlUpload;

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Datacentre getDatacentre() {
        return datacentre;
    }

    public void setDatacentre(Datacentre datacentre) {
        this.datacentre = datacentre;
    }

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }
    
    public byte[] getXmlUpload() {
        return xmlUpload;
    }

    public void setXmlUpload(byte[] xml) {
        this.xmlUpload = xml;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    
}
