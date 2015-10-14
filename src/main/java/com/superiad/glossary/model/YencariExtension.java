package com.superiad.glossary.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Aaron
 */
@Embeddable
public class YencariExtension implements Serializable {
    
    private String firstYencourName;
    private String secondYencourName;
    private String thirdYencourName;
    private String firstAegeaName;
    private String secondAegeaBirthName;
    private String secondAegeaUniversityName;
    private String secondAegeaGreatWarName;
    private String sargarathName;
    private String aYencariName;
    private String superiName;
    private String modernName;
    private String chara;
    private String marg;

    @Column(length=500)
    public String getChara() {
        return chara;
    }

    public void setChara(String chara) {
        this.chara = chara;
    }

    @Column(length=200)
    public String getMarg() {
        return marg;
    }

    public void setMarg(String marg) {
        this.marg = marg;
    }

    @Column(length=100)
    public String getFirstYencourName() {
        return firstYencourName;
    }

    public void setFirstYencourName(String firstYencourName) {
        this.firstYencourName = firstYencourName;
    }

    @Column(length=100)
    public String getSecondYencourName() {
        return secondYencourName;
    }

    public void setSecondYencourName(String secondYencourName) {
        this.secondYencourName = secondYencourName;
    }

    @Column(length=100)
    public String getThirdYencourName() {
        return thirdYencourName;
    }

    public void setThirdYencourName(String thirdYencourName) {
        this.thirdYencourName = thirdYencourName;
    }

    @Column(length=100)
    public String getFirstAegeaName() {
        return firstAegeaName;
    }

    public void setFirstAegeaName(String firstAegeaName) {
        this.firstAegeaName = firstAegeaName;
    }

    @Column(length=100)
    public String getSecondAegeaBirthName() {
        return secondAegeaBirthName;
    }

    public void setSecondAegeaBirthName(String secondAegeaBirthName) {
        this.secondAegeaBirthName = secondAegeaBirthName;
    }

    @Column(length=100)
    public String getSecondAegeaUniversityName() {
        return secondAegeaUniversityName;
    }

    public void setSecondAegeaUniversityName(String secondAegeaUniversityName) {
        this.secondAegeaUniversityName = secondAegeaUniversityName;
    }

    @Column(length=100)
    public String getSecondAegeaGreatWarName() {
        return secondAegeaGreatWarName;
    }

    public void setSecondAegeaGreatWarName(String secondAegeaGreatWarName) {
        this.secondAegeaGreatWarName = secondAegeaGreatWarName;
    }

    @Column(length=100)
    public String getSargarathName() {
        return sargarathName;
    }

    public void setSargarathName(String sargarathName) {
        this.sargarathName = sargarathName;
    }

    @Column(length=100)
    public String getaYencariName() {
        return aYencariName;
    }

    public void setaYencariName(String aYencariName) {
        this.aYencariName = aYencariName;
    }

    @Column(length=100)
    public String getSuperiName() {
        return superiName;
    }

    public void setSuperiName(String superiName) {
        this.superiName = superiName;
    }

    @Column(length=100)
    public String getModernName() {
        return modernName;
    }

    public void setModernName(String modernName) {
        this.modernName = modernName;
    } 
    
}
