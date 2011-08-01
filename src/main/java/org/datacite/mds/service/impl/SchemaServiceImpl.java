package org.datacite.mds.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.service.SchemaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class SchemaServiceImpl implements SchemaService {

    public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    private static Logger log4j = Logger.getLogger(SchemaServiceImpl.class);

    Map<String, Schema> schemaCache;

    @Value("${xml.schema.caching}")
    boolean validatorCacheEnabled;
    
    @Value("${xml.schema.xpath.doi}")
    String doiXPath;
    
    @Value("${xml.schema.location.local}")
    String schemaLocationLocal;

    @Value("${xml.schema.location.prefix}")
    String schemaLocationPrefix;

    
    XPathExpression doiXPathExpression;

    public SchemaServiceImpl() {
        schemaCache = new ConcurrentHashMap<String, Schema>();
    }
    
    @PostConstruct
    private void initXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try {
            doiXPathExpression = xPath.compile(doiXPath);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getNamespace(byte[] xml) throws ValidationException {
        return getSchemaInfo(xml).namespace;
    }
    
    @Override
    public String getSchemaLocation(byte[] xml) throws ValidationException {
        return getSchemaInfo(xml).location;
    }

    public SchemaInfo getSchemaInfo(byte[] xml) throws ValidationException {
        try {
            XMLStreamReader xmlStreamReader = createStreamReader(xml);
            seekRootElement(xmlStreamReader);
            return parseRootElement(xmlStreamReader);
        } catch (XMLStreamException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private XMLStreamReader createStreamReader(byte[] xml) throws XMLStreamException {
        InputStream xmlStream = new ByteArrayInputStream(xml);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlStream);
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

    private SchemaInfo parseRootElement(XMLStreamReader root) throws ValidationException {
        SchemaInfo schemaInfo = new SchemaInfo(); 
        schemaInfo.namespace = root.getNamespaceURI();

        if (StringUtils.isEmpty(schemaInfo.namespace)) {
            schemaInfo.location = root.getAttributeValue(XSI_NAMESPACE_URI, "noNamespaceSchemaLocation");
        } else {
            String locations = root.getAttributeValue(XSI_NAMESPACE_URI, "schemaLocation");
            schemaInfo.location = getLocationForNamespace(schemaInfo.namespace, locations);
        }

        if (schemaInfo.location == null)
            throw new ValidationException("cannot find namespace location");
        else
            return schemaInfo;
    }

    private String getLocationForNamespace(String rootNamespace, String locationAttr) {
        String[] locations = StringUtils.split(locationAttr);
        int maxIndex = locations.length - 1;
        for (int i = 0; i < maxIndex; i += 2) {
            String ns = locations[i];
            String uri = locations[i + 1];
            if (StringUtils.equals(rootNamespace, ns))
                return uri;
        }
        return null;
    }
    
    @Override
    public Validator getSchemaValidator(String schemaLocation) throws SAXException {
        Schema schema;
        if (validatorCacheEnabled)
            schema = getCachedSchema(schemaLocation);
        else
            schema = getFreshSchema(schemaLocation);
        Validator validator = schema.newValidator();
        return validator;
    }

    private Schema getCachedSchema(String schemaLocation) throws SAXException {
        Schema schema;
        if (schemaCache.containsKey(schemaLocation)) {
            log4j.debug("cache-hit for '" + schemaLocation + "'");
            schema = schemaCache.get(schemaLocation);
        } else {
            log4j.debug("cache-miss for '" + schemaLocation + "'");
            schema = getFreshSchema(schemaLocation);
            schemaCache.put(schemaLocation, schema);
        }
        return schema;
    }

    private Schema getFreshSchema(String schemaLocation) throws SAXException {
        String localSchemaLocation = convertSchemaLocationToLocal(schemaLocation);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(localSchemaLocation);
        Schema schema = schemaFactory.newSchema(schemaSource);
        return schema;
    }
    
    protected String convertSchemaLocationToLocal(String schemaLocation) {
        if (StringUtils.isEmpty(schemaLocationLocal))
            return schemaLocation;
        else {
            String localSchemaLocation = StringUtils.replaceOnce(schemaLocation, schemaLocationPrefix,
                    schemaLocationLocal);
            log4j.debug("converting schemaLocation " + schemaLocation + " -> " + localSchemaLocation);
            return localSchemaLocation;
        }
    }

    @Override
    synchronized public String getDoi(byte[] xml) {
        // TODO throw Exception instead of returning null
        InputStream stream = new ByteArrayInputStream(xml);
        InputSource source = new InputSource(stream);
        String doi = null;
        try {
            doi = doiXPathExpression.evaluate(source);
            doi = doi.trim();
        } catch (XPathExpressionException e) {
            log4j.debug("catch Exception: " + ExceptionUtils.getThrowableList(e));
        }
        return doi;
    }
    
    private class SchemaInfo {
        String namespace = null;
        String location = null;
    }

}
