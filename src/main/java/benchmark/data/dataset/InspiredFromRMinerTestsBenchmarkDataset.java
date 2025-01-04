package benchmark.data.dataset;

import benchmark.data.diffcase.AbstractIBenchmarkCase;
import benchmark.data.diffcase.IBenchmarkCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/* Created by pourya on 2024-09-28*/
public abstract class InspiredFromRMinerTestsBenchmarkDataset implements IBenchmarkDataset {
    final String perfectInfoName = "cases.json";
    final String problematicInfoName = "cases-problematic.json";

    private final String perfectDiffDir;

    public InspiredFromRMinerTestsBenchmarkDataset(String perfectDiffDir) {
        this.perfectDiffDir = perfectDiffDir;
    }

    @Override
    public Path getPerfectDirPath() {
        return Path.of(perfectDiffDir);
    }

    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        return makeAllCases(getTypeReference(),
//                getPerfectDirPath().resolve(perfectInfoName),
                getPerfectDirPath().resolve(problematicInfoName)
        );
    }

    public abstract TypeReference<? extends Set<? extends AbstractIBenchmarkCase>> getTypeReference();

    Set<? extends IBenchmarkCase> makeAllCases(TypeReference<? extends Set<? extends AbstractIBenchmarkCase>> valueTypeRef, Path... casesPath) {
        ObjectMapper mapper = new ObjectMapper();
        Set<AbstractIBenchmarkCase> allCases = new TreeSet<>(Comparator.comparing(IBenchmarkCase::getID));
        for (Path path : casesPath) {
            Set<? extends AbstractIBenchmarkCase> loaded;
            try {
                loaded = mapper.readValue(path.toFile(), valueTypeRef);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            allCases.addAll(loaded);
        }
        for (AbstractIBenchmarkCase aCase : allCases) {
            aCase.setDataset(this);
        }
        return allCases;
    }
}
