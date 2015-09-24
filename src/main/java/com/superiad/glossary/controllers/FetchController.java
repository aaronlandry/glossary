package com.superiad.glossary.controllers;

import com.superiad.glossary.authentication.NoActiveUserException;
import com.superiad.glossary.controllers.environment.WebContext;
import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.Novel;
import com.superiad.glossary.model.SuperiadEpoch;
import com.superiad.glossary.model.Term;
import com.superiad.glossary.persistence.CategoryRepository;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.NovelRepository;
import com.superiad.glossary.persistence.TermRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Aaron
 */
@Controller
@RequestMapping("/Fetch")
public class FetchController {

    @Autowired
    private transient WebContext webContext;
    @Autowired
    private transient TermRepository termRepositoryImpl;
    @Autowired
    private transient CategoryRepository categoryRepositoryImpl;
    @Autowired
    private transient NovelRepository novelRepositoryImpl;
    
    @RequestMapping(value = "/Terms/ByLetter/{letter}", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupByLetter(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("letter") String letter) throws NoActiveUserException {
        List<Map<String,Object>> json =  new ArrayList<>();
        for (Term term : termRepositoryImpl.findSortedByLetter(letter)) {
            json.add(term.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Terms/ByCategory/{categoryID}", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupByCategory(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("categoryID") Long categoryID) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        Category category = categoryRepositoryImpl.findById(categoryID);
        for (Term term : termRepositoryImpl.findDirectOrChildrenSortedByCategory(category)) {
            json.add(term.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Terms/ByEpoch/{epochID}", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupByEpoch(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("epochID") Integer epochID) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        SuperiadEpoch epoch = SuperiadEpoch.findForValue(epochID);
        for (Term term : termRepositoryImpl.findSortedByEpoch(epoch)) {
            json.add(term.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Terms/ByNovel/{novelID}", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupByNovel(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("novelID") Long novelID) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        Novel novel = novelRepositoryImpl.findById(novelID);
        for (Term term : termRepositoryImpl.findSortedByNovel(novel)) {
            json.add(term.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Categories", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupCategories(HttpServletResponse response, 
            HttpServletRequest request) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        List<Category> categories = categoryRepositoryImpl.getCategoriesAsTree();
        for (Category category : categories) {
            json.add(category.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Novels", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupNovels(HttpServletResponse response, 
            HttpServletRequest request) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        List<Novel> novels = novelRepositoryImpl.findAll();
        for (Novel novel : novels) {
            json.add(novel.toJson());
        }
        return json; 
    }
    
    @RequestMapping(value = "/Epochs", method=RequestMethod.GET)
    public @ResponseBody List<Map<String,Object>> lookupEpochs(HttpServletResponse response, 
            HttpServletRequest request) throws NoActiveUserException, EntityNotFoundException {
        List<Map<String,Object>> json =  new ArrayList<>();
        for (SuperiadEpoch epoch : SuperiadEpoch.values()) {
            json.add(epoch.toJson());
        }
        return json; 
    }
    
}
