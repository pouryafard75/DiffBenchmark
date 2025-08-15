package diffQ;

import benchmark.data.diffcase.QCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StudyConfigDeserializer extends JsonDeserializer<StudyConfig> {
    @Override
    public StudyConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);
        StudyConfig config = new StudyConfig();

        config.title = node.get("title").asText();
        config.qid = node.get("qid").asText();
        config.description = node.get("description").asText();
        config.randomize = node.get("randomize").asBoolean();
        config.anonymize = node.get("anonymize").asBoolean();

        // Tools
        config.tools = new ArrayList<>();
        for (JsonNode toolNode : node.get("tools")) {
            String toolId = toolNode.get("id").asText();
            config.tools.add(ASTDiffToolEnum.valueOf(toolId));
        }

        // PRs
        config.prs = new LinkedHashMap<>();
        JsonNode prsNode = node.get("prs");
        for (int i = 0; i < prsNode.size(); i++) {
            JsonNode prNode = prsNode.get(i);
            QCase qCase = new QCase(prNode.get("url").asText()); // Ensure QCase is created
            config.prs.put(qCase, String.valueOf(i+1)); // Use index as the ID
        }
        return config;
    }
}
