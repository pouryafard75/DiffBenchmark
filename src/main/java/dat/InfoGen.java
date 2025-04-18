package dat;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.github.gumtreediff.actions.model.Action;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.ArrayList;
import java.util.List;

public class InfoGen {
    private static class Info{
        String commit;
        String filePath;
        boolean multiMappings;
        boolean interFilers;
        public Info(String commit, String filePath, boolean multiMappings, boolean interFilers) {
            this.commit = commit;
            this.filePath = filePath;
            this.multiMappings = multiMappings;
            this.interFilers = interFilers;
        }
    }
    static IExperiment exp = ExperimentsEnum.LITERATURE_EXP;
    public static void main(String[] args) throws Exception {
        List<Info> infos = new ArrayList<>();
        for (IBenchmarkCase aCase : exp.getDataset().getCases()) {
            for (ASTDiff astDiff : aCase.getProjectASTDiff().getDiffSet()) {
                System.out.println("Working on " + aCase.getRepo() + " " + aCase.getCommit() + " " + astDiff.getSrcPath() + "");
                ASTDiff diff = ASTDiffToolEnum.GOD.diff(aCase, (x) -> astDiff);
                boolean hasMultiMappings = false;
                boolean hasInterFilers = false;
                if (!diff.getAllMappings().srcToDstMultis().isEmpty() || !diff.getAllMappings().dstToSrcMultis().isEmpty()) {
                    hasMultiMappings = true;
                }
                for (Action action : diff.editScript) {
                    if (action instanceof MoveIn || action instanceof MoveOut) {
                        hasInterFilers = true;
                        break;
                    }
                }
                infos.add(new Info(aCase.getCommit(), diff.getSrcPath(), hasMultiMappings, hasInterFilers));
            }
        }
        writeToCsv(infos);
    }

    private static void writeToCsv(List<Info> infos) {
        StringBuilder sb = new StringBuilder();
        sb.append("commit,file,multiMappings,interFilers\n");
        for (Info info : infos) {
            sb.append(info.commit).append(",").append(info.filePath).append(",").append(info.multiMappings).append(",").append(info.interFilers).append("\n");
        }
        System.out.println(sb.toString());
        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("info.csv"), sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


