package benchmark.data.dataset;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.diffcase.GithubCase;
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
    public Set<? extends BenchmarkCase> getCases() {
        return makeAllCases(getTypeReference(),
                getPerfectDirPath().resolve(perfectInfoName),
                getPerfectDirPath().resolve(problematicInfoName)
        );
    }

    public abstract TypeReference<? extends Set<? extends BenchmarkCase>> getTypeReference();

    Set<? extends BenchmarkCase> makeAllCases(TypeReference<? extends Set<? extends BenchmarkCase>> valueTypeRef, Path... casesPath) {
        ObjectMapper mapper = new ObjectMapper();
        Set<BenchmarkCase> allCases = new TreeSet<>(Comparator.comparing(BenchmarkCase::getID));
        for (Path path : casesPath) {
            Set<GithubCase> loaded;
            try {
                loaded = mapper.readValue(path.toFile(), valueTypeRef);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            allCases.addAll(loaded);
        }
        for (BenchmarkCase allCase : allCases) {
            allCase.setDataset(this);
        }
        return allCases;
    }
}
