package org.datacite.mds.web.ui.controller;

import java.util.Collection;

import org.datacite.mds.util.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@RequestMapping("/")
@Controller
@SessionAttributes("symbols")
public class WelcomeController {
    @RequestMapping
    public String welcome(Model model) {
        return "index";
    }

    @ModelAttribute("symbols")
    public Collection<String> populateSymbols() {
        Collection<String> symbols = Utils.getAllSymbols();
        symbols.add(""); // add empty string to disable symbol default selection
        return symbols;
    }
}
