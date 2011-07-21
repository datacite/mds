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

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.util.DomainUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * accept admin user as master user for all other accounts
 */
public class AdminMasterUserDetailsServiceImpl extends BaseMasterUserDetailsService {
 
    @Override
    public String getMasterUsername(String username) {
        Allocator admin = DomainUtils.getAdmin();
        if (admin == null) {
            throw new UsernameNotFoundException("cannot find a admin user");
        }
        String mastername = admin.getSymbol();
        return mastername;
    }

}
