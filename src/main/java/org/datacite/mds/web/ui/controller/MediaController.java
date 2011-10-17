package org.datacite.mds.web.ui.controller;

import java.util.Arrays;
import java.util.Collection;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "medias", formBackingObject = Media.class, populateMethods = false)
@RequestMapping("/medias")
@Controller
public class MediaController {
    
    @ModelAttribute("datasets")
    public Collection<Dataset> populateDatasets(@RequestParam(value = "dataset", required = false) Long datasetId) {
        Dataset dataset = Dataset.findDataset(datasetId);
        return Arrays.asList(dataset);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Media media = Media.findMedia(id);
        Dataset dataset = media.getDataset();
        media.remove();
        uiModel.asMap().clear();
        return "redirect:/datasets/" + dataset.getId();
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        Media media = Media.findMedia(id);
        uiModel.addAttribute("media", media);
        uiModel.addAttribute("datasets", Arrays.asList(media.getDataset()));
        return "medias/update";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list() {
        return "index";
    }

}
