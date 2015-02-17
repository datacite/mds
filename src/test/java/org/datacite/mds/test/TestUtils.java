package org.datacite.mds.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Utils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class TestUtils {

    public static final String DEFAULT_ALLOCATOR_SYMBOL = "AL";
    public static final String DEFAULT_DATACENTRE_SYMBOL = "AL.DC";

    /**
     * call a constructor of a given class even if it's private.
     * 
     * @param cls
     */
    public static void callConstructor(final Class<?> cls) {
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        c.setAccessible(true);
        try {
            final Object n = c.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setUsernamePassword(String username, String password) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, password));
    }

    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void login(AllocatorOrDatacentre user) {
        if (user == null) {
            logout();
        } else {
            setUsernamePassword(user.getSymbol(), user.getPassword());
        }
    }

    public static void persist(Object... objects) {
        for (Object object : objects) {
            if (object instanceof AllocatorOrDatacentre)
                ((AllocatorOrDatacentre) object).persist();
            else
                throw new IllegalArgumentException("unknown type");
        }
    }

    public static Allocator createAllocator(String symbol) {
        Allocator allocator = new Allocator();
        allocator.setSymbol(symbol);
        allocator.setPassword("12345678");
        allocator.setContactEmail("dummy@example.com");
        allocator.setContactName("example contact");
        allocator.setDoiQuotaAllowed(-1);
        allocator.setDoiQuotaUsed(0);
        allocator.setIsActive(true);
        allocator.setName("example name");
        allocator.setRoleName("ROLE_ALLOCATOR");
        return allocator;
    }

    public static Allocator createAdmin(String symbol) {
        Allocator admin = createAllocator(symbol);
        admin.setRoleName("ROLE_ADMIN");
        return admin;
    }

    public static Allocator createDev(String symbol) {
        Allocator dev = createAllocator(symbol);
        dev.setRoleName("ROLE_DEV");
        return dev;
    }

    public static Datacentre createDatacentre(String symbol, Allocator allocator) {
        Datacentre datacentre = new Datacentre();
        datacentre.setSymbol(symbol);
        datacentre.setAllocator(allocator);
        datacentre.setContactEmail("dummy@example.com");
        datacentre.setContactName("example contact");
        datacentre.setDoiQuotaAllowed(-1);
        datacentre.setDoiQuotaUsed(0);
        datacentre.setDomains("example.com");
        datacentre.setIsActive(true);
        datacentre.setName("example name");
        datacentre.setRoleName("ROLE_DATACENTRE");
        return datacentre;
    }

    public static Datacentre createDefaultDatacentre(String... prefixes) {
        Allocator allocator = TestUtils.createAllocator(DEFAULT_ALLOCATOR_SYMBOL);
        allocator.setPrefixes(TestUtils.createPrefixes(prefixes));
        allocator.persist();
        Datacentre datacentre = TestUtils.createDatacentre(DEFAULT_DATACENTRE_SYMBOL, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());
        datacentre.persist();
        return datacentre;
    }

    public static Dataset createDataset(String doi, Datacentre datacentre) {
        Dataset dataset = new Dataset();
        dataset.setDoi(doi);
        dataset.setDatacentre(datacentre);
        return dataset;
    }
    
    public static Dataset createDefaultDataset(String doi) {
        Datacentre datacentre = createDefaultDatacentre(Utils.getDoiPrefix(doi));
        Dataset dataset = createDataset(doi, datacentre);
        dataset.persist();
        return dataset;
    }

    public static Prefix createPrefix(String prefix) {
        Prefix prefixObj = new Prefix();
        prefixObj.setPrefix(prefix);
        return prefixObj;
    }

    public static Set<Prefix> createPrefixes(String... prefixes) {
        Set<Prefix> prefixSet = new HashSet<Prefix>();
        if (prefixes != null) {
            for (String prefix : prefixes) {
                prefixSet.add(createPrefix(prefix));
            }
        }
        return prefixSet;
    }

    public static Metadata createMetadata(byte[] xml, Dataset dataset) {
        Metadata metadata = new Metadata();
        metadata.setDataset(dataset);
        metadata.setXml(setDoiOfMetadata(xml, dataset.getDoi()));
        return metadata;
    }
    
    public static Media createMedia(String mediaType, String url, Dataset dataset) {
        Media media = new Media();
        media.setDataset(dataset);
        media.setMediaType(mediaType);
        media.setUrl(url);
        return media;
    }
    
    public static Media createMedia(String mediaType,  Dataset dataset) {
        return createMedia(mediaType, "http://example.com", dataset);
    }

    public static byte[] getTestMetadata() {
        return getTestMetadata21();
    }

    // schema 2.0 is no longer accepted by production MDS.
    @Deprecated
    public static byte[] getTestMetadata20() {
        return getTestMetadata("datacite-metadata-sample-v2.0.xml");
    }

    public static byte[] getTestMetadata21() {
        return getTestMetadata("datacite-metadata-sample-v2.1.xml");
    }

    public static byte[] getTestMetadata31() {
        return getTestMetadata("datacite-metadata-sample-v3.1.xml");
    }

    public static byte[] getTestMetadata(String filename) {
        Resource resource = new ClassPathResource(filename);
        try {
            return FileUtils.readFileToByteArray(resource.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] setDoiOfMetadata(byte[] xml, String doi) {
        try {
            Document doc = bytesToDocument(xml);
            Node identifier = doc.getElementsByTagName("identifier").item(0);
            Node identifierContent = identifier.getFirstChild();
            identifierContent.setNodeValue(doi);
            return documentToBytes(doc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document bytesToDocument(byte[] bytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        Document doc = builder.parse(input);
        return doc;
    }

    public static byte[] documentToBytes(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
        byte[] bytes = output.toByteArray();
        return bytes;
    }
}
