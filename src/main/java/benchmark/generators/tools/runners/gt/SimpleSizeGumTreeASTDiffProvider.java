package benchmark.generators.tools.runners.gt;

import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.ConfigurationOptions;
import com.github.gumtreediff.matchers.GumtreeProperties;
import com.github.gumtreediff.matchers.Matcher;
import org.refactoringminer.astDiff.models.ASTDiff;


public class SimpleSizeGumTreeASTDiffProvider extends BaseGumTreeASTDiffProvider{
    private static final Matcher matcher;

    static {{
        matcher = new CompositeMatchers.SimpleGumtree();
        GumtreeProperties properties = new GumtreeProperties();
        properties.tryConfigure(ConfigurationOptions.st_minprio, 1);
        properties.tryConfigure(ConfigurationOptions.st_priocalc, "size");
        matcher.configure(properties);
    }}

    public SimpleSizeGumTreeASTDiffProvider(ASTDiff input) {
        super(matcher, input);
    }
}
