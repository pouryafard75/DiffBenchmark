package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;

/* Created by pourya on 2024-09-29*/
public abstract class AbstractBenchmarkCase implements BenchmarkCase
{
    IBenchmarkDataset dataset;

    @Override
    public IBenchmarkDataset getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(IBenchmarkDataset dataset) {
        this.dataset = dataset;
    }
}
