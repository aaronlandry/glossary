package com.superiad.glossary.controllers;

import com.superiad.glossary.authentication.NoActiveUserException;
import com.superiad.glossary.controllers.environment.WebContext;
import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.Novel;
import com.superiad.glossary.model.Persistable;
import com.superiad.glossary.model.SuperiadEpoch;
import com.superiad.glossary.model.Term;
import com.superiad.glossary.model.User;
import com.superiad.glossary.persistence.CategoryRepository;
import com.superiad.glossary.persistence.ClassNotManagedException;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.NovelRepository;
import com.superiad.glossary.persistence.TermRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Aaron
 */
@Controller
@RequestMapping("/CRUD")
public class CRUDController {
    
    @Autowired
    private transient WebContext webContext;
    @Autowired
    private transient TermRepository termRepositoryImpl;
    @Autowired
    private transient CategoryRepository categoryRepositoryImpl;
    @Autowired
    private transient NovelRepository novelRepositoryImpl;
    @Autowired
    private Validator validator;
    private final ObjectMapper MAPPER = new ObjectMapper();
    
    
    @RequestMapping(value = "/PromptManageCategories", method=RequestMethod.GET)
    public ModelAndView promptManageCategories(HttpServletResponse response, HttpServletRequest request) throws NoActiveUserException {
        ModelAndView model = new ModelAndView("/glossary/manage-categories", "entity", new Term());
        List<Category> categories = categoryRepositoryImpl.getCategoriesAsTree();
        model.addObject("categories",categories);
        return model;
    }
    
