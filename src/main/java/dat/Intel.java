package dat;

import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.models.hrd.NecessaryMappings;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


/* Created by pourya on 2024-01-22*/


@Entity
@Table(name = "Intel")
public class Intel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    int tp_raw_mappings;
    @JsonIgnore
    int tp_raw_programElements;

    @Column(name = "repo")
    String repo;
    @Column(name = "commit")
    String commit;
    @Column(name = "srcPath")
    String srcPath;
    @Column(name = "matcher")
    String matcher;
    @Column(name = "conf")
    String conf;
    @Column(name = "trv_mappings")
    int trv_mappings;
    @Column(name = "trv_programElements")
    int trv_programElements;

    @Column(name = "tp_mappings")
    int tp_mappings;
    @Column(name = "fp_mappings")
    int fp_mappings;
    @Column(name = "fn_mappings")
    int fn_mappings;


    @Column(name = "tp_programElements")
    int tp_programElements;
    @Column(name = "fp_programElements")
    int fp_programElements;
    @Column(name = "fn_programElements")
    int fn_programElements;
    @Column(name = "precision")
    double precision;
    @Column(name = "recall")
    double recall;
    @Column(name = "f1")
    double f1;

    @Column(name = "s") int s;
    @Column(name = "ni") int ni;
    @Column(name = "nd") int nd;
    @Column(name = "nu") int nu;
    @Column(name = "nm") int nm;
    @Column(name = "n_sum") int n_sum;
    @Column(name="dataset") String dataset;


    public Intel(String repo, String commit, String srcPath,
                 String matcher, String conf,
                 EdFootPrint edFootPrint,
                 NecessaryMappings ignore,
                 DiffStats mappingsStats, String dataset) {

        this.repo = repo; this.commit = commit; this.srcPath = srcPath;
        this.matcher = matcher; this.conf = conf;

        this.trv_mappings = ignore.getMappings().size(); this.trv_programElements = ignore.getMatchedElements().size();
        this.tp_raw_mappings = mappingsStats.getAbstractMappingStats().getTP();
        this.tp_mappings = Math.max(tp_raw_mappings - trv_mappings, 0);
        this.fp_mappings = mappingsStats.getAbstractMappingStats().getFP();
        this.fn_mappings = mappingsStats.getAbstractMappingStats().getFN();

        this.tp_raw_programElements = mappingsStats.getProgramElementStats().getTP();
        this.tp_programElements = Math.max(tp_raw_programElements - trv_programElements, 0);
        this.fp_programElements = mappingsStats.getProgramElementStats().getFP();
        this.fn_programElements = mappingsStats.getProgramElementStats().getFN();
        Stats effective_stats = new Stats(tp_mappings + tp_programElements, fp_mappings + fp_programElements, fn_mappings + fn_programElements);
        this.f1 = effective_stats.calcF1();
        this.precision = effective_stats.calcPrecision();
        this.recall = effective_stats.calcRecall();
        this.s = edFootPrint.size;
        this.ni = edFootPrint.nbIns;
        this.nd = edFootPrint.nbDel;
        this.nu = edFootPrint.nbUpd;
        this.nm = edFootPrint.nbMov;
        this.n_sum = edFootPrint.nbIns + edFootPrint.nbDel + edFootPrint.nbUpd + edFootPrint.nbMov;
        this.dataset = dataset;
    }
    public Intel() {
    }
    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getMatcher() {
        return matcher;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public int getTrv_mappings() {
        return trv_mappings;
    }

    public void setTrv_mappings(int trv_mappings) {
        this.trv_mappings = trv_mappings;
    }

    public int getTrv_programElements() {
        return trv_programElements;
    }

    public void setTrv_programElements(int trv_programElements) {
        this.trv_programElements = trv_programElements;
    }

    public int getTp_raw_mappings() {
        return tp_raw_mappings;
    }

    public void setTp_raw_mappings(int tp_raw_mappings) {
        this.tp_raw_mappings = tp_raw_mappings;
    }

    public int getTp_mappings() {
        return tp_mappings;
    }

    public void setTp_mappings(int tp_mappings) {
        this.tp_mappings = tp_mappings;
    }

    public int getFp_mappings() {
        return fp_mappings;
    }

    public void setFp_mappings(int fp_mappings) {
        this.fp_mappings = fp_mappings;
    }

    public int getFn_mappings() {
        return fn_mappings;
    }

    public void setFn_mappings(int fn_mappings) {
        this.fn_mappings = fn_mappings;
    }

    public int getTp_raw_programElements() {
        return tp_raw_programElements;
    }

    public void setTp_raw_programElements(int tp_raw_programElements) {
        this.tp_raw_programElements = tp_raw_programElements;
    }

    public int getTp_programElements() {
        return tp_programElements;
    }

    public void setTp_programElements(int tp_programElements) {
        this.tp_programElements = tp_programElements;
    }

    public int getFp_programElements() {
        return fp_programElements;
    }

    public void setFp_programElements(int fp_programElements) {
        this.fp_programElements = fp_programElements;
    }

    public int getFn_programElements() {
        return fn_programElements;
    }

    public void setFn_programElements(int fn_programElements) {
        this.fn_programElements = fn_programElements;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getNi() {
        return ni;
    }

    public void setNi(int ni) {
        this.ni = ni;
    }

    public int getNd() {
        return nd;
    }

    public void setNd(int nd) {
        this.nd = nd;
    }

    public int getNu() {
        return nu;
    }

    public void setNu(int nu) {
        this.nu = nu;
    }

    public int getNm() {
        return nm;
    }

    public void setNm(int nm) {
        this.nm = nm;
    }

    public int getN_sum() {
        return n_sum;
    }

    public void setN_sum(int n_sum) {
        this.n_sum = n_sum;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}
