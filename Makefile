all: Main.class

Main.class: Main.java Temp/CombineMap.class Temp/DefaultMap.class Temp/Label.class Temp/Offset.class Temp/SimpleExp.class Temp/Temp.class Temp/TempMap.class Mips/MipsFrame.class Mips/InFrame.class Mips/InReg.class
	javac Main.java


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

minijava: minijava.sablecc
	rm -rf minijava
	sablecc minijava.sablecc

.PHONY: clean
clean:
	rm -rf minijava *.class visitor/*.class Frame/*.class Mips/*.class Symbol/*.class Temp/*.class
