package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.Novel;
import com.superiad.glossary.model.SuperiadEpoch;
import com.superiad.glossary.model.Term;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 *
 * @author Aaron
 */
@org.springframework.stereotype.Repository
public class TermRepositoryImpl extends AbstractRepository<Long,Term> implements TermRepository {

    @Override
    public List<Term> findSortedByLetter(String letter) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Term.class);
        Root e = cq.from(Term.class);
        cq.where(
            cb.or(
                cb.like(cb.lower(e.get("name")), letter.substring(0,1).toLowerCase() + "%"),
                cb.like(cb.lower(e.get("name")), "&" + letter.substring(0,1).toLowerCase() + "%")
            )
        );
        Query query = getEntityManager().createQuery(cq);
        List<Term> terms = query.getResultList();
        Collections.sort(terms);
        return terms;
    }

    @Override
    public List<Term> findDirectOrChildrenSortedByCategory(Category category) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Term.class);
        Root e = cq.from(Term.class);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        categories.addAll(category.getAllChildren());
        cq.where(e.get("categories").in(categories));
        Query query = getEntityManager().createQuery(cq);
        List<Term> terms = query.getResultList();
        Collections.sort(terms);
        return terms;
    }
    
    @Override
    public List<Term> findSortedByCategory(Category category) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Term.class);
        Root e = cq.from(Term.class);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        cq.where(e.get("categories").in(categories));
        Query query = getEntityManager().createQuery(cq);
        List<Term> terms = query.getResultList();
        Collections.sort(terms);
        return terms;
    }

    @Override
    public List<Term> findSortedByEpoch(SuperiadEpoch epoch) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Term.class);
        Root e = cq.from(Term.class);
        Join j = e.join("eventDate");
        cq.where(cb.equal(j.get("epoch"),epoch));
        Query query = getEntityManager().createQuery(cq);
        List<Term> terms = query.getResultList();
        Collections.sort(terms);
        return terms;
    }

    @Override
    public List<Term> findSortedByNovel(Novel novel) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Term.class);
        Root e = cq.from(Term.class);
        List<Novel> novels = new ArrayList<>();
        novels.add(novel);
        cq.where(e.get("novels").in(novels));
        Query query = getEntityManager().createQuery(cq);
        List<Term> terms = query.getResultList();
        Collections.sort(terms);
        return terms;
    }
    
}