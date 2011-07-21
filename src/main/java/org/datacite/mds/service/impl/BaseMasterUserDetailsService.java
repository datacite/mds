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

import org.apache.log4j.Logger;
import org.datacite.mds.util.Utils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This class is responsible for retrieving master user credentials. The
 * crendentials for a given username are built from the role and username of a
 * datacentre and the password of the assigned allocator.
 */
public abstract class BaseMasterUserDetailsService implements UserDetailsService {

    Logger log4j = Logger.getLogger(BaseMasterUserDetailsService.class);

    UserDetailsService userDetailsService;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    final public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        String mastername = getMasterUsername(username);
        log4j.debug("building master user '" + mastername + "'");

        UserDetails user = userDetailsService.loadUserByUsername(username);
        UserDetails master = userDetailsService.loadUserByUsername(mastername);

        return new User(user.getUsername(), //
                master.getPassword(), //
                master.isEnabled(), //
                master.isAccountNonExpired(), //
                master.isCredentialsNonExpired(), //
                master.isAccountNonLocked(), //
                user.getAuthorities());
    }
    
    public abstract String getMasterUsername(String username);

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
