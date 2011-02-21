package org.datacite.mds.util;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.web.ui.Converters;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.core.Conventions;
import org.springframework.core.convert.converter.Converter;

/**
 * Class with several static util methods
 */
public class Utils {

    private static final String AZ_LOWER_CASE = "abcdefghijklmnopqrstuvxyz";
    private static final String AZ_UPPER_CASE = AZ_LOWER_CASE.toUpperCase(Locale.ENGLISH);

    public static final Character CSV_SEPARATOR = ',';
    public static final String CSV_WHITESPACE = " ";

    static Logger log4j = Logger.getLogger(Utils.class);

    /**
     * normalizes a DOI according to the DOI Handbook by converting 'a' to 'z'
     * to upper-case.
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
        String[] split = doi.split("/", 2);
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
        if (symbol == null || !symbol.contains("."))
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
        return Arrays.asList(csv.split(CSV_SEPARATOR.toString(), -1));
    }

    /**
     * <p>
     * normalizes a string with comma or newline separated values
     * </p>
     * 
     * <p>
     * A trailing newline char is removed. Whitespace at the beginning or end of
     * a value is trimmed. Optionally newline chars are converted to the default
     * separator (',') and empty values are skipped
     * </p>
     * 
     * @param csv
     *            String to be normalized
     * @param additionalSeparators
     *            replace all entries of this list with the default separator
     * @param skipEmptyValues
     *            if true delete empty values (e.g. 'a,,b' -> 'a,b')
     * @return
     */
    public static String normalizeCsv(String csv, Collection<String> additionalSeparators, boolean skipEmptyValues) {
        String SEP = CSV_SEPARATOR.toString();
        String WHITE = CSV_WHITESPACE;
        String ret = csv;
        ret = StringUtils.chomp(ret); // delete trailing newline char
        ret = ret.replaceAll("\r\n", "\n"); // uniform newline chars
        if (additionalSeparators != null) {
            for (String additionalSeparator : additionalSeparators) {
                // convert additional separators to default separator
                ret = ret.replaceAll(additionalSeparator, SEP);
            }
        }
        // remove leading and trailing whitespace
        ret = ret.replaceAll("[" + WHITE + "]*(" + SEP + "|^|$)[" + WHITE + "]*", "$1");
        if (skipEmptyValues) {
            // remove empty values in the middle
            ret = ret.replaceAll(SEP + "+", SEP);
            // remove empty leading and trailing values
            ret = ret.replaceAll("(^" + SEP + "|" + SEP + "$)", "");
        }
        log4j.debug("normalizeCsv: " + csv + " -> " + ret);
        return ret;
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
        if (StringUtils.isBlank(xml))
            return xml;
        Document doc = DocumentHelper.parseText(xml);
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        String result = sw.toString();
        return result;
    }

    public static <T> String collectionToString(Collection<T> collection, Converter<T,String> converter) {
        List<String> list = new ArrayList<String>();
        for (T entry : collection) {
            list.add(converter.convert(entry));
        }
        return StringUtils.join(list,",");
    }

}
