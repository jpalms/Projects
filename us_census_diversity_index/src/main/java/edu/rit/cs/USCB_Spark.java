package edu.rit.cs;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.USCBPopulationStat;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

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
            // create threads to have maps for each year
                // user provided map code is state or year
                // Reduce code is the value
        //Config.calcDivIndex();

         Dataset<Row> WA;
         WA = ds2.select("sum(" + Config.WA_MALE + ")", "sum(" + Config.WA_FEMALE + ")");

        Dataset<Row> BA;
        BA = ds2.select("sum(" + Config.BA_MALE + ")", "sum(" + Config.BA_FEMALE + ")");

        Dataset<Row> IA;
        IA = ds2.select("sum(" + Config.IA_MALE + ")", "sum(" + Config.IA_FEMALE + ")");

        Dataset<Row> AA;
        AA = ds2.select("sum(" + Config.AA_MALE + ")", "sum(" + Config.AA_FEMALE + ")");

        Dataset<Row> NA;
        NA = ds2.select("sum(" + Config.NA_MALE + ")", "sum(" + Config.NA_FEMALE + ")");

        Dataset<Row> TOM;
        TOM = ds2.select("sum(" + Config.TOM_MALE + ")", "sum(" + Config.TOM_FEMALE + ")");

        ArrayList<Dataset<Row>> list = new ArrayList<>();
        list.add(WA);
        list.add(BA);
        list.add(IA);
        list.add(AA);
        list.add(NA);
        list.add(TOM);

        String str = "";
        ds2.foreach((ForeachFunction<Row>) row -> System.out.println(Config.calcDivIndex(row)));
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
