package org.datacite.mds.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "datasets", formBackingObject = Dataset.class, delete = false)
@RequestMapping("/datasets")
@Controller
public class DatasetController {

    @Autowired
    private GenericConversionService myConversionService;

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getSimpleDatacentreConverter());
        myConversionService.addConverter(Converters.getSimpleDatasetConverter());
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model, HttpServletRequest request) {
        String symbol = request.getUserPrincipal().getName();
        Datacentre datacentre = (Datacentre) Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        Dataset dataset = new Dataset();
        dataset.setDatacentre(datacentre);
        model.addAttribute("dataset", dataset);
        addDateTimeFormatPatterns(model);
        List dependencies = new ArrayList();
        if (Datacentre.countDatacentres() == 0) {
            dependencies.add(new String[] { "datacentre", "datacentres" });
        }
        model.addAttribute("dependencies", dependencies);
        return "datasets/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        Dataset dataset = Dataset.findDataset(id);
        model.addAttribute("dataset", dataset);
        model.addAttribute("metadatas", Metadata.findMetadatasByDataset(dataset).getResultList());
        model.addAttribute("itemId", id);
        return "datasets/show";
    }
}
