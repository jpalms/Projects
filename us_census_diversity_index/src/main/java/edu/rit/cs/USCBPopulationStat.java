package edu.rit.cs;

// SUMLEV,STATE,COUNTY,STNAME,CTYNAME,YEAR,AGEGRP,TOT_POP,TOT_MALE,TOT_FEMALE,WA_MALE,WA_FEMALE,BA_MALE,BA_FEMALE,IA_MALE,IA_FEMALE,AA_MALE,AA_FEMALE,NA_MALE,NA_FEMALE,TOM_MALE,TOM_FEMALE,WAC_MALE,WAC_FEMALE,BAC_MALE,BAC_FEMALE,IAC_MALE,IAC_FEMALE,AAC_MALE,AAC_FEMALE,NAC_MALE,NAC_FEMALE,NH_MALE,NH_FEMALE,NHWA_MALE,NHWA_FEMALE,NHBA_MALE,NHBA_FEMALE,NHIA_MALE,NHIA_FEMALE,NHAA_MALE,NHAA_FEMALE,NHNA_MALE,NHNA_FEMALE,NHTOM_MALE,NHTOM_FEMALE,NHWAC_MALE,NHWAC_FEMALE,NHBAC_MALE,NHBAC_FEMALE,NHIAC_MALE,NHIAC_FEMALE,NHAAC_MALE,NHAAC_FEMALE,NHNAC_MALE,NHNAC_FEMALE,H_MALE,H_FEMALE,HWA_MALE,HWA_FEMALE,HBA_MALE,HBA_FEMALE,HIA_MALE,HIA_FEMALE,HAA_MALE,HAA_FEMALE,HNA_MALE,HNA_FEMALE,HTOM_MALE,HTOM_FEMALE,HWAC_MALE,HWAC_FEMALE,HBAC_MALE,HBAC_FEMALE,HIAC_MALE,HIAC_FEMALE,HAAC_MALE,HAAC_FEMALE,HNAC_MALE,HNAC_FEMALE
public class USCBPopulationStat {
    private int SUMLEV, STATE, COUNTY;
    private String STNAME;
    private String CTYNAME;
    private int YEAR,AGEGRP,TOT_POP,TOT_MALE,TOT_FEMALE,WA_MALE,WA_FEMALE,BA_MALE,BA_FEMALE,IA_MALE,IA_FEMALE,AA_MALE,AA_FEMALE,NA_MALE,NA_FEMALE,TOM_MALE,TOM_FEMALE,WAC_MALE,WAC_FEMALE,BAC_MALE,BAC_FEMALE,IAC_MALE,IAC_FEMALE,AAC_MALE,AAC_FEMALE,NAC_MALE,NAC_FEMALE,NH_MALE,NH_FEMALE,NHWA_MALE,NHWA_FEMALE,NHBA_MALE,NHBA_FEMALE,NHIA_MALE,NHIA_FEMALE,NHAA_MALE,NHAA_FEMALE,NHNA_MALE,NHNA_FEMALE,NHTOM_MALE,NHTOM_FEMALE,NHWAC_MALE,NHWAC_FEMALE,NHBAC_MALE,NHBAC_FEMALE,NHIAC_MALE,NHIAC_FEMALE,NHAAC_MALE,NHAAC_FEMALE,NHNAC_MALE,NHNAC_FEMALE,H_MALE,H_FEMALE,HWA_MALE,HWA_FEMALE,HBA_MALE,HBA_FEMALE,HIA_MALE,HIA_FEMALE,HAA_MALE,HAA_FEMALE,HNA_MALE,HNA_FEMALE,HTOM_MALE,HTOM_FEMALE,HWAC_MALE,HWAC_FEMALE,HBAC_MALE,HBAC_FEMALE,HIAC_MALE,HIAC_FEMALE,HAAC_MALE,HAAC_FEMALE,HNAC_MALE,HNAC_FEMALE;

    public int getSUMLEV() {
        return SUMLEV;
    }

    public void setSUMLEV(int SUMLEV) {
        this.SUMLEV = SUMLEV;
    }

