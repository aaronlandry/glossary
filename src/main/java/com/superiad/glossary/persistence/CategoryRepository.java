package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Category;
import java.util.List;

/**
 *
 * @author Aaron
 */
public interface CategoryRepository extends Repository<Long,Category> {
    
    List<Category> findRootCategories();
    
    List<Category> getCategoriesAsTree();
    
}
