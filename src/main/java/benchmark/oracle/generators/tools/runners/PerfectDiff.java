package benchmark.oracle.generators.tools.runners;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.ExportUtils;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.refactoringminer.astDiff.utils.ExportUtils.getFileNameFromSrcDiff;
import static org.refactoringminer.astDiff.utils.ExportUtils.repoToFolder;

/* Created by pourya on 2023-07-25 9:54 p.m. */
public class PerfectDiff extends ASTDiffConvertor{
    public static final String JSON_SUFFIX = ".json";
    private final Configuration configuration;
    private final String repo;
    private final String commit;

    public PerfectDiff(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo info, Configuration conf) {
        super(rm_astDiff.getSrcPath(), projectASTDiff);
        repo = info.getRepo();
        commit = info.getCommit();
        configuration = conf;
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        List<MappingExportModel> exportedMappings;
        try {
            exportedMappings = new ObjectMapper().readValue(new File(getFileNameBasedOnAST()), new TypeReference<List<MappingExportModel>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return exportedMappings;

    }

    private String getFileNameBasedOnAST() {
        return configuration.getPerfectDiffDir() + "/" + repoToFolder(repo) + "/" + commit + "/" + getFileNameFromSrcDiff(rm_astDiff.getSrcPath());
    }
}
