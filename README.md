# Capítulo 3

Continue editando seu arquivo SableCC e adicione as produções da gramática de MiniJava.

## Gerar o Parser

Execute o seguinte comando:

$ sablecc minijava.sablecc

Se o comando executar com sucesso, deverá surgir uma pasta chamada "minijava"

## Compilar o Parser

$ javac Main.java

## Testar o Parser

Utilize os programas da pasta "programs" para testar seu parser:

$ java Main < programs/Factorial.java