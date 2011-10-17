package org.datacite.mds.web.ui.controller;

import java.util.Arrays;
import java.util.Collection;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Utils;
import org.datacite.mds.web.ui.UiController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public Collection<Dataset> populateDatasets(@RequestParam(value = "dataset", required = false) Long datasetId) {
        Dataset dataset = Dataset.findDataset(datasetId);
        return Arrays.asList(dataset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        Metadata metadata = Metadata.findMetadata(id);
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
    public ResponseEntity<? extends Object> showRaw(@PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        Metadata metadata = Metadata.findMetadata(id);
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
