package benchmark.defects4j;

import java.io.Serializable;
import java.util.Objects;

class Defect4jCaseInfo implements Serializable {
    String project;
    String id;

    public Defect4jCaseInfo(String project, String id) {
        this.project = project;
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Defect4jCaseInfo{" +
                "project='" + project + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Defect4jCaseInfo that = (Defect4jCaseInfo) o;
        return Objects.equals(project, that.project) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, id);
    }
}
