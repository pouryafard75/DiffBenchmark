package benchmark;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/* Created by pourya on 2023-02-08 2:48 a.m. */
public class AbstractMapping implements Serializable {
    @JsonIgnore
    final int leftOffset,leftEndOffset,rightOffset,rightEndOffset;
//    @JsonIgnore
    final String left,right;
    @JsonIgnore
    final String leftType,rightType;

    String info;


    public AbstractMapping(int leftOffset, int leftEndOffset, int rightOffset, int rightEndOffset, String left, String right, String leftType, String rightType) {
        this.leftOffset = leftOffset;
        this.leftEndOffset = leftEndOffset;
        this.rightOffset = rightOffset;
        this.rightEndOffset = rightEndOffset;
        this.left = left;
        this.right = right;
        this.leftType = leftType;
        this.rightType = rightType;
        info = makeInfo();
    }

    private String makeInfo() {
        info = leftType + "[" + leftOffset + "-" + leftEndOffset + "]" +
                ":" +
                rightType + "[" + rightOffset + "-" + rightEndOffset + "]";
        return info;
    }

    enum AbstractMappingKind
    {
        STATEMENT,
        EXPRESSION
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public int getLeftEndOffset() {
        return leftEndOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public int getRightEndOffset() {
        return rightEndOffset;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String getLeftType() {
        return leftType;
    }

    public String getRightType() {
        return rightType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
