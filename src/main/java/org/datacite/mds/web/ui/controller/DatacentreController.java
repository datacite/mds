package org.datacite.mds.web.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.mail.MailMessageFactory;
import org.datacite.mds.service.MagicAuthStringService;
import org.datacite.mds.service.MailService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.Constants;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.web.ui.UiController;
import org.datacite.mds.web.ui.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "datacentres", formBackingObject = Datacentre.class, delete = false, populateMethods = false)
@RequestMapping("/datacentres")
@Controller
public class DatacentreController implements UiController {

    @Autowired
    private MagicAuthStringService magicAuthStringService;
    
    @Autowired
    MailService mailService;
    
    @Autowired
    MailMessageFactory mailMessageFactory;

    @Transactional
    private Allocator getCurrentAllocator() {
        try {
            return SecurityUtils.getCurrentAllocator();
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        Allocator allocator = getCurrentAllocator();
        Datacentre datacentre = new Datacentre();
        datacentre.setAllocator(allocator);
        datacentre.setSymbol(allocator.getSymbol() + ".");
        model.addAttribute("datacentre", datacentre);
        model.addAttribute("sendWelcomeMail", "true");
        List dependencies = new ArrayList();
        if (Allocator.countAllocators() == 0) {
            dependencies.add(new String[] { "allocator", "allocators" });
        }
        model.addAttribute("dependencies", dependencies);
        return "datacentres/create";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model model) {
        Allocator allocator = getCurrentAllocator();
        int sizeNo = size == null ? LIST_DEFAULT_SIZE : Math.min(size.intValue(), LIST_MAX_SIZE);
        model.addAttribute("datacentres", Datacentre.findDatacentreEntriesByAllocator(allocator, page == null ? 0
                : (page.intValue() - 1) * sizeNo, sizeNo));
        float nrOfPages = (float) Datacentre.countDatacentresByAllocator(allocator) / sizeNo;
        model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                : nrOfPages));
        System.out.println(model.asMap());
        model.addAttribute("size", sizeNo);
        return "datacentres/list";
    }

    @ModelAttribute("prefixes")
    public Collection<Prefix> populatePrefixes() {
        Set<Prefix> allPrefixes = getCurrentAllocator().getPrefixes();
        List<Prefix> assignedPrefixes = new ArrayList<Prefix>(); 
        List<Prefix> unassignedPrefixes = new ArrayList<Prefix>(); 
        for (Prefix prefix : allPrefixes) {
            boolean isAssigned = Datacentre.findDatacentresByPrefix(prefix).size() > 0;
            if (isAssigned) 
                assignedPrefixes.add(prefix);
            else
                unassignedPrefixes.add(prefix);
        }
        return ListUtils.union(unassignedPrefixes, assignedPrefixes);
    }

    @ModelAttribute("allocators")
    public Collection<Allocator> populateAllocators() {
        return Arrays.asList(getCurrentAllocator());
    }

    @RequestMapping(params = "find=BySymbolEquals", method = RequestMethod.GET)
    public String findDatacentresBySymbolEquals(@RequestParam("symbol") String symbol, Model model) {
        Datacentre datacentre = Datacentre.findDatacentreBySymbol(symbol);
        model.asMap().clear();
        return (datacentre == null) ? "datacentres/show" : "redirect:/datacentres/" + datacentre.getId();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        Datacentre datacentre = Datacentre.findDatacentre(id);
        model.addAttribute("datacentre", datacentre);
        model.addAttribute("itemId", id);
        model.addAttribute("magicAuthString", magicAuthStringService.getCurrentAuthString(datacentre));
        return "datacentres/show";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Datacentre datacentre, BindingResult result, @RequestParam(required=false) Boolean sendWelcomeMail, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("datacentre", datacentre);
            model.addAttribute("sendWelcomeMail", sendWelcomeMail);
            return "datacentres/create";
        }
        datacentre.persist();
        UiUtils.refreshSymbolsForSwitchUser(session);
     
        if (BooleanUtils.isTrue(sendWelcomeMail)) {
            MailMessage mail = mailMessageFactory.createWelcomeDatacentreMail(datacentre);
            mailService.sendAsync(mail);
        }
        
        model.asMap().clear();
        return "redirect:/datacentres/" + datacentre.getId();
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Datacentre datacentre = Datacentre.findDatacentre(id);
        model.addAttribute("datacentre", datacentre);
        model.addAttribute("sendWelcomeMail", "false");
        model.addAttribute("magicAuthString", magicAuthStringService.getCurrentAuthString(datacentre));
        return "datacentres/update";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Datacentre datacentre, BindingResult result, @RequestParam(required=false) Boolean sendWelcomeMail, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("datacentre", datacentre);
            model.addAttribute("sendWelcomeMail", sendWelcomeMail);
            return "datacentres/update";
        }
        datacentre.merge();
        UiUtils.refreshSymbolsForSwitchUser(session);
        
        if (BooleanUtils.isTrue(sendWelcomeMail)) {
            MailMessage mail = mailMessageFactory.createWelcomeDatacentreMail(datacentre);
            mailService.sendAsync(mail);
        }
        
        model.asMap().clear();
        return "redirect:/datacentres/" + datacentre.getId();
    }
    
    
    @ModelAttribute("experiments")
    public Collection<String> populateExperiments() {
        return Constants.EXPERIMENTS_AVAILABLE;
    }
}
