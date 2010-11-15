package org.datacite.mds.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.web.util.Converters;
import org.datacite.mds.web.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "datasets", formBackingObject = Dataset.class, delete = false)
@RequestMapping("/datasets")
@Controller
public class DatasetController {

    private static Logger log = Logger.getLogger(DatasetController.class);

    @Autowired
    private GenericConversionService myConversionService;

    @Autowired
    HandleService handleService;

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getSimpleDatacentreConverter());
        myConversionService.addConverter(Converters.getSimpleDatasetConverter());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        Dataset dataset = Dataset.findDataset(id);
        model.addAttribute("dataset", dataset);
        model.addAttribute("metadatas", Metadata.findMetadatasByDataset(dataset).getResultList());
        model.addAttribute("itemId", id);
        return "datasets/show";
    }

    @ModelAttribute("datacentres")
    public Collection<Datacentre> populateDatacentres(HttpServletRequest request) {
        String symbol = request.getUserPrincipal().getName();
        Datacentre datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        return Arrays.asList(datacentre);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model model, HttpServletRequest request) {
        String symbol = request.getUserPrincipal().getName();
        Datacentre datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("datasets", Dataset.findDatasetEntriesByDatacentres(datacentre, page == null ? 0 : (page
                    .intValue() - 1)
                    * sizeNo, sizeNo));
            float nrOfPages = (float) Dataset.countDatasetsByDatacentre(datacentre) / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            model.addAttribute("datasets", Dataset.findDatasetsByDatacentres(datacentre));
        }
        return "datasets/list";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Dataset dataset, BindingResult result, Model model, HttpServletRequest request) {
        if (!dataset.getUrl().isEmpty() && !result.hasErrors()) {
            log.info("URL is set; try to mint the DOI");
            try {
                handleService.create(dataset.getDoi(), dataset.getUrl());
            } catch (HandleException e) {
                ObjectError error = new ObjectError("", "HandleService: " + e.getMessage());
                result.addError(error);
            }
        }

        try {
            SecurityUtils.checkQuota(dataset.getDatacentre());
        } catch (SecurityException e) {
            ObjectError error = new ObjectError("", e.getMessage());
            result.addError(error);
        }

        if (result.hasErrors()) {
            model.addAttribute("dataset", dataset);
            return "datasets/create";
        }

        dataset.persist();
        dataset.getDatacentre().incQuotaUsed();
        return "redirect:/datasets/" + dataset.getId().toString();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Dataset dataset, BindingResult result, Model model, HttpServletRequest request) {
        if (!dataset.getUrl().isEmpty() && !result.hasErrors()) {
            log.info("URL is set; try to update the DOI");
            try {
                handleService.update(dataset.getDoi(), dataset.getUrl());
            } catch (HandleException e) {
                log.info("updating DOI failed; try to mint it");
                try {
                    handleService.create(dataset.getDoi(), dataset.getUrl());
                } catch (HandleException e1) {
                    ObjectError error = new ObjectError("", "HandleService: " + e.getMessage());
                    result.addError(error);
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("dataset", dataset);
            return "datasets/update";
        }
        dataset.merge();
        return "redirect:/datasets/" + dataset.getId().toString();
    }
}
