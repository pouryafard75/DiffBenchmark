package rq;

import benchmark.utils.Configuration.Configuration;

public interface RQ {
    void run(Configuration[] confs) throws Exception;
}
