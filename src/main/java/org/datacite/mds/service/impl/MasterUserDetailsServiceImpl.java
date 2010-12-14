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
public class MasterUserDetailsServiceImpl implements UserDetailsService {

    Logger log4j = Logger.getLogger(MasterUserDetailsServiceImpl.class);

    UserDetailsService userDetailsService;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        String mastername = Utils.getAllocatorFromDatacentreSymbol(username);
        if (mastername == null) {
            throw new UsernameNotFoundException("cannot parse allocator symbol");
        }

        log4j.debug("try to build master user");

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

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}