package rq;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.EExperiment;
import benchmark.data.exp.IExperiment;

import java.io.*;

public class NativeRace {
    public static void main(String[] args) throws IOException {
        IExperiment experimentConfiguration = EExperiment.REF_EXP_3_0;
        BufferedWriter writer = new BufferedWriter(new FileWriter("nativeRace.csv"));
        writer.write("CaseInfo, JVMBinariesTime, NativeBinariesTime\n");
        for (IBenchmarkCase caseInfo : experimentConfiguration.getDataset().getCases()) {
            System.out.println("Working on "  + caseInfo.getID());
            long jvmTime = new JVMBinariesCMDRunner().exeTime(caseInfo);
            long nativeTime = new NativeBinariesCMDRunner().exeTime(caseInfo);
            writer.write(String.format("%s, %d, %d\n", caseInfo.getID(), jvmTime, nativeTime));
            writer.flush(); // Ensure data is written to the file immediately
        };
    }

}
interface CMDRunner{
    String getBinariesPath();
    default void kill(){
        String scriptFile = "/home/pourya/IdeaProjects/DiffBenchmark/kill.sh"; // Replace with the path to your script file
        ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptFile);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    default void exe(IBenchmarkCase caseInfo) {
        Process run = null;

        try {
            //Fix it for the D4J case (local cases to be more precise)
            String command = String.format("%s diff --url %s --silent", getBinariesPath(), caseInfo.getID());
            run = Runtime.getRuntime().exec(command);
            run.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (run != null) {
                run.destroy();
            }
        }

    }
    default long exeTime(IBenchmarkCase caseInfo){
        kill();
        long start = System.currentTimeMillis();
        exe(caseInfo);
        long end = System.currentTimeMillis();
        return end - start;
    }
}
class NativeBinariesCMDRunner implements CMDRunner {
    @Override
    public String getBinariesPath() {
        return "/home/pourya/IdeaProjects/RM-ASTDiff/RM-native";
    }
}

class JVMBinariesCMDRunner implements CMDRunner {
    @Override
    public String getBinariesPath() {
        return "/home/pourya/IdeaProjects/RM-ASTDiff/build/distributions/RefactoringMiner-3.0.4/bin/RefactoringMiner";
    }
}

