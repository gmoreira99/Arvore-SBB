// Guilherme Moreira de Carvalho, 26/06/2021 - Para Laboratório de Algoritmos e Estruturas de Dados II, 2021/1
// Classe Item: implementa o registro de um nó da árvore; contém um atributo inteiro "chave"; compara sua chave com a de outro Item passado como argumento.

public class Item {
    private int chave;

    public Item(int chave) {
        this.chave = chave;
    }

    public int getChave() {
        return chave;
    }
    
    public int compara(Item it) {
        if (this.chave < it.chave) {
            return -1;
        }
        else if (this.chave > it.chave) {
            return 1;
        }
        return 0;
    }
}