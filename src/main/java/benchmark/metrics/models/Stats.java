package benchmark.metrics.models;

public class Stats {
    private final int TP;
    private final int FP;
    private final int FN;
    private final int TN = 0;

    public Stats(int TP, int FP, int FN) {
        this.TP = TP;
        this.FP = FP;
        this.FN = FN;
    }

    public double calcAccuracy() {
        return ((double) (TP + TN)) / ((double) (TP + TN + FP + FN));
    }

    public double calcPrecision() {
        return ((double) (TP)) / ((double) (TP + FP));
    }

    public double calcRecall() { return ((double) (TP)) / ((double) (TP + FN)); }

    public int getTP() {
        return TP;
    }

    public int getFP() {
        return FP;
    }

    public int getFN() {
        return FN;
    }

    public int getTN() {
        return TN;
    }
}
