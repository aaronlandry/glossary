package com.superiad.glossary.model;

import com.superiad.glossary.validation.AllGroup;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.eclipse.persistence.annotations.Convert;

/**
 *
 * @author Aaron
 */
@Embeddable
public class SuperiadDate implements Serializable, Comparable<SuperiadDate> {

    private Integer relativeDate;
    private SuperiadEpoch epoch;
    
    @PropertyAlias("Date")
    @Min(value=0,message="Date must be a value between 0 - 10,000,000",groups = AllGroup.class)
    @Max(value=10000000,message="Date must be a value between 0 - 10,000,000",groups = AllGroup.class)
    @Column(name="sdate_date")
    public Integer getRelativeDate() {
        return relativeDate;
    }
    
    public void setRelativeDate(Integer relativeDate) {
        this.relativeDate = relativeDate;
    }
    
    @PropertyAlias("Epoch")
    @Convert("persistableEnum")
    @Column(name="sdate_epoch")
    public SuperiadEpoch getEpoch() {
        return epoch;
    }
    
    public void setEpoch(SuperiadEpoch epoch) {
        this.epoch = epoch;
    }
    
    @Transient
    public Integer getAbsoluteDate() {
        return (getEpoch() == null || getRelativeDate() == null) ? null : getEpoch().getStartPoint() + getRelativeDate();
    }
    
    @Override
    public int compareTo(SuperiadDate B) {
        return getAbsoluteDate().compareTo(B.getAbsoluteDate());
    }
    
    @Override
    public String toString() {
        return getEpoch() + " " + getRelativeDate();
    }
    
    public Map<String, Object> toJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("epoch",getEpoch() == null ? null : getEpoch().toJson());
        rtn.put("relativeDate",getRelativeDate());
        rtn.put("absoluteDate",getAbsoluteDate());
        return rtn;
    }
    
}
