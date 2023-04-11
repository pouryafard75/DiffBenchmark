package benchmark.Oracle;

public class Stats {
    int TP;
    int FP;
    int FN;

    int TN = 0;

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
}
