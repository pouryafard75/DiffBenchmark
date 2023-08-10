package benchmark.oracle.generators;

import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.util.List;

/* Created by pourya on 2023-07-25 9:54 p.m. */
public class PerfectDiff {
    private static final String JSON_SUFFIX = ".json";
    private final ASTDiff rm_astDiff;
    private final String repo;
    private final String commit;

    public PerfectDiff(ASTDiff rm_astDiff, String repo, String commit) {
        this.rm_astDiff = rm_astDiff;
        this.repo = repo;
        this.commit = commit;
    }
    public ASTDiff makeASTDiff() throws Exception {
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(rm_astDiff.src.getRoot(),rm_astDiff.dst.getRoot());
        populateMappingsFromJson(mappings);
        ASTDiff astDiff = new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), rm_astDiff.src, rm_astDiff.dst, mappings);
        astDiff.setSrcContents(this.rm_astDiff.getSrcContents());
        astDiff.setDstContents(this.rm_astDiff.getDstContents());
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
                }
                else{
                    throw new Exception("bug bug bug");
                }
            }
            else{
                mappings.addMapping(srcNode, dstNode);
            }
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
