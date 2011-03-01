package org.datacite.mds.domain;

public interface AllocatorOrDatacentre {
    
    AllocatorOrDatacentre merge();
    
    void persist();

    Boolean getIsActive();
    
    String getRoleName();
    
    String getSymbol();
    
    String getContactName();

    String getContactEmail();
    
    String getPassword();
    
    void setPassword(String password);
    
    /**
     * calculate String to be used for magic auth key
     * 
     * @return (unhashed) base part of the magic auth string
     */
    String getBaseAuthString();

}
