package rs.ac.bg.etf.pp1;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class SemanticAnalyzer extends VisitorAdaptor {

	boolean errorDetected = false;

	public static final Struct boolType = new Struct(Struct.Bool);
	Obj currentMethod = null;
	boolean returnFound = false;
	boolean finalFound;
	HashSet<String> labels = new HashSet<String>();
	HashSet<String> notDefLabels = new HashSet<String>();
	int nVars;
	Struct currentType;

	Logger log = Logger.getLogger(getClass());

	public SemanticAnalyzer() {
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
	}

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgramName().obj);
		Obj main = Tab.currentScope.findSymbol("main");
		if(main == null){
			report_error("Simbol sa imenom main ne postoji u glavnom programu. ", null);
		} else if (main.getKind() != Obj.Meth) {
			report_error(" Main mora biti metoda ! ", null);
		} else if (main.getType() != Tab.noType){
			report_error(" Main metoda mora biti 'void' ! ", null);
		}
		Tab.closeScope();
	}
	
	public void visit(FormParams formParams){
		if(currentMethod.getName().equals("main")){
			report_error(" Main metoda ne sme imati parametre ! ", null);
		}
	}

	public void visit(ProgramName progName) {
		if (Tab.currentScope.findSymbol(progName.getName()) == null) {
			progName.obj = Tab.insert(Obj.Prog, progName.getName(), Tab.noType);
		} else {
			progName.obj = Tab.noObj;
			report_error("Simbol sa imenom" + progName.getName()
					+ "vec postoji u trenutnom scope-u!", progName);
		}
		Tab.openScope();
	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getName()
					+ " u tabeli simbola", null);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Greska: Ime " + type.getName()
						+ " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}
		currentType = type.struct;
	}

	public void visit(MethodDecl methodDecl) {
		if (!returnFound && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine()
					+ ": funkcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		}

		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		for (Iterator<String> iterator = notDefLabels.iterator(); iterator.hasNext();) {
			report_error("Skace se na labelu " + iterator.next() + " koja nije definisana!", null);
		}
		notDefLabels.clear();
		labels.clear();
		returnFound = false;
		currentMethod = null;
	}

	public void visit(MethodType methodType) {
		if (Tab.currentScope.findSymbol(methodType.getName()) == null) {
			Struct type = Tab.noType;
			if (methodType.getTypeOrVoid() instanceof ReturnType) {
				type = ((ReturnType) methodType.getTypeOrVoid()).getType().struct;
			}
			currentMethod = Tab.insert(Obj.Meth, methodType.getName(), type);
			methodType.obj = currentMethod;
		} else {
			currentMethod = Tab.noObj;
			report_error("Simbol sa imenom" + methodType.getName()
					+ "vec postoji u trenutnom scope-u!", methodType);
		}
		Tab.openScope();
	}
	
	public void visit(YesOptFinal yesOptFinal){
		finalFound = true;
	}
	
	public void visit(NoOptFinal noOptFinal){
		finalFound = false;
	}

	public void visit(VarElemNoArray varElemNoArray) {
		if(finalFound){
			report_error("Final moze biti samo niz int-ova! ", varElemNoArray);
		}
		if (Tab.currentScope.findSymbol(varElemNoArray.getName()) == null) {
			Tab.insert(Obj.Var, varElemNoArray.getName(), currentType);
		} else {
			report_error("Simbol sa imenom" + varElemNoArray.getName()
					+ "vec postoji u trenutnom scope-u!", varElemNoArray);
		}
	}

	public void visit(VarElemArray varElemArray) {
		if(finalFound && currentType != Tab.intType){
			report_error("Final moze biti samo niz int-ova! ", varElemArray);
		}
		if (Tab.currentScope.findSymbol(varElemArray.getName()) == null) {
			Obj obj = Tab.insert(Obj.Var, varElemArray.getName(), new Struct(
					Struct.Array, currentType));
			if(finalFound){
				obj.setFpPos(1);
			}
		} else {
			report_error("Simbol sa imenom" + varElemArray.getName()
					+ "vec postoji u trenutnom scope-u!", varElemArray);
		}
	}

	public void visit(NumberConst numberConst) {
		numberConst.obj = new Obj(Obj.Con, "", Tab.intType);
		numberConst.obj.setAdr((int) numberConst.getVal());
	}

	public void visit(CharConst charConst) {
		charConst.obj = new Obj(Obj.Con, "", Tab.charType);
		charConst.obj.setAdr((int) charConst.getVal());
	}

	public void visit(BoolConst boolConst) {
		boolConst.obj = new Obj(Obj.Con, "", boolType);
		boolConst.obj.setAdr(boolConst.getVal() ? 1 : 0);
	}

	public void visit(ConstElem constElem) {
		if (Tab.currentScope.findSymbol(constElem.getName()) == null) {
			Obj con = Tab.insert(Obj.Con, constElem.getName(), currentType);
			con.setAdr(constElem.getConstElemExt().obj.getAdr());
		} else {
			report_error("Simbol sa imenom" + constElem.getName()
					+ "vec postoji u trenutnom scope-u!", constElem);
		}
		if (!currentType.compatibleWith(constElem.getConstElemExt().obj
				.getType())) {
			report_error("Konstanti " + constElem.getName()
					+ " je dodeljen nekompatibilan tip !", constElem);
		}
	}

	public void visit(ReturnExpr returnExpr) {
		if (currentMethod == null) {
			report_error("return ne sme postojati van tela funkcija",
					returnExpr);
		} else {
			returnFound = true;
			Struct currMethType = currentMethod.getType();
			if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
				report_error(
						"Greska na liniji "
								+ returnExpr.getLine()
								+ " : "
								+ "tip izraza u 'return' naredbi ne slaze se sa tipom povratne vrednosti funkcije "
								+ currentMethod.getName(), null);
			}
		}
	}
	
	public void visit(ReturnNoExpr returnNoExpr){
		if (currentMethod == null) {
			report_error("return ne sme postojati van tela funkcija",
					returnNoExpr);
		} else if(currentMethod.getType() != Tab.noType) {
			report_error(
					"Greska na liniji "
							+ returnNoExpr.getLine()
							+ " : "
							+ " return bez izraza se moze nalaziti samo unutar void metoda. "
							+ currentMethod.getName(), null);
		}
		
	}

	public void visit(ExprAddop addExpr) {
		Struct te = addExpr.getExprElem().struct;
		Struct t = addExpr.getTerm().struct;
		if (te.equals(t) && te == Tab.intType)
			addExpr.struct = te;
		else {
			report_error("Greska na liniji " + addExpr.getLine()
					+ " : nekompatibilni tipovi u izrazu za sabiranje.", null);
			addExpr.struct = Tab.noType;
		}
	}

	public void visit(DesignatorVar designatorVar) {
		Obj obj = Tab.find(designatorVar.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designatorVar.getLine()
					+ " : ime " + designatorVar.getName()
					+ " nije deklarisano! ", null);
		}else {
			DumpSymbolTableVisitor visitor = new DumpSymbolTableVisitor();
			visitor.visitObjNode(obj);
			if(obj.getKind() == Obj.Con){
				report_info("Detektovano je koriscenje konstante " + visitor.getOutput(), designatorVar);
			} else if(obj.getKind() == Obj.Var){
				if(obj.getLevel() == 0){
					report_info("Detektovano je koriscenje globalne promenljive " + visitor.getOutput(), designatorVar);
				}else {
					report_info("Detektovano je koriscenje lokalne promenljive " + visitor.getOutput(), designatorVar);
				}
			}
		}
		designatorVar.obj = obj;
	}

	public void visit(DesignatorArray designatorArray) {
		Obj arrayObj = designatorArray.getDesignatorArrayHelp().getDesignator().obj;
		designatorArray.obj = new Obj(Obj.Elem, "desElem", arrayObj.getType()
				.getElemType());
		designatorArray.obj.setFpPos(arrayObj.getFpPos());
		if (arrayObj.getType().getKind() != Struct.Array) {
			report_error("Greska na liniji " + designatorArray.getLine() + " "
					+ arrayObj.getName() + " nije niz", null);
			designatorArray.obj = Tab.noObj;
		}
		if (designatorArray.getExpr().struct != Tab.intType) {
			report_error("Greska na liniji " + designatorArray.getLine()
					+ ": indeksiranje se vrsi samo tipom 'int' ! "
					+ designatorArray.getExpr().struct.getKind(), null);
			designatorArray.obj = Tab.noObj;
		}
	}

	public void visit(FactorConst factorConst) {
		factorConst.struct = factorConst.getConstElemExt().obj.getType();
	}

	public void visit(FactorExpr factorExpr) {
		factorExpr.struct = factorExpr.getExpr().struct;
	}

	public void visit(FactorNewArray factorNewArray) {
		if (factorNewArray.getExpr().struct == Tab.intType) {
			factorNewArray.struct = new Struct(Struct.Array, currentType);
		} else {
			report_error("Index niza mora biti tipa 'int' ", factorNewArray);
			factorNewArray.struct = Tab.noType;
		}
	}

	public void visit(FactorDesignator factorDesignator) {
		factorDesignator.struct = factorDesignator.getDesignator().obj
				.getType();

	}
	
	public void visit(FactorAt factorAt){
		factorAt.struct = Tab.intType;
		if(!factorAt.getDesignator().obj.getType().equals(new Struct(Struct.Array, Tab.intType))){
			report_error("Prvi operand mora biti niz int-ova! ", factorAt);
		}
		if(factorAt.getFactor().struct != Tab.intType){
			report_error("Drugi operand mora biti int! ", factorAt);
		}
	}
	
	public void visit(FactorMax factorMax){
		factorMax.struct = Tab.intType;
		if(factorMax.getExpr().struct != Tab.intType ){
			report_error("Prvi operand funkcije MAX mora biti tipa int! ", factorMax);
		}
		if(factorMax.getExpr1().struct != Tab.intType ){
			report_error("Drugi operand funkcije MAX mora biti tipa int! ", factorMax);
		}
		if(factorMax.getExpr2().struct != Tab.intType ){
			report_error("Treci operand funkcije MAX mora biti tipa int! ", factorMax);
		}
	}
	
	public void visit(FactorHash factorHash){
		factorHash.struct = Tab.charType;
		if(factorHash.getExpr().struct != Tab.charType ){
			report_error("Prvi operand funkcije HASH mora biti tipa char! ", factorHash);
		}
		if(factorHash.getExpr1().struct != Tab.intType ){
			report_error("Drugi operand funkcije HASH mora biti tipa int! ", factorHash);
		}
	}
	
	public void visit(FactorDol factorDol){
		factorDol.struct = new Struct(Struct.Array, Tab.charType);
		if(factorDol.getFactor().struct != Tab.intType){
			report_error("Prvi operand funkcije DOL mora biti tipa int! ", factorDol);
		}
	}
	
	public void visit(FactorCap factorCap){
		factorCap.struct = Tab.charType;
		if(!factorCap.getDesignator().obj.getType().equals(new Struct(Struct.Array, boolType))){
			report_error("Prvi operand funkcije CAP mora biti niz bool-ova ! ", factorCap);
		}
	}

	public void visit(TermFactor termFactor) {
		termFactor.struct = termFactor.getFactor().struct;
		;
	}

	public void visit(TermMulop termMulop) {
		Struct te = termMulop.getFactor().struct;
		Struct t = termMulop.getTerm().struct;
		if (te.equals(t) && te == Tab.intType)
			termMulop.struct = te;
		else {
			report_error("Greska na liniji " + termMulop.getLine()
					+ " : nekompatibilni tipovi u izrazu za mnozenje.", null);
			termMulop.struct = Tab.noType;
		}
	}

	public void visit(ExprTerm exprTerm) {
		exprTerm.struct = exprTerm.getTerm().struct;
	}

	public void visit(ExprTermMinus exprTermMinus) {
		Struct te = exprTermMinus.getTerm().struct;
		if (te == Tab.intType)
			exprTermMinus.struct = te;
		else {
			report_error("Greska na liniji " + exprTermMinus.getLine()
					+ " : pogresan tip.", null);
			exprTermMinus.struct = Tab.noType;
		}
	}

	public void visit(ExprSimple exprSimple) {
		exprSimple.struct = exprSimple.getExprElem().struct;
	}

	public void visit(ExprTernar exprTernar) {
		Struct te = exprTernar.getExpr().struct;
		Struct t = exprTernar.getExpr1().struct;
		if (te.equals(t))
			exprTernar.struct = te;
		else {
			report_error("Greska na liniji " + exprTernar.getLine()
					+ " : nekompatibilni tipovi.", null);
			exprTernar.struct = Tab.noType;
		}

	}
	
	public void visit(CondFactRelop condFactRelop) {
		Struct te = condFactRelop.getExprElem().struct;
		Struct t = condFactRelop.getExprElem1().struct;
		if (!te.compatibleWith(t)) {
			report_error("Greska na liniji " + condFactRelop.getLine()
					+ " : nekompatibilni tipovi.", null);
		} else if(te.getKind() == Struct.Array 
				&& !(condFactRelop.getRelop() instanceof RelopEq || condFactRelop.getRelop() instanceof RelopNe)){
			report_error("Greska na liniji " + condFactRelop.getLine()
					+ " : Uz nizove se mogu koristiti samo operatori '==' i '!=' .", null);
		}
	}
	
	public void visit(CondFactNoRelop condFactNoRelop){
		if(condFactNoRelop.getExprElem().struct != boolType){
			report_error("Greska na liniji " + condFactNoRelop.getLine()
					+ " : CondFact mora biti tipa bool. ", null);
		}
	}

	public void visit(DeStatDec deStatDec) {
		Obj obj = deStatDec.getDesignator().obj;
		if(obj.getFpPos() == 1){
			report_error("Greska na liniji: " + deStatDec.getLine()
					+ ": elemenat niza koji je final ne moze biti dekrementiran!", null);
		}
		if (obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji: " + deStatDec.getLine()
					+ ": Simbol mora biti promenjliva ili element niza!", null);
		}
		if (obj.getType().getKind() != Struct.Int) {
			report_error("Greska na liniji: " + deStatDec.getLine()
					+ "pogresan tip, operator -- zahteva tip int ! ", null);
		}
	}

	public void visit(DeStatInc deStatInc) {
		Obj obj = deStatInc.getDesignator().obj;
		if(obj.getFpPos() == 1){
			report_error("Greska na liniji: " + deStatInc.getLine()
					+ ": elemenat niza koji je final ne moze biti inkrementiran!", null);
		}
		if (obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji: " + deStatInc.getLine()
					+ ": Simbol mora biti promenjliva ili element niza!", null);
		}
		if (obj.getType().getKind() != Struct.Int) {
			report_error("Greska na liniji: " + deStatInc.getLine()
					+ "pogresan tip, operator ++ zahteva tip int ! ", null);
		}
	}

	public void visit(DeStatEqu deStatEqu) {
		Obj obj = deStatEqu.getDesignator().obj;
		if (obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji: " + deStatEqu.getLine()
					+ ": Simbol mora biti promenjliva ili element niza!", null);
		}
		if (!deStatEqu.getExpr().struct.assignableTo(obj.getType())) {
			report_error("Greska na liniji " + deStatEqu.getLine()
					+ ": tipovi su nekompatibilni", deStatEqu);
		}
	}
	
	public void visit(VarDeclError varDeclError){
		report_info("Oporavak od sintaksne greske pri deklaraciji promenjlivih do ';' ", varDeclError);
	}
	
	public void visit(VarListError varListError){
		report_info("Oporavak od sintaksne greske pri deklaraciji promenjlivih do ',' ", varListError);
	}
	
	public void visit(DeStatEquError deStatEquError){
		report_info("Oporavak od sintaksne greske pri dodeli vrednosti do ';' ", deStatEquError);
	}

	public void visit(StatPrint statPrint) {
		Struct expr = statPrint.getExpr().struct;

		if (expr.getKind() != Struct.Int && expr.getKind() != Struct.Char
				&& expr.getKind() != Struct.Bool) {
			report_error(
					"Greska na liniji "
							+ statPrint.getLine()
							+ ": ulazni parametar funkcije 'print' mora biti tipa int, char ili bool",
					null);
		}
	}

	public void visit(StatRead statRead) {
		Obj obj = statRead.getDesignator().obj;
		if (obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji: " + statRead.getLine()
					+ ": Simbol mora biti promenjliva ili element niza!", null);
		}
		if (obj.getType().getKind() != Struct.Int
				&& obj.getType().getKind() != Struct.Char
				&& obj.getType().getKind() != Struct.Bool) {
			report_error(
					"Greska na liniji "
							+ statRead.getLine()
							+ ": ulazni parametar funkcije 'read' mora biti tipa int, char ili bool",
					null);
		}
	}

	public void visit(GotoLab gotoLab){
		if(!labels.contains(gotoLab.getLab())){
			notDefLabels.add(gotoLab.getLab());
		}		
	}
	
	public void visit(Lab lab){
		notDefLabels.remove(lab.getLab());
		if(!labels.add(lab.getLab())){
			report_error("Vec postoji labela sa tim imenom!", lab);
		}
	}
	
	public boolean passed() {
		return !errorDetected;
	}
	

}
