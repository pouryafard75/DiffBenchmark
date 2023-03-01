package gui.utils;

import at.aau.softwaredynamics.gen.NodeType;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;

import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.matchers.OptimizedVersions;
import shaded.com.github.gumtreediff.tree.ITree;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-02-27 11:16 p.m. */
public class DiffUtils {
    public static int missed = 0;
    public static void main(String[] args) throws IOException {
        String srcPath = "/Users/pourya/IdeaProjects/TestCases/v1/DistributedCacheStream.java";
        String dstPath = "/Users/pourya/IdeaProjects/TestCases/v2/DistributedCacheStream.java";
        String srcContent = FileUtils.readFileToString(new File(srcPath),"utf-8");
        String dstContent = FileUtils.readFileToString(new File(dstPath),"utf-8");
        Set<com.github.gumtreediff.matchers.Mapping> mappings = IJMDiff(srcContent, dstContent);
        System.out.println();

    }
    public static Set<com.github.gumtreediff.matchers.Mapping> IJMDiff (String srcContent, String dstContent) throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcTC = gen.generateFromString(srcContent).getRoot();
        ITree dstTC = gen.generateFromString(dstContent).getRoot();
        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher_V2.class).createMatcher(srcTC, dstTC);
        m.match();
        return convert(m.getMappingSet(),srcContent,dstContent);
    }
    public static Set<com.github.gumtreediff.matchers.Mapping> MTDiff (String srcContent, String dstContent) throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcTC = gen.generateFromString(srcContent).getRoot();
        ITree dstTC = gen.generateFromString(dstContent).getRoot();
        Matcher m = new MatcherFactory(OptimizedVersions.MtDiff.class).createMatcher(srcTC, dstTC);
        m.match();
        return convert(m.getMappingSet(),srcContent,dstContent);
    }
    public static Set<com.github.gumtreediff.matchers.Mapping> convert(Set<Mapping> mappingSet, String srcContent, String dstContent) throws IOException {
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContent).getRoot(); // instantiates and applies the JDT generator
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContent).getRoot(); // instantiates and applies the JDT generator
        Set<com.github.gumtreediff.matchers.Mapping> output = new LinkedHashSet<>();
        for (Mapping mapping : mappingSet) {
            Tree first  = convertITreeToTree(srcTree, mapping.first);
            Tree second  = convertITreeToTree(dstTree, mapping.second);
            if (first == null || second == null)
            {
                System.out.println("Missed");
                missed += 1;
                continue;
            }
            output.add(new com.github.gumtreediff.matchers.Mapping(first,second));
        }
        return output;
    }

    private static Tree convertITreeToTree(Tree inpTree, ITree input) {
        Tree result = TreeUtilFunctions.getTreeBetweenPositions(inpTree,input.getPos(),input.getEndPos());
        if (result == null)
            System.out.println();
        return result;
    }
}