    public int getSTATE() {
        return STATE;
    }

    public void setSTATE(int STATE) {
        this.STATE = STATE;
    }

    public int getCOUNTY() {
        return COUNTY;
    }

    public void setCOUNTY(int COUNTY) {
        this.COUNTY = COUNTY;
    }

    public String getSTNAME() {
        return STNAME;
    }

    public void setSTNAME(String STNAME) {
        this.STNAME = STNAME;
    }

    public String getCTYNAME() {
        return CTYNAME;
    }

    public void setCTYNAME(String CTYNAME) {
        this.CTYNAME = CTYNAME;
    }

    public int getYEAR() {
        return YEAR;
    }

    public void setYEAR(int YEAR) {
        this.YEAR = YEAR;
    }

    public int getAGEGRP() {
        return AGEGRP;
    }

    public void setAGEGRP(int AGEGRP) {
        this.AGEGRP = AGEGRP;
    }

    public int getTOT_POP() {
        return TOT_POP;
    }

    public void setTOT_POP(int TOT_POP) {
        this.TOT_POP = TOT_POP;
    }

    public int getTOT_MALE() {
        return TOT_MALE;
    }

    public void setTOT_MALE(int TOT_MALE) {
        this.TOT_MALE = TOT_MALE;
    }

    public int getTOT_FEMALE() {
        return TOT_FEMALE;
    }

    public void setTOT_FEMALE(int TOT_FEMALE) {
        this.TOT_FEMALE = TOT_FEMALE;
    }

    public int getWA_MALE() {
        return WA_MALE;
    }

    public void setWA_MALE(int WA_MALE) {
        this.WA_MALE = WA_MALE;
    }

    public int getWA_FEMALE() {
        return WA_FEMALE;
    }

    public void setWA_FEMALE(int WA_FEMALE) {
        this.WA_FEMALE = WA_FEMALE;
    }

    public int getBA_MALE() {
        return BA_MALE;
    }

    public void setBA_MALE(int BA_MALE) {
        this.BA_MALE = BA_MALE;
    }

    public int getBA_FEMALE() {
        return BA_FEMALE;
    }

    public void setBA_FEMALE(int BA_FEMALE) {
        this.BA_FEMALE = BA_FEMALE;
    }

    public int getIA_MALE() {
        return IA_MALE;
    }

    public void setIA_MALE(int IA_MALE) {
        this.IA_MALE = IA_MALE;
    }

    public int getIA_FEMALE() {
        return IA_FEMALE;
    }

    public void setIA_FEMALE(int IA_FEMALE) {
        this.IA_FEMALE = IA_FEMALE;
    }

    public int getAA_MALE() {
        return AA_MALE;
    }

    public void setAA_MALE(int AA_MALE) {
        this.AA_MALE = AA_MALE;
    }

    public int getAA_FEMALE() {
        return AA_FEMALE;
    }

    public void setAA_FEMALE(int AA_FEMALE) {
        this.AA_FEMALE = AA_FEMALE;
    }

    public int getNA_MALE() {
        return NA_MALE;
    }

    public void setNA_MALE(int NA_MALE) {
        this.NA_MALE = NA_MALE;
    }

    public int getNA_FEMALE() {
        return NA_FEMALE;
    }

    public void setNA_FEMALE(int NA_FEMALE) {
        this.NA_FEMALE = NA_FEMALE;
    }

    public int getTOM_MALE() {
        return TOM_MALE;
    }

    public void setTOM_MALE(int TOM_MALE) {
        this.TOM_MALE = TOM_MALE;
    }

    public int getTOM_FEMALE() {
        return TOM_FEMALE;
    }

    public void setTOM_FEMALE(int TOM_FEMALE) {
        this.TOM_FEMALE = TOM_FEMALE;
    }

    public int getWAC_MALE() {
        return WAC_MALE;
    }

    public void setWAC_MALE(int WAC_MALE) {
        this.WAC_MALE = WAC_MALE;
    }

