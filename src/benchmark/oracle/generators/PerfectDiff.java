package benchmark.oracle.generators;

import benchmark.utils.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Created by pourya on 2023-07-25 9:54 p.m. */
public class PerfectDiff {
    private static final String JSON_SUFFIX = ".json";
    private final Set<ASTDiff> rm_diffSet;
    private final String repo;
    private final String commit;
    private ASTDiff rm_astDiff;
    private Map<String, TreeContext> ptc;
    private Map<String, TreeContext> ctc;

    public PerfectDiff(String srcPath, Set<ASTDiff> rm_astDiff, String repo, String commit) {
        this.rm_diffSet = rm_astDiff;
        this.repo = repo;
        this.commit = commit;
        rm_astDiff.forEach(astDiff -> {
            if (astDiff.getSrcPath().equals(srcPath))
            {
                this.rm_astDiff = astDiff;
            }
        });
    }
    public ASTDiff makeASTDiff() throws Exception {
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(rm_astDiff.src.getRoot(),rm_astDiff.dst.getRoot());
        ptc = new HashMap<>();
        ctc = new HashMap<>();
        for (ASTDiff diff : rm_diffSet) {
            ptc.put(diff.getSrcPath(), diff.src);
            ctc.put(diff.getDstPath(), diff.dst);
        }
        populateMappingsFromJson(mappings);
        ASTDiff astDiff = new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), rm_astDiff.src, rm_astDiff.dst, mappings);
        astDiff.computeEditScript(ptc, ctc);
        return astDiff;
    }

    private void populateMappingsFromJson(ExtendedMultiMappingStore mappings) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<MappingExportModel> perfectMappings = mapper.readValue(new File(getFileNameBasedOnAST()), new TypeReference<List<MappingExportModel>>(){});
        Tree src = rm_astDiff.src.getRoot();
        Tree dst = rm_astDiff.dst.getRoot();
        for (MappingExportModel perfectMapping : perfectMappings) {
            Tree srcNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(src, perfectMapping.getFirstPos(), perfectMapping.getFirstEndPos(),perfectMapping.getFirstType(), perfectMapping.getFirstParentType());
            Tree dstNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(dst, perfectMapping.getSecondPos(), perfectMapping.getSecondEndPos(),perfectMapping.getSecondType(), perfectMapping.getSecondParentType());
            if (srcNode == null || dstNode == null)
            {
                if (srcNode != null || dstNode != null)
                {
                    //inter-file mappings
                    if (srcNode == null) {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ptc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), perfectMapping.getFirstPos(), perfectMapping.getFirstEndPos(), perfectMapping.getFirstType(), perfectMapping.getFirstParentType());
                            if (treeBetweenPositionsSecure != null) {
                                srcNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                    else {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ctc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), perfectMapping.getSecondPos(), perfectMapping.getSecondEndPos(), perfectMapping.getSecondType(), perfectMapping.getSecondParentType());
                            if (treeBetweenPositionsSecure != null) {
                                dstNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                }
                else{
                    throw new Exception("bug bug bug");
                }
            }
            mappings.addMapping(srcNode, dstNode);
        }
    }

    private String getFileNameBasedOnAST() {
        String p = Configuration.perfectDiffDir + "/" + repoToFolder(repo) + "/" + commit + "/" + getFileNameFromSrcDiff(rm_astDiff.getSrcPath());
        File file = new File(p);

        return p;
    }
    public static String repoToFolder(String repo) {
        String folderName = repo.replace("https://github.com/", "").replace(".git","");
        return folderName.replace("/","_") + "/";
    }
    public static String getFileNameFromSrcDiff(String astSrcName)
    {
        String exportName1 = astSrcName.replace("/",".").replace(".java","");
        return exportName1 + JSON_SUFFIX;
    }
}
