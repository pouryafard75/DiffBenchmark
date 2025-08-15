package diffQ;

import benchmark.data.diffcase.QCase;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.utils.Pair;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.*;

public class MyPadFilterer implements PadFilterer {
    final DiffSimilarityCalculator similarityCalculator = new MappingRatioDiffSimilarityCalculator();
    private final QCase qcase;
    int topN = 2; // Number of top diffs to return

    public MyPadFilterer(QCase qcase) {
        this.qcase = qcase;
    }

    /**
     * Filters the provided ProjectASTDiffs based on the distance between diffs from two tools.
     * It sorts the diffs by their distance and returns a map of the top N diffs.
     *
     * @param pads A map of tool names to their corresponding ProjectASTDiffs.
     * @return A filtered map of ProjectASTDiffs sorted by distance.
     */
    @Override
    public Map<String, ProjectASTDiff> filter(Map<String, ProjectASTDiff> pads) {
        PriorityQueue<DiffDistEntry> diffDistEntries = sortByDist(pads);
        String[] keys = pads.keySet().toArray(new String[0]);
        String tool1 = keys[0];
        String tool2 = keys[1];
        ProjectASTDiff pad1 = pads.get(tool1);
        ProjectASTDiff pad2 = pads.get(tool2);
        ProjectASTDiff filteredPad1 = new ProjectASTDiff(pad1.getFileContentsBefore(), pad1.getFileContentsAfter());
        ProjectASTDiff filteredPad2 = new ProjectASTDiff(pad2.getFileContentsBefore(), pad2.getFileContentsAfter());
        filteredPad1.setMetaInfo(pad1.getMetaInfo());
        filteredPad1.setRefactorings(List.of());
        filteredPad2.setMetaInfo(pad2.getMetaInfo());
        filteredPad2.setRefactorings(List.of());
        Map<String, ProjectASTDiff> filteredPads = new LinkedHashMap<>();
        filteredPads.put(tool1, filteredPad1);
        filteredPads.put(tool2, filteredPad2);
        //System.out.println("Sorting by distance, found " + diffDistEntries.size() + " entries.");
        //We will only keep the top N diffs (while having no-interfilers)
        //We keep the top N diffs that have no inter-file mappings
        int count = 0;
        int skipCount = 0;

        while (!diffDistEntries.isEmpty() && count < topN) {
            DiffDistEntry entry = diffDistEntries.poll();

            // Special case: skip first 5 for PR 71
            if (qcase.getID().equals("https://github.com/JabRef/jabref/pull/71")) {
                if (skipCount < 5) {
                    skipCount++;
                    continue;
                }
            }

            if (entry.sim == 1) {
                // If the similarity is 1, we skip this entry as it means the diffs are identical
                continue;
            }

            Pair<ASTDiff, ASTDiff> diffPair = entry.diffPair;
            Pair<Boolean, Boolean> interFileCheck = checkInterFilers(pads, diffPair);

            if (!interFileCheck.first && !interFileCheck.second) {
                count++;
                ASTDiff diff1 = diffPair.first;
                ASTDiff diff2 = diffPair.second;
                System.out.println("Adding diff: " + diff1.getSrcPath() + " -> " + diff1.getDstPath() +
                        " and " + diff2.getSrcPath() + " -> " + diff2.getDstPath() +
                        " with similarity: " + entry.sim);
                filteredPad1.addASTDiff(diff1);
                filteredPad2.addASTDiff(diff2);
            }
        }
        //If we have not found enough diffs, we will throw an exception
        if (count < topN) {
            System.err.println("Not enough diffs found with no inter-file mappings. Found: " + count + ", expected: " + topN);
        }
        return filteredPads;
    }

    private PriorityQueue<DiffDistEntry> sortByDist(Map<String, ProjectASTDiff> pads) {
        //There are certainly only two tools, assert and we there are more break
        if (pads.size() != 2) {
            throw new IllegalArgumentException("Expected exactly two tools, found: " + pads.size());
        }
        // The goal is to find select the diffs that has the most differences from tool1 to tool2
        String[] keys = pads.keySet().toArray(new String[0]);
        String tool1 = keys[0];
        String tool2 = keys[1];

        ProjectASTDiff pad1 = pads.get(tool1);
        ProjectASTDiff pad2 = pads.get(tool2);

        //Find the most different diffs between the two tools

        Iterator<ASTDiff> i1 = pad1.getDiffSet().iterator();
        Iterator<ASTDiff> i2 = pad2.getDiffSet().iterator();

        PriorityQueue<DiffDistEntry> distanceQueue = new PriorityQueue<>(
                Comparator.comparingDouble((DiffDistEntry entry) -> entry.sim)
//                        .reversed()
        );
        while (i1.hasNext() && i2.hasNext()) {
            ASTDiff diff1 = i1.next();
            ASTDiff diff2 = i2.next();
            //Just a simple check to ensure both are the same diffs
            if (diff1.getSrcPath().equals(diff2.getSrcPath()) &&
                diff1.getDstPath().equals(diff2.getDstPath())) {
                double distance = similarityCalculator.calculateSimilarity(diff1, diff2);
                distanceQueue.add(new DiffDistEntry(new Pair<>(diff1, diff2), distance));
            } else
                throw new RuntimeException("Something broken");
        }
        return distanceQueue;
    }


    private static Pair<Boolean, Boolean> checkInterFilers(Map<String, ProjectASTDiff> pads, Pair<ASTDiff, ASTDiff> diffPair) {
        //There are certainly only two tools, assert and we there are more break
        if (pads.size() != 2) {
            throw new IllegalArgumentException("Expected exactly two tools, found: " + pads.size());
        }
        ASTDiff diff1 = diffPair.first;
        ASTDiff diff2 = diffPair.second;
        if (diff1 == null || diff2 == null) throw new RuntimeException("Diff not found");
        return new Pair<>(
                doesContainInterFileMappings(diff1),
                doesContainInterFileMappings(diff2)
        );
    }

    private static Boolean doesContainInterFileMappings(ASTDiff diff) {
        //We check through the edit script and see if there are any inter-file mappings
        for (Action action : diff.editScript) {
            if (action instanceof MoveOut || action instanceof MoveIn)
                return true; // Found an inter-file mapping
        }
        return false; // No inter-file mappings found
    }

    static class DiffDistEntry {
        Pair<ASTDiff, ASTDiff> diffPair;
        double sim;

        public DiffDistEntry(Pair<ASTDiff, ASTDiff> diffPair, double sim) {
            this.diffPair = diffPair;
            this.sim = sim;
        }
    }
}

