all: Main.class

Main.class: Main.java visitor/SymbolTable.class visitor/BuildSymbolTableAnalysis.class visitor/TypeCheckAnalysis.class
	javac Main.java

visitor/SymbolTable.class: visitor/SymbolTable.java minijava
	javac visitor/SymbolTable.java

visitor/BuildSymbolTableAnalysis.class: visitor/BuildSymbolTableAnalysis.java minijava
	javac visitor/BuildSymbolTableAnalysis.java

visitor/TypeCheckAnalysis.class: visitor/TypeCheckAnalysis.java minijava visitor/TypeCheckExpAnalysis.class
	javac visitor/TypeCheckAnalysis.java

visitor/TypeCheckExpAnalysis.class: visitor/TypeCheckExpAnalysis.java
	javac visitor/TypeCheckExpAnalysis.java

minijava: minijava.sablecc
	rm -rf minijava
	sablecc minijava.sablecc

.PHONY: clean
clean:
	rm -rf minijava *.class visitor/*.class
