
//set parameters
final int populationSize = 50;
final float crossoverRate = 0.6f;
final float mutationRate = 0.05f;

void main() {

    int goal = 48;
    List<Organism> population = testCreatePopulation(goal);


    performSelection(population);


}

private Pair<Organism> performSelection(List<Organism> population) {

    List<Organism> linkedlist = new LinkedList<>(population); //to maintain the order

    List<Double> cumulativeFitnesses = new ArrayList<>();
    double currentTotal = 0;
    for (Organism organism : linkedlist) {
        currentTotal += organism.fitness;
        cumulativeFitnesses.add(currentTotal);
    }
    System.out.println("currentTotal = " + currentTotal);


    //select a random number between 0 (exclusive) and total fitness (inclusive)
    Random random = new Random();
    double selectionPoint1 = random.nextDouble() * currentTotal;
    double selectionPoint2 = random.nextDouble() * currentTotal;

    Organism parent1 = null;
    Organism parent2 = null;

    for (int i = 0; i < cumulativeFitnesses.size(); i++) {
        if (parent1 == null && selectionPoint1 <= cumulativeFitnesses.get(i)) {
            parent1 = linkedlist.get(i);
        }
        if (parent2 == null && selectionPoint2 <= cumulativeFitnesses.get(i)) {
            parent2 = linkedlist.get(i);
        }
        if (parent1 != null && parent2 != null) break;
    }


    return new Pair<>(parent1, parent2);
}

private List<Organism> testCreatePopulation(int goal) {

    List<Organism> population = new ArrayList<>();

    for(int i=1; i<=populationSize; i++) {
        Organism og = new Organism();

        String genes = randomSingleDigitAs4BytesString() + randomOperatorAs2BytesString() + randomSingleDigitAs4BytesString() + randomOperatorAs2BytesString() + randomSingleDigitAs4BytesString();
        og.chromosome = new Chromosome(genes);
        og.calculateFitness(goal);
        population.add(og);
    }

    System.out.println("population = " + population);
    return population;
}

private String randomOperatorAs2BytesString() {
    int op = (int) (Math.random() * 4) + 1;
    return lookupOperatorString(op);
}

private String randomSingleDigitAs4BytesString() {
    int num = (int) (Math.random() * 10);
    return lookupNumberString(num);
}

private String lookupNumberString(int number) {
    return switch (number) {
        case 0 -> "0000";
        case 1 -> "0001";
        case 2 -> "0010";
        case 3 -> "0011";
        case 4 -> "0100";
        case 5 -> "0101";
        case 6 -> "0110";
        case 7 -> "0111";
        case 8 -> "1000";
        case 9 -> "1001";
        default -> "JUNK";
    };
}

private String lookupOperatorString(int number) {
    return switch (number) {
        case 1 -> "00"; //"+";
        case 2 -> "01"; //"-";
        case 3 -> "10"; //"*";
        case 4 -> "11"; //"/";
        default -> "JK";
    };
}
