package com.superiad.glossary.model;

import com.superiad.glossary.persistence.PersistableEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Aaron
 */
public enum SuperiadEpoch implements PersistableEnum {
    BEFORE_TIME(1,0,"Before Time"),
    THE_DAWNING(2,10000,"The Dawning"),
    JARDAIN(3,15000,"The Age of Jardain"),
    AGE_OF_KALITHKHIS(4,500000,"The Age of Kalitkhis"),
    HUAAHTIAAH(5,50200, "Huaahtiaah"),
    SARGARATH(6,51300, "Sargarath"),
    YENCOUR_1(7,52450, "First Yencour"),
    DOMINIION_OF_SEMA(8,52837, "Dominion of Sema"),
    SEN_CANAAR(9,123200, "Sen Canaar"),
    SKY_REALM(10,433405,"The Sky Realm"),
    NINE_SPEARS(11,500670,"The Nine Spears"),
    YENCOUR_2(12,643200, "Second Yencour"),
    ALLIANCE_OF_SANDS(13,846207,"Alliance of Sands"),
    AEGEA(14,1246207, "Aegean Empire"),
    YENCOUR_3(15,1246887,"Third Yencour"),
    AEGEA_2(16,1249307,"Second Aegean Empire"),
    AGE_OF_CHAOS(18,1252307,"The Modern Era"),
    SHADOW_COUNCIL(19,1252907,"The Shadow Covenant"),
    FINAL_LATTICE(20,1254307,"The Final Lattice");
    
    public static SuperiadEpoch findForValue(Integer value) {
        for (SuperiadEpoch se : SuperiadEpoch.values()) {
            if (se.getValue().equals(value)) {
                return se;
            }
        }
        return null;
    }
    
    private Integer value;
    private Integer startPoint;
    private String label;
    private Integer endPoint;
    private static final SuperiadEpoch[] ENTRIES = values();
    
    SuperiadEpoch(Integer value, Integer startPoint, String label) {
        this.value = value;
        this.startPoint = startPoint;
        this.label = label;
    }
    
    public Integer getEndPoint() {
        if (endPoint == null) {
            SuperiadEpoch next = next();
            if (next != null) {
                endPoint = next.getStartPoint() - 1;
            }
        }
        if (endPoint == null) {
            endPoint = -1;
        }
        return endPoint;
    }
    
    public Integer getStartPoint() {
        return startPoint;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public Integer getValue() {
        return value;
    }
    
    @Override
    public String getHtmlLabel() {
        return getLabel() + " (" + getStartPoint() + " - " + (getEndPoint() == -1 ? "?" : getEndPoint()) + ")";
    }
    
    // For consistency with Persistables
    
    @Override
    public String getSortableString() {
        return getLabel();
    }
    
    @Override
    public Integer getId() {
        return getValue();
    }
    
    public SuperiadEpoch next() {
        int next = ordinal() + 1;
        int max = ENTRIES.length - 1;
        if (next > max) {
            return null;
        }
        return ENTRIES[next];
    }
 
    public Map<String, Object> toJson() {
        Map<String,Object> rtn = new HashMap<>();
        rtn.put("id",getId());
        rtn.put("name",getLabel());
        rtn.put("longName",getHtmlLabel());
        rtn.put("startPoint",getStartPoint());
        rtn.put("endPoint",getEndPoint());
        return rtn;
    }
    
}
