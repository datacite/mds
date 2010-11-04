// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.datacite.mds.web;

import java.io.UnsupportedEncodingException;
import java.lang.Long;
import java.lang.String;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.datacite.mds.domain.Prefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect PrefixController_Roo_Controller {
    
    @Autowired
    private GenericConversionService PrefixController.conversionService;
    
    @RequestMapping(method = RequestMethod.POST)
    public String PrefixController.create(@Valid Prefix prefix, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("prefix", prefix);
            return "prefixes/create";
        }
        prefix.persist();
        return "redirect:/prefixes/" + encodeUrlPathSegment(prefix.getId().toString(), request);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String PrefixController.createForm(Model model) {
        model.addAttribute("prefix", new Prefix());
        return "prefixes/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String PrefixController.show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("prefix", Prefix.findPrefix(id));
        model.addAttribute("itemId", id);
        return "prefixes/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String PrefixController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("prefixes", Prefix.findPrefixEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Prefix.countPrefixes() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("prefixes", Prefix.findAllPrefixes());
        }
        return "prefixes/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String PrefixController.update(@Valid Prefix prefix, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("prefix", prefix);
            return "prefixes/update";
        }
        prefix.merge();
        return "redirect:/prefixes/" + encodeUrlPathSegment(prefix.getId().toString(), request);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String PrefixController.updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("prefix", Prefix.findPrefix(id));
        return "prefixes/update";
    }
    
    @RequestMapping(params = { "find=ByPrefixLike", "form" }, method = RequestMethod.GET)
    public String PrefixController.findPrefixesByPrefixLikeForm(Model model) {
        return "prefixes/findPrefixesByPrefixLike";
    }
    
    @RequestMapping(params = "find=ByPrefixLike", method = RequestMethod.GET)
    public String PrefixController.findPrefixesByPrefixLike(@RequestParam("prefix") String prefix, Model model) {
        model.addAttribute("prefixes", Prefix.findPrefixesByPrefixLike(prefix).getResultList());
        return "prefixes/list";
    }
    
    Converter<Prefix, String> PrefixController.getPrefixConverter() {
        return new Converter<Prefix, String>() {
            public String convert(Prefix prefix) {
                return new StringBuilder().append(prefix.getCreated()).append(" ").append(prefix.getPrefix()).toString();
            }
        };
    }
    
    @PostConstruct
    void PrefixController.registerConverters() {
        conversionService.addConverter(getPrefixConverter());
    }
    
    private String PrefixController.encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
