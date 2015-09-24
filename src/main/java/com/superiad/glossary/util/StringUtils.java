package com.superiad.glossary.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aaron
 */
public class StringUtils {
    
    private final static Logger logger = LoggerFactory.getLogger(StringUtils.class);
    
    public static String capitalize(String input) {
        if (input == null) {
            return null;
        }
        return org.apache.commons.lang.StringUtils.capitalize(input.toLowerCase());
    }
    
    public static String splitCamelCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(String.format(
            "%s|%s|%s",
            "(?<=[A-Z])(?=[A-Z][a-z])",
            "(?<=[^A-Z])(?=[A-Z])",
            "(?<=[A-Za-z])(?=[^A-Za-z])"),
            " "
        );
    }
    
    public static String newlineToBR(String originalText) {
        if (originalText == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '\n') result.append("<BR />");
            else if (character == '\r') result.append("<BR />");
            // THE CHARACTER IS NOT A SPECIAL ONE
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }
    
    public static String escapeQuotes(String originalText) {
        if (originalText == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '\"') result.append("\\\"");
            else if (character == '\'') result.append("\\\'");
            else if (character == '\\') result.append("\\\\");
            // THE CHARACTER IS NOT A SPECIAL ONE
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }
    
    public static String encodeQuotes(String originalText) {
        if (originalText == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '\"') result.append("&quot;");
            else if (character == '\'') addCharEntity(39, result);
            // THE CHARACTER IS NOT A SPECIAL ONE
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }
    
    public static String truncate(String originalText, Integer length) {
        if (length == null || length == 0) {
            return originalText;
        }
        if (originalText == null) {
            return null;
        }
        if (originalText.length() <= length) {
            return originalText;
        }
        else {
            return originalText.substring(0,length-3)+"...";
        }
    }
    
    /**
     * Escapes characters for text appearing in HTML markup.
     * <P>Note that JSTL's {@code <c:out>} escapes <em>only the first 
     * five</em> of the above characters.
     * @param originalText
     * @return  
     */
    public static String escapeHtml(String originalText){
        if (originalText == null) return "";
        return StringEscapeUtils.escapeHtml(originalText);
    }
  
    public static String escapeSql(String originalText){
        if (originalText == null) return "";
        return StringEscapeUtils.escapeSql(originalText);
    }
    
    /**
     * Escape characters for text appearing as XML data, between tags.
     * <P>Note that JSTL's {@code <c:out>} escapes the exact same set of 
     * characters as this method. <span class='highlight'>That is, {@code <c:out>}
     * is good for escaping to produce valid XML, but not for producing safe HTML.</span>
     * @param originalText
     * @return  
     */
    public static String escapeXML(String originalText){
        if (originalText == null) return "";
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '<') result.append("&lt;");
            else if (character == '>') result.append("&gt;");
            else if (character == '\"') result.append("&quot;");
            else if (character == '\'') result.append("&#039;");
            else if (character == '&') result.append("&amp;");
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Return <tt>aText</tt> with all <tt>'<'</tt> and <tt>'>'</tt> characters
     * replaced by their escaped equivalents.
     * @param originalText
     * @return  
     */
    public static String disableTags(String originalText){
        if (originalText == null) return "";
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '<') result.append("&lt;");
            else if (character == '>') result.append("&gt;");
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }
  
    public static String transformLineBreaks(String originalText){
        if (originalText == null) return "";
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '\n') result.append("<br />");
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Replace characters having special meaning in regular expressions
     * with their escaped equivalents, preceded by a '\' character.
     *
     * @param originalText
     * @return  
     */
    public static String escapeRegex(String originalText){
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(originalText);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            /*
             * All literals need to have backslashes doubled.
             */
            if (character == '.') result.append("\\.");
            else if (character == '\\') result.append("\\\\");
            else if (character == '?') result.append("\\?");
            else if (character == '*') result.append("\\*");
            else if (character == '+') result.append("\\+");
            else if (character == '&') result.append("\\&");
            else if (character == ':') result.append("\\:");
            else if (character == '{') result.append("\\{");
            else if (character == '}') result.append("\\}");
            else if (character == '[') result.append("\\[");
            else if (character == ']') result.append("\\]");
            else if (character == '(') result.append("\\(");
            else if (character == ')') result.append("\\)");
            else if (character == '^') result.append("\\^");
            else if (character == '$') result.append("\\$");
            else result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }
  
    /**
     * Escape <tt>'$'</tt> and <tt>'\'</tt> characters in replacement strings. 
     * @param originalText
     * @return  
     */
    public static String escapeReplacementString(String originalText){
        return Matcher.quoteReplacement(originalText);
    }
  
    /**
     * Disable all <tt><SCRIPT></tt> tags.
     * 
     * <P>Insensitive to case.
     * @param originalText
     * @return  
     */  
    public static String escapeJavascript(String originalText){
        if (originalText == null) return "";
        return StringEscapeUtils.escapeJavaScript(originalText);
    }
    
    private static void addCharEntity(Integer aIdx, StringBuilder aBuilder){
        String padding = "";
        if( aIdx <= 9 ) padding = "00";
        else if( aIdx <= 99 ) padding = "0";
        else {
            //no prefix
        }
        String number = padding + aIdx.toString();
        aBuilder.append("&#").append(number).append(";");
    }
    
    public static List<Long> splitIds(String rawIDs) {
        List<Long> ids = new ArrayList<>();
        if (rawIDs == null || rawIDs.trim().isEmpty()) {
            return ids;
        }
        for (String rawID : rawIDs.trim().split("\\s*[,:]")) {
            ids.add(Long.valueOf(rawID));
        }
        return ids;
    }
    
    public static List<Integer> splitValues(String rawValues) {
        List<Integer> values = new ArrayList<>();
        if (rawValues == null || rawValues.trim().isEmpty()) {
            return values;
        }
        for (String rawID : rawValues.trim().split("\\s*[,:]")) {
            values.add(Integer.valueOf(rawID));
        }
        return values;
    }
    
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    } 
    
    public static String stripXSS(String value, Boolean stripHtml) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line 
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);
            // Avoid null characters
            value = value.replaceAll("", "");
            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid javascript:... expressions
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid vbscript:... expressions
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            if (stripHtml) {
                value = value.replaceAll("\\<.*?\\>","");
            }
        }
        return value;
    }
    
}
