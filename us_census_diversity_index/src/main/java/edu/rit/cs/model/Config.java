package edu.rit.cs.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Iterator;

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

    public static final int AGEGRP = 0;
    public static final int NUM_RACES = 6;

    public static final String STATE = "STNAME";
    public static final String CITY = "CTYNAME";
    public static final String TOTAL_POP = "TOT_POP";

    public static final String WA = "WA";
    public static final String WA_MALE = "WA_MALE";
    public static final String WA_FEMALE = "WA_FEMALE";

    public static final String BA = "BA";
    public static final String BA_MALE = "BA_MALE";
    public static final String BA_FEMALE = "BA_FEMALE";

    public static final String IA = "IA";
    public static final String IA_MALE = "IA_MALE";
    public static final String IA_FEMALE = "IA_FEMALE";

    public static final String AA = "AA";
    public static final String AA_MALE = "AA_MALE";
    public static final String AA_FEMALE = "AA_FEMALE";

    public static final String NA = "NA";
    public static final String NA_MALE = "NA_MALE";
    public static final String NA_FEMALE = "NA_FEMALE";

    public static final String TOM = "WA";
    public static final String TOM_MALE = "WA_MALE";
    public static final String TOM_FEMALE = "WA_FEMALE";

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

    public static String calcDivIndex(Iterator rows) {

        String answer = "";
        while (rows.hasNext()) {
            Row row = (Row)rows.next();
            int total = 0;
            double result = 0;
            double[] arr = new double[NUM_RACES];

            for (int i = 0; i < NUM_RACES; i++) {
                arr[i] = row.getLong(i * 2 + 2) + row.getLong(i * 2 + 3);
                total += arr[i];
            }

            for (int i = 0; i < arr.length; i++) {
                result += arr[i] * (total - arr[i]);
            }
            result = result / Math.pow(total, 2);

            answer += ("[" + row.getString(0) + "," + row.getString(1) + "," + result + "]\n");
        }
        return answer;
    }
}
