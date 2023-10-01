package benchmark.metrics;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.github.gumtreediff.matchers.Mapping;
import com.opencsv.CSVWriter;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static benchmark.oracle.generators.tools.models.ASTDiffTool.*;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-09-19 6:18 p.m. */
public class SemanticViolationCalculator {
    public static void main(String[] args) throws Exception {
        run();
    }
    public static void run() throws Exception {
        Configuration configuration = ConfigurationFactory.refOracle();
        ASTDiffTool[] experiment_tools = new ASTDiffTool[]{GTG,GTS,RMD};
//        ASTDiffTool[] experiment_tools = new ASTDiffTool[]{RMD};
//        ASTDiffTool[] experiment_tools = new ASTDiffTool[]{RMD};
        Map<ASTDiffTool, List<SemanticViolationRecord>> toolViolationMap = new LinkedHashMap<>();
        for (ASTDiffTool experimentTool : experiment_tools)
            toolViolationMap.put(experimentTool,new ArrayList<>());

        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Working on " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
                ASTDiff perfect = GOD.getFactory().getASTDiff(projectASTDiff, rm_astDiff, info, configuration);
                for (ASTDiffTool tool : experiment_tools) {
                    ASTDiff generated = tool.getFactory().getASTDiff(projectASTDiff, rm_astDiff, info, configuration);
                    Set<Mapping> violations = collectSemanticViolations(perfect, generated);
                    List<SemanticViolationRecord> toolViolationRecordList = toolViolationMap.get(tool);
                    populate(violations, toolViolationRecordList, info.makeURL());
                }
            }
        }
        for (Entry<ASTDiffTool, List<SemanticViolationRecord>> item : toolViolationMap.entrySet()) {
            ASTDiffTool tool = item.getKey();
            List<SemanticViolationRecord> semanticViolationRecords = item.getValue();
            writeRecordsToCSV(semanticViolationRecords, "sV-" + configuration.getName() + "-" + tool.name() +  ".csv");
        }
    }

    private static <K,V> void writeToFile(Map<K,V> dictionary, String filePath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // Iterate through the dictionary and write each entry to the file
            for (Entry<K, V> entry : dictionary.entrySet()) {
                fileWriter.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            System.out.println("Dictionary has been written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    private static void populate(Set<Mapping> violations, List<SemanticViolationRecord> toolViolationRecordList, String infoURL) {
        for (Mapping violation : violations) {
            String key = makeKey(violation);
            toolViolationRecordList.add(new SemanticViolationRecord(
                    key, violation.first.toString(), violation.second.toString(), infoURL)
            );
        }
    }

    private static String makeKey(Mapping violation) {
        String p1 = violation.first.getParent().getType().name;
        String p2 = violation.second.getParent().getType().name;
        String[] types = {p1, p2};
        Arrays.sort(types);
        return  types[0] + ":" + types[1];
    }

    private static Set<Mapping> collectSemanticViolations(ASTDiff perfect, ASTDiff generated) {
        Set<Mapping> ret = new LinkedHashSet<>();
        for (Mapping mapping : generated.getAllMappings()) {
            if (!semanticSensitiveTypes(mapping)) continue;
            if (mapping.first.getParent() == null || mapping.second.getParent() == null) continue;
            if (mapping.first.getParent().getType().name.equals(mapping.second.getParent().getType().name)) continue;
            if (contains(perfect,mapping)) continue;
            ret.add(mapping);
        }
        return ret;
    }

    private static boolean semanticSensitiveTypes(Mapping mapping) {
        if (mapping.first.getType().name.equals(Constants.SIMPLE_NAME))
        {
            if (!mapping.first.getLabel().equals(mapping.second.getLabel()))
                return true;
            else
                return false;
        }
        else if (mapping.first.getType().name.equals(Constants.BLOCK))
        {
            if (mapping.first.getParent().getType().name.equals(Constants.METHOD_DECLARATION)
                    & !mapping.second.getParent().getType().name.equals(Constants.METHOD_DECLARATION))
                return true;
            else if (!mapping.first.getParent().getType().name.equals(Constants.METHOD_DECLARATION)
                    & mapping.second.getParent().getType().name.equals(Constants.METHOD_DECLARATION))
                return true;
            else
                return false;
        }
        else if (mapping.first.getType().name.equals("SingleVariableDeclaration")) return true;
        return true;
    }

    private static boolean contains(ASTDiff perfect, Mapping mapping) {
        for (Mapping godMapping : perfect.getAllMappings()) {
            if (isEquivalent(godMapping,mapping)) return true;
        }
        return false;
    }

    private static boolean isEquivalent(Mapping godMapping, Mapping mapping) {
        return
        mapping.first.getPos() == godMapping.first.getPos()
        &&
        mapping.first.getEndPos() == godMapping.first.getEndPos()
        &&
        mapping.second.getPos() == godMapping.second.getPos()
        &&
        mapping.second.getEndPos() == godMapping.second.getEndPos()
        &&
        mapping.first.getType().name.equals(godMapping.first.getType().name)
        &&
        mapping.second.getType().name.equals(godMapping.second.getType().name)
        &&
        mapping.first.getLabel().equals(godMapping.first.getLabel())
        &&
        mapping.second.getLabel().equals(godMapping.second.getLabel());
    }
    public static void writeRecordsToCSV(List<SemanticViolationRecord> records, String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        String[] header = {"ParentTypesPair", "First", "Second", "URL"};
        csvWriter.writeNext(header);

        for (SemanticViolationRecord record : records) {
            String[] data = {
                    record.parentTypesPair,
                    record.first,
                    record.second,
                    record.url
            };
            csvWriter.writeNext(data);
        }
        csvWriter.close();
    }
    static class SemanticViolationRecord {
        String parentTypesPair;
        String first;
        String second;
        String url;
        public SemanticViolationRecord(String parentTypesPair, String first, String second, String url) {
            this.parentTypesPair = parentTypesPair;
            this.first = first;
            this.second = second;
            this.url = url;
        }
    }
}

