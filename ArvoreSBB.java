// Guilherme Moreira de Carvalho, 26/06/2021 - Para Laboratório de Algoritmos e Estruturas de Dados II, 2021/1
// Classe ArvoreSBB: implementa a árvore; contém um atributo privado do tipo No "raiz", um atributo booleano "prop" para verificar o estado da propriedade da árvore SBB e
// quatro atributos estáticos: "count" inteiro para contar quantas comparações são feitas na pesquisa, "generator" do tipo SecureRandom para gerar inteiros aleátorios na construção da árvore e
// Horizontal e Vertical do tipo byte para qualificar a inclinação da ligação pai/filho;
// insere e pesquisa por itens nos nós e faz as transformações para balanceamento.
// Classe No: implementa um nó da árvore; contém um atributo do tipo Item "reg" e dois nos filhos "esq" e "dir".

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

public class ArvoreSBB {
    static final byte Horizontal = 0;
    static final byte Vertical = 1;

    private static class No {
        Item reg;
        No esq, dir;
        byte incE, incD;

        No(Item reg) {
            this.reg = reg;
            esq = null; dir = null;
            incE = Vertical; incD = Vertical;
        }
    }

    private No raiz;
    private boolean prop;

    static int count = 0;
    static SecureRandom generator = new SecureRandom();

    public ArvoreSBB() {
        raiz = null;
        prop = true;
    }

    // Se o No "ap" e seu filho e neto estão no mesmo nível
    // há duas ligações horizontais seguidas e
    // as ligações do filho se tornam verticais e ele se torna pai dos demais - transformação

    // transformação esquerda-esquerda
    private No ee(No ap) {
        No ap1 = ap.esq;
        ap.esq = ap1.dir; ap1.dir = ap;
        ap1.incE = Vertical; ap.incE = Vertical;
        ap = ap1;
        return ap; 
    }

    // transformação esquerda-direita
    private No ed(No ap) {
        No ap1 = ap.esq; No ap2 = ap1.dir;
        ap1.incD = Vertical; ap.incE = Vertical;
        ap1.dir = ap2.esq; ap2.esq = ap1;
        ap.esq = ap2.dir; ap2.dir = ap; ap = ap2;    
        return ap; 
    }

    // transformação direira-direita
    private No dd(No ap) {
        No ap1 = ap.dir;
        ap.dir = ap1.esq; ap1.esq = ap;
        ap1.incD = Vertical; ap.incD = Vertical;
        ap = ap1;
        return ap; 
    }

    // transformação direita-esquerda
    private No de(No ap) {
        No ap1 = ap.dir; No ap2 = ap1.esq;
        ap1.incE = Vertical; ap.incD = Vertical;
        ap1.esq = ap2.dir; ap2.dir = ap1;
        ap.dir = ap2.esq; ap2.esq = ap; ap = ap2;    
        return ap; 
    }

    private No insere(Item reg, No pai, No filho, boolean filhoEsq) {
        if (filho == null) {                            // se o nó corrente estiver vazio
            filho = new No(reg);                        // insere o registrador nele
            if (pai != null)                            // se o nó antecessor não for nulo
                if (filhoEsq) pai.incE = Horizontal;    // reduz um nivel no lado da inserção - Vertical -> Horizontal
                else pai.incD = Horizontal;
            prop = false;   // considera desbalancento
        } else if (reg.compara(filho.reg) < 0) {             // se o registro for maior que o nó corrente
            filho.esq = insere(reg, filho, filho.esq, true); // tenta inserir na sub-árvore esquerda
            if (!prop) {    // se a propriedade não é atendida - nós filho, neto e tataraneto no mesmo nível
                if (filho.incE == Horizontal) { // se o neto está no mesmo nivel do filho
                    if (filho.esq.incE == Horizontal) { // verifica o sentido do crescimento e efetua uma transformação
                        filho = ee(filho);
                        if (pai != null)
                            if (filhoEsq) pai.incE = Horizontal;
                            else pai.incD = Horizontal;
                    } else if (filho.esq.incD == Horizontal) {
                        filho = ed(filho);
                        if (pai != null) 
                            if (filhoEsq) pai.incE = Horizontal;
                            else pai.incD = Horizontal;            
                    }
                } else prop = true; // caso contrário, a propriedade já está atendida
            }
        } else if (reg.compara (filho.reg) > 0) {
            filho.dir = insere(reg, filho, filho.dir, false);
            if (!prop) {
                if (filho.incD == Horizontal) {
                    if (filho.dir.incD == Horizontal) {
                        filho = dd(filho);
                        if (pai != null)
                            if (filhoEsq) pai.incE = Horizontal;
                            else pai.incD = Horizontal;
                    } else if (filho.dir.incE == Horizontal) {
                        filho = de(filho);
                        if (pai != null)
                            if (filhoEsq) pai.incE = Horizontal;
                            else pai.incD = Horizontal;            
                    }
                } else prop = true;
            }
        } else {      
          //System.out.println ("Erro: Registro ja existente"); 
          prop = true;  // não altera a árvore
        }

        return filho;
    }
    
