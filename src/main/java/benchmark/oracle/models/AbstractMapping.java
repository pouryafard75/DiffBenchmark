package benchmark.oracle.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.gumtreediff.matchers.Mapping;

import java.io.Serializable;
import java.util.Objects;

/* Created by pourya on 2023-02-08 2:48 a.m. */
public class AbstractMapping implements Serializable {
    @JsonIgnore
    int leftOffset,leftEndOffset,rightOffset,rightEndOffset;
//    @JsonIgnore
    String left,right;
    @JsonIgnore
    String leftType,rightType;

    String info;

    public AbstractMapping(){}


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

//    @JsonCreator
//    public AbstractMapping(
//            @JsonProperty("left") String left,
//            @JsonProperty("right") String right,
//            @JsonProperty("info") String info) {
//        this.left = left;
//        this.right = right;
//        this.info = info;
//        deserialize();
//    }

    public AbstractMapping(Mapping mapping, String srcString, String dstString) {
        this.leftOffset = mapping.first.getPos();
        this.leftEndOffset = mapping.first.getEndPos();
        this.rightOffset = mapping.second.getPos();
        this.rightEndOffset = mapping.second.getEndPos();;
        this.left = srcString;
        this.right = dstString;
        this.leftType = mapping.first.getType().name;
        this.rightType = mapping.second.getType().name;;
        info = makeInfo();
    }

    private String makeInfo() {
        info = leftType + "[" + leftOffset + "-" + leftEndOffset + "]" +
                ":" +
                rightType + "[" + rightOffset + "-" + rightEndOffset + "]";
        return info;
    }
    @JsonIgnore
    public String getLeftInfo() {
        return info.split(":")[0];
    }

    @JsonIgnore
    public String getRightInfo() {
        return info.split(":")[1];
    }

    public boolean isMultiMappingPartner(AbstractMapping other) {
        boolean notTheSame = !this.getInfo().equals(other.getInfo());
        boolean sameRight = this.getLeftInfo().equals(other.getLeftInfo());
        boolean sameLeft = this.getRightInfo().equals(other.getRightInfo());
        return notTheSame && (sameRight || sameLeft);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMapping that = (AbstractMapping) o;
        return Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }
}
