package org.datacite.mds.util;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Class with several static util methods
 */
public class Utils {

    /**
     * returns the prefix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "10.5072")
     */
    public static String getDoiPrefix(String doi) {
        return doi.split("/")[0];
    }

    /**
     * returns the suffix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "foobar")
     */
    public static String getDoiSuffix(String doi) {
        return doi.split("/")[1];
    }

    /**
     * converts a string with comma separated values to a List of Strings
     * 
     * @param csv
     *            comma separated values
     * @return List of Strings
     */
    public static List<String> csvToList(String csv) {
        if (csv == null) {
            return new ArrayList<String>();
        }
        return Arrays.asList(csv.split(","));
    }

    public static String getHostname(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
            String hostname = url.getHost();
            return hostname;
        } catch (MalformedURLException e) {
            return null;
        }

    }

    public static String formatXML(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        String result = sw.toString();
        return result;
    }

    public static SortedSet<String> getAllSymbols() {
        SortedSet<String> symbols = new TreeSet<String>();
        for (Datacentre datacentre : Datacentre.findAllDatacentres()) {
            symbols.add(datacentre.getSymbol());
        }
        for (Allocator allocator : Allocator.findAllAllocators()) {
            symbols.add(allocator.getSymbol());
        }
        return symbols;
    }

}
