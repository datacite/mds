package org.datacite.mds.web;

import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.util.Converters;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "datasets", formBackingObject = Dataset.class, delete = false)
@RequestMapping("/datasets")
@Controller
public class DatasetController {

    @InitBinder
    void registerConverters(WebDataBinder binder) {
        if (binder.getConversionService() instanceof GenericConversionService) {
            GenericConversionService conversionService = (GenericConversionService) binder.getConversionService();
            conversionService.addConverter(Converters.getSimpleDatacentreConverter());
        }
    }
}
