package benchmark.metrics;

import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;

/* Created by pourya on 2023-09-22 12:33 p.m. */
public class Make {
    public static void main(String[] args) throws IOException {
        ConfigurationFactory.defects4j().getAllCases().forEach(info -> {
            System.out.println(info.makeURL());
        });
    }

}
