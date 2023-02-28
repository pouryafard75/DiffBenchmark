package benchmark;

import java.io.Serializable;

/* Created by pourya on 2023-02-08 2:46 a.m. */
public class RightProgramElement extends ProgramElement implements Serializable {

    private boolean match;
    public RightProgramElement(String element, String className, String fileName) {
        super(element, className, fileName);
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
    public String getElement() {
        return super.getElement();
    }

    public String getClassName() {
        return super.getClassName();
    }

    public String getFileName() {
        return super.getFileName();
    }
}
