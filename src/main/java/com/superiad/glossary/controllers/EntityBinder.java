package com.superiad.glossary.controllers;

import com.superiad.glossary.controllers.editors.CategoryCollectionEditor;
import com.superiad.glossary.controllers.editors.CategorySingleEditor;
import com.superiad.glossary.controllers.editors.DateEditor;
import com.superiad.glossary.controllers.editors.EpochEditor;
import com.superiad.glossary.controllers.editors.NovelCollectionEditor;
import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.SuperiadEpoch;
import com.superiad.glossary.model.User;
import com.superiad.glossary.persistence.ClassNotManagedException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 *
 * @author Aaron
 */
@Configurable(preConstruction=true,dependencyCheck=true)    // EntityIntrospector bean referenced in constructor
public class EntityBinder extends ServletRequestDataBinder {
    
    public EntityBinder(HttpServletRequest request, User user, Object entity, String string) throws ClassNotManagedException {
        super(entity,string);
        //registerCustomEditor(String.class, new StringEscapeEditor());
        registerCustomEditor(Date.class, new DateEditor(user));       
        registerCustomEditor(List.class, "categories", new CategoryCollectionEditor());
        registerCustomEditor(List.class, "children", new CategoryCollectionEditor());
        registerCustomEditor(Category.class, "parent", new CategorySingleEditor());
        registerCustomEditor(List.class, "novels", new NovelCollectionEditor());
        registerCustomEditor(SuperiadEpoch.class, new EpochEditor());
    }
    
}
