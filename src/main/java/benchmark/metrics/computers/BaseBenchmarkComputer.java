package benchmark.metrics.computers;


import benchmark.data.exp.IExperiment;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/* Created by pourya on 2023-11-29 9:13 a.m. */
public abstract class BaseBenchmarkComputer implements StatsComputer {
    private final IExperiment experiment;
    private final ObjectMapper mapper;

    protected BaseBenchmarkComputer(IExperiment experiment) {
        this.experiment = experiment;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public IExperiment getExperiment() {
        return experiment;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }




}
