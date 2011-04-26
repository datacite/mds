package org.datacite.mds.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class SchemaServiceImpl implements SchemaService {

    public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    @Override
    public String getSchemaLocation(Metadata metadata) throws ValidationException {
        try {
            XMLStreamReader xmlStreamReader = createStreamReader(metadata);
            seekRootElement(xmlStreamReader);
            return parseRootElement(xmlStreamReader);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new ValidationException();
        }
    }

    private XMLStreamReader createStreamReader(Metadata metadata) throws XMLStreamException {
        InputStream xml = new ByteArrayInputStream(metadata.getXml());
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xml);
        return xmlStreamReader;
    }

    private void seekRootElement(XMLStreamReader xmlStreamReader) throws XMLStreamException, ValidationException {
        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.isStartElement())
                return;
        }
        throw new ValidationException("cannot find root Element");
    }

    private String parseRootElement(XMLStreamReader root) throws ValidationException {
        String location = null;
        String rootNamespace = root.getNamespaceURI();
        
        if (StringUtils.isEmpty(rootNamespace)) {
            location = root.getAttributeValue(XSI_NAMESPACE_URI, "noNamespaceSchemaLocation");
        } else {
            String locations = root.getAttributeValue(XSI_NAMESPACE_URI, "schemaLocation");
            location = getLocationForNamespace(rootNamespace, locations);
        }

        if (location == null)
            throw new ValidationException("cannot find namespace location");
        else
            return location;
    }

    private String getLocationForNamespace(String rootNamespace, String locationAttr) {
        String[] locations = StringUtils.split(locationAttr);
        int maxIndex = locations.length - 1;
        for (int i = 0; i < maxIndex; i += 2) {
            String ns = locations[i];
            String uri = locations[i+1];
            if (StringUtils.equals(rootNamespace, ns))
                return uri;
        }
        return null;
    }

}
