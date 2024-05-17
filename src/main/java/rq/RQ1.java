package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


import static benchmark.metrics.mm.writeToFile;
import static benchmark.utils.PathResolver.exportedFolderPathByCaseInfo;
import static benchmark.utils.PathResolver.getPaths;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How many multi-mappings are missed by each tool?
 */
public class RQ1 implements RQ{
    private MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.MULTI_ONLY;
    private MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

    public void setMappingsTypeFilter(MappingsTypeFilter mappingsTypeFilter) {
        this.mappingsTypeFilter = mappingsTypeFilter;
    }

    public void setMappingsLocationFilter(MappingsLocationFilter mappingsLocationFilter) {
        this.mappingsLocationFilter = mappingsLocationFilter;
    }

    @Override
    public void run(Configuration[] confs) {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (Configuration configuration : confs) {
            try {
                stats.addAll(new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter).compute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            name.append(configuration.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq1-" + name + ".csv", false);

    }
    public void verifyMultiMappings(Configuration configuration) throws IOException {
        int mm = 0 ;
        List<String > urls = new ArrayList<>();
        for (CaseInfo info : configuration.getAllCases()) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            Path dir = Paths.get(configuration.getOutputFolder() + folderPath  + "/");
            List<Path> paths = getPaths(dir, 1);
            for (Path path : paths) {
                if (path.getFileName().toString().equals("mm")) continue;
                File mmFolder = new File(path.toString(), "mm");
                File GodMM = new File(mmFolder, "GOD-MM.csv");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                HumanReadableDiff godMMHRD = mapper.readValue(GodMM, HumanReadableDiff.class);
                if (!godMMHRD.getIntraFileMappings().getMappings().isEmpty()
                ||
                    !godMMHRD.getIntraFileMappings().getMatchedElements().isEmpty())
                {
                    mm++;
                    urls.add(info.makeURL());
                    break;
                }
            }
        }
        writeToFile(urls, "rq1-mm.txt");
        System.out.println(mm);
    }
}

