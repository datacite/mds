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

package org.datacite.mds.service.userdetails;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This class is responsible for retrieving master user details. The
 * crendentials for a given username are built from its role and username 
 * and the password of the master user (e.g. allocator or admin).
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
    
    /**
     * returns the username of the master user of the given username,
     * e.g. allocator username for a given datacentre
     */
    public abstract String getMasterUsername(String username);

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
