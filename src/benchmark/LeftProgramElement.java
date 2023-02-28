package benchmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Created by pourya on 2023-02-08 2:44 a.m. */
public class LeftProgramElement extends ProgramElement implements Serializable {
    List<ProgramElementMapping> mappings;
    public LeftProgramElement(String element, String className, String fileName) {
        super(element, className, fileName);
        mappings = new ArrayList<>();
    }
    public void addMapping(ProgramElementMapping programElementMapping)
    {
        this.mappings.add(programElementMapping);
    }
    LeftProgramElement(){}

    public List<ProgramElementMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<ProgramElementMapping> mappings) {
        this.mappings = mappings;
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
