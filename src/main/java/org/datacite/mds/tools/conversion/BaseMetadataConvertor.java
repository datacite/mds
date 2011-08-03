package org.datacite.mds.tools.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.tools.AbstractTool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public abstract class BaseMetadataConvertor extends AbstractTool {

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
    public void run(String[] args) throws Exception {
        List<Dataset> datasets = Dataset.findAllDatasets();
        for (Dataset dataset : datasets) {
            Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
            if (metadata != null && needsConversion(metadata)) {
                Metadata converted = convert(metadata);
                converted.persist();
            }
        }
    }

    public abstract boolean needsConversion(Metadata metadata);

    Metadata convert(Metadata orig) throws TransformerException {
        System.out.println("Converting " + orig.getId());

        ByteArrayInputStream input = new ByteArrayInputStream(orig.getXml());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(input),new StreamResult(output));
        
        Metadata converted = new Metadata();
        converted.setDataset(orig.getDataset());
        converted.setXml(output.toByteArray());
        converted.setIsConvertedByMds(true);
        return converted;
    }

}
