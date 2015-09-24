package com.superiad.glossary.model;

import com.superiad.glossary.persistence.BooleanConverter;
import com.superiad.glossary.persistence.PersistableEnumConverter;
import com.superiad.glossary.persistence.NovelRepository;
import com.superiad.glossary.util.Hasher;
import com.superiad.glossary.validation.AllGroup;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Converters;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Mutable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Aaron
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Novels")
@Converters({
    @Converter(name="persistableEnum", converterClass=PersistableEnumConverter.class),
    @Converter(name="boolean", converterClass=BooleanConverter.class)
})
public class Novel extends AbstractPersistable<Novel, NovelRepository> {
    
    // INJECTION
    @Autowired(required=true)
    private transient NovelRepository novelRepositoryImpl;
    private String name;
    private User creator;
    private Integer ordinal;

    @Override
    public NovelRepository getRepository() {
        return novelRepositoryImpl;
    }
    
    @Override
    // PERSISTENCE
    @Id
    @SequenceGenerator(name = "NOVEL_ID_GENERATOR", sequenceName = "novel_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOVEL_ID_GENERATOR")
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
    
    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }
    
    @PropertyAlias("Ordinal")
    // VALIDATION
    @NotNull(message="Ordinal is required", groups = AllGroup.class )
    // PERSISTENCE
    @Column(name = "ordinal", nullable = false)
    public Integer getOrdinal() {
        return ordinal;
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
    
    @Override
    public int compareTo(Novel B) {
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
        if (!Novel.class.isInstance(b)) {
            return false;
        }
        final Novel B = (Novel) b;
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
        rtn.put("creator",getCreator().toJson());
        return rtn;
    }
    
}
