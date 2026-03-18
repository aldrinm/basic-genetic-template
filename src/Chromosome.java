public class Chromosome {

    public Chromosome(String genes) {
        this.genes = genes;
    }

    //first 4 bits for digits (0-9)
    //next 2 bits for operator
    String genes;

    public String getGenes() {
        return genes;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "genes='" + genes + '\'' +
                '}';
    }
}
