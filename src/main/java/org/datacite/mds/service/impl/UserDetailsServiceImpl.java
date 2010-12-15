/*******************************************************************************
 * Copyright (c) 2010 DataCite
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/

package org.datacite.mds.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This class is responsible for retrieving user credentials from the database
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    Logger log4j = Logger.getLogger(UserDetailsServiceImpl.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        log4j.debug("trying to find user name " + username);
        
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        String symbol, password, role;
        boolean isActive;
        
        Allocator allocator = Allocator.findAllocatorBySymbol(username);

        if (allocator != null) {
            log4j.debug("found allocator = " + username);
            symbol = allocator.getSymbol(); 
            password = allocator.getPassword();
            role = allocator.getRoleName();
            isActive = allocator.getIsActive() == null ? false : allocator.getIsActive();
        } else {
            Datacentre datacentre = Datacentre.findDatacentreBySymbol(username);
            if (datacentre == null) {
                throw new UsernameNotFoundException("user not found");
            }
            
            log4j.debug("found datacentre = " + username);
            symbol = datacentre.getSymbol(); 
            password = datacentre.getPassword();
            role = datacentre.getRoleName();
            isActive = datacentre.getIsActive() == null ? false : datacentre.getIsActive();
        }

        authorities.add(new GrantedAuthorityImpl(role));
        return new User(symbol, password, isActive, 
                            true, /* account not expired */
                            true, /* credentials not expired */
                            true, /* account not locked */
                            authorities);
        
    }
}