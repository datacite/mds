package org.datacite.mds.web.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.mail.MailMessageFactory;
import org.datacite.mds.service.MagicAuthStringService;
import org.datacite.mds.service.MailService;
import org.datacite.mds.util.Constants;
import org.datacite.mds.web.ui.UiController;
import org.datacite.mds.web.ui.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "allocators", formBackingObject = Allocator.class, delete = false, populateMethods = false)
@RequestMapping("/allocators")
@Controller
public class AllocatorController implements UiController {
    
    @Autowired
    private MagicAuthStringService magicAuthStringService;
    
    @Autowired
    MailService mailService;
    
    @Autowired
    MailMessageFactory mailMessageFactory;
    
    @RequestMapping(params = "find=BySymbolEquals", method = RequestMethod.GET)
    public String findAllocatorsBySymbolEquals(@RequestParam("symbol") String symbol, Model model) {
        Allocator allocator = Allocator.findAllocatorBySymbol(symbol);
        model.asMap().clear();
        return (allocator == null) ? "allocators/show" : "redirect:/allocators/" + allocator.getId();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        int sizeNo = size == null ? LIST_DEFAULT_SIZE : Math.min(size.intValue(), LIST_MAX_SIZE);
        model.addAttribute("allocators", Allocator.findAllocatorEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
        float nrOfPages = (float) Allocator.countAllocators() / sizeNo;
        model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        model.addAttribute("size", sizeNo);
        return "allocators/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        Allocator allocator = Allocator.findAllocator(id);
        model.addAttribute("allocator", allocator);
        model.addAttribute("itemId", id);
        model.addAttribute("magicAuthString", magicAuthStringService.getCurrentAuthString(allocator));
        return "allocators/show";
    }
    

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("allocator", new Allocator());
        model.addAttribute("sendWelcomeMail", "true");
        return "allocators/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Allocator allocator, BindingResult result, @RequestParam(required=false) Boolean sendWelcomeMail, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("allocator", allocator);
            model.addAttribute("sendWelcomeMail", sendWelcomeMail);
            return "allocators/create";
        }
        allocator.persist();
        UiUtils.refreshSymbolsForSwitchUser(session);
        
        if (BooleanUtils.isTrue(sendWelcomeMail)) {
            MailMessage mail = mailMessageFactory.createWelcomeAllocatorMail(allocator);
            mailService.sendAsync(mail);
        }
        
        model.asMap().clear();
        return "redirect:/allocators/" + allocator.getId();
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Allocator allocator = Allocator.findAllocator(id);
        model.addAttribute("allocator", allocator);
        model.addAttribute("magicAuthString", magicAuthStringService.getCurrentAuthString(allocator));
        model.addAttribute("sendWelcomeMail", "false");
        return "allocators/update";
    }
    
    
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Allocator allocator, BindingResult result, @RequestParam(required=false) Boolean sendWelcomeMail, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("allocator", allocator);
            model.addAttribute("sendWelcomeMail", sendWelcomeMail);
            return "allocators/update";
        }
        allocator.merge();
        UiUtils.refreshSymbolsForSwitchUser(session);
        
        if (BooleanUtils.isTrue(sendWelcomeMail)) {
            MailMessage mail = mailMessageFactory.createWelcomeAllocatorMail(allocator);
            mailService.sendAsync(mail);
        }

        model.asMap().clear();
        return "redirect:/allocators/" + allocator.getId();
    }
    
    @ModelAttribute("prefixes")
    public Collection<Prefix> populatePrefixes() {
        List<Prefix> allPrefixes = Prefix.findAllPrefixes();
        List<Prefix> assignedPrefixes = new ArrayList<Prefix>(); 
        List<Prefix> unassignedPrefixes = new ArrayList<Prefix>(); 
        for (Prefix prefix : allPrefixes) {
            boolean isAssigned = Allocator.findAllocatorsByPrefix(prefix).size() > 0;
            if (isAssigned) 
                assignedPrefixes.add(prefix);
            else
                unassignedPrefixes.add(prefix);
        }
        return ListUtils.union(unassignedPrefixes, assignedPrefixes);
    }
    
    @ModelAttribute("experiments")
    public Collection<String> populateExperiments() {
        return Constants.EXPERIMENTS_AVAILABLE;
    }
}
