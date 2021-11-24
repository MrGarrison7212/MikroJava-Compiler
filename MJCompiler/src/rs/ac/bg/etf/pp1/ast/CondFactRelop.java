// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class CondFactRelop extends CondFact {

    private ExprElem ExprElem;
    private Relop Relop;
    private ExprElem ExprElem1;

    public CondFactRelop (ExprElem ExprElem, Relop Relop, ExprElem ExprElem1) {
        this.ExprElem=ExprElem;
        if(ExprElem!=null) ExprElem.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.ExprElem1=ExprElem1;
        if(ExprElem1!=null) ExprElem1.setParent(this);
    }

    public ExprElem getExprElem() {
        return ExprElem;
    }

    public void setExprElem(ExprElem ExprElem) {
        this.ExprElem=ExprElem;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public ExprElem getExprElem1() {
        return ExprElem1;
    }

    public void setExprElem1(ExprElem ExprElem1) {
        this.ExprElem1=ExprElem1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprElem!=null) ExprElem.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(ExprElem1!=null) ExprElem1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprElem!=null) ExprElem.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(ExprElem1!=null) ExprElem1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprElem!=null) ExprElem.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(ExprElem1!=null) ExprElem1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFactRelop(\n");

        if(ExprElem!=null)
            buffer.append(ExprElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprElem1!=null)
            buffer.append(ExprElem1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFactRelop]");
        return buffer.toString();
    }
}
