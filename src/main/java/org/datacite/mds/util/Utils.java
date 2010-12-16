package org.datacite.mds.util;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
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

    private static final String AZ_LOWER_CASE = "abcdefghijklmnopqrstuvxyz";
    private static final String AZ_UPPER_CASE = AZ_LOWER_CASE.toUpperCase(Locale.ENGLISH);

    /**
     * normalizes a DOI according to the DOI Handbook by converting 'a' to 'z' to
     * upper-case.
     * 
     * @param doi
     * @return normalized DOI
     */
    public static String normalizeDoi(String doi) {
        return StringUtils.replaceChars(doi, AZ_LOWER_CASE, AZ_UPPER_CASE);
    }

    private static String splitDoi(String doi, int index) {
        if (doi == null || !doi.contains("/"))
            return null;
        String[] split = doi.split("/", -1);
        return split[index];
    }

    /**
     * returns the prefix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "10.5072")
     */
    public static String getDoiPrefix(String doi) {
        return splitDoi(doi, 0);
    }

    /**
     * returns the suffix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "foobar")
     */
    public static String getDoiSuffix(String doi) {
        return splitDoi(doi, 1);
    }

    /**
     * returns the allocator part of a symbol
     * 
     * @param symbol
     *            Symbol (e.g. "BL.DC")
     * @return allocator symbol (e.g. "BL");
     */
    public static String getAllocatorFromDatacentreSymbol(String symbol) {
        if (symbol == null || !symbol.contains(".") || symbol.length() == 0)
            return null;
        return symbol.split("\\.", -1)[0];
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
        return Arrays.asList(csv.split(",", -1));
    }

    /**
     * returns the hostname of the given URL
     * 
     * @param urlStr
     *            URL
     * @return hostname; null if the url is malformed
     */
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

    /**
     * pretty print an xml string
     * 
     * @param xml
     *            String containing a xml document
     * @return
     * @throws Exception
     *             if the xml cannot be parsed
     */
    public static String formatXML(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        String result = sw.toString();
        return result;
    }

    /**
     * @return joined list of allocator and datacentre symbols
     */
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
