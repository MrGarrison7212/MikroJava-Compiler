// generated with ast extension for cup
// version 0.8
// 8/1/2021 20:38:6


package rs.ac.bg.etf.pp1.ast;

public class FormParsListDerived2 extends FormParsList {

    private FormParsElem FormParsElem;

    public FormParsListDerived2 (FormParsElem FormParsElem) {
        this.FormParsElem=FormParsElem;
        if(FormParsElem!=null) FormParsElem.setParent(this);
    }

    public FormParsElem getFormParsElem() {
        return FormParsElem;
    }

    public void setFormParsElem(FormParsElem FormParsElem) {
        this.FormParsElem=FormParsElem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FormParsElem!=null) FormParsElem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormParsElem!=null) FormParsElem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormParsElem!=null) FormParsElem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsListDerived2(\n");

        if(FormParsElem!=null)
            buffer.append(FormParsElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParsListDerived2]");
        return buffer.toString();
    }
}
