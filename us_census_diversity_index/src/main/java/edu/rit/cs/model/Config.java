package edu.rit.cs.model;

public class Config {

    public final String YEAR_1 = "April 1, 2010 census population";
    public final String YEAR_2 = "April 1, 2010 population estimates base";
    public final String YEAR_3 = "July 1, 2010 population estimate";
    public final String YEAR_4 = "July 1, 2011 population estimate";
    public final String YEAR_5 = "July 1, 2012 population estimate";
    public final String YEAR_6 = "July 1, 2013 population estimate";
    public final String YEAR_7 = "July 1, 2014 population estimate";
    public final String YEAR_8 = "July 1, 2015 population estimate";
    public final String YEAR_9 = "July 1, 2016 population estimate";
    public final String YEAR_10 = "July 1, 2017 population estimate";

    public final int AGEGRP = 0;
    public final int NUM_RACES = 6;

    public String getYear(int year){
        switch (year){
            case (1): {
                return YEAR_1;
            }
            case (2): {
                return YEAR_2;
            }
            case (3):{
                return YEAR_3;
            }
            case (4):{
                return YEAR_4;
            }
            case (5):{
                return YEAR_5;
            }
            case (6): {
                return YEAR_6;
            }
            case (7): {
                return YEAR_7;
            }
            case (8):{
                return YEAR_8;
            }
            case (9):{
                return YEAR_9;
            }
            case (10):{
                return YEAR_10;
            }
            default:
                return "DNE";
        }
    }

    public int getAgeGroup(){
        return AGEGRP;
    }

    public double calcDivIndex(int total, int [] individual){
        double result = 0;

        for (int i = 0; i < individual.length; i++) {
            result += individual[i] * (total - individual[i]);
        }
        result = result/(total ^ 2);
        return result;
    }
}