    public int getWAC_FEMALE() {
        return WAC_FEMALE;
    }

    public void setWAC_FEMALE(int WAC_FEMALE) {
        this.WAC_FEMALE = WAC_FEMALE;
    }

    public int getBAC_MALE() {
        return BAC_MALE;
    }

    public void setBAC_MALE(int BAC_MALE) {
        this.BAC_MALE = BAC_MALE;
    }

    public int getBAC_FEMALE() {
        return BAC_FEMALE;
    }

    public void setBAC_FEMALE(int BAC_FEMALE) {
        this.BAC_FEMALE = BAC_FEMALE;
    }

    public int getIAC_MALE() {
        return IAC_MALE;
    }

    public void setIAC_MALE(int IAC_MALE) {
        this.IAC_MALE = IAC_MALE;
    }

    public int getIAC_FEMALE() {
        return IAC_FEMALE;
    }

    public void setIAC_FEMALE(int IAC_FEMALE) {
        this.IAC_FEMALE = IAC_FEMALE;
    }

    public int getAAC_MALE() {
        return AAC_MALE;
    }

    public void setAAC_MALE(int AAC_MALE) {
        this.AAC_MALE = AAC_MALE;
    }

    public int getAAC_FEMALE() {
        return AAC_FEMALE;
    }

    public void setAAC_FEMALE(int AAC_FEMALE) {
        this.AAC_FEMALE = AAC_FEMALE;
    }

    public int getNAC_MALE() {
        return NAC_MALE;
    }

    public void setNAC_MALE(int NAC_MALE) {
        this.NAC_MALE = NAC_MALE;
    }

    public int getNAC_FEMALE() {
        return NAC_FEMALE;
    }

    public void setNAC_FEMALE(int NAC_FEMALE) {
        this.NAC_FEMALE = NAC_FEMALE;
    }

    public int getNH_MALE() {
        return NH_MALE;
    }

    public void setNH_MALE(int NH_MALE) {
        this.NH_MALE = NH_MALE;
    }

    public int getNH_FEMALE() {
        return NH_FEMALE;
    }

    public void setNH_FEMALE(int NH_FEMALE) {
        this.NH_FEMALE = NH_FEMALE;
    }

    public int getNHWA_MALE() {
        return NHWA_MALE;
    }

    public void setNHWA_MALE(int NHWA_MALE) {
        this.NHWA_MALE = NHWA_MALE;
    }

    public int getNHWA_FEMALE() {
        return NHWA_FEMALE;
    }

    public void setNHWA_FEMALE(int NHWA_FEMALE) {
        this.NHWA_FEMALE = NHWA_FEMALE;
    }

    public int getNHBA_MALE() {
        return NHBA_MALE;
    }

    public void setNHBA_MALE(int NHBA_MALE) {
        this.NHBA_MALE = NHBA_MALE;
    }

    public int getNHBA_FEMALE() {
        return NHBA_FEMALE;
    }

    public void setNHBA_FEMALE(int NHBA_FEMALE) {
        this.NHBA_FEMALE = NHBA_FEMALE;
    }

    public int getNHIA_MALE() {
        return NHIA_MALE;
    }

    public void setNHIA_MALE(int NHIA_MALE) {
        this.NHIA_MALE = NHIA_MALE;
    }

    public int getNHIA_FEMALE() {
        return NHIA_FEMALE;
    }

    public void setNHIA_FEMALE(int NHIA_FEMALE) {
        this.NHIA_FEMALE = NHIA_FEMALE;
    }

    public int getNHAA_MALE() {
        return NHAA_MALE;
    }

    public void setNHAA_MALE(int NHAA_MALE) {
        this.NHAA_MALE = NHAA_MALE;
    }

    public int getNHAA_FEMALE() {
        return NHAA_FEMALE;
    }

    public void setNHAA_FEMALE(int NHAA_FEMALE) {
        this.NHAA_FEMALE = NHAA_FEMALE;
    }

    public int getNHNA_MALE() {
        return NHNA_MALE;
    }

