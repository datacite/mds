package org.datacite.mds.web.ui.controller;

import java.util.Collection;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.util.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class WelcomeController {
    @RequestMapping
    public String welcome(HttpSession session) {
        refreshSymbolsForSwitchUser(session);
        return "index";
    }

    public void refreshSymbolsForSwitchUser(HttpSession session) {
        Collection<? extends AllocatorOrDatacentre> users = SecurityUtils.getDirectInferiorsOfCurrentAllocator();
        Collection<String> symbols = new TreeSet<String>();
        symbols.addAll(Utils.toSymbols(users));

        if (!symbols.isEmpty())
            symbols.add(""); // add empty string to disable symbol default selection

        session.setAttribute("symbols", symbols);
    }
}
