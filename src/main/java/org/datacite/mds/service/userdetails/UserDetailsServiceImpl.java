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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.util.DomainUtils;
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
        AllocatorOrDatacentre user = DomainUtils.findAllocatorOrDatacentreBySymbol(username);

        if (user == null)
            throw new UsernameNotFoundException("user not found");

        String symbol = user.getSymbol();
        String password = user.getPassword();
        String role = user.getRoleName();
        boolean isActive = BooleanUtils.toBoolean(user.getIsActive());
        log4j.debug("found " + symbol + " (" + role + ")");

        boolean credentialsNonExpired = true;
        if (StringUtils.isEmpty(password)) {
            password = "password must not be empty";
            credentialsNonExpired = false;
        }

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl(role));

        return new User(symbol, password, isActive, // 
                true, /* account not expired */
                credentialsNonExpired, 
                true, /* account not locked */
                authorities);

    }
}
