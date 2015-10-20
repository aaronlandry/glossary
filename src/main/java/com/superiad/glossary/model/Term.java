package com.superiad.glossary.model;

import com.superiad.glossary.persistence.BooleanConverter;
import com.superiad.glossary.persistence.PersistableEnumConverter;
import com.superiad.glossary.persistence.TermRepository;
import com.superiad.glossary.util.Hasher;
import com.superiad.glossary.validation.AllGroup;
import com.superiad.glossary.validation.NoDuplicateCategories;
import com.superiad.glossary.validation.NoDuplicateTerms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converters;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Mutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Aaron
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Terms")
@Converters({
    @Converter(name="persistableEnum", converterClass=PersistableEnumConverter.class),
    @Converter(name="boolean", converterClass=BooleanConverter.class)
})
@NoDuplicateCategories(groups = AllGroup.class)
@NoDuplicateTerms(groups = AllGroup.class)
public class Term extends AbstractPersistable<Term, TermRepository> {
    
    // INJECTION
    @Autowired(required=true)
    private transient TermRepository termRepositoryImpl;
    private String name;
    private String definition;
    private String parsedDefinition;
    private List<Category> categories = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private List<Novel> novels = new ArrayList<>();
    private SuperiadDate eventDate = new SuperiadDate();
    private User creator;
    private Boolean matchLowercase = Boolean.FALSE;

    @Override
    public TermRepository getRepository() {
        return termRepositoryImpl;
    }
    
    @Override
    // PERSISTENCE
    @Id
    @SequenceGenerator(name = "TERM_ID_GENERATOR", sequenceName = "term_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TERM_ID_GENERATOR")
    @Mutable(false)
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @PropertyAlias("Name")
    // VALIDATION
    @NotNull(message="Name is required", groups = AllGroup.class )
    @Size(max = 100, message = "Name cannot exceed 100 characters", groups = AllGroup.class)
    // PERSISTENCE
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }
    
    public void setMatchLowercase(Boolean matchLowercase) {
        this.matchLowercase = matchLowercase;
    }
    
    @PropertyAlias("Match Lowercase")
    // VALIDATION
    @NotNull(message="Match Lowercase is required", groups = AllGroup.class )
    @Convert("boolean")
    // PERSISTENCE
    @Column(name = "matchLowercase", nullable = false)
    public Boolean getMatchLowercase() {
        return matchLowercase;
    }
    
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    
    @PropertyAlias("Definition")
    // VALIDATION
    @NotNull(message="Definition is required", groups = AllGroup.class )
    @Size(max = 4000, message = "Definition cannot exceed 4000 characters", groups = AllGroup.class)
    // PERSISTENCE
    @Column(name = "definition", nullable = false)
    public String getDefinition() {
        return definition;
    }
    
    public void setParsedDefinition(String parsedDefinition) {
        this.parsedDefinition = parsedDefinition;
    }
    
    @PropertyAlias("Parsed Definition")
    @Lob
    @Size(max = 20000, message = "Parsed Definition cannot exceed 20,000 characters", groups = AllGroup.class)
    // PERSISTENCE
    @Column(name = "parsedDefinition")
    public String getParsedDefinition() {
        return parsedDefinition;
    }
    
    @PropertyAlias("Created By")
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @NotNull(message="Created By is required", groups = AllGroup.class)
    @JoinColumn(name = "userID")
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    @Transient
    public String getLetter() {
        return getName().substring(0,1);
    }
    
    @PropertyAlias("First Character")
    @Transient
    public Character getFirstCharacter() {
        return getName().charAt(0);
    }
    
    @PropertyAlias("Categories")
    @ManyToMany(targetEntity=Category.class, fetch = FetchType.LAZY,cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name="TermCategoryLinks",
        joinColumns=
            @JoinColumn(name="termID", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="categoryID", referencedColumnName="id")
    )
    //@Size(min=1,message="Categories must include at least one selection",groups=AllGroup.class)
    @OrderBy("name")
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    /*
     * This includes  direct categories and all their children.
     * This is updated whenever the term is edited, drawing from the list of stored
     * children stored at the time in the category.
     * As a result, it is possible to fall out of sync.
     * It is the resposibility of the code that updates the category hierarchy
     * to update all terms.
     */
    @PropertyAlias("All Categories")
    @ManyToMany(targetEntity=Category.class, fetch = FetchType.LAZY,cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name="TermAllCategoryLinks",
        joinColumns=
            @JoinColumn(name="termID", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="categoryID", referencedColumnName="id")
    )
    //@Size(min=1,message="All Categories must include at least one selection",groups=AllGroup.class)
    @OrderBy("name")
    public List<Category> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }
    
    @PropertyAlias("Novels")
    @ManyToMany(targetEntity=Novel.class, fetch = FetchType.LAZY,cascade = { CascadeType.ALL })
    @JoinTable(name="TermNovelLinks",
        joinColumns=
            @JoinColumn(name="termID", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="novelID", referencedColumnName="id")
    )
    @OrderBy("ordinal")
    public List<Novel> getNovels() {
        return novels;
    }

    public void setNovels(List<Novel> novels) {
        this.novels = novels;
    }
    
    @PropertyAlias("Event Date")
    //@Valid
    // PERSISTENCE
    @Embedded
    public SuperiadDate getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(SuperiadDate eventDate) {
        this.eventDate = eventDate;
    }
    
    @Transient
    public Boolean getIsDated() {
        return eventDate != null;
    }
    
    @Override
    public int compareTo(Term B) {
        return getName().compareTo(B.getName());
    }
    
    // Maintain contract between hashcode and equals
    @Override
    public int hashCode() {
        return new Hasher().hash(getName()).hash(getId()).getValue();
    }

    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void configure() {
        // STORE ALL CATEGORIES
        getAllCategories().clear();
        getAllCategories().addAll(buildFullCategoryHierarchy());
    }
    
    public List<Category> buildFullCategoryHierarchy() {
        // STORE ALL CATEGORIES
        List<Category> localCategories = new ArrayList<>();
        for (Category category : getCategories()) {
            localCategories.add(category);
            localCategories.addAll(category.getAllChildren());
        }
        return localCategories;
    }
    
    @Override
    public boolean equals(Object b) {
        if (b == null) {
            return false;
        }
        if (this == b) {
            return true;
        }
        if (!Term.class.isInstance(b)) {
            return false;
        }
        final Term B = (Term) b;
        if (getId() == null || B.getId() == null) {
            return getName().equals(B.getName());
        }
        return getId().equals(B.getId());
    }
    
    @Override
    public Map<String, Object> toJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("id",getId());
        rtn.put("name",getName());
        rtn.put("definition",getParsedDefinition() == null ? getDefinition() : getParsedDefinition());
        List<Object> icategories = new ArrayList<>();
        for (Category category : getCategories()) {
            icategories.add(category.toJson());
        }
        rtn.put("categories",icategories);
        List<Object> acategories = new ArrayList<>();
        for (Category category : getAllCategories()) {
            acategories.add(category.toSmallJson());
        }
        rtn.put("allCategories",acategories);
        List<Object> inovels = new ArrayList<>();
        for (Novel novel : getNovels()) {
            inovels.add(novel.toJson());
        }
        rtn.put("novels",inovels);
        rtn.put("eventDate",(getEventDate() == null || getEventDate().getEpoch() == null || getEventDate().getRelativeDate() == null) ? null : 
             getEventDate().toJson());
        rtn.put("creator",getCreator().toJson());
        return rtn;
    }
    
}
