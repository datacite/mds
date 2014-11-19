package org.datacite.mds.web.ui.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.util.Utils;
import org.datacite.mds.web.ui.UiController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

@RooWebScaffold(path = "metadatas", formBackingObject = Metadata.class, delete = false, update = false, populateMethods = false)
@RequestMapping("/metadatas")
@Controller
public class MetadataController implements UiController {
    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @ModelAttribute("datasets")
    public Collection<Dataset> populateDatasets(@RequestParam(value = "dataset", required = false) Long datasetId) throws SecurityException {
        Dataset dataset = Dataset.findDataset(datasetId);
        if (dataset != null)
            SecurityUtils.checkDatasetOwnership(dataset);
        return Arrays.asList(dataset);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Metadata metadata, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws SecurityException {
        SecurityUtils.checkDatasetOwnership(metadata.getDataset());
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("metadata", metadata);
            return "metadatas/create";
        }
        uiModel.asMap().clear();
        metadata.persist();
        return "redirect:/metadatas/" + encodeUrlPathSegment(metadata.getId().toString(), httpServletRequest);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) throws SecurityException {
        Metadata metadata = Metadata.findMetadata(id);
        SecurityUtils.checkDatasetOwnership(metadata.getDataset());
        model.addAttribute("metadata", metadata);
        String prettyXml;
        try {
            byte[] xml = metadata.getXml();
            prettyXml = Utils.formatXML(xml);
        } catch (Exception e) {
            prettyXml = "error formatting xml: " + e.getMessage();
        }
        model.addAttribute("prettyxml", prettyXml);
        model.addAttribute("itemId", id);
        return "metadatas/show";
    }

    @RequestMapping(value = "/{id}", params = "raw", method = RequestMethod.GET)
    public ResponseEntity<? extends Object> showRaw(@PathVariable("id") Long id) throws SecurityException {
        HttpHeaders headers = new HttpHeaders();
        Metadata metadata = Metadata.findMetadata(id);
        SecurityUtils.checkDatasetOwnership(metadata.getDataset());
        if (metadata == null) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(metadata.getXml(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String list() {
        return "index";
    }
}
