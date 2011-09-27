package org.datacite.mds.web.ui.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.web.ui.UiUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class WelcomeController {
    @RequestMapping
    public String welcome(HttpSession session) {
        UiUtils.refreshSymbolsForSwitchUser(session);
        session.setAttribute("login_pathway", getLoginPathway());
        return "index";
    }

    public List<String> getLoginPathway() {
        LinkedList<String> pathway = new LinkedList<String>();

        Authentication auth = SecurityUtils.getCurrentAuthentication();
        
        while(auth != null) {
            pathway.addFirst(auth.getName());
            auth = SecurityUtils.getOriginalUser(auth);
        }
        System.out.println(pathway);

        return pathway;
    }
    

}
