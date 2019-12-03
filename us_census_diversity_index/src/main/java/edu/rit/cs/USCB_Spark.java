package edu.rit.cs;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.io.File;

/**
 * U.S. Census Diversity Index
 * Based on https://www2.census.gov/programs-surveys/popest/datasets/2010-2017/counties/asrh/cc-est2017-alldata.csv
 */
public class USCB_Spark
{
    public static final String OutputDirectory = "dataset/USCB-outputs";
    public static final String DatasetFile = "dataset/USCB.csv";

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null)
            for (File file : allContents)
                deleteDirectory(file);
        return directoryToBeDeleted.delete();
    }

    public static void uscb(SparkSession spark) {
        // parse dataset file
        Dataset ds = spark.read()
                .option("header", "true")
                .option("sep", ",")
                .option("inferSchema", "true")
                .csv("us_census_diversity_index/" + DatasetFile);

        // encoders are created for Java beans
        Encoder<USCBPopulationStat> uscbEncoder = Encoders.bean(USCBPopulationStat.class);
        Dataset<USCBPopulationStat> ds1 = ds.as(uscbEncoder);
        // show initial table after import
        ds1.show();

        // filter and sum data across multiple years for all year groups
        Dataset ds2 = ds1.filter("AGEGRP = 0")
                .select("STNAME", "CTYNAME",
                "WA_MALE", "WA_FEMALE", "BA_MALE", "BA_FEMALE", "IA_MALE", "IA_FEMALE", "AA_MALE", "AA_FEMALE", "NA_MALE", "NA_FEMALE", "TOM_MALE", "TOM_FEMALE")
                .groupBy("STNAME", "CTYNAME")
                .sum();

        // You need to complete the rest
    }

    public static void main( String[] args )
    {
       // fill your code
    }
}
