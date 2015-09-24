package com.superiad.glossary.controllers;

import com.superiad.glossary.authentication.NoActiveUserException;
import com.superiad.glossary.model.Term;
import com.superiad.glossary.persistence.ClassNotManagedException;
import com.superiad.glossary.persistence.TermRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Primary Controller.
 * @author Aaron
 */
@Controller
@RequestMapping("/glossary")
public class GlossaryController {

    @Autowired
    private transient TermRepository termRepositoryImpl;
    private final ObjectMapper MAPPER = new ObjectMapper();
    private static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    
    @RequestMapping(value={"/home","/dashboard","/index"})
    public ModelAndView renderUI(HttpServletRequest req, HttpServletResponse res) 
            throws NoActiveUserException, ClassNotManagedException, InstantiationException, IllegalAccessException, 
                JsonGenerationException, JsonMappingException, IOException {
        ModelAndView model = new ModelAndView();
        model.setViewName("/glossary/index");
        return model;    
    }
    
    @RequestMapping("/crossReference")
    @Transactional
    /* THIS IS VERY INEFFICENT - WE DON'T REALLY CARE.  IT SHOULD BE RARELY EXECUTED */
    public void crossReference(HttpServletRequest request, HttpServletResponse response) 
            throws NoActiveUserException, ClassNotManagedException, InstantiationException, IllegalAccessException, 
                JsonGenerationException, JsonMappingException, IOException {
        Map<String,Term> allTerms = new HashMap<>();
        for (Term term : termRepositoryImpl.findAll()) {
            allTerms.put(term.getName(),term);
            term.setParsedDefinition(term.getDefinition());
        }
        List<String> sortedAllTerms = new ArrayList<>();
        // SORT BY LENGTH SO THAT FOR EXAMPLE "LOST POLIS" TAKES PRECEDENCE OVER "POLIS"
        sortedAllTerms.addAll(allTerms.keySet());
        Collections.sort(sortedAllTerms, 
            new Comparator<String>() {

                @Override
                public int compare(String A, String B) {
                    if (A == null || B == null || A.length() == B.length()) {
                        return 0;
                    }
                    return B.length() > A.length() ? 1 : -1;
                }
                
            }
        );
        List<Term> terms = termRepositoryImpl.findAll();
        Collections.sort(terms);
        OUTERLOOP: for (Term term : terms) {
            INNERLOOP: for (String otherTerm : sortedAllTerms) {
                List<String> formattedOtherTerms = _formatOtherTerm(otherTerm);
                for (String formattedOtherTerm : formattedOtherTerms) {
                    if (term.getName().equals(formattedOtherTerm) || term.getName().toLowerCase().contains(formattedOtherTerm.toLowerCase())) {
                        continue INNERLOOP;
                    }
                }
                for (String formattedOtherTerm : formattedOtherTerms) {
                    Term idterm = null;
                    IINNERLOOP: for (String iformattedOtherTerm : formattedOtherTerms) {
                        idterm = allTerms.get(iformattedOtherTerm);
                        if (idterm != null) {
                            break IINNERLOOP;
                        }
                    }
                    if (idterm == null) {
                        idterm = allTerms.get(otherTerm);
                    }
                    if (idterm != null) {
                        Pattern pttn = _toSafePattern(_escapeSpecialRegexChars(formattedOtherTerm),idterm.getMatchLowercase());
                        Matcher matcher = pttn.matcher(term.getParsedDefinition());
                        if (matcher.find()) {
                            //String pluralization = matcher.group(1);
                            term.setParsedDefinition(matcher.replaceAll("<a class='term-link' termID='" + 
                                idterm.getId() + "'>" + _addEmptySpaces(matcher.group(0) /*+ ((pluralization != null && !pluralization.isEmpty()) ? pluralization : ""*)*/) + "</a>"));
                            continue INNERLOOP;
                        }
                        
                    }
                }
            }
        }
        // RESPONSE
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("validationError",false);
        setHeader(request,response);
        response.getWriter().write(MAPPER.writeValueAsString(rtn));    
    }
    
    // ADDS AN EMPTY SPACE CHARACTER TO BEGINNING OF EACH WORD IN TERM TO AVOID THE STRING MATCHING TWICE
    // AGAINST PARTIAL TERMS
    // ATTEMPTS TO AVOID BREAKING OTHER SPECIAL CHARACTERS
    private String _addEmptySpaces(String term) {
        int i = 0;
        while ((i+1) < term.length()) {
            if (i == -1) {
                return term;
            }
            else if (term.charAt(i) == '&') {
                // SET i = position of ; character + 1
                i = term.indexOf(";", i);
                if (i != -1) {
                    i++;
                }
            }
            else {
                term = term.substring(0,i+1) + "&#8291;" + term.substring(i+1);
                // move to the next word
                i = term.indexOf(" ", i+6);
                if (i != -1) {
                    i++;
                }
            }
        }
        return term;
    }
    
    private Pattern _toSafePattern(String text, Boolean matchLowercase) {
        //return Pattern.compile("\\b" + text + "\\b");
        if (matchLowercase && text.startsWith("&")) {
            return Pattern.compile(text + "(s[;,\\:\\.\\s]){0,1}", Pattern.CASE_INSENSITIVE);
        }
        else if (matchLowercase) {
            return Pattern.compile("\\b" + text + "(s[;,\\:\\.\\s]){0,1}", Pattern.CASE_INSENSITIVE);
        }
        else if (text.startsWith("&")) {
            return Pattern.compile(text + "(s[;,\\:\\.\\s]){0,1}");
        }
        else {
            return Pattern.compile("\\b" + text + "(s[;,\\:\\.\\s]){0,1}");
        }
    }
    
    private List<String> _formatOtherTerm(String rawTerm) {
        List<String> rtn = new ArrayList<>();
        if (rawTerm.endsWith(", The")) {
            rtn.add("The " + rawTerm.substring(0,rawTerm.length()-5));
            rtn.add(rawTerm.substring(0,rawTerm.length()-5));
            //rtn.add("the " + rawTerm.substring(0,rawTerm.length()-5));
        }
        else {
            rtn.add(rawTerm);
        }
        return rtn;
    }
    
    private String _escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }
    
    private void setHeader(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json; charset=UTF-8");
        } 
        else {
            // IE workaround
            response.setContentType("text/html; charset=UTF-8");
        }
    }
    
}
