package benchmark.gui.drivers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.srcml.SrcmlCppTreeGenerator;
import gr.uom.java.xmi.UMLModel;
import gui.webdiff.WebDiff;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.matchers.ProjectASTDiffer;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

/* Created by pourya on 2025-02-26*/
public class CompareAny {
    public static void main(String[] args) throws Exception {
        String parentPath = "/Users/pourya/IdeaProjects/DiffBenchmark/src/test/resources/ExtractMethod/Cpp/current/OrderProcessor.cpp";
        String currentPath = "/Users/pourya/IdeaProjects/DiffBenchmark/src/test/resources/ExtractMethod/Cpp/parent/OrderProcessor.cpp";
        String parentJson = parentPath + ".json";
        String currentJson = currentPath + ".json";

        makeJson(parentPath, parentJson);
        makeJson(currentPath, currentJson);
        ProjectASTDiff projectASTDiff = diffByJson(parentPath, currentPath, parentJson, currentJson);
        WebDiff webDiff = new WebDiff(projectASTDiff);
        webDiff.run();

    }

    static void makeJson(String input, String output) throws IOException {
        Runtime.getRuntime().exec(String.format("cap %s %s", input, output));
    }
    public static ProjectASTDiff diffByJson(String parentActualPath, String currentActualPath, String parentJsonPath, String currentJsonPath) throws Exception
    {
        String parentContent = FileUtils.readFileToString(new File(parentActualPath));
        String currentContent = FileUtils.readFileToString(new File(currentActualPath));
        ObjectMapper objectMapper = new ObjectMapper();
        UMLModel current = objectMapper.readValue(new File(currentJsonPath), UMLModel.class);
        UMLModel parent = objectMapper.readValue(new File(parentJsonPath), UMLModel.class);
        TreeGenerator gen  = new SrcmlCppTreeGenerator();
//        parent.setTreeContextMap(new LinkedHashMap<>(){{this.put(parentActualPath, gen.generateFrom().string(parentContent));}});
//        current.setTreeContextMap(new LinkedHashMap<>(){{this.put(currentActualPath, gen.generateFrom().string(currentContent));}});
        ProjectASTDiffer differ = new ProjectASTDiffer(parent.diff(current),
                new LinkedHashMap<>(){{this.put(parentActualPath, parentContent);}},
                new LinkedHashMap<>(){{this.put(currentActualPath, currentContent);}}
        );
        return differ.getProjectASTDiff();
    }
}
