This repository contains all the source code and experiments related to the following anonymized paper submission.

**AST Diff Benchmarking Framework**

[//]: # (Pouria Alikhanifard and Nikolaos Tsantalis, "[A Novel Refactoring and Semantic Aware Abstract Syntax Tree Differencing Tool and a Benchmark for Evaluating the Accuracy of Diff Tools]&#40;https://dl.acm.org/doi/10.1145/3696002&#41;," *ACM Transactions on Software Engineering and Methodology*, 2024.)

The experiments are available in `src/main/java/rq/adb` and the exepriment results can be found in `csv-outputs/adb-paper/`.

Verify RQ1 by running: [LiteratureRQDriver.java](https://github.com/pouryafard75/DiffBenchmark/blob/8f4a5d613822212a94cb0ffdd22a321eaaf8e924/src/main/java/rq/adb/literature/LiteratureRQDriver.java)

Verify RQ2 by running: [VisitorsImpactRQDriver.java](https://github.com/pouryafard75/DiffBenchmark/blob/31bf4b2758eac0587ea594baa629522e7175e441/src/main/java/rq/adb/visitor/VisitorsImpactRQDriver.java)


# How to build DiffBenchmark

First, clone the [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner.git) repository.

Then, you have to update the `REFACTORING_MINER_PATH` in **one** of the following ways:

Update the field `REFACTORING_MINER_PATH` in the [benchmark.conf.Paths](https://github.com/pouryafard75/DiffBenchmark/blob/master/src/main/java/benchmark/conf/Paths.java) class to the RefactoringMiner cloned repository path in your hard drive **or** you can have it as env variable **REFACTORING_MINER_PATH**.

Import DiffBenchmark as a gradle project in your IDE, or run `./gradlew jar` to build.
