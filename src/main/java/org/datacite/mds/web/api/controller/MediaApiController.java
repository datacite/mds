package org.datacite.mds.web.api.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.datacite.mds.util.Utils;
import org.datacite.mds.web.api.ApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/media")
public class MediaApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(MediaApiController.class);

    @RequestMapping(value = "**", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<String> get(HttpServletRequest httpRequest) throws IOException {
        String doi = getDoiFromRequest(httpRequest);

        Dataset dataset = Dataset.findDatasetByDoi(doi);
        List<Media> medias = Media.findMediasByDataset(dataset).getResultList();

        StringBuffer responseBody = new StringBuffer();
        for (Media media : medias)
            responseBody.append(media.getMediaType() + "=" + media.getUrl() + "\n");

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    private String getDoiFromRequest(HttpServletRequest request) {
        String uri = request.getServletPath();
        String doi = uri.replaceFirst("/media/", "");
        doi = Utils.normalizeDoi(doi);
        return doi;
    }

}
