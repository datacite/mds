package org.datacite.mds.tools.migration;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.tools.AbstractTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This tool tries to set currently empty namespace field
 */
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class NamespaceSetter extends AbstractTool {

    private static Logger log = Logger.getLogger(NamespaceSetter.class);

    @Autowired
    SchemaService schemaService;

    @PostConstruct
    public void init() {
        disableValidation();
    }

    @Override
    public void run(String[] args) throws IOException {
        System.out.println("Searching for metadata with empty namespace...");
        List<Metadata> metadatas = Metadata.findAllMetadatas();
        for (Metadata metadata : metadatas) {
            if (StringUtils.isEmpty(metadata.getNamespace())) {
                checkNamespace(metadata);
            }
        }
    }

    private void checkNamespace(Metadata metadata) {
        log.debug("checking namespace: " + metadata);

        byte[] xml = metadata.getXml();
        String namespace = schemaService.getNamespace(xml);
        if (namespace != null) {
            setNamespace(metadata, namespace);
        }
    }

    private void setNamespace(Metadata metadata, String namespace) {
        metadata.setNamespace(namespace);
        metadata.merge();

        String msg = "set namespace: " + metadata + " => " + namespace;
        System.out.println(msg);
        log.info(msg);
    }

    public static void main(String[] args) {
        initAndRun(args);
    }

}
