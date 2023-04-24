package benchmark.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class Configuration {
    private static final String OUTPUT_FOLDER = "output/";
    public static final String GOD_PATH = OUTPUT_FOLDER + "GOD/";
    public static final String RMD_PATH =  OUTPUT_FOLDER + "RMD/";
    public static final String GTG_PATH = OUTPUT_FOLDER + "GTG/";
    public static final String GTS_PATH = OUTPUT_FOLDER + "GTS/";
    public static final String IJM_PATH = OUTPUT_FOLDER + "IJM/";
    public static final String MTD_PATH = OUTPUT_FOLDER + "MTD/";
    public static final String casesPath = "cases.json";
    public static final String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/tmp1"; //TODO: Modify this based on your local path
    public static final String STATS_CSV = "stats.csv";
    public static Map<String, String> toolPathMap = new LinkedHashMap<>();

    static{
        populateTools();
    }
    private static void populateTools() {
//        toolPathMap.put("GOD", GOD_PATH);
        toolPathMap.put("RMD", RMD_PATH);
        toolPathMap.put("GTG", GTG_PATH);
        toolPathMap.put("GTS", GTS_PATH);
//        toolPathMap.put("IJM", IJM_PATH);
//        toolPathMap.put("MTD", MTD_PATH);
//        System.out.println("Finished populating tools...");
    }
}
