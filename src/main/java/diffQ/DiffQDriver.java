package diffQ;

import benchmark.data.diffcase.QCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class DiffQDriver {
    static String dir = "WIP";
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(StudyConfig.class, new StudyConfigDeserializer());
        mapper.registerModule(module);

        StudyConfig config = mapper.readValue(new File("suggested.json"), StudyConfig.class);

        System.out.println("Tools: " + config.tools);
        System.out.println("PRs: " + config.prs);

        Certificate certificate = new Certificate(config);
        System.out.println("Certificate: " + certificate.cle);
        Map<QCase, Integer> filteredCounts = StaticDiffFilesGenerator.generate(certificate);
        writeCertificate(certificate, DiffQDriver.dir + "/exports/" + certificate.config.qid + "/" + "/certificate.json");
        writeFrontendConfig(certificate, filteredCounts);
    }

    private static void writeFrontendConfig(Certificate certificate, Map<QCase, Integer> filteredCounts) {
        /* supposed to create config.json for frontend with the following format
        {
          "diffCounts": {
            "1": 1,
            "2": 1
          },
          "tools": ["GOD", "RMD"]
        }
        */
        class FrontendConfig {
            public Map<String, String> urls;
            public Map<String, Integer> diffCounts;
            public String[] tools;
            public String qid;

            public FrontendConfig(Map<String, Integer> diffCounts, Map<String, String> urls, String[] tools, String qid) {
                this.diffCounts = diffCounts;
                this.urls = urls;
                this.tools = tools;
                this.qid = qid;
            }
        }
        String configFile = DiffQDriver.dir + "/exports/" + certificate.config.qid + "/config.json";
        Map<String, Integer> diffCounts = new LinkedHashMap<>();
        Map<String, String> urls = new LinkedHashMap<>();
        for (QCase qCase : certificate.config.prs.keySet()) {
            diffCounts.put(certificate.config.prs.get(qCase), filteredCounts.get(qCase));
            urls.put(certificate.config.prs.get(qCase), qCase.getID());
        }
        String[] tools;
        if (certificate.cle.isEmpty()) {
            tools = new String[0];
        } else {
            tools = certificate.cle.values().stream()
                    .flatMap(map -> map.keySet().stream())
                    .distinct()
                    .toArray(String[]::new);
        }

        FrontendConfig frontendConfig = new FrontendConfig(diffCounts, urls, tools, certificate.config.qid);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(configFile), frontendConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeCertificate(Certificate certificate, String outputPath) throws IOException {
        //generate certificate.json file
        //for each case, what is the alias name for each tool
        Map<QCase, Map<String, ASTDiffToolEnum>> cle = certificate.cle;
        //cle is a map of QCase to a map of tool alias to ASTDiffToolEnum
        //write to file

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Map<String, String>> jsonReadyMap = cle.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        v -> v.getValue().name(),
                                        (v1, v2) -> v1,
                                        LinkedHashMap::new  // preserves insertion order of inner map
                                )),
                        (v1, v2) -> v1,
                        LinkedHashMap::new  // preserves insertion order of outer map
                ));

        //If outputPath doesnt exist, create it
        File outputFile = new File(outputPath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        mapper.writeValue(new File(outputPath), jsonReadyMap);

    }
}
