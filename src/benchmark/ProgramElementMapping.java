package benchmark;

import java.io.Serializable;
import java.util.List;

/* Created by pourya on 2023-02-08 2:46 a.m. */
public class ProgramElementMapping implements Serializable {
    RightProgramElement rightProgramElement;
    List<AbstractMapping> abstractMappingList;

    public ProgramElementMapping() {
    }

    public ProgramElementMapping(RightProgramElement rightProgramElement, List<AbstractMapping> abstractMappingList) {
        this.rightProgramElement = rightProgramElement;
        this.abstractMappingList = abstractMappingList;
    }

    public RightProgramElement getRightProgramElement() {

        return rightProgramElement;
    }

    public void setRightProgramElement(RightProgramElement rightProgramElement) {
        this.rightProgramElement = rightProgramElement;
    }

    public List<AbstractMapping> getAbstractMappingList() {
        return abstractMappingList;
    }

    public void setAbstractMappingList(List<AbstractMapping> abstractMappingList) {
        this.abstractMappingList = abstractMappingList;
    }
}
