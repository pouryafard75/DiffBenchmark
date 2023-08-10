package benchmark.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class Configuration {
    private static final String OUTPUT_FOLDER = "output/";
    public static final String casesPath = "snapshots/2023-08-01/refactoringOracle/merged_cases.json";
    public static String perfectDiffDir = "/Users/pourya/IdeaProjects/RM-ASTDiff/src-test/data/astDiff/commits";
    public static final String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/tmp1/"; //TODO: Modify this based on your local path
    public static final String STATS_CSV = "stats.csv";
    public static Map<String, String> toolPathMap = new LinkedHashMap<>();

    public static final String GOD_PATH = OUTPUT_FOLDER + "GOD/";
    public static final String RMD_PATH =  OUTPUT_FOLDER + "RMD/";
    public static final String GTG_PATH = OUTPUT_FOLDER + "GTG/";
    public static final String GTS_PATH = OUTPUT_FOLDER + "GTS/";
    public static final String IJM_PATH = OUTPUT_FOLDER + "IJM/";
    public static final String MTD_PATH = OUTPUT_FOLDER + "MTD/";


    static{
        toolPathMap.put("RMD", RMD_PATH);
        populateTools();
    }
    private static void populateTools() {
//        toolPathMap.put("GOD", GOD_PATH);
        toolPathMap.put("GTG", GTG_PATH);
        toolPathMap.put("GTS", GTS_PATH);
//        toolPathMap.put("IJM", IJM_PATH);
//        toolPathMap.put("MTD", MTD_PATH);
//        System.out.println("Finished populating tools...");
    }
}
