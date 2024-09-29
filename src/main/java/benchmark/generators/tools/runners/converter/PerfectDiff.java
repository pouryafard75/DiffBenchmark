package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.IExperiment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.refactoringminer.astDiff.utils.ExportUtils.getFileNameFromSrcDiff;
import static org.refactoringminer.astDiff.utils.ExportUtils.repoToFolder;

/* Created by pourya on 2023-07-25 9:54 p.m. */
public class PerfectDiff extends AbstractASTDiffProviderFromExportedMappings {

    public PerfectDiff(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) {
        super(projectASTDiff, input, info);
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        List<MappingExportModel> exportedMappings;
        try {
            exportedMappings = new ObjectMapper().readValue(new File(getFileNameBasedOnAST()), new TypeReference<List<MappingExportModel>>() {
            });
        } catch (IOException e) {
            throw new NoPerfectDiffException("No Perfect Diff found for " + info.getRepo() + " at " + info.getCommit() + " for " + input.getSrcPath() + " " + e.getMessage());
        }
        return exportedMappings;

    }

    private String getFileNameBasedOnAST() {
        return info.getDataset().getPerfectDirPath() + "/" + repoToFolder(info.getRepo()) + "/" + info.getCommit() + "/" + getFileNameFromSrcDiff(input.getSrcPath());
    }
}
