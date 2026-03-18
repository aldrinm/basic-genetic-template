
//set parameters
//final int populationSize = 50;
final int populationSize = 50;
final float crossoverRate = 0.6f;
final float mutationRate = 0.05f;

void main() {

    int goal = 200;
    List<Organism> population = testCreatePopulation(goal);

    boolean done = false;
    // Fail-safe for max number of generations
    int generationCount = 0;
    int maxGenerations = 1000;

    while (!done) {
        System.out.println("generationCount = " + generationCount);
        System.out.println("population = " + population);
        List<Organism> nextPopulation = new ArrayList<>();
        while (nextPopulation.size() < populationSize) {
            Pair<Organism> parents = performSelection(population);

            //Crossover
            Random random = new Random();
            var crossoverTriggerPoint = random.nextFloat();
            Pair<Organism> offspring = null;
            if (crossoverTriggerPoint <= crossoverRate) {
                //preform crossover
                offspring = performCrossover(parents);
            } else {
                //offspring should be clones of parents
                offspring = new Pair<>(parents.t1(), parents.t2());
            }

            //Mutation
            var mutatedOffspring = performMutation(offspring);

            nextPopulation.add(mutatedOffspring.t1());
            nextPopulation.add(mutatedOffspring.t2());
        }

        //calculate the fitness for this new population
        for (Organism organism : nextPopulation) {
            organism.calculateFitness(goal);
        }

        population = nextPopulation;

        // Check if any organism has reached the goal (fitness of 1.0 or higher)
        for (Organism organism : population) {
            if (organism.fitness >= 1.0f) {
                System.out.println("Solution found in the " + generationCount + " gen : " + organism.chromosome.getGenes());

                System.out.println("recalc... ");
                organism.calculateFitness(goal);
                System.out.println("...done ");

                done = true;
                break;
            }
        }
        generationCount++;
        if (generationCount >= maxGenerations) {
            System.out.println("Max generations reached without finding a perfect solution.");
            done = true;
        }

    }
}

private Pair<Organism> performMutation(Pair<Organism> offspring) {
    Random random = new Random();
    for (Organism organism : List.of(offspring.t1(), offspring.t2())) {
        for (int i = 0; i < organism.chromosome.getGenes().length(); i++) {
            if (random.nextFloat() <= mutationRate) {
                char bit = organism.chromosome.getGenes().charAt(i);
                char toggledBit = (bit == '0') ? '1' : '0';
                String newGenes = organism.chromosome.getGenes().substring(0, i) + toggledBit + organism.chromosome.getGenes().substring(i + 1);
                organism.chromosome.setGenes(newGenes);
            }
        }
    }

    return offspring;
}

private Pair<Organism> performCrossover(Pair<Organism> parents) {
    //choose a random crossover point
    var random = new Random();
    int geneLen = parents.t1().chromosome.getGenes().length(); //genes are the same length
    int crossoverPoint = random.nextInt(geneLen);

    //now swap all bits to the right of the crossover point
    String parent1Genes = parents.t1().chromosome.getGenes();
    String parent2Genes = parents.t2().chromosome.getGenes();
    String child1Genes = parent1Genes.substring(0, crossoverPoint) + parent2Genes.substring(crossoverPoint);
    String child2Genes = parent2Genes.substring(0, crossoverPoint) + parent1Genes.substring(crossoverPoint);

    Organism child1 = new Organism();
    Organism child2 = new Organism();
    child1.chromosome = new Chromosome(child1Genes);
    child2.chromosome = new Chromosome(child2Genes);

    return new Pair<>(child1, child2);

}

private Pair<Organism> performSelection(List<Organism> population) {
    List<Organism> linkedlist = new LinkedList<>(population); //to maintain the order

    List<Double> cumulativeFitnesses = new ArrayList<>();
    double currentTotal = 0;
    for (Organism organism : linkedlist) {
        currentTotal += organism.fitness;
        cumulativeFitnesses.add(currentTotal);
    }
//    System.out.println("currentTotal = " + currentTotal);

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
