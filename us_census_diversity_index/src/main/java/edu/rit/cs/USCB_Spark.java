package edu.rit.cs;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Partition;
import edu.rit.cs.model.USCBPopulationStat;
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

    public static void uscb(SparkSession spark) throws SQLException {
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
        //ds1.show();

        // filter and sum data across multiple years for all year groups
        Dataset ds2 = ds1.filter("AGEGRP = " + Config.AGEGRP)
                .select("STNAME", "CTYNAME",
                "WA_MALE", "WA_FEMALE", "BA_MALE", "BA_FEMALE", "IA_MALE", "IA_FEMALE", "AA_MALE", "AA_FEMALE", "NA_MALE", "NA_FEMALE", "TOM_MALE", "TOM_FEMALE")
                .groupBy("STNAME", "CTYNAME")
                .sum().orderBy("STNAME", "CTYNAME");

        // You need to complete the rest
        System.out.println("Show ds2");
        //ds2.show();
            // create threads to have maps for each year
                // user provided map code is state or year
                // Reduce code is the value
        //Config.calcDivIndex();

        String str = "";
        JavaRDD javaRDD = ds2.javaRDD();
        javaRDD = javaRDD.coalesce(4, true);//javaRDD.repartition(4);
        ArrayList<Partition> list = new ArrayList<>();
        javaRDD.foreachPartition((VoidFunction<Iterator>) row -> {
            Partition partition = new Partition(row);
            partition.start();
            partition.join();
            System.out.println("thread");
        });
        //System.out.println(list.size());
        //ds2.foreach((ForeachFunction<Row>) row -> System.out.println(Config.calcDivIndex(row)));

    }

    public static void main( String[] args )
    {
       // fill your code

        SparkSession sparkSession = SparkSession.builder()
                                                .appName("Census Report")
                                                .config("spark.master", "local")
                                                .getOrCreate();
        System.out.println("get ready");
        try {
            uscb(sparkSession);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
