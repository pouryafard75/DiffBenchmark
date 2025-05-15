This repository contains all the source-code and experiments related to the following paper:

Pouria Alikhanifard and Nikolaos Tsantalis, AST Diff Benchmarking Framework

[//]: # (Pouria Alikhanifard and Nikolaos Tsantalis, "[A Novel Refactoring and Semantic Aware Abstract Syntax Tree Differencing Tool and a Benchmark for Evaluating the Accuracy of Diff Tools]&#40;https://dl.acm.org/doi/10.1145/3696002&#41;," *ACM Transactions on Software Engineering and Methodology*, 2024.)

The experiments are available in `src/main/java/rq/adb` and the exeprioment results can be found in `csv-outputs/adb-paper/`.


# How to build DiffBenchmark

First, clone the [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner.git) repository.

Then, you have to update the `REFACTORING_MINER_PATH` in **one** of the following ways:

Update the field `REFACTORING_MINER_PATH` in the `benchmark.conf.Paths` class to the RefactoringMiner cloned repository path in your hard drive **or** you can have it as env variable **REFACTORING_MINER_PATH**.

Import DiffBenchmark as a gradle project in your IDE, or run `./gradlew jar` to build.
