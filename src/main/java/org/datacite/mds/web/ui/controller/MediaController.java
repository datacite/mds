package org.datacite.mds.web.ui.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "medias", formBackingObject = Media.class, populateMethods = false, delete=false)
@RequestMapping("/medias")
@Controller
public class MediaController {
    
    @ModelAttribute("datasets")
    public Collection<Dataset> populateDatasets(@RequestParam(value = "dataset", required = false) Long datasetId) throws SecurityException {
        Dataset dataset = Dataset.findDataset(datasetId);
        if (dataset != null)
            SecurityUtils.checkDatasetOwnership(dataset);
        return Arrays.asList(dataset);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Media media, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("media", media);
            return "medias/create";
        }
        uiModel.asMap().clear();
        media.persist();
        return "redirect:/medias/" + encodeUrlPathSegment(media.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("media", Media.findMedia(id));
        uiModel.addAttribute("itemId", id);
        return "medias/show";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Media media, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws SecurityException {
        SecurityUtils.checkDatasetOwnership(media.getDataset());
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("media", media);
            return "medias/update";
        }
        uiModel.asMap().clear();
        media.merge();
        return "redirect:/medias/" + encodeUrlPathSegment(media.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) throws SecurityException {
        Media media = Media.findMedia(id);
        SecurityUtils.checkDatasetOwnership(media.getDataset());
        uiModel.addAttribute("media", media);
        uiModel.addAttribute("datasets", Arrays.asList(media.getDataset()));
        return "medias/update";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list() {
        return "index";
    }

}
