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

    public float calcAccuracy() {
        return ((float) (TP + TN)) / ((float) (TP + TN + FP + FN));
    }

    public float calcPrecision() {
        return ((float) (TP)) / ((float) (TP + FP));
    }

    public float calcRecall() {
        return ((float) (TP)) / ((float) (TP + FN));
    }

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
