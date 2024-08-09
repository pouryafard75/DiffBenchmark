This repository contains all the source-code and experiments related to the following paper:

Pouria Alikhanifard and Nikolaos Tsantalis, "[A Novel Refactoring and Semantic Aware Abstract Syntax Tree Differencing Tool and a Benchmark for Evaluating the Accuracy of Diff Tools](https://arxiv.org/pdf/2403.05939)," *ACM Transactions on Software Engineering and Methodology*, 2024.

The oracle files are located in the [oracle](https://github.com/pouryafard75/DiffBenchmark/tree/master/oracle) folder and the **Experiment Results** are located in the [out](https://github.com/pouryafard75/DiffBenchmark/tree/master/out) folder.

Please check the [Wiki](https://github.com/pouryafard75/DiffBenchmark/wiki) for the detailed explanation. You can find the list of all supported ASTDiffTools [here](
https://github.com/pouryafard75/DiffBenchmark/wiki/Supported-ASTDiff-Tools).

Table of Contents
=================
   * [General info](#general-info)
   * [How to build DiffBenchmark](#how-to-build-diffbenchmark)
   * [Usage guidelines](#usage-guidelines)
      * [With a locally cloned git repository](#with-a-locally-cloned-git-repository)
      * [With two directories containing Java source code](#with-two-directories-containing-java-source-code)
      * [With all information fetched directly from GitHub](#with-all-information-fetched-directly-from-github)

# General info
DiffBenchmark is a visualization and benchmarking tool for ASTDiff tools.
It provides an infrastructure to compare the results of different ASTDiff tools with the oracle files.

It's extensible architecture allows you to add new ASTDiff tools and compare them with the existing ones.

You can visualize, compare, and evaluate the results of different ASTDiff tools using the DiffBenchmark.

# How to build DiffBenchmark

First, clone the [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner.git) repository.

Then, you have to update the `REFACTORING_MINER_PATH` in **one** of the following ways:

Update the field `REFACTORING_MINER_PATH` in the `benchmark.utils.Configuration.ConfigurationFactory` class to the RefactoringMiner cloned repository path in your hard drive **or** you can have it as env variable **REFACTORING_MINER_PATH**

Import DiffBenchmark as a gradle project in your IDE, or run `./gradlew jar` to build.

# Usage guidelines

There are 3 different ways you can execute DiffBenchmark:
You can find the examples [here](https://github.com/pouryafard75/DiffBenchmark/blob/master/src/main/java/benchmark/gui/drivers).

## With a locally cloned git repository

Execute [CompareWithLocallyClonedRepository.java](https://github.com/pouryafard75/DiffBenchmark/blob/master/src/main/java/benchmark/gui/drivers/CompareWithLocallyClonedRepository.java)
```java
String repo = "https://github.com/Alluxio/alluxio.git";
String commit = "9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
String pathToClonedRepository = "tmp/" + "Alluxio/Alluxio";
Repository repository = new GitServiceImpl().cloneIfNotExists(pathToClonedRepository, repo);
new BenchmarkWebDiffFactory().withLocallyClonedRepo(repository, commit).run();
```
## With two directories containing Java source code

Execute [CompareWithTwoDirectories.java](https://github.com/pouryafard75/DiffBenchmark/blob/master/src/main/java/benchmark/gui/drivers/CompareWithTwoDirectories.java)
```java
String folder1 = "PATH_TO_FOLDER1";
String folder2 = "PATH_TO_FOLDER2";
new BenchmarkWebDiffFactory().withTwoDirectories(folder1,folder2).run()
```

## With all information fetched directly from GitHub
To use the following API, please provide a valid OAuth token in the `github-oauth.properties` file.
You can generate an OAuth token in GitHub `Settings` -> `Developer settings` -> `Personal access tokens`.

Then add the token as **OAuthToken** env variable


Execute [CompareWithGitHubAPI.java](https://github.com/pouryafard75/DiffBenchmark/blob/master/src/main/java/benchmark/gui/drivers/CompareWithGitHubAPI.java)
```java
String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
new BenchmarkWebDiffFactory().withURL(url).run();
```

