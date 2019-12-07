package edu.rit.cs;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.USCBPopulationStat;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;

import org.apache.spark.sql.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * U.S. Census Diversity Index
 * Based on https://www2.census.gov/programs-surveys/popest/datasets/2010-2017/counties/asrh/cc-est2017-alldata.csv
 */
public class USCB_Spark
{
    public static final String OutputDirectory = "us_census_diversity_index/dataset/USCB-outputs";
    public static final String DatasetFile = "dataset/USCB.csv";

    /**
     *
     * @param directoryToBeDeleted - directory which gets deleted
     * @return - true if deleted, false is directory doesn't exist
     */
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null)
            for (File file : allContents)
                deleteDirectory(file);
        return directoryToBeDeleted.delete();
    }

    /**
     * Manipulates the datables to determine the Diversity Index for all ages and year in each state, county
     * @param spark - spark session
     */
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
        System.out.println("Show ds1");
        ds1.show();

        // filter and sum data across multiple years for all year groups
        Dataset ds2 = ds1.filter(Config.AGEGRP)
                .select(Config.STATE, Config.CITY,
                "WA_MALE", "WA_FEMALE", "BA_MALE", "BA_FEMALE", "IA_MALE", "IA_FEMALE", "AA_MALE", "AA_FEMALE", "NA_MALE", "NA_FEMALE", "TOM_MALE", "TOM_FEMALE")
                .groupBy("STNAME", "CTYNAME")
                .sum().orderBy(Config.STATE, Config.CITY);

        // show table of all ages
        System.out.println("Show ds2");
        ds2.show();

        // combine gender
        Dataset ds3 = ds2
                        .withColumn(Config.WA, ds2.col(Config.WA_MALE).plus(ds2.col(Config.WA_FEMALE))).drop(Config.WA_MALE).drop(Config.WA_FEMALE)
                        .withColumn(Config.BA, ds2.col(Config.BA_MALE).plus(ds2.col(Config.BA_FEMALE))).drop(Config.BA_MALE).drop(Config.BA_FEMALE)
                        .withColumn(Config.IA, ds2.col(Config.IA_MALE).plus(ds2.col(Config.IA_FEMALE))).drop(Config.IA_MALE).drop(Config.IA_FEMALE)
                        .withColumn(Config.AA, ds2.col(Config.AA_MALE).plus(ds2.col(Config.AA_FEMALE))).drop(Config.AA_MALE).drop(Config.AA_FEMALE)
                        .withColumn(Config.NA, ds2.col(Config.NA_MALE).plus(ds2.col(Config.NA_FEMALE))).drop(Config.NA_MALE).drop(Config.NA_FEMALE)
                        .withColumn(Config.TOM, ds2.col(Config.TOM_MALE).plus(ds2.col(Config.TOM_FEMALE))).drop(Config.TOM_MALE).drop(Config.TOM_FEMALE);

        // show table of all ages and race's genders are combined
        System.out.println("Show ds3");
        ds3.show();

        // calculate total population
        ds3 = ds3.withColumn(Config.TOTAL_POP, ds3.col(Config.WA).plus(ds3.col(Config.BA)).plus(ds3.col(Config.IA).plus(ds3.col(Config.AA).plus(ds3.col(Config.NA).plus(ds3.col(Config.TOM))))));

        // List of Column names
        String [] arr = ds3.columns();

        // calculates the summation in the Diversity Index formula
        for (int i = 0; i < Config.NUM_RACES; i++) {
            String col = arr[i + 2];
            if(i == 0)
                ds3 = ds3.withColumn(Config.SIGMA, ds3.col(col).multiply(ds3.col(Config.TOTAL_POP).minus(ds3.col(col)))).drop(col);
            else
                ds3 = ds3.withColumn(Config.SIGMA, ds3.col(Config.SIGMA).plus(ds3.col(col).multiply(ds3.col(Config.TOTAL_POP).minus(ds3.col(col))))).drop(col);
        }

        // divides the summation by total population squared to get the Diversity Index
        ds3 = ds3.withColumn(Config.DIV, ds3.col(Config.SIGMA).divide(ds3.col(Config.TOTAL_POP).multiply(ds3.col(Config.TOTAL_POP)))).drop(Config.TOTAL_POP, Config.SIGMA);

        // show diversity index
        ds3.show();

        // repartition with minimal shuffling
        JavaRDD javaRDD = ds3.toJavaRDD().coalesce(1, true);

        // empty directory
        File file = new File(OutputDirectory);
        deleteDirectory(file);

        // save table results in the empty directory
        javaRDD.saveAsTextFile(OutputDirectory);

        // rename the output file
        try {
            Files.move(Paths.get(OutputDirectory + "/part-00000"),
                    Paths.get(OutputDirectory + "/USCB-outputs.txt"));
        }catch(IOException e){
            System.err.println("File Not Found");
        }
    }

    public static void main( String[] args )
    {
       // sets path to hadoop
        System.setProperty("hadoop.home.dir", "C:\\winutils\\");

        // set ups up data to be separated into 4 partitions
        SparkConf sparkConf = new SparkConf();
        sparkConf.set("spark.master", "local[4]");
        sparkConf.setAppName("Census Report");

        // sets up spark
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

        uscb(sparkSession);

        // closes spark session
        sparkSession.close();
    }
}
