
public class Genetics {
    // Chromosome: {EMASignalWeight, TBRSignalWeight, VOLSignalWeight,
    // MOMSignalWeight, PurchaseCount}

    int populationSize;
    double mutationChance;
    double crossoverChance;
    double eliteProportion;
    int tournamentK;
    int generations;

    // Initialises the genetics class with the provided parameters
    public Genetics(int populationSize, double mutationChance, double crossoverChance, double eliteProportion,
            int tournamentK, int generations) {
        this.populationSize = populationSize;
        this.mutationChance = mutationChance;
        this.crossoverChance = crossoverChance;
        this.eliteProportion = eliteProportion;
        this.tournamentK = tournamentK;
        this.generations = generations;

    }

    // Runs a single trading session with the specified weights (Similr to fitness
    // func but loads the data itself)
    public double runSingleRound(double[] weightsPop, double initalBudget, double initialPortfolio) {
        double[][] values = Base.loadAllValues();
        Trader trader = new Trader(initalBudget, initialPortfolio);
        return trader.trade(values, weightsPop);
    }

    // Runs a simulation using the parameters provided
    public void runSimulation(double initalBudget, double initialPortfolio, double[][] values) {

        double[][] weightsPop = generatePopulation(populationSize);
        double[] fitnesses = calculateFitnesses(weightsPop, values, initalBudget, initialPortfolio);

        double maxFitness = 0;
        double[] maxWeights = new double[5];
        int generation = 0;
        double eliteSize = eliteProportion * populationSize;

        for (int i = 0; i < generations; i++) {
            double[][] elite = selectElite(weightsPop, fitnesses, (int) eliteSize);
            double[][] pop1 = selection(weightsPop, fitnesses);
            double[][] pop2 = crossover(pop1, fitnesses);
            double[][] pop3 = mutate(pop2);
            double[][] finalPop = placeElite(pop3, elite);
            weightsPop = finalPop;
            fitnesses = calculateFitnesses(weightsPop, values, initalBudget, initialPortfolio);

            int newIndex = Helpers.getMaxIndex(fitnesses);
            if (fitnesses[newIndex] > maxFitness) {
                generation = i;
                maxFitness = fitnesses[newIndex];
                maxWeights = weightsPop[newIndex];
            }
        }
        Base.saveResult(maxFitness, maxWeights, generation, "results.txt");
    }

    // Returns an array of fitnesses which corrospond to the weights population
    public double[] calculateFitnesses(double[][] weightsPop, double[][] values, double initalBudget,
            double initialPortfolio) {
        double[] fitnesses = new double[weightsPop.length];
        for (int i = 0; i < weightsPop.length; i++) {
            Trader trader = new Trader(initalBudget, initialPortfolio);

            fitnesses[i] = trader.trade(values, weightsPop[i]);

        }

        return fitnesses;
    }

    // Returns a newly selected population, selected using the tourament selection
    // method
    public double[][] selection(double[][] weightsPop, double[] fitnesses) {
        double[][] newPop = new double[weightsPop.length][weightsPop[0].length];

        for (int i = 0; i < weightsPop.length; i++) {
            newPop[i] = tournamentSelection(weightsPop, tournamentK, fitnesses);
        }

        return newPop;
    }

    // Returns a single point mutated population, mutated depending on the mutation
    // probability
    public double[][] mutate(double[][] weightsPop) {
        double[][] newWeightsPop = new double[weightsPop.length][weightsPop[0].length];

        for (int i = 0; i < weightsPop.length; i++) {
            for (int j = 0; j < weightsPop[i].length; j++) {
                if (Math.random() < mutationChance) {
                    if (j == weightsPop[i].length - 1) {
                        newWeightsPop[i][j] = getShareCountGene();
                    } else {
                        newWeightsPop[i][j] = getWeightsGene();
                    }
                } else {
                    newWeightsPop[i][j] = weightsPop[i][j];
                }
            }
        }

        return newWeightsPop;
    }

