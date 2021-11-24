// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class DesignatorArray extends Designator {

    private DesignatorArrayHelp DesignatorArrayHelp;
    private Expr Expr;

    public DesignatorArray (DesignatorArrayHelp DesignatorArrayHelp, Expr Expr) {
        this.DesignatorArrayHelp=DesignatorArrayHelp;
        if(DesignatorArrayHelp!=null) DesignatorArrayHelp.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
    }

    public DesignatorArrayHelp getDesignatorArrayHelp() {
        return DesignatorArrayHelp;
    }

    public void setDesignatorArrayHelp(DesignatorArrayHelp DesignatorArrayHelp) {
        this.DesignatorArrayHelp=DesignatorArrayHelp;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorArrayHelp!=null) DesignatorArrayHelp.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorArrayHelp!=null) DesignatorArrayHelp.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorArrayHelp!=null) DesignatorArrayHelp.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorArray(\n");

        if(DesignatorArrayHelp!=null)
            buffer.append(DesignatorArrayHelp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorArray]");
        return buffer.toString();
    }
}
