package org.datacite.mds.web;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.impl.ValidXMLValidator;
import org.datacite.mds.web.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
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

@RooWebScaffold(path = "metadatas", formBackingObject = Metadata.class, delete = false, update = false)
@RequestMapping("/metadatas")
@Controller
public class MetadataController {

    @Autowired
    private GenericConversionService myConversionService;

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getByteArrayConverter());
        myConversionService.addConverter(Converters.getSimpleDatasetConverter());
    }

    @ModelAttribute("datasets")
    public Collection<Dataset> populateDatasets(@RequestParam(value = "dataset", required = false) Long dataset_id) {
        Dataset dataset = Dataset.findDataset(dataset_id);
        return Arrays.asList(dataset);
    }

    @ModelAttribute("xsd")
    public String getXsd() {
        // ugly; I was not able to use Springs @Value annotation
        return new ValidXMLValidator().getXsd();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        Metadata metadata = Metadata.findMetadata(id);
        model.addAttribute("metadata", metadata);
        String prettyXml;
        try {
            String xml = new String(metadata.getXml());
            prettyXml = Utils.formatXML(xml);
        } catch (Exception e) {
            prettyXml = "error formatting xml: " + e.getMessage();
        }
        model.addAttribute("prettyxml", prettyXml);
        model.addAttribute("itemId", id);
        return "metadatas/show";
    }

    @RequestMapping(value = "/{id}", params = "raw", method = RequestMethod.GET)
    public ResponseEntity<? extends Object> show_raw(@PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        Metadata metadata = Metadata.findMetadata(id);
        if (metadata == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(metadata.getXml(), headers, HttpStatus.OK);
    }
}