    @RequestMapping(value = "/DoManageCategories", method=RequestMethod.POST)
    @Transactional
    public void doManageCategories(HttpServletResponse response, HttpServletRequest request) throws NoActiveUserException, ClassNotManagedException, IOException {
        User user = webContext.getActiveUser();
        if (request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json; charset=UTF-8");
        } 
        else {
            // IE workaround
            response.setContentType("text/html; charset=UTF-8");
        }
        String ids[] = request.getParameterValues("category-ids");
        Map<Long,Category> categoryMap = new HashMap<>();
        for (String stringId : ids) {
            Long id = Long.parseLong(stringId);
            String name = request.getParameter("category-name-"+id);
            // null names will throw exceptions ...
            if (name == null || name.isEmpty()) {
                name = "No Name [" + new Date().getTime() + "]";
            }
            if (id > 0L) {
                // this is an edit
                try {
                    Category category = categoryRepositoryImpl.findById(id);
                    category.setName(name);
                    category.setParent(null);           // reset below
                    category.getChildren().clear();     // reset below
                    categoryMap.put(id, category);
                }
                catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (id < 0L) {
                // this is new
                try {
                    Category category = new Category();
                    category.setName(name);
                    category.setCreator(user);
                    category.setParent(null);           // reset below
                    category.getChildren().clear();     // reset below
                    category.persist();
                    categoryMap.put(id, category);
                }
                catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // NOW FOR THE RELATIONSHIPS
        for (Long id : categoryMap.keySet()) {
            Long parentID = Long.parseLong(request.getParameter("category-parent-" + id));
            if (parentID == null || parentID == 0L) {
                continue;
            }
            Category thisCategory = categoryMap.get(id);
            Category parentCategory = categoryMap.get(parentID);
            thisCategory.setParent(parentCategory);
            parentCategory.getChildren().add(thisCategory);
        }
        // UPDATE STORED METADATA, MUST HAPPEN BEFORE TERM UPDATE
        List<Category> categories = categoryRepositoryImpl.findAll();
        for (Category category : categories) {
            category.configure();
        }
        // DUE TO POSSIBLE CHANGES ... WE NEED TO UPDATE THE FULL CATEGORY HIERARCHY OF EVERY TERM ...
        for (Term term : termRepositoryImpl.findAll()) {
            term.getAllCategories().clear();
            term.getAllCategories().addAll(term.buildFullCategoryHierarchy());
        }
        // RESPONSE
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("validationError",false);
        setHeader(request,response);
        response.getWriter().write(MAPPER.writeValueAsString(rtn));
    }
    
    @RequestMapping(value = "/Read/Term/{id}", method=RequestMethod.GET)
    public ModelAndView readTerm(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("id") Long id) throws NoActiveUserException, EntityNotFoundException, IOException {
        Term term = termRepositoryImpl.findById(id);
        ModelAndView model = new ModelAndView("/glossary/read-term", "entity", term);
        List<Novel> novels = novelRepositoryImpl.findAll();
        Collections.sort(novels);
        model.addObject("novels",novels);
        List<Category> rootCategories = categoryRepositoryImpl.findRootCategories();
        List<Map<String,Object>> categoriesJson = new ArrayList<>();
        for (Category category : rootCategories) {
            categoriesJson.add(category.toHierarchicalJson());
        }
        model.addObject("categories",MAPPER.writeValueAsString(categoriesJson));
        model.addObject("epochs",SuperiadEpoch.values());
        return model;
    }
    
    @RequestMapping(value = "/PromptCreate/Term", method=RequestMethod.GET)
    public ModelAndView promptCreateTerm(HttpServletResponse response, HttpServletRequest request) throws NoActiveUserException, 
            EntityNotFoundException, IOException {
        Category defaultCategory = categoryRepositoryImpl.findById(112L);
        Term term = new Term();
        term.getCategories().add(defaultCategory);
        ModelAndView model = new ModelAndView("/glossary/create-term", "entity", term);
        List<Novel> novels = novelRepositoryImpl.findAll();
        Collections.sort(novels);
        model.addObject("novels",novels);
        List<Category> rootCategories = categoryRepositoryImpl.findRootCategories();
        List<Map<String,Object>> categoriesJson = new ArrayList<>();
        for (Category category : rootCategories) {
            categoriesJson.add(category.toHierarchicalJson());
        }
        model.addObject("categories",MAPPER.writeValueAsString(categoriesJson));
        model.addObject("epochs",SuperiadEpoch.values());
        return model;
    }
    
    @RequestMapping(value = "/PromptUpdate/Term/{id}", method=RequestMethod.GET)
    public ModelAndView promptUpdateTerm(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("id") Long id) throws NoActiveUserException, EntityNotFoundException, IOException {
        Term term = termRepositoryImpl.findById(id);
        ModelAndView model = new ModelAndView("/glossary/update-term", "entity", term);
        List<Novel> novels = novelRepositoryImpl.findAll();
        Collections.sort(novels);
        model.addObject("novels",novels);
        List<Category> rootCategories = categoryRepositoryImpl.findRootCategories();
        List<Map<String,Object>> categoriesJson = new ArrayList<>();
        for (Category category : rootCategories) {
            categoriesJson.add(category.toHierarchicalJson());
        }
        model.addObject("categories",MAPPER.writeValueAsString(categoriesJson));
        model.addObject("epochs",SuperiadEpoch.values());
        return model;
    }
    
    @RequestMapping(value = "/DoCreate/Term", method=RequestMethod.POST)
    @Transactional
    public void doCreateTerm(HttpServletResponse response, HttpServletRequest request) throws NoActiveUserException, ClassNotManagedException, IOException {
        User user = webContext.getActiveUser();
        if (request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json; charset=UTF-8");
        } 
        else {
            // IE workaround
            response.setContentType("text/html; charset=UTF-8");
        }
        Term term = new Term();
        term.getCategories();
        term.setCreator(user);
        ServletRequestDataBinder binder = new EntityBinder(request,user,term,"entity"); 
        binder.bind(request);
        Set<ConstraintViolation<Object>> failures = new EntityValidator(validator,term).validate();
        if (!failures.isEmpty()) {
            Map<String,Object> rtn = validationMessages(failures);
            setHeader(request,response);
            response.getWriter().write(MAPPER.writeValueAsString(rtn));
            return;
        }
        term.persist();
        // RESPONSE
        Map<String,Object> rtn = success(term);
        setHeader(request,response);
        response.getWriter().write(MAPPER.writeValueAsString(rtn));
    }
    
    @RequestMapping(value = "/DoUpdate/Term/{id}", method=RequestMethod.POST)
    @Transactional
    public void doUpdateTerm(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("id") Long id) throws NoActiveUserException, EntityNotFoundException, ClassNotManagedException, IOException {
        User user = webContext.getActiveUser();
        if (request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json; charset=UTF-8");
        } 
        else {
            // IE workaround
            response.setContentType("text/html; charset=UTF-8");
        }
        Term term = termRepositoryImpl.findById(id);
        term.detach();
        ServletRequestDataBinder binder = new EntityBinder(request,user,term,"entity"); 
        binder.bind(request);
        Set<ConstraintViolation<Object>> failures = new EntityValidator(validator,term).validate();
        if (!failures.isEmpty()) {
            Map<String,Object> rtn = validationMessages(failures);
            setHeader(request,response);
            response.getWriter().write(MAPPER.writeValueAsString(rtn));
            return;
        }
        term = term.update();
        // RESPONSE
        Map<String,Object> rtn = success(term);
        setHeader(request,response);
        response.getWriter().write(MAPPER.writeValueAsString(rtn));
    }
    
    @RequestMapping(value = "/DoDelete/Term/{id}", method=RequestMethod.POST)
    @Transactional
    public void doDeleteTerm(HttpServletResponse response, HttpServletRequest request,
            @PathVariable("id") Long id) throws NoActiveUserException, EntityNotFoundException, ClassNotManagedException, IOException {
        if (request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json; charset=UTF-8");
        } 
        else {
            // IE workaround
            response.setContentType("text/html; charset=UTF-8");
        }
        Term term = termRepositoryImpl.findById(id);
        term.delete();
        // RESPONSE
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("validationError",false);
        setHeader(request,response);
        response.getWriter().write(MAPPER.writeValueAsString(rtn));
    }
    
    private Map<String,Object> validationMessages(Set<ConstraintViolation<Object>> failures) {
        Map<String,Object> failureMessages = new HashMap<>();
        failureMessages.put("validationError",true);
        for (ConstraintViolation failure : failures) {
            failureMessages.put(failure.getPropertyPath().toString(), failure.getMessage());
        }
        return failureMessages;
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
    
    private Map<String,Object> success(final Persistable entity) {
        Map<String,Object> map = entity.toJson();
        map.put("validationError",false);
        return map;
    }
    
}
