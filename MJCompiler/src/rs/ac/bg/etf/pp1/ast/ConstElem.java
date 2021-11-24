// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class ConstElem implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String name;
    private ConstElemExt ConstElemExt;

    public ConstElem (String name, ConstElemExt ConstElemExt) {
        this.name=name;
        this.ConstElemExt=ConstElemExt;
        if(ConstElemExt!=null) ConstElemExt.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public ConstElemExt getConstElemExt() {
        return ConstElemExt;
    }

    public void setConstElemExt(ConstElemExt ConstElemExt) {
        this.ConstElemExt=ConstElemExt;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
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
        buffer.append("ConstElem(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        if(ConstElemExt!=null)
            buffer.append(ConstElemExt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstElem]");
        return buffer.toString();
    }
}
