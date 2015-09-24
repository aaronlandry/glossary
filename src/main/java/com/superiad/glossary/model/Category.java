package com.superiad.glossary.model;

import com.superiad.glossary.persistence.CategoryRepository;
import com.superiad.glossary.persistence.CollectionClass;
import com.superiad.glossary.util.Hasher;
import com.superiad.glossary.validation.AllGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Mutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *
 * @author Aaron
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Categories")
public class Category extends AbstractPersistable<Category, CategoryRepository> {
    
    // INJECTION
    @Autowired(required=true)
    private transient CategoryRepository categoryRepositoryImpl;
    private String name;
    private Category parent;
    private List<Category> children = new ArrayList<>();
    private List<Category> allChildren = new ArrayList<>();
    private User creator;
    private Integer indent = 0;
    private String longName;

    @Override
    public CategoryRepository getRepository() {
        return categoryRepositoryImpl;
    }
    
    @Override
    // PERSISTENCE
    @Id
    @SequenceGenerator(name = "CAT_ID_GENERATOR", sequenceName = "cat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_ID_GENERATOR")
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
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }
    
    public void setLongName(String longName) {
        this.longName = longName;
    }
    
    @PropertyAlias("Long Name")
    // VALIDATION
    @NotNull(message="Long Name is required", groups = AllGroup.class )
    @Size(max = 1000, message = "Long Name cannot exceed 1000 characters", groups = AllGroup.class)
    // PERSISTENCE
    @Column(name = "LongName", nullable = false, length = 1000)
    public String getLongName() {
        return longName;
    }
    
    @PropertyAlias("Created By")
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @NotNull(message="Created By is required", groups = AllGroup.class)
    @JoinColumn(name = "userID", nullable = false)
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID")
    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        if (this.parent != null && !this.parent.equals(parent)) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
    }

    @CollectionClass(Category.class)
    @OneToMany(mappedBy = "parent", targetEntity = Category.class, fetch = FetchType.LAZY)
    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
    
    @PropertyAlias("All Children")
    @ManyToMany(targetEntity=Category.class, fetch = FetchType.LAZY,cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name="CategoryAllChildrenLinks",
        joinColumns=
            @JoinColumn(name="parentID", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="childID", referencedColumnName="id")
    )
    @Size(min=1,message="All Children must include at least one selection",groups=AllGroup.class)
    @OrderBy("name")
    public List<Category> getAllChildren() {
        return allChildren;
    }

    public void setAllChildren(List<Category> allChildren) {
        this.allChildren = allChildren;
    }
    
    @PropertyAlias("Indent")
    // VALIDATION
    @NotNull(message="Indent is required", groups = AllGroup.class )
    // PERSISTENCE
    @Column(name = "indent", nullable = false)
    public Integer getIndent() {
        return indent;
    }
    
    public void setIndent(Integer indent) {
        this.indent = indent;
    }
    
    @Override
    public int compareTo(Category B) {
        return getName().compareTo(B.getName());
    }
    
    // Maintain contract between hashcode and equals
    @Override
    public int hashCode() {
        return new Hasher().hash(getName()).hash(getId()).getValue();
    }

    @Override
    public boolean equals(Object b) {
        if (b == null) {
            return false;
        }
        if (this == b) {
            return true;
        }
        if (!Category.class.isInstance(b)) {
            return false;
        }
        final Category B = (Category) b;
        if (getId() == null || B.getId() == null) {
            return getName().equals(B.getName());
        }
        return getId().equals(B.getId());
    }

    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void configure() {
        setIndent(determineIndent());
        setLongName(determineLongName());
        getAllChildren().clear();
        getAllChildren().addAll(determineAllChildren());
    }
    
    public String determineLongName() {
        if (longName == null) {
            List<String> parts = new ArrayList<>();
            parts.add(0, getName());
            Category p = getParent();
            while (p != null) {
                parts.add(0,p.getName());
                p = p.getParent();
            }
            return StringUtils.collectionToDelimitedString(parts, " - ");
        }
        return longName;
    }
    
    public Integer determineIndent() {
        int iindent = 0;
        Category p = getParent();
        while (p != null) {
            iindent++;
            p = p.getParent();
        }
        return iindent;
    }
    
    public List<Category> determineAllChildren() {
        List<Category> ichildren = new ArrayList<>();
        for (Category child : getChildren()) {
            ichildren.add(child);
            ichildren.addAll(child.determineAllChildren());
        }
        return ichildren;
    }
    
    @Override
    public Map<String, Object> toJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("id",getId());
        rtn.put("name",getName());
        rtn.put("longName",getLongName());
        rtn.put("creator",getCreator().toJson());
        rtn.put("indent",getIndent());
        return rtn;
    }
    
    public Map<String, Object> toSmallJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("id",getId());
        rtn.put("name",getName());
        return rtn;
    }
    
    public Map<String, Object> toHierarchicalJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("id",getId());
        rtn.put("name",getName());
        List<Map<String,Object>> childrenJson = new ArrayList<>();
        for (Category category : getChildren()) {
            childrenJson.add(category.toHierarchicalJson());
        }
        rtn.put("children",childrenJson);
        rtn.put("parentID",getParent()==null ? null : getParent().getId());
        return rtn;
    }
    
}
