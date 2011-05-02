package org.datacite.mds.tools;

import java.io.Console;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.util.DomainUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountCreator extends AbstractTool {
    
    private final String ADMIN_DEFAULT_SYMBOL = "ADMIN";
    private final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private final String ADMIN_NAME = "Admin";
    private final String ADMIN_DEFAULT_MAIL = "admin@example.com";
    
    private Console console = System.console();
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ValidationHelper validationHelper;
    
    
    @Override
    public void run(String[] args) {
        if (DomainUtils.getAdmin() != null)
            throw new RuntimeException("admin account already exists");
        Allocator admin = readAdminFromConsole();
        System.out.println(admin);
        validationHelper.validate(admin);
        admin.persist();
    }
    
    private Allocator readAdminFromConsole() {
        Allocator admin = new Allocator();
        admin.setRoleName(ADMIN_ROLE_NAME);
        admin.setContactName(ADMIN_NAME);
        admin.setName(ADMIN_NAME);
        
        String symbol = readLine("Symbol", ADMIN_DEFAULT_SYMBOL);
        admin.setSymbol(symbol);
        
        String password = readEncodedPassword();
        admin.setPassword(password);
        
        String email = readLine("email", ADMIN_DEFAULT_MAIL);
        admin.setContactEmail(email);
        
        return admin;
    }
    
    private String readLine(String label, String defaultValue) {
        String value = console.readLine("%s [%s]:", label, defaultValue);
        return StringUtils.defaultIfEmpty(value, defaultValue);
    }
    
    private String readEncodedPassword() {
        char[] passwordChar = console.readPassword("Password:");
        if (passwordChar.length == 0)
            throw new RuntimeException("password cannot be empty");
        String password = new String(passwordChar);
        String encodedPassword = passwordEncoder.encodePassword(password, null);
        return encodedPassword;
        
    }

    public static void main(String[] args) {
        initAndRun(args);
    }
    
}