    public void setNHNA_MALE(int NHNA_MALE) {
        this.NHNA_MALE = NHNA_MALE;
    }

    public int getNHNA_FEMALE() {
        return NHNA_FEMALE;
    }

    public void setNHNA_FEMALE(int NHNA_FEMALE) {
        this.NHNA_FEMALE = NHNA_FEMALE;
    }

    public int getNHTOM_MALE() {
        return NHTOM_MALE;
    }

    public void setNHTOM_MALE(int NHTOM_MALE) {
        this.NHTOM_MALE = NHTOM_MALE;
    }

    public int getNHTOM_FEMALE() {
        return NHTOM_FEMALE;
    }

    public void setNHTOM_FEMALE(int NHTOM_FEMALE) {
        this.NHTOM_FEMALE = NHTOM_FEMALE;
    }

    public int getNHWAC_MALE() {
        return NHWAC_MALE;
    }

    public void setNHWAC_MALE(int NHWAC_MALE) {
        this.NHWAC_MALE = NHWAC_MALE;
    }

    public int getNHWAC_FEMALE() {
        return NHWAC_FEMALE;
    }

    public void setNHWAC_FEMALE(int NHWAC_FEMALE) {
        this.NHWAC_FEMALE = NHWAC_FEMALE;
    }

    public int getNHBAC_MALE() {
        return NHBAC_MALE;
    }

    public void setNHBAC_MALE(int NHBAC_MALE) {
        this.NHBAC_MALE = NHBAC_MALE;
    }

    public int getNHBAC_FEMALE() {
        return NHBAC_FEMALE;
    }

    public void setNHBAC_FEMALE(int NHBAC_FEMALE) {
        this.NHBAC_FEMALE = NHBAC_FEMALE;
    }

    public int getNHIAC_MALE() {
        return NHIAC_MALE;
    }

    public void setNHIAC_MALE(int NHIAC_MALE) {
        this.NHIAC_MALE = NHIAC_MALE;
    }

    public int getNHIAC_FEMALE() {
        return NHIAC_FEMALE;
    }

    public void setNHIAC_FEMALE(int NHIAC_FEMALE) {
        this.NHIAC_FEMALE = NHIAC_FEMALE;
    }

    public int getNHAAC_MALE() {
        return NHAAC_MALE;
    }

    public void setNHAAC_MALE(int NHAAC_MALE) {
        this.NHAAC_MALE = NHAAC_MALE;
    }

    public int getNHAAC_FEMALE() {
        return NHAAC_FEMALE;
    }

    public void setNHAAC_FEMALE(int NHAAC_FEMALE) {
        this.NHAAC_FEMALE = NHAAC_FEMALE;
    }

    public int getNHNAC_MALE() {
        return NHNAC_MALE;
    }

    public void setNHNAC_MALE(int NHNAC_MALE) {
        this.NHNAC_MALE = NHNAC_MALE;
    }

    public int getNHNAC_FEMALE() {
        return NHNAC_FEMALE;
    }

    public void setNHNAC_FEMALE(int NHNAC_FEMALE) {
        this.NHNAC_FEMALE = NHNAC_FEMALE;
    }

    public int getH_MALE() {
        return H_MALE;
    }

    public void setH_MALE(int h_MALE) {
        H_MALE = h_MALE;
    }

    public int getH_FEMALE() {
        return H_FEMALE;
    }

    public void setH_FEMALE(int h_FEMALE) {
        H_FEMALE = h_FEMALE;
    }

    public int getHWA_MALE() {
        return HWA_MALE;
    }

    public void setHWA_MALE(int HWA_MALE) {
        this.HWA_MALE = HWA_MALE;
    }

    public int getHWA_FEMALE() {
        return HWA_FEMALE;
    }

    public void setHWA_FEMALE(int HWA_FEMALE) {
        this.HWA_FEMALE = HWA_FEMALE;
    }

    public int getHBA_MALE() {
        return HBA_MALE;
    }

    public void setHBA_MALE(int HBA_MALE) {
        this.HBA_MALE = HBA_MALE;
    }

