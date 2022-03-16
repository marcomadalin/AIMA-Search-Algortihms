package Azamon;

import Azamon.*;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.io.*;
import java.util.*;



public class Main {
    private static double tmp_cost;
    private static int tmp_happiness;
    private static int tmp_time;


    public static void main(String[] args) throws IOException {
        //System.out.println("Introduiu el nom del fitxer");
        //Scanner sc = new Scanner(System.in);
        //String file_name = String.valueOf(sc.next());
//        String file_name = "debug";
        BufferedWriter w1 = new BufferedWriter(new FileWriter("experiment3.1.txt"));
//        BufferedWriter w1 = new BufferedWriter(new FileWriter(file_name +"-cost.txt"));
//        BufferedWriter w2 = new BufferedWriter(new FileWriter(file_name +"-felicitat.txt"));
//        BufferedWriter w3 = new BufferedWriter(new FileWriter(file_name +"-temps.txt"));
        //System.out.println("Introduiu 1 per executar un problema nou");
        /*while (Integer.valueOf(sc.next()) == 1) {
            System.out.println("Introduiu #paquets");
            int packs = Integer.valueOf(sc.next());
            System.out.println("Introduiu la proporcio");
            double prop = Double.valueOf(sc.next());
            System.out.println("Introduiu 1 per la solucio inicial 1, 2 per la segona, i qualsevol altre valor per la 3");
            int sol = Integer.valueOf(sc.next());
            System.out.println("Introduiu 1 per heuristic 1, qualsevol altre valor per a 2");
            int h = Integer.valueOf(sc.next());
            System.out.println("Introduiu 1 per hill climbing, qualsevol altre valor per SA");
            int alg = Integer.valueOf(sc.next());
            int op = 0;
            if (alg == 1) {
                System.out.println("Introduiu 1 per op1, 2 per op2, qualsevol altre valor per mix");
                op = Integer.valueOf(sc.next());
            }
            w1.write("Parametres: Paquets = " + packs + ", Proporcio = " + prop + ", Algoritme = " + alg +
                            ", Operador = " + op + ", Heuristic1 = " + h + ", Solucio = " + sol + "\n");
            System.out.println("Introduiu 1 per a introduir les seeds, qualsevol altre valor per aleatori");
            if (Integer.valueOf(sc.next()) == 1) {
                System.out.println("Introduiu la seed paquets seguida de la de transport");
                long startTime = System.currentTimeMillis();
                AzamonState a = new AzamonState(packs, Integer.valueOf(sc.next()), Integer.valueOf(sc.next()), prop);
                if (sol == 1) a.generate_solution1();
                else if (sol == 2) a.generate_solution2();
                else a.generate_solution3();
                azamon_local_search(a, h, op, alg, w1, w2, w3, startTime);
            } else {
                System.out.println("Introduiu el nombre de iteracions");
                int n = Integer.valueOf(sc.next());
                for (int i = 0; i < n; ++i) {
                    long startTime = System.currentTimeMillis();
                    Random rand = new Random(System.currentTimeMillis());
                    AzamonState a = new AzamonState(packs, rand.nextInt(), rand.nextInt(), prop);
                    if (sol == 1) a.generate_solution1();
                    else if (sol == 2) a.generate_solution2();
                    else a.generate_solution3();
                    azamon_local_search(a, h, op, alg, w1, w2, w3, startTime);
                }
            }
            System.out.println("Introduiu 1 per executar un problema nou");
        }*/
        //sc.close();

        int[] k_values = {1, 5, 10, 25, 125};
        double[] lambda_values = {1.0, 0.1, 0.01, 0.001, 0.0001};

        for (int k = 0; k < k_values.length; ++k) {
            for (int l = 0; l < lambda_values.length; ++l) {
                System.out.println("K: "+ k_values[k] + ", LAMBDA: " + lambda_values[l]);
                //w1.write("K: "+ k_values[k] + ", LAMBDA: " + lambda_values[l] + "\n");
                tmp_time = 0;
                tmp_happiness = 0;
                tmp_cost = 0;
                for (int i = 0; i < 10; ++i) {
                    long startTime = System.currentTimeMillis();
                    Random rand = new Random(System.currentTimeMillis());
                    AzamonState a = new AzamonState(100, rand.nextInt(), rand.nextInt(), 1.2);
                    a.generate_solution3();
                    azamon_local_search(a, 2, -1, 0, w1, null, null, startTime, k_values[k], lambda_values[l]);
                }
                w1.write(tmp_cost/10 + "\t" + tmp_happiness/10 + "\t" + tmp_time/10 + "\n");
            }
        }

        w1.close();
//        w2.close();
//        w3.close();
    }

    private static void azamon_local_search(AzamonState azamon, int h, int op, int alg, BufferedWriter w1, BufferedWriter w2, BufferedWriter w3, long startTime, int k, double l) {

        try {

            HeuristicFunction hc;
            if (h == 1) hc = new AzamonHeuristicFunction1();
            else hc = new AzamonHeuristicFunction2();

            SuccessorFunction fc;
            if (op == 1) fc = new AzamonSuccesorHCFunction1();
            else if (op == 2) fc = new AzamonSuccesorHCFunction2();
            else if (op > 2) fc = new AzamonSuccesorHCFunction();
            else fc = new AzamonSuccesorSAFunction();

            Problem problem =  new Problem(azamon, fc, new AzamonGoalTest(), hc);

            Search search;
            if (alg == 1) search =  new HillClimbingSearch();
            else search = new SimulatedAnnealingSearch(10000,100,k,l);

            SearchAgent agent = new SearchAgent(problem, search);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            //printActions(agent.getActions());
            //print_instrumentation(agent.getInstrumentation());

            AzamonState fin = (AzamonState) search.getGoalState();

//            w1.write(fin.get_cost() + "\n");
//            w2.write(fin.get_happiness() + "\n");
//            w3.write(totalTime + "\n");
            tmp_cost += fin.get_cost();
            tmp_happiness += fin.get_happiness();
            tmp_time += totalTime;

//            w1.write(fin.get_cost() + "\t");
//            w1.write(fin.get_happiness() + "\t");
//            w1.write(totalTime + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print_instrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}