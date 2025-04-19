package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;
import com.fasterxml.jackson.annotation.JsonIgnore;

/* Created by pourya on 2024-09-29*/
public abstract class AbstractIBenchmarkCase implements IBenchmarkCase
{
    @JsonIgnore
    IBenchmarkDataset dataset;

    @Override
    public IBenchmarkDataset getDataset() {
        return dataset;
    }

    public void setDataset(IBenchmarkDataset dataset) {
        this.dataset = dataset;
    }
}
