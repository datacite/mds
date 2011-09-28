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
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.tools.AbstractTool;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public abstract class BaseMetadataConvertor extends AbstractTool {

    Logger log = Logger.getLogger(this.getClass());

    Transformer transformer;
    
    boolean allNamespaces = true;
    String namespace;

    private long count_check = 0;
    private long count_needs_conversion = 0;
    private long count_converted = 0;

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
    
    public BaseMetadataConvertor(String xsltPath, String namespace) {
        this(xsltPath);
        allNamespaces = false;
        this.namespace = namespace;
    }

    @Override
    public void run(String[] args) {
        log.info("starting");
        
        List<Metadata> metadatas;
        if (allNamespaces)
            metadatas = Metadata.findLatestMetadatas();
        else
            metadatas = Metadata.findLatestMetadatasByNamespace(namespace);

        for (Metadata metadata : metadatas) {
            checkAndConvert(metadata);
        }
        
        long count_failed = count_needs_conversion - count_converted;
        String stats = "checked: " + count_check + ", converted: " + count_converted + ", failed: " + count_failed;
        log.info("done (" + stats + ")");
        System.out.println(stats);
    }

    void checkAndConvert(Metadata metadata) {
        log.debug("checking " + metadata);
        count_check++;
        if (!needsConversion(metadata))
            return;
        count_needs_conversion++;

        Metadata converted;
        try {
            converted = convert(metadata);
            converted.persist();
            log.info("converted " + metadata + " => " + converted);
            count_converted++;
        } catch (ConstraintViolationException e) {
            String msg = ValidationUtils.collateViolationMessages(e.getConstraintViolations());
            log.warn("failed to validate converted " + metadata + ": " + msg);
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
