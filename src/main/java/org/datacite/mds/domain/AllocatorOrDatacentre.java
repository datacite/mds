package org.datacite.mds.domain;

public interface AllocatorOrDatacentre {
    
    public AllocatorOrDatacentre merge();
    
    public String getSymbol();

    public String getPassword();
    
    public void setPassword(String password);
    
    /**
     * calculate String to be used for magic auth key
     * 
     * @return (unhashed) base part of the magic auth string
     */
    public String getBaseAuthString();

}
