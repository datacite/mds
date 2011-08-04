package org.datacite.mds.tools.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.tools.AbstractTool;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public abstract class BaseMetadataConvertor extends AbstractTool {

    Logger log = Logger.getLogger(this.getClass());

    Transformer transformer;

    public BaseMetadataConvertor(String xsltPath) {
        try {
            Resource xslt = new ClassPathResource(xsltPath);
            Source xsltSource;
            xsltSource = new StreamSource(xslt.getInputStream());
            TransformerFactory tFactory = TransformerFactory.newInstance();
            transformer = tFactory.newTransformer(xsltSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String[] args) {
        log.info("starting");
        List<Dataset> datasets = Dataset.findAllDatasets();
        for (Dataset dataset : datasets) {
            Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
            if (metadata != null)
                checkAndConvert(metadata);
        }
        log.info("done");
    }

    void checkAndConvert(Metadata metadata) {
        log.debug("checking " + metadata);
        if (!needsConversion(metadata))
            return;

        Metadata converted;
        try {
            converted = convert(metadata);
            converted.persist();
            log.info("converted " + metadata + " => " + converted);
        } catch (ConstraintViolationException e) {
            String msg = ValidationUtils.collateViolationMessages(e.getConstraintViolations());
            log.warn("failed to convert " + metadata + ": " + msg);
        } catch (Exception e) {
            log.warn("failed to convert " + metadata + ": " + e);
        }
    }

    public abstract boolean needsConversion(Metadata metadata);

    Metadata convert(Metadata orig) throws TransformerException {
        ByteArrayInputStream input = new ByteArrayInputStream(orig.getXml());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(input), new StreamResult(output));

        Metadata converted = new Metadata();
        converted.setDataset(orig.getDataset());
        converted.setXml(output.toByteArray());
        converted.setIsConvertedByMds(true);
        return converted;
    }

}
