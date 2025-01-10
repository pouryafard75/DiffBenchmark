package benchmark.manupilator;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;

public enum DiffSide {
    LEFT {
        @Override
        public Tree getTree(Mapping mapping) {
            return mapping.first;
        }
    },
    RIGHT {
        @Override
        public Tree getTree(Mapping mapping) {
            return mapping.second;
        }
    }
    ;
    public abstract Tree getTree(Mapping mapping);
}
