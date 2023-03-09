package benchmark;
import java.io.Serializable;
import java.util.Objects;

/* Created by pourya on 2023-02-08 2:42 a.m. */
public class ProgramElement implements Serializable {
    private String element;
    private String className;
    private String fileName;

    ProgramElement(){

    }

    public ProgramElement(String element, String className, String fileName) {
        this.element = element;
        this.className = className;
        this.fileName = fileName;
    }

    public String getElement() {
        return element;
    }

    public String getClassName() {
        return className;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ProgramElement) obj;
        return Objects.equals(this.element, that.element) &&
                Objects.equals(this.className, that.className) &&
                Objects.equals(this.fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, className, fileName);
    }

    @Override
    public String toString() {
        return "ProgramElement[" +
                "element=" + element + ", " +
                "className=" + className + ", " +
                "fileName=" + fileName + ']';
    }

}
