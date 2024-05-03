package benchmark.metrics.computers;


import benchmark.utils.Configuration.Configuration;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/* Created by pourya on 2023-11-29 9:13 a.m. */
public abstract class BaseBenchmarkComputer implements StatsComputer {
    private final Configuration configuration;
    private final ObjectMapper mapper;

    protected BaseBenchmarkComputer(Configuration configuration) {
        this.configuration = configuration;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }




}
