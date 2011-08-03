<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no"/>
	<xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
	<xsl:template match="//resource">  
        <xsl:copy copy-namespaces="no">
			<xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
			<xsl:attribute name="noNamespaceSchemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://schema.datacite.org/meta/kernel-2.0/metadata.xsd</xsl:attribute>
            <xsl:apply-templates select="@*[local-name(.)!='noNamespaceSchemaLocation']|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
