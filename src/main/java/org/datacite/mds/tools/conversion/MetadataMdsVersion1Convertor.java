package org.datacite.mds.tools.conversion;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetadataMdsVersion1Convertor extends BaseMetadataConvertor {

    private static final String SCHEMA_LOCATION = "http://schema.datacite.org/meta/kernel-2.0/metadata.xsd";

    @Autowired
    SchemaService schemaService;

    public MetadataMdsVersion1Convertor() {
        super("xslt/mds_old.xslt");
    }

    @Override
    public boolean needsConversion(Metadata metadata) {
        if (StringUtils.isNotEmpty(metadata.getNamespace()))
            return false;

        byte[] xml = metadata.getXml();
        String schemaLocation;
        try {
            schemaLocation = schemaService.getSchemaLocation(xml);
            return ! SCHEMA_LOCATION.equals(schemaLocation);
        } catch (Exception e) {
            return true;
        }
    }

    public static void main(String[] args) {
        initAndRun(args);
    }

}
