// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class FactorConst extends Factor {

    private ConstElemExt ConstElemExt;

    public FactorConst (ConstElemExt ConstElemExt) {
        this.ConstElemExt=ConstElemExt;
        if(ConstElemExt!=null) ConstElemExt.setParent(this);
    }

    public ConstElemExt getConstElemExt() {
        return ConstElemExt;
    }

    public void setConstElemExt(ConstElemExt ConstElemExt) {
        this.ConstElemExt=ConstElemExt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstElemExt!=null) ConstElemExt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstElemExt!=null) ConstElemExt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstElemExt!=null) ConstElemExt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorConst(\n");

        if(ConstElemExt!=null)
            buffer.append(ConstElemExt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorConst]");
        return buffer.toString();
    }
}
