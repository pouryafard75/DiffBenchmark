package gui.utils;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.matchers.OptimizedVersions;
import shaded.com.github.gumtreediff.tree.ITree;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-02-27 11:16 p.m. */
public class DiffUtils {
    public static Set<Mapping> IJMDiff (String srcContent, String dstContent) throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcTC = gen.generateFromString(srcContent).getRoot();
        ITree dstTC = gen.generateFromString(dstContent).getRoot();
        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher_V2.class).createMatcher(srcTC, dstTC);
        m.match();
        return m.getMappingSet();
    }
    public static Set<Mapping> MTDiff (String srcContent, String dstContent) throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcTC = gen.generateFromString(srcContent).getRoot();
        ITree dstTC = gen.generateFromString(dstContent).getRoot();
        Matcher m = new MatcherFactory(OptimizedVersions.MtDiff.class).createMatcher(srcTC, dstTC);
        m.match();
        return m.getMappingSet();
    }
}
