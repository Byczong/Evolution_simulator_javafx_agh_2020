package pl.edu.agh.po.Simulation.GeneralClasses;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Genes {
    private static final int SIZE_OF_GENES = 32;

    private final int [] genesArray;

    public Genes(){
        this.genesArray = new int[SIZE_OF_GENES];
        this.generateRandomGenes();
        this.fixAllRotations();
    }

    public Genes(Genes parent1, Genes parent2){
        this.genesArray = new int[SIZE_OF_GENES];
        this.applyParentsGenes(parent1, parent2);
        this.fixAllRotations();
    }

    private void generateRandomGenes(){
        for(int i = 0; i < SIZE_OF_GENES; i++)
            genesArray[i] = ThreadLocalRandom.current().nextInt(0, 8);
        Arrays.sort(genesArray);
    }

    private void applyParentsGenes(Genes parent1, Genes parent2) {
        int spanLimit1 = ThreadLocalRandom.current().nextInt(1, SIZE_OF_GENES - 1);
        int spanLimit2 = spanLimit1;
        while(spanLimit2 == spanLimit1)
            spanLimit2 = ThreadLocalRandom.current().nextInt(1, SIZE_OF_GENES - 1);
        if (spanLimit1 > spanLimit2) {
            int tmp = spanLimit1;
            spanLimit1 = spanLimit2;
            spanLimit2 = tmp;
        }

        System.arraycopy(parent1.getGenesArray(), 0, genesArray, 0, spanLimit1);
        System.arraycopy(parent2.getGenesArray(), spanLimit1, genesArray, spanLimit1, spanLimit2 - spanLimit1);
        System.arraycopy(parent1.getGenesArray(), spanLimit2, genesArray, spanLimit2, SIZE_OF_GENES - spanLimit2);

        Arrays.sort(genesArray);
    }

    public void fixAllRotations(){
        boolean check = true;
        while(check){
            check = false;
            boolean[] occurs = new boolean[8];
            for(int i = 0; i < 8; i++)
                occurs[i] = false;
            for(int i = 0; i < SIZE_OF_GENES; i++)
                occurs[genesArray[i]] = true;
            for(int i = 0; i < 8; i++)
                if(!occurs[i]){
                    check = true;
                    break;
                }
            if(check)
                for(int i = 0; i < 8; i++)
                    if(!occurs[i])
                        genesArray[ThreadLocalRandom.current().nextInt(0, SIZE_OF_GENES)] = i;
        }

        Arrays.sort(genesArray);
    }

    public int[] getGenesArray() {
        return genesArray;
    }

    public int returnRandomGene(){
        return genesArray[ThreadLocalRandom.current().nextInt(0, SIZE_OF_GENES)];
    }

    @Override
    public String toString() {
        return Arrays.toString(genesArray);
    }
}
