package diffQ;

import benchmark.data.diffcase.QCase;
import benchmark.generators.tools.ASTDiffToolEnum;

import java.util.List;
import java.util.Map;

public class StudyConfig {
    public String title;
    public String qid;
    public String description;
    public boolean randomize;
    public boolean anonymize;
    public List<ASTDiffToolEnum> tools;
    public Map<QCase, String> prs;
}
