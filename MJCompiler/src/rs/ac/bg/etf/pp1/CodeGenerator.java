package rs.ac.bg.etf.pp1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPc;
	
	HashMap<String, Integer> labels = new HashMap<>();
	HashMap<String, List<Integer>> noDefLabels = new HashMap<>();
	
	public int getMainPc() {
		return mainPc;
	}
	
	@Override
	public void visit(MethodType methodType) {
		methodType.obj.setAdr(Code.pc);
		if ("main".equalsIgnoreCase(methodType.getName())) {
			mainPc = Code.pc;
		}
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(0);
		Code.put(methodType.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		labels.clear();
		noDefLabels.clear();
	}
	
	@Override
	public void visit(ReturnExpr ReturnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(ReturnNoExpr ReturnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(FactorConst factorConst) {
		Code.load(factorConst.getConstElemExt().obj);
	}
	
	@Override
	public void visit(FactorDesignator factorDesignator) {
		Code.load(factorDesignator.getDesignator().obj);
	}
	
	public void visit(FactorNewArray factorNewArray) {
		SyntaxNode ancestor = factorNewArray.getParent();
		while(!(ancestor instanceof DeStatEqu)){
			ancestor = ancestor.getParent();
		}
		DeStatEqu assignment = (DeStatEqu)ancestor;
		if(assignment.getDesignator().obj.getFpPos() == 1){
			Code.put(Code.dup);
			Code.put(Code.add);
		}
		Code.put(Code.newarray);
		if(factorNewArray.getType().struct == Tab.charType){
			Code.put(0);
		} else {
			Code.put(11);
		}
	}
	
	@Override
	public void visit(ExprAddop еxprAddop) {
		if(еxprAddop.getAddop() instanceof AddopPlus){
			Code.put(Code.add);
		} else {
			Code.put(Code.sub);			
		}
	}
	
	public void visit(TermMulop termMulop){
		if(termMulop.getMulop() instanceof MulopMul){
			Code.put(Code.mul);
		} else if(termMulop.getMulop() instanceof MulopMod){
			Code.put(Code.rem);
		} else {
			Code.put(Code.div);
		}
	}
	
	public void visit(ExprTermMinus exprTermMinus){
		Code.put(Code.neg);
	}
	
	@Override
	public void visit(DeStatEqu deStatEqu) {
		Obj obj = deStatEqu.getDesignator().obj;
		assignValue(obj);
	}

	private void assignValue(Obj obj) {
		if(obj.getFpPos() == 1 && obj.getKind() == Obj.Elem){
			Code.put(Code.enter);
			Code.put(3);
			Code.put(4);
			
			Code.put(Code.load_n);
			Code.put(Code.arraylength);
			Code.loadConst(2);
			Code.put(Code.div);
			Code.put(Code.load_1);
			Code.put(Code.add);
			Code.put(Code.store_3);
			
			Code.put(Code.load_n);
			Code.put(Code.load_3);
			Code.put(Code.aload);
			Code.loadConst(0);
			Code.putFalseJump(Code.eq, 0);
			int overThen = Code.pc - 2;
			
			Code.put(Code.load_n);
			Code.put(Code.load_3);
			Code.loadConst(1);
			Code.put(Code.astore);
			
			Code.put(Code.load_n);
			Code.put(Code.load_1);
			Code.put(Code.load_2);
			Code.put(Code.astore);
			
			Code.fixup(overThen);
			
			Code.put(Code.exit);
		} else {
			Code.store(obj);			
		}
	}
	
	public void visit(DeStatInc deStatInc){
		Obj obj = deStatInc.getDesignator().obj;
		if(obj.getKind() == Obj.Elem){
			Code.put(Code.dup2);
		}
		Code.load(obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(obj);
	}

	public void visit(DeStatDec deStatDec){
		Obj obj = deStatDec.getDesignator().obj;
		if(obj.getKind() == Obj.Elem){
			Code.put(Code.dup2);
		}
		Code.load(obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(obj);
	}
	
	@Override
	public void visit(StatPrint statPrint) {
		Code.loadConst(1);
		if(statPrint.getExpr().struct == Tab.charType){
			Code.put(Code.bprint);
		} else{
			Code.put(Code.print);
		}
	}
	
	public void visit(StatPrintMulti statPrintMulti) {
		Code.loadConst(statPrintMulti.getVal());
		if(statPrintMulti.getExpr().struct == Tab.charType){
			Code.put(Code.bprint);
		} else{
			Code.put(Code.print);
		}
	}	
	
	public void visit(StatRead statRead) {
		Obj obj = statRead.getDesignator().obj;
		if(obj.getType() == Tab.charType){
			Code.put(Code.bread);
		} else{
			Code.put(Code.read);
		}
		assignValue(obj);
	}
	
	public void visit(DesignatorArrayHelp designatorArrayHelp){
		Code.load(designatorArrayHelp.getDesignator().obj);
	}
	
	public void visit(CondFactNoRelop condFactNoRelop){
		Code.loadConst(1);
		Code.putFalseJump(Code.eq, 0);
		int adrBack = Code.pc - 2;
		condFactNoRelop.obj = new Obj(adrBack, null , null);
	}
	
	public void visit(CondFactRelop condFactRelop){
		int op = 1;
		if(condFactRelop.getRelop() instanceof RelopGt){
			op = Code.gt;
		} else if(condFactRelop.getRelop() instanceof RelopLt){
			op = Code.lt;
		} else if(condFactRelop.getRelop() instanceof RelopGe){
			op = Code.ge;
		} else if(condFactRelop.getRelop() instanceof RelopLe){
			op = Code.le;
		} else if(condFactRelop.getRelop() instanceof RelopEq){
			op = Code.eq;
		} else {
			op = Code.ne;
		}
		Code.putFalseJump(op, 0);
		int adrBack = Code.pc - 2;
		condFactRelop.obj = new Obj(adrBack, null , null);
	}
	
	public void visit(ExprCol exprCol){
		Code.putJump(0);
		int adrBack = Code.pc - 2;
		exprCol.obj = new Obj(adrBack, null , null);
		
		ExprTernar exprTernar = (ExprTernar)exprCol.getParent();		
		Code.fixup(exprTernar.getCondFact().obj.getKind());
	}
	
	public void visit(ExprTernar exprTernar){
		Code.fixup(exprTernar.getExprCol().obj.getKind());
	}
	
	public void visit(FactorAt factorAt){
		Code.load(factorAt.getDesignator().obj);
		
		Code.put(Code.enter);
		Code.put(2);
		Code.put(2);

		Code.put(Code.load_1);
		Code.put(Code.load_n);
		Code.put(Code.aload);
		
		Code.put(Code.load_1);

		Code.put(Code.load_1);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.load_n);
		Code.put(Code.sub);
		
		Code.put(Code.aload);
		
		Code.put(Code.add);
		
		Code.put(Code.exit);
	}
	
	public void visit(FactorMax factorMax){
		Code.put(Code.enter);
		Code.put(3);
		Code.put(4);
		
		// if
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.putFalseJump(Code.ge, 0);
		int overThen = Code.pc - 2;
		//then
		Code.put(Code.load_n);
		Code.putJump(0);
		int overElse = Code.pc - 2;
		//else
		Code.fixup(overThen);
		Code.put(Code.load_1);
		//end if
		Code.fixup(overElse);
		
		Code.put(Code.store_3);
		
		// if
		Code.put(Code.load_3);
		Code.put(Code.load_2);
		Code.putFalseJump(Code.ge, 0);
		overThen = Code.pc - 2;
		//then
		Code.put(Code.load_3);
		Code.putJump(0);
		overElse = Code.pc - 2;
		//else
		Code.fixup(overThen);
		Code.put(Code.load_2);
		//end if
		Code.fixup(overElse);
		
		
		Code.put(Code.exit);
	}
	
	public void visit(FactorHash factorHash){
		
		Code.put(Code.add);
		Code.loadConst('A');
		Code.put(Code.sub);
		Code.loadConst(26);
		Code.put(Code.rem);
		Code.loadConst(26);
		Code.put(Code.add);
		Code.loadConst(26);
		Code.put(Code.rem);
		Code.loadConst('A');
		Code.put(Code.add);
		
//		Code.put(Code.enter);
//		Code.put(2);
//		Code.put(3);
//		
//		Code.put(Code.load_1);
//		Code.loadConst(26);
//		Code.put(Code.rem);
//		
//		Code.put(Code.load_n);
//		Code.put(Code.add);
//		
//		Code.put(Code.store_2);
//		//if
//		Code.put(Code.load_2);
//		Code.loadConst('A');
//		Code.putFalseJump(Code.lt, 0);
//		int overThen = Code.pc - 2;
//		//then
//		Code.put(Code.load_2);
//		Code.loadConst(26);
//		Code.put(Code.add);
//		Code.put(Code.store_2);
//		// end if
//		Code.fixup(overThen);
//		//drugi if
//		Code.put(Code.load_2);
//		Code.loadConst('Z');
//		Code.putFalseJump(Code.gt, 0);
//		overThen = Code.pc - 2;
//		//then
//		Code.put(Code.load_2);
//		Code.loadConst(26);
//		Code.put(Code.sub);
//		Code.put(Code.store_2);
//		// end if
//		Code.fixup(overThen);
//		
//		Code.put(Code.load_2);
//		
//		Code.put(Code.exit);

	}
	
	public void visit(GotoLab gotoLab){
		Integer adrLab = labels.get(gotoLab.getLab());
		if(adrLab == null){
			Code.putJump(0);
			int gotoJump = Code.pc - 2;
			noDefLabels.putIfAbsent(gotoLab.getLab(), new LinkedList<>());
			noDefLabels.get(gotoLab.getLab()).add(gotoJump);
		} else {
			Code.putJump(adrLab);
		}
	}
	
	public void visit(Lab lab){
		List<Integer> fixupList = noDefLabels.getOrDefault(lab.getLab(), new LinkedList<>());
		for (Integer fixupAdr : fixupList) {
			Code.fixup(fixupAdr);
		}
		labels.put(lab.getLab(), Code.pc);
	}
	
	public void visit(FactorDol factorDol){
		Code.put(Code.enter);
		Code.put(1);
		Code.put(2);
		
		Code.loadConst(4);
		Code.put(Code.newarray);
		Code.put(0);
		Code.put(Code.store_1);
		for (int i = 0; i < 4; i++) {
			Code.put(Code.load_1);
			Code.loadConst(i);
			Code.put(Code.load_n);
			Code.loadConst((3-i)*8);
			Code.put(Code.shr);
			Code.put(Code.bastore);
		}
		
		Code.put(Code.load_1);
		
		Code.put(Code.exit);
	}
	
	public void visit(FactorCap factorCap){ 
		Code.loadConst(0);
		for (int i = 0; i < 8; i++) {
			Code.loadConst(2);
			Code.put(Code.mul);
			Code.load(factorCap.getDesignator().obj);
			Code.loadConst(i);
			Code.put(Code.aload);
			Code.put(Code.add);
		}
		
	}
	
	
}
