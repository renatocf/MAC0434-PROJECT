# Capítulo 5

Escreva 3 visitor para verificar os tipos de um programa MiniJava.

* `BuildSymbolTableAnalysis`: Deve criar a tabela de símbolos.
   Note que estamos disponibilizando a implementação da tabela
   de símbolos (`SymbolTable.java`) e alguns métodos do visitor.

* `TypeCheckExpAnalysis`: Deve realizar a verificação de tipos
   após a construção da tabela de símbolos. Alguns métodos já
   estão implementados.

* `TypeCheckExpAnalysis`: É um visitor auxiliar que verifica
   o tipo de expressões. Assim como os outros visitors, alguns
   métodos já estão implementados.

Para compilar, coloque o arquivo `minijava.sablecc` (fase anterior)
contendo a definição da gramática e ast na raiz do projeto e execute:

```bash
$ make
```
