package edu.rit.cs;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Partition;
import edu.rit.cs.model.USCBPopulationStat;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.*;
import scala.Function1;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
        int year = 2010;
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
        Dataset ds2 = ds1.filter("AGEGRP = " + Config.AGEGRP)
                .select("STNAME", "CTYNAME",
                "WA_MALE", "WA_FEMALE", "BA_MALE", "BA_FEMALE", "IA_MALE", "IA_FEMALE", "AA_MALE", "AA_FEMALE", "NA_MALE", "NA_FEMALE", "TOM_MALE", "TOM_FEMALE")
                .groupBy("STNAME", "CTYNAME")
                .sum().orderBy("STNAME", "CTYNAME");

        // You need to complete the rest

        System.out.println("Show ds2");
        ds2.show();

        Dataset ds3 = ds2
                        .withColumn(Config.WA, ds2.col(Config.WA_MALE).plus(ds2.col(Config.WA_FEMALE))).drop(Config.WA_MALE).drop(Config.WA_FEMALE)
                        .withColumn(Config.BA, ds2.col(Config.BA_MALE).plus(ds2.col(Config.BA_FEMALE))).drop(Config.BA_MALE).drop(Config.BA_FEMALE)
                        .withColumn(Config.IA, ds2.col(Config.IA_MALE).plus(ds2.col(Config.IA_FEMALE))).drop(Config.IA_MALE).drop(Config.IA_FEMALE)
                        .withColumn(Config.AA, ds2.col(Config.AA_MALE).plus(ds2.col(Config.AA_FEMALE))).drop(Config.AA_MALE).drop(Config.AA_FEMALE)
                        .withColumn(Config.NA, ds2.col(Config.NA_MALE).plus(ds2.col(Config.NA_FEMALE))).drop(Config.NA_MALE).drop(Config.NA_FEMALE)
                        .withColumn(Config.TOM, ds2.col(Config.TOM_MALE).plus(ds2.col(Config.TOM_FEMALE))).drop(Config.TOM_MALE).drop(Config.TOM_FEMALE);

        System.out.println("Show ds3");
        ds3.show();

        ds3 = ds3.withColumn(Config.TOTAL_POP, ds3.col(Config.WA).plus(ds3.col(Config.BA)).plus(ds3.col(Config.IA).plus(ds3.col(Config.AA).plus(ds3.col(Config.NA).plus(ds3.col(Config.TOM))))));


        System.out.println("Show ds3");
        ds3.show();
        //Dataset

    }

    public static void main( String[] args )
    {
       // fill your code

        SparkConf sparkConf = new SparkConf();
        sparkConf.set("spark.master", "local[4]");
        sparkConf.setAppName("Census Report");

        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

        System.out.println("get ready");

        uscb(sparkSession);

        sparkSession.close();
    }
}
