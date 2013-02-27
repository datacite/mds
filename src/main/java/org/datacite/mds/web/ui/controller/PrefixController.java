package org.datacite.mds.web.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.ui.UiController;
import org.datacite.mds.web.ui.model.PrefixCreateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "prefixes", formBackingObject = Prefix.class, delete = false, populateMethods = false)
@RequestMapping("/prefixes")
@Controller
public class PrefixController implements UiController {
    
    @Autowired
    ValidationHelper validationHelper;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        int sizeNo = size == null ? LIST_DEFAULT_SIZE : Math.min(size.intValue(), LIST_MAX_SIZE);
        uiModel.addAttribute("prefixes", Prefix.findPrefixEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
        float nrOfPages = (float) Prefix.countPrefixes() / sizeNo;
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        uiModel.addAttribute("size", sizeNo);
        return "prefixes/list";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid PrefixCreateModel createModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        Collection<String> prefixesStr = new HashSet<String>(Utils.csvToList(createModel.getPrefixes()));

        ArrayList<Prefix> prefixes = new ArrayList<Prefix>();
        for (String prefixStr : prefixesStr) {
            Prefix prefix = new Prefix();
            prefix.setPrefix(prefixStr);
            prefixes.add(prefix);
        }
        
        try {
            validationHelper.validate(prefixes.toArray());
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                String msg = ((Prefix) violation.getRootBean()).getPrefix() + ": " + violation.getMessage();
                bindingResult.rejectValue("prefixes", null, msg);
            }
        }
        
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            uiModel.addAttribute("prefixCreateModel", createModel);
            return "prefixes/create";
        }
        
        for (Prefix prefix : prefixes)
            prefix.persist();
       
        uiModel.asMap().clear();
        return "redirect:/prefixes/";
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("prefixCreateModel", new PrefixCreateModel());
        return "prefixes/create";
    }
}
