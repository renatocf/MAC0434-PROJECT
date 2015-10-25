all: Main.class

Main.class: Main.java visitor/SymbolTable.class visitor/BuildSymbolTableAnalysis.class visitor/TypeCheckAnalysis.class Temp/CombineMap.class Temp/DefaultMap.class Temp/Label.class Temp/Offset.class Temp/SimpleExp.class Temp/Temp.class Temp/TempMap.class Mips/MipsFrame.class Mips/InFrame.class Mips/InReg.class visitor/Translate.class InterpreterVisitor.class
	javac Main.java

InterpreterVisitor.class: InterpreterVisitor.java Tree/BINOP.class Tree/CALL.class Tree/CJUMP.class Tree/CONST.class Tree/CodeVisitor.class Tree/ESEQ.class Tree/EXPR.class Tree/Exp.class  Tree/ExpList.class  Tree/Hospitable.class  Tree/IntVisitor.class  Tree/JUMP.class  Tree/LABEL.class  Tree/MEM.class  Tree/MOVE.class  Tree/NAME.class  Tree/Print.class  Tree/ResultVisitor.class  Tree/SEQ.class  Tree/Stm.class  Tree/TEMP.class
	javac InterpreterVisitor.java

visitor/SymbolTable.class: visitor/SymbolTable.java minijava
	javac visitor/SymbolTable.java

visitor/BuildSymbolTableAnalysis.class: visitor/BuildSymbolTableAnalysis.java minijava
	javac visitor/BuildSymbolTableAnalysis.java

visitor/TypeCheckAnalysis.class: visitor/TypeCheckAnalysis.java minijava visitor/TypeCheckExpAnalysis.class
	javac visitor/TypeCheckAnalysis.java

visitor/TypeCheckExpAnalysis.class: visitor/TypeCheckExpAnalysis.java
	javac visitor/TypeCheckExpAnalysis.java

visitor/Translate.class: visitor/Translate.java visitor/Level.class visitor/Frag.class visitor/Exp.class  Tree/BINOP.class Tree/CALL.class Tree/CJUMP.class Tree/CONST.class Tree/CodeVisitor.class Tree/ESEQ.class Tree/EXPR.class Tree/Exp.class  Tree/ExpList.class  Tree/Hospitable.class  Tree/IntVisitor.class  Tree/JUMP.class  Tree/LABEL.class  Tree/MEM.class  Tree/MOVE.class  Tree/NAME.class  Tree/Print.class  Tree/ResultVisitor.class  Tree/SEQ.class  Tree/Stm.class  Tree/TEMP.class visitor/DataFrag.class visitor/Nx.class visitor/Ex.class
	javac visitor/Translate.java

visitor/Nx.class: visitor/Nx.java
	javac visitor/Nx.java

visitor/Ex.class: visitor/Ex.java
	javac visitor/Ex.java

visitor/DataFrag.class: visitor/DataFrag.java
	javac visitor/DataFrag.java

visitor/Frag.class: visitor/Frag.java
	javac visitor/Frag.java

visitor/Exp.class: visitor/Exp.java
	javac visitor/Exp.java

visitor/Level.class: visitor/Level.java visitor/Access.class
	javac visitor/Level.java

visitor/Access.class: visitor/Access.java
	javac visitor/Access.java

Temp/CombineMap.class: Temp/CombineMap.java
	javac Temp/CombineMap.java

Temp/DefaultMap.class: Temp/DefaultMap.java
	javac Temp/DefaultMap.java

Temp/Label.class: Symbol/Symbol.class Symbol/Table.class Temp/Label.java
	javac Temp/Label.java

Temp/Offset.class: Temp/Offset.java
	javac Temp/Offset.java

Temp/SimpleExp.class: Temp/SimpleExp.java
	javac Temp/SimpleExp.java

Temp/Temp.class: Temp/Temp.java
	javac Temp/Temp.java

Temp/TempMap.class: Temp/TempMap.java
	javac Temp/TempMap.java

Symbol/Symbol.class: Symbol/Symbol.java
	javac Symbol/Symbol.java

Symbol/Table.class: Symbol/Table.java
	javac Symbol/Table.java

Frame/Access.class: Frame/Access.java
	javac Frame/Access.java

Frame/Frame.class: Temp/TempMap.class Frame/Frame.java
	javac Frame/Frame.java

Mips/MipsFrame.class: Frame/Frame.class Mips/MipsFrame.java
	javac Mips/MipsFrame.java

Mips/InFrame.class: Frame/Access.class Mips/InFrame.java
	javac Mips/InFrame.java

Mips/InReg.class: Frame/Access.class Mips/InReg.java
	javac Mips/InReg.java

Tree/BINOP.class: Tree/BINOP.java
	javac Tree/BINOP.java

Tree/CALL.class: Tree/CALL.java
	javac Tree/CALL.java

Tree/CJUMP.class: Tree/CJUMP.java
	javac Tree/CJUMP.java

Tree/CONST.class: Tree/CONST.java
	javac Tree/CONST.java

Tree/CodeVisitor.class: Tree/CodeVisitor.java
	javac Tree/CodeVisitor.java

Tree/ESEQ.class: Tree/ESEQ.java
	javac Tree/ESEQ.java

Tree/EXPR.class: Tree/EXPR.java
	javac Tree/EXPR.java

Tree/Exp.class: Tree/Exp.java
	javac Tree/Exp.java

Tree/ExpList.class: Tree/ExpList.java
	javac Tree/ExpList.java

Tree/Hospitable.class: Tree/Hospitable.java
	javac Tree/Hospitable.java

Tree/IntVisitor.class: Tree/IntVisitor.java
	javac Tree/IntVisitor.java

Tree/JUMP.class: Tree/JUMP.java
	javac Tree/JUMP.java

Tree/LABEL.class: Tree/LABEL.java
	javac Tree/LABEL.java

Tree/MEM.class: Tree/MEM.java
	javac Tree/MEM.java

Tree/MOVE.class: Tree/MOVE.java
	javac Tree/MOVE.java

Tree/NAME.class: Tree/NAME.java
	javac Tree/NAME.java

Tree/Print.class: Tree/Print.java
	javac Tree/Print.java

Tree/ResultVisitor.class: Tree/ResultVisitor.java
	javac Tree/ResultVisitor.java

Tree/SEQ.class: Tree/SEQ.java
	javac Tree/SEQ.java

Tree/Stm.class: Tree/Stm.java
	javac Tree/Stm.java

Tree/TEMP.class: Tree/TEMP.java
	javac Tree/TEMP.java

minijava: minijava.sablecc
	rm -rf minijava
	./sablecc minijava.sablecc

.PHONY: clean
clean:
	rm -rf minijava *.class visitor/*.class Frame/*.class Mips/*.class Symbol/*.class Temp/*.class Tree/*.class
