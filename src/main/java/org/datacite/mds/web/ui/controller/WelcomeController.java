package org.datacite.mds.web.ui.controller;

import javax.servlet.http.HttpSession;

import org.datacite.mds.web.ui.UiController;
import org.datacite.mds.web.ui.UiUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class WelcomeController implements UiController {
    @RequestMapping
    public String welcome(HttpSession session) {
        UiUtils.refreshSymbolsForSwitchUser(session);
        UiUtils.setLoginPathwaySessionAttribute(session);
        return "index";
    }
    

}
