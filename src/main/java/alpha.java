import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.BenchmarkComparisonInput;
import benchmark.metrics.computers.vanilla.HRDBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.FileDiffComparisonResult;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;

import static benchmark.metrics.computers.filters.MappingsLocationFilter.NO_FILTER;

/* Created by pourya on 2024-01-27*/
public class alpha {
    private static MappingsLocationFilter humanReadableDiffFilter = NO_FILTER;
    private static MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;
    public static void main(String[] args) throws IOException {
        Configuration configuration = ConfigurationFactory.dummy();
        CaseInfo info = new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825");
        String fileName = "servers/src/main/java/tachyon/worker/block/allocator/MaxFreeAllocator.java";
        BenchmarkComparisonInput input = BenchmarkComparisonInput.read(configuration, info, fileName);
        HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(humanReadableDiffFilter.getFilter(), mappingsTypeFilter, input);
        BaseDiffComparisonResult baseDiffComparisonResult = new FileDiffComparisonResult(info, fileName);
        hrdBenchmarkComputer.compute(baseDiffComparisonResult);

    }
}
