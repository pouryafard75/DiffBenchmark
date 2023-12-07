package benchmark.metrics.computers.refactoring;

import benchmark.metrics.computers.filters.QueryBasedHumanReadableDiffFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.FileDiffComparisonResult;
import benchmark.metrics.models.RefactoringSpecificComparisonResult;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import gr.uom.java.xmi.diff.*;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.*;
import static rq.Utils.mergeStats;

/* Created by pourya on 2023-11-29 9:01 a.m. */
public class RefactoringWiseBenchmarkComputer extends VanillaBenchmarkComputer {
    final Set<RefactoringType> acceptedRefactoringTypes;
    private final static Logger logger = LoggerFactory.getLogger(RefactoringWiseBenchmarkComputer.class);
    private final Set<Class<? extends Refactoring>> forbiddenTypes;

    public RefactoringWiseBenchmarkComputer(Configuration configuration, Set<RefactoringType> acceptedRefactoringTypes) {
        super(configuration);
        this.acceptedRefactoringTypes = acceptedRefactoringTypes;
        forbiddenTypes = Set.of(
                MoveOperationRefactoring.class,
                MoveAttributeRefactoring.class,
                MoveClassRefactoring.class,
                MoveAndRenameAttributeRefactoring.class,
                MoveAndRenameClassRefactoring.class,
                MoveSourceFolderRefactoring.class,
                ChangeAttributeAccessModifierRefactoring.class //All the cases for this refactoring caused because of the move file refactorings (has been verified)
        );
    }

    @Override
    public Collection<? extends BaseDiffComparisonResult> compute() throws IOException {
        //todo:
        Collection<RefactoringSpecificComparisonResult> result = new ArrayList<>();
        for (CaseInfo info : this.getConfiguration().getAllCases()) {
            result.addAll(compute(info));
        }
        return result;
    }

    @Override
    public Collection<RefactoringSpecificComparisonResult> compute(CaseInfo info) throws IOException {
        logger.info("Computing for " + info.getRepo() + " " + info.getCommit());
        Collection<RefactoringSpecificComparisonResult> result = new ArrayList<>();
        ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
        for (Refactoring refactoring : projectASTDiff.getRefactorings()) {
            if (!acceptedRefactoringTypes.contains(refactoring.getRefactoringType())) continue;
            if (result.stream().map(RefactoringSpecificComparisonResult::getRefactoring).anyMatch(ref -> makeCodeRangesAssociatedWithRefactoring(ref).equals(makeCodeRangesAssociatedWithRefactoring(refactoring)))) continue;
//            if (result.stream().map(RefactoringSpecificComparisonResult::getRefactoring).anyMatch(refactoring1 -> refactoring1.toString().equals(refactoring.toString()))) continue;
            boolean moveRelated = false;
            for (Class<?> clazz : forbiddenTypes) {
                if (clazz.isInstance(refactoring)) {
                    moveRelated = true;
                    break;
                }
            }
            if (moveRelated) {
                logger.info(" Skipping... " + refactoring.getRefactoringType() + " , Since it contains inter-file mappings");
                break;
            }
            RefactoringSpecificComparisonResult refactoringSpecificComparisonResult = new RefactoringSpecificComparisonResult(info, refactoring);
            RefactoringRanges ranges = makeCodeRangesAssociatedWithRefactoring(refactoring);
            populateStats(refactoringSpecificComparisonResult, ranges);
            if (refactoringSpecificComparisonResult.getDiffStatsList().isEmpty()) continue;
//            refactoringSpecificComparisonResult.getDiffStatsList()

            result.add(refactoringSpecificComparisonResult);
        }
        return result;
    }

    private void populateStats(RefactoringSpecificComparisonResult refactoringSpecificComparisonResult, RefactoringRanges ranges) throws IOException {
        Collection<CodeRange> leftRanges = ranges.leftRanges();
        Collection<CodeRange> rightRanges = ranges.rightRanges();
        Collection<BaseDiffComparisonResult> benchmarkStats = new ArrayList<>();
        CaseInfo info = refactoringSpecificComparisonResult.getCaseInfo();
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(getConfiguration().getOutputFolder() + folderPath  + "/");
        //Since the HRD folders, are named based on the srcFile (leftFile), There is nothing to process regarding the rightFileName;
        if (leftRanges == null || leftRanges.isEmpty() || rightRanges == null || rightRanges.isEmpty()) {
            logger.error("Skipping " + refactoringSpecificComparisonResult.getRefactoring()  + " @ " +  refactoringSpecificComparisonResult.getCaseInfo().makeURL() + " because it does not have left/right ranges!");
            return;
        }
        String leftFile = leftRanges.iterator().next().getFilePath();
        Path dirPath = dir.resolve(fileNameAsFolder(leftFile));
        if (!new File(String.valueOf(dirPath)).exists())
        {
            logger.error("Skipping " + refactoringSpecificComparisonResult.getRefactoring() + " @ " + refactoringSpecificComparisonResult.getCaseInfo().makeURL() + " " + dirPath + " because it does not exist!");
            return;
        }
        BaseDiffComparisonResult baseDiffComparisonResult = new FileDiffComparisonResult(info, dirPath.getFileName().toString());
        populateComparisonResults(baseDiffComparisonResult, dirPath, new QueryBasedHumanReadableDiffFilter(leftRanges, rightRanges));
        benchmarkStats.add(baseDiffComparisonResult);
        mergeStats(refactoringSpecificComparisonResult, benchmarkStats);
    }

    private static RefactoringRanges makeCodeRangesAssociatedWithRefactoring(Refactoring refactoring) {
        Collection<CodeRange> leftRanges = new HashSet<>(refactoring.leftSide());
        Collection<CodeRange> rightRanges = new HashSet<>(refactoring.rightSide());
        if (refactoring instanceof ReferenceBasedRefactoring){
            ReferenceBasedRefactoring referenceBasedRefactoring = (ReferenceBasedRefactoring) refactoring;
            Set<AbstractCodeMapping> references = referenceBasedRefactoring.getReferences();
            references.forEach(abstractCodeMapping -> {
                leftRanges.add(abstractCodeMapping.getFragment1().codeRange());
                rightRanges.add(abstractCodeMapping.getFragment2().codeRange());
            });
        }
        return new RefactoringRanges(leftRanges, rightRanges);
    }
    record RefactoringRanges(Collection<CodeRange> leftRanges, Collection<CodeRange> rightRanges) {
        boolean involveMultipleFiles() {
            return this.leftRanges().stream().map(CodeRange::getFilePath).distinct().count() > 1
                    ||
                    this.rightRanges().stream().map(CodeRange::getFilePath).distinct().count() > 1;
        }

        public boolean involveMovedSubtrees() {
            // TODO:
            // Here the purpose is to filter out the scenarios in which the file has been renamed.
            // As a result we do have different names but within the same file
            if (this.involveMultipleFiles()) return true;
            String leftFileName = this.leftRanges().stream().map(CodeRange::getFilePath).collect(Collectors.toSet()).iterator().next();
            String rightFileName = this.rightRanges().stream().map(CodeRange::getFilePath).collect(Collectors.toSet()).iterator().next();
            return !rightFileName.equals(leftFileName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RefactoringRanges that = (RefactoringRanges) o;
            return Objects.equals(leftRanges, that.leftRanges) && Objects.equals(rightRanges, that.rightRanges);
        }

        @Override
        public int hashCode() {
            return Objects.hash(leftRanges, rightRanges);
        }
    }
}
