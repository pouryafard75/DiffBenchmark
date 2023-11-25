package rq;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;

import java.util.List;

/* Created by pourya on 2023-11-23 10:00 p.m. */

/***
 * What is the accuracy of each tool in matching program elements (i.e., method, field declarations)?
 */
public class RQ3 implements RQProvider{

    private static final MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
    private final MappingsTypeFilter mappingsTypeFilter;

    public RQ3(MappingsTypeFilter mappingsTypeFilter) {
        this.mappingsTypeFilter = mappingsTypeFilter;
    }


    @Override
    public void run(Configuration configuration) {
        try {
            rq3(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void rq3(Configuration configuration) throws Exception {

        for (CaseInfo info : configuration.getAllCases()) {
            List<DiffComparisonResult> myList = null;
            new BenchmarkMetricsComputer(configuration).
                    oneCaseStats(info, myList, mappingsLocationFilter, mappingsTypeFilter);

            //TOOD
        }
    }
}
