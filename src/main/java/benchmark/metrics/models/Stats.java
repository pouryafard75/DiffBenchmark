package benchmark.metrics.models;

import java.util.Objects;

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

    public double calcF1() {
        return 2 * calcPrecision() * calcRecall() / (calcPrecision() + calcRecall());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return TP == stats.TP && FP == stats.FP && FN == stats.FN && TN == stats.TN;
    }

    @Override
    public int hashCode() {
        return Objects.hash(TP, FP, FN, TN);
    }
}
