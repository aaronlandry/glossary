package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.Novel;
import com.superiad.glossary.model.SuperiadEpoch;
import com.superiad.glossary.model.Term;
import java.util.List;

/**
 *
 * @author Aaron
 */
public interface TermRepository extends Repository<Long,Term> {
    
    List<Term> findSortedByLetter(String letter);
            
    List<Term> findDirectOrChildrenSortedByCategory(Category category);
    
    List<Term> findSortedByCategory(Category category);
    
    List<Term> findSortedByNovel(Novel novel);
    
    List<Term> findSortedByEpoch(SuperiadEpoch epoch);
    
}
