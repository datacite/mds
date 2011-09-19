package org.datacite.mds.web.ui.controller;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "medias", formBackingObject = Media.class)
@RequestMapping("/medias")
@Controller
public class MediaController {
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Media media = Media.findMedia(id);
        Dataset dataset = media.getDataset();
        media.remove();
        uiModel.asMap().clear();
        return "redirect:/datasets/" + dataset.getId();
    }
    
    
    @RequestMapping(method = RequestMethod.GET)
    public String list() {
        return "index";
    }

}
