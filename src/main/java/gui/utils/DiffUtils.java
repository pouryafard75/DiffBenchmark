package gui.utils;

import at.aau.softwaredynamics.gen.NodeType;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;

import com.github.gumtreediff.tree.DefaultTree;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.matchers.OptimizedVersions;
import shaded.com.github.gumtreediff.tree.ITree;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.tree.Tree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.io.File;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-02-27 11:16 p.m. */
public class DiffUtils {
    public static int missed = 0;
    private static int cc = 0;
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
        Set<com.github.gumtreediff.matchers.Mapping> convert = convert(m.getMappingSet(), srcContent, dstContent);
        return convert;
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
//        Tree result = TreeUtilFunctions.getTreeBetweenPositions(inpTree,input.getPos(),input.getEndPos());
        List<Tree> ret = getTreesExactPosition(inpTree,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() > 1)
        {
            String replaced = ASTNode.nodeClassForType(input.getType()).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
            for (Tree tree : ret) {
                if (tree.getType().name.equals(replaced))
                {
                    result = tree;
                    break;
                }
            }
            if (result == null)
                System.out.println();
        }
        else{
            result = ret.get(0);
        }
        return result;
    }
    public static Tree changeAPI(Tree target, ITree input) throws Exception {
        List<Tree> ret = getTreesExactPosition(target,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() > 1)
        {
            String replaced = ASTNode.nodeClassForType(input.getType()).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
            for (Tree tree : ret) {
                if (tree.getType().name.equals(replaced))
                {
                    result = tree;
                    break;
                }
            }
            if (result == null)
                throw new Exception();
        }
        else{
            result = ret.get(0);
        }

        target.getTreesBetweenPositions(input.getPos(),input.getEndPos());
        if (result != null)
            cc += 1;
        DefaultTree clone = TreeUtilFunctions.makeDefaultTree(result);
        for (ITree child : input.getChildren()) {
            clone.addChild(changeAPI(target,child));
        }
        return clone;
    }
    public static Tree myMethod(Tree target, String content) throws Exception {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcITree = gen.generateFromString(content).getRoot();
        Tree cloned = changeAPI(target, srcITree);
        System.out.println();
        return cloned;
    }


    public static List<Tree> getTreesExactPosition(Tree tree, int position, int endPosition) {
        List<Tree> ret = new ArrayList<>();
        for (Tree t: tree.preOrder()) {
            if (t.getPos() == position && t.getEndPos() == endPosition)
                ret.add(t);
        }
        return ret;
    }

}
