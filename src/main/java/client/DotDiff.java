package client;

import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.TreeClassifier;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.io.StringWriter;
import java.io.Writer;

/* Created by pourya on 2025-02-01*/
public final class DotDiff {
    private final Diff diff;
    private final TreeClassifier classifier;

    public DotDiff(Diff diff) {
        this.diff = diff;
        this.classifier = diff.createRootNodesClassifier();
    }


    public StringWriter getWriter() throws Exception {
        StringWriter writer = new StringWriter();
        writer.write("digraph G {\n");
        writer.write("\tnode [style=filled];\n");
        writer.write("\tsubgraph cluster_src {\n");
        writeTree(diff.src, writer);
        writer.write("\t}\n");
        writer.write("\tsubgraph cluster_dst {\n");
        writeTree(diff.dst, writer);
        writer.write("\t}\n");
        for (Mapping m: getMappingSet())
            writer.write(String.format("\t%s -> %s [style=dashed];\n",
                    getDotId(diff.src, m.first), getDotId(diff.dst, m.second)));
        writer.write("}\n");
        return writer;
    }

    private Iterable<Mapping> getMappingSet() {
        if (diff instanceof ASTDiff) return ((ASTDiff) diff).getAllMappings();
        else return diff.mappings;
    }

    private void writeTree(TreeContext context, Writer writer) throws Exception {
        for (Tree tree : context.getRoot().preOrder()) {
            String fillColor = getDotColor(tree);
            writer.write(String.format("\t\t%s [label=\"%s\", color=%s];\n",
                    getDotId(context, tree), getDotLabel(tree), fillColor));
            if (tree.getParent() != null)
                writer.write(String.format("\t\t%s -> %s;\n",
                        getDotId(context, tree.getParent()), getDotId(context, tree)));
        }
    }

    private String getDotColor(Tree tree) {
        if (classifier.getDeletedSrcs().contains(tree))
            return "red";
        else if (classifier.getInsertedDsts().contains(tree))
            return "green";
        else if (classifier.getMovedDsts().contains(tree) || classifier.getMovedSrcs().contains(tree))
            return "blue";
        else if (classifier.getUpdatedDsts().contains(tree) || classifier.getUpdatedSrcs().contains(tree))
            return "orange";
        else
            return "lightgrey";
    }

    private String getDotId(TreeContext context, Tree tree) {
        String contextStr = context == diff.src ? "src" : "dst";
        String s = "n_" + contextStr + "_" + tree.getMetrics().position;
        return s;
    }

    private String getDotLabel(Tree tree) {
        String label = customRepr(tree);
        if (label.contains("\"") || label.contains("\\s"))
            label = label
                    .replaceAll("\"", "")
                    .replaceAll("\\s", "")
                    .replaceAll("\\\\", "");
        if (label.length() > 30)
            label = label.substring(0, 20);
        return label;
    }

    private static String customRepr(Tree tree) {
        return tree.hasLabel() ?
                String.format("%s: %s", tree.getType(), tree.getLabel()) :
                String.format("%s", tree.getType());
    }

}