package benchmark.generators.tools.runners.converter;

import java.io.IOException;

/* Created by pourya on 2024-05-08*/
public class NoPerfectDiffException extends RuntimeException {
    public NoPerfectDiffException(IOException e) {
        super(e);
    }
    NoPerfectDiffException(String message) {
        super(message);
    }
}