    // Returns the highest fitness individual from a tournament of size k
    public double[] tournamentSelection(double[][] weightsPop, int k, double[] fitnesses) {
        int[] tournamentIndexes = new int[k];
        double[] tournamentFitnesses = new double[k];
        for (int i = 0; i < k; i++) {
            int randomIndex = (int) Math.round(Math.random() * (populationSize - 1));
            tournamentIndexes[i] = randomIndex;
            tournamentFitnesses[i] = fitnesses[randomIndex];
        }

        int maxIndex = Helpers.getMaxIndex(tournamentFitnesses);

        return weightsPop[tournamentIndexes[maxIndex]];
    }

    // Places the chosen elite individuals into the population in random positions
    public double[][] placeElite(double[][] weightsPop, double[][] elite) {
        double[][] newPop = weightsPop;

        for (int i = 0; i < elite.length; i++) {
            int randPositiion = (int) Math.floor(Math.random() * weightsPop.length);

            newPop[randPositiion] = elite[i];
        }

        return newPop;
    }

    // Retruns the eliteSize number of fittest individuals from the population
    public double[][] selectElite(double[][] weightsPop, double[] fitnesses, int eliteSize) {
        double[][] elite = new double[eliteSize][5];
        double[] eliteFitnesses = new double[eliteSize];

        for (int i = 0; i < eliteSize; i++) {
            int maxIndex = Helpers.getMaxIndex(fitnesses);
            eliteFitnesses[i] = fitnesses[maxIndex];
            fitnesses[maxIndex] = Double.MIN_VALUE;
            elite[i] = weightsPop[maxIndex];
        }

        return elite;
    }

    // Performs single point corssover on a population
    public double[][] crossover(double[][] weightsPop, double[] fitnesses) {
        double[][] newWeightsPop = new double[weightsPop.length][weightsPop[0].length];

        for (int i = 0; i < weightsPop.length - 1; i++) {
            if (Math.random() < crossoverChance) {
                double[] parent1 = weightsPop[i];
                double[] parent2 = tournamentSelection(weightsPop, tournamentK, fitnesses);

                double[] child1 = new double[parent1.length];
                double[] child2 = new double[parent2.length];

                // 0 | 1 | 2 | 3 | (buy/sell count)
                int crossoverPoint = (int) Math.round((Math.random() * 2) + 1);

                // Don't crossover the last geneome
                child1[parent1.length - 1] = parent1[parent1.length - 1];
                child2[parent2.length - 1] = parent2[parent2.length - 1];

                for (int j = 0; j < parent1.length - 1; j++) {
                    if (j < crossoverPoint) {
                        child1[j] = parent1[j];
                        child2[j] = parent2[j];
                    } else {
                        child1[j] = parent2[j];
                        child2[j] = parent1[j];
                    }
                }

                newWeightsPop[i] = child1;
                newWeightsPop[i + 1] = child2;
                i++;

            } else {
                newWeightsPop[i] = weightsPop[i];
            }
        }

        return newWeightsPop;
    }

    // Generates the last gene which dictates how many shares are bought or sold
    public double getShareCountGene() {
        return Helpers.round((Math.random() * 9) + 1, 0);
    }

    // Generates a random weight gene
    public double getWeightsGene() {
        return Helpers.round(Math.random(), 2);
    }

    // Generates an individual chromosome with 4 weight genes and 1 share count gene
    public double[] generateIndividual() {
        double[] individual = new double[5];

        for (int i = 0; i < 4; i++) {
            individual[i] = getWeightsGene();
        }

        individual[4] = getShareCountGene();

        return individual;
    }

    // Generates a population of individuals of size populationSize
    public double[][] generatePopulation(int populationSize) {
        double[][] newPopulation = new double[populationSize][5];
        for (int i = 0; i < populationSize; i++) {
            newPopulation[i] = generateIndividual();
        }

        return newPopulation;
    }

}