    public int getHBA_FEMALE() {
        return HBA_FEMALE;
    }

    public void setHBA_FEMALE(int HBA_FEMALE) {
        this.HBA_FEMALE = HBA_FEMALE;
    }

    public int getHIA_MALE() {
        return HIA_MALE;
    }

    public void setHIA_MALE(int HIA_MALE) {
        this.HIA_MALE = HIA_MALE;
    }

    public int getHIA_FEMALE() {
        return HIA_FEMALE;
    }

    public void setHIA_FEMALE(int HIA_FEMALE) {
        this.HIA_FEMALE = HIA_FEMALE;
    }

    public int getHAA_MALE() {
        return HAA_MALE;
    }

    public void setHAA_MALE(int HAA_MALE) {
        this.HAA_MALE = HAA_MALE;
    }

    public int getHAA_FEMALE() {
        return HAA_FEMALE;
    }

    public void setHAA_FEMALE(int HAA_FEMALE) {
        this.HAA_FEMALE = HAA_FEMALE;
    }

    public int getHNA_MALE() {
        return HNA_MALE;
    }

    public void setHNA_MALE(int HNA_MALE) {
        this.HNA_MALE = HNA_MALE;
    }

    public int getHNA_FEMALE() {
        return HNA_FEMALE;
    }

    public void setHNA_FEMALE(int HNA_FEMALE) {
        this.HNA_FEMALE = HNA_FEMALE;
    }

    public int getHTOM_MALE() {
        return HTOM_MALE;
    }

    public void setHTOM_MALE(int HTOM_MALE) {
        this.HTOM_MALE = HTOM_MALE;
    }

    public int getHTOM_FEMALE() {
        return HTOM_FEMALE;
    }

    public void setHTOM_FEMALE(int HTOM_FEMALE) {
        this.HTOM_FEMALE = HTOM_FEMALE;
    }

    public int getHWAC_MALE() {
        return HWAC_MALE;
    }

    public void setHWAC_MALE(int HWAC_MALE) {
        this.HWAC_MALE = HWAC_MALE;
    }

    public int getHWAC_FEMALE() {
        return HWAC_FEMALE;
    }

    public void setHWAC_FEMALE(int HWAC_FEMALE) {
        this.HWAC_FEMALE = HWAC_FEMALE;
    }

    public int getHBAC_MALE() {
        return HBAC_MALE;
    }

    public void setHBAC_MALE(int HBAC_MALE) {
        this.HBAC_MALE = HBAC_MALE;
    }

    public int getHBAC_FEMALE() {
        return HBAC_FEMALE;
    }

    public void setHBAC_FEMALE(int HBAC_FEMALE) {
        this.HBAC_FEMALE = HBAC_FEMALE;
    }

    public int getHIAC_MALE() {
        return HIAC_MALE;
    }

    public void setHIAC_MALE(int HIAC_MALE) {
        this.HIAC_MALE = HIAC_MALE;
    }

    public int getHIAC_FEMALE() {
        return HIAC_FEMALE;
    }

    public void setHIAC_FEMALE(int HIAC_FEMALE) {
        this.HIAC_FEMALE = HIAC_FEMALE;
    }

    public int getHAAC_MALE() {
        return HAAC_MALE;
    }

    public void setHAAC_MALE(int HAAC_MALE) {
        this.HAAC_MALE = HAAC_MALE;
    }

    public int getHAAC_FEMALE() {
        return HAAC_FEMALE;
    }

    public void setHAAC_FEMALE(int HAAC_FEMALE) {
        this.HAAC_FEMALE = HAAC_FEMALE;
    }

    public int getHNAC_MALE() {
        return HNAC_MALE;
    }

    public void setHNAC_MALE(int HNAC_MALE) {
        this.HNAC_MALE = HNAC_MALE;
    }

    public int getHNAC_FEMALE() {
        return HNAC_FEMALE;
    }

    public void setHNAC_FEMALE(int HNAC_FEMALE) {
        this.HNAC_FEMALE = HNAC_FEMALE;
    }
}
