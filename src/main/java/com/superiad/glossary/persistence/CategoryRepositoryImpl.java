package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Category;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Aaron
 */
@org.springframework.stereotype.Repository
public class CategoryRepositoryImpl extends AbstractRepository<Long,Category> implements CategoryRepository {
    
    @Override
    public List<Category> findRootCategories() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Category.class);
        Root e = cq.from(Category.class);
        cq.where(cb.isNull(e.get("parent")));
        Query query = getEntityManager().createQuery(cq);
        List<Category> categories = query.getResultList();
        Collections.sort(categories);
        return categories;
    }
    
    @Transient
    @Override
    public List<Category> getCategoriesAsTree() {
        // well, not really as a tree, but ordered as expected in the tree ...
        List<Category> ordered = new ArrayList<>();
        List<Category> categories = findAll();
        Collections.sort(categories);
        for (Category category : categories) {
            if (category.getParent() != null) {
                continue;
            }
            ordered.add(category);
            _addChildren(ordered,category);
        }
        return ordered;
    }
    
    private void _addChildren(List<Category> ordered, Category parent) {
        if (parent.getChildren().isEmpty()) {
            return;
        }
        List<Category> children = parent.getChildren();
        Collections.sort(children);
        for (Category category : children) {
            ordered.add(category);
            _addChildren(ordered,category);
        }
    }
    
}
