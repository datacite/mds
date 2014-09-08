package org.datacite.mds.domain;

import java.util.Collection;
import java.util.Set;

public interface AllocatorOrDatacentre {
    
    AllocatorOrDatacentre merge();
    
    void persist();

    Boolean getIsActive();
    
    String getRoleName();
    
    String getSymbol();
    
    String getContactName();

    String getContactEmail();
    
    void setContactEmail(String contactEmail);
    
    String getPassword();
    
    Collection<String> getExperiments();
    
    void setPassword(String password);
    
    /**
     * calculate String to be used for magic auth key
     * 
     * @return (unhashed) base part of the magic auth string
     */
    String getBaseAuthString();
    
    Set<Prefix> getPrefixes();

}
