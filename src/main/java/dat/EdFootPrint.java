package dat;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.model.*;

public class EdFootPrint{
    public int nbIns, nbDel, nbMov, nbUpd, size;
    public EdFootPrint(int nbIns, int nbDel, int nbMov, int nbUpd, int size) {
        this.nbIns = nbIns;
        this.nbDel = nbDel;
        this.nbMov = nbMov;
        this.nbUpd = nbUpd;
        this.size = size;
    }

    static EdFootPrint make(EditScript editScript){
        int nbIns = 0;
        int nbDel = 0;
        int nbMov = 0;
        int nbUpd = 0;
        for (Action a : editScript) {
            if (a instanceof Insert)
                nbIns++;
            else if (a instanceof Delete)
                nbDel++;
            else if (a instanceof Update)
                nbUpd++;
            else if (a instanceof Move)
                nbMov += a.getNode().getMetrics().size;
            else if (a instanceof TreeInsert)
                nbIns += a.getNode().getMetrics().size;
            else if (a instanceof TreeDelete)
                nbDel += a.getNode().getMetrics().size;
        }
        return new EdFootPrint(nbIns, nbDel, nbMov, nbUpd, editScript.size());
    }
}
