package org.datacite.mds.web;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.web.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