    private Item pesquisa(Item reg, No p) {
        if (p == null) {                    /*condição de parada: se o registro corrente estiver vazio*/
            return null;                    /*retorna null - registro não encontrado*/
        }
        else if (reg.compara(p.reg) < 0) {  /*se o registro for menor que o nó corrente*/
            count++;                        /*soma uma comparação ao contador*/
            return pesquisa(reg, p.esq);    /*retorna o registro encontrado na sub-árvore esquerda*/
        }
        else if (reg.compara(p.reg) > 0) {  /*se o registro for maior que o nó corrente*/
            count++;                        /*soma uma comparação ao contador*/
            return pesquisa(reg, p.dir);    /*retorna o registro encontrado na sub-árvore direita*/
        }
        return p.reg;
    }

    public static void main(String[] args) {
        String out = "";    /*saída para um arquivo de texto - resultado das comparações e tempo gasta em cada caso*/
        Item reg;           /*o registro a ser inserido ou procurado*/
        long tempoInicial, tempoFinal; /*tempos em nano segundos inicial e final da pesquisa por um registro*/

        for(int n = 1000; n <= 9000; n += 1000) {   /*árvores com n variando de 1000 a 9000 com passo 1000 - 9x2 árvores*/
            out += "n = " + n + "\n";
            ArvoreSBB tree_ord = new ArvoreSBB();   /*árvore ordenada*/
            ArvoreSBB tree_ran = new ArvoreSBB();   /*árvore aleatória*/

            for(int i = 0; i < n; i++) {    /*insere n nós nas árvores*/
                reg = new Item(i);  /*0 - n-1*/
                tree_ord.raiz = tree_ord.insere(reg, null, tree_ord.raiz, false);

                reg = new Item(generator.nextInt(n)); /*aleatórios de 0 - n-1*/
                tree_ran.raiz = tree_ran.insere(reg, null, tree_ran.raiz, false);
            }

            /*procura por itens inexistentes na árvore (-1 e n+1)*/
            count = 0;
            reg = new Item(-1);
            tempoInicial = System.nanoTime();
            tree_ord.pesquisa(reg, tree_ord.raiz);
            tempoFinal = System.nanoTime();
            out += "ERRO:REGISTRO NAO ENCONTRADO NA ARVORE ORDENADA (-1)\n";
            out += "Tempo gasto: " + (tempoFinal-tempoInicial) + " ns - Comparacoes: " + count + "\n";

            count = 0;
            reg = new Item(n+1);
            tempoInicial = System.nanoTime();
            tree_ord.pesquisa(reg, tree_ord.raiz);
            tempoFinal = System.nanoTime();
            out += "ERRO:REGISTRO NAO ENCONTRADO NA ARVORE ORDENADA (n+1)\n";
            out += "Tempo gasto: " + (tempoFinal-tempoInicial) + " ns - Comparacoes: " + count + "\n";

            count = 0;
            reg = new Item(-1);
            tempoInicial = System.nanoTime();
            tree_ran.pesquisa(reg, tree_ran.raiz);
            tempoFinal = System.nanoTime();
            out += "ERRO:REGISTRO NAO ENCONTRADO NA ARVORE RANDOMICA(-1)\n";
            out += "Tempo gasto: " + (tempoFinal-tempoInicial) + " ns - Comparacoes: " + count + "\n";

            count = 0;
            reg = new Item(n+1);
            tempoInicial = System.nanoTime();
            tree_ran.pesquisa(reg, tree_ran.raiz);
            tempoFinal = System.nanoTime();
            out += "ERRO:REGISTRO NAO ENCONTRADO NA ARVORE RANDOMICA(n+1)\n";
            out += "Tempo gasto: " + (tempoFinal-tempoInicial) + " ns - Comparacoes: " + count + "\n";
        }

        /*escreve a saída em um arquivo "pratica02.txt"*/
        try {
            FileWriter fp = new FileWriter("ArvoreSBB_Saida.txt");
            fp.write(out);
            fp.close();
        } catch (IOException e) {
            System.out.println("ERRO:NAO FOI POSSIVEL CRIAR O ARQUIVO");
        }
    }
}
