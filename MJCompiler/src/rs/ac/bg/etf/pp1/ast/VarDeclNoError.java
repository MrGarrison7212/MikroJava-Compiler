// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class VarDeclNoError extends VarDecl {

    private OptFinal OptFinal;
    private Type Type;
    private VarList VarList;

    public VarDeclNoError (OptFinal OptFinal, Type Type, VarList VarList) {
        this.OptFinal=OptFinal;
        if(OptFinal!=null) OptFinal.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.VarList=VarList;
        if(VarList!=null) VarList.setParent(this);
    }

    public OptFinal getOptFinal() {
        return OptFinal;
    }

    public void setOptFinal(OptFinal OptFinal) {
        this.OptFinal=OptFinal;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public VarList getVarList() {
        return VarList;
    }

    public void setVarList(VarList VarList) {
        this.VarList=VarList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptFinal!=null) OptFinal.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(VarList!=null) VarList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptFinal!=null) OptFinal.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(VarList!=null) VarList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptFinal!=null) OptFinal.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(VarList!=null) VarList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclNoError(\n");

        if(OptFinal!=null)
            buffer.append(OptFinal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarList!=null)
            buffer.append(VarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclNoError]");
        return buffer.toString();
    }
}
