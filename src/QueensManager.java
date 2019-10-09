import java.util.ArrayList;
import java.util.Random;

public class QueensManager {
    // constantes del problema
    public static int MAX_QUEENS = 8;
    public static int MAX_POPULATION = 30;
    public static int MAX_GENERATIONS = 200000;
    public static int SOLUTION_VAL = 28;
    public static double PROB_MUTATION = 0.01;

    // arreglo de strings que contiene poblacion
    public static ArrayList<String> population = new ArrayList<>();
    public static int totalfitness = 0;

    public static void initialPopulation(){
        String state = "";
        Random rand = new Random();

        for(int i = 0; i < MAX_QUEENS; i++){
            state += rand.nextInt(MAX_QUEENS) + 1;
        }

        for(int i = 0; i < MAX_POPULATION; i++){
            String s = shuffling(state, MAX_QUEENS);
            population.add(s);
            totalfitness += fitnessFunction(s);
        }
    }

    public static String shuffling(String state, int n) {
        int[] a = new int[n];

        int[] ind = new int[n];
        for (int i = 0; i < n; i++) {
            ind[i] = 0;
        }
        int index;
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            do {
                index = rand.nextInt(n);
            } while (ind[index] != 0);

            ind[index] = 1;
            a[i] = Character.getNumericValue(state.charAt(index));
        }

        String stateFinal = "";

        for(int i = 0; i < n; i++){
            stateFinal += a[i];
        }

        return stateFinal;
    }

    public static String geneticAlgorithm(){

        int indx = 0;
        String individual = "11111111";
        int n = population.size();

        while(indx < MAX_GENERATIONS && fitnessFunction(individual) != SOLUTION_VAL){
            ArrayList<String> newPopulation = new ArrayList<>();

            for(int i = 0; i < n; i++){
                int x = rouletteWheelSelection();
                int y = rouletteWheelSelection();
                String child = reproduce(x, y);
//            System.out.println("Child: " + child);

                if(PROB_MUTATION < 0.5) {
                    child = mutate(child);
                }
//            System.out.println("Mutated Child: " + child);

                newPopulation.add(child);

                if(fitnessFunction(child) == SOLUTION_VAL){
                    System.out.println("Solution found!");

                    individual = child;
                }
            }

            population = newPopulation;
            indx++;

//            System.out.println("Individual Candiadte");
//            System.out.println(individual);
//            System.out.println("Fitness: " + fitnessFunction(individual));
        }

        return individual;
    }

    public static String reproduce(int x, int y) {
        String parent1 = population.get(x);
        String parent2 = population.get(y);

//        System.out.println("Parent1: " + parent1);
//        System.out.println("Parent2: " + parent2);
        Random random = new Random();

        int n = parent1.length();
        int c = random.nextInt(n) + 1;

        return parent1.substring(0, c) + parent2.substring(c, n);
    }

    public static String mutate(String child){
        Random random = new Random();
        int n = child.length();
        int pos = random.nextInt(n);
        int val = random.nextInt(MAX_QUEENS) + 1;
        String newChild = "";
        newChild = child.substring(0, pos);
        newChild += val;
        newChild += child.substring(pos+1, n);

        return newChild;
    }

    public static int fitnessFunction(String s){
        int clashes = 0;
        int n = s.length();
//        System.out.println("String");
//        System.out.println(s);

        int row_col_clashes = Math.abs(s.length() - (int)s.chars().distinct().count());
//        System.out.println("row col clashes");
//        System.out.println(row_col_clashes);
//        System.out.println("distinct");
//        System.out.println(s.chars().distinct().count());
        clashes += row_col_clashes;

        ArrayList<String> savedDiagonals = new ArrayList<>();

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(i != j){
                    int firstVal = Character.getNumericValue(s.charAt(i));
                    int secondVal = Character.getNumericValue(s.charAt(j));
//                    int dx = i-j;
                    int dx = Math.abs(i-j);
                    int dy = Math.abs(firstVal - secondVal);
                    if(dx == dy && dx > 0) {
//                        System.out.println("gotcha");
//                        System.out.println("first" + firstVal);
//                        System.out.println("second" + secondVal);
//                        System.out.println("dx" + dx);
//                        System.out.println("dy" + dy);

                        if(!containsDiagonalPair(firstVal, secondVal, savedDiagonals)){
                            savedDiagonals.add(firstVal + "-" + secondVal);
                            clashes += 1;
                        }
                    }
                }
            }
        }

        return SOLUTION_VAL - clashes;
    }

    public static boolean containsDiagonalPair(int firstVal, int secondVal, ArrayList<String> diagonals){
        int n = diagonals.size();

        for(int i = 0; i < n; i++){
            String[] pairs = diagonals.get(i).split("-");
            int first = Integer.parseInt(pairs[0]);
            int second = Integer.parseInt(pairs[1]);

            if((first == firstVal && second == secondVal) || (second == firstVal && first == secondVal)){
                return true;
            }
        }

        return false;
    }

    public static int rouletteWheelSelection(){
        Random random = new Random();
        double r = random.nextDouble();
        double sum = 0;
        int n = population.size();

        for(int i = 0; i < n; i++){
            sum += individualProbability(population, i);

            if(r < sum) {
                return i;
            }
        }
        return 0;
    }

    public static double individualProbability(ArrayList<String> population, int indx){
//        System.out.println("totalfitness: " + totalfitness);
        return (double)fitnessFunction(population.get(indx)) / totalfitness;
    }

    public static void main(String[] args) {
        // Casos de prueba para evaluar fitness function
        System.out.println("Sample fitness function scores");
        System.out.println(fitnessFunction("51842736"));
        System.out.println(fitnessFunction("24748552"));
        System.out.println(fitnessFunction("32752411"));
        System.out.println(fitnessFunction("24415124"));
        System.out.println(fitnessFunction("32543213"));

        System.out.println("Generating population...");
        initialPopulation();
        System.out.println("Initial Population");
        System.out.println(population);
        System.out.println();

        System.out.println("****************************");
        System.out.println("Sorting...");
        System.out.println("Sorted population based on fitness");
        population.sort((String s1, String s2) -> {
           if(fitnessFunction(s1) > fitnessFunction(s2)) {
               return -1;
           } else if (fitnessFunction(s1) < fitnessFunction(s2)) {
               return 1;
           } else {
               return 0;
           }
        });
        System.out.println(population);
        System.out.println();

        System.out.println("*****************************");
        System.out.println("Implementing Algorithm and Finding Solution...");
        String solution = geneticAlgorithm();
        System.out.println("Individual Solution: " + solution);
        System.out.println("Fitness Function: " + fitnessFunction(solution));
        System.out.println();
    }
}
