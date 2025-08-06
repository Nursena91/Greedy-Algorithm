import java.io.*;
import java.util.*;


public class _2023510142_HW3 {

    static class Cashier {
    	double  totalCost = 0;  //initialize total cost of cashier
        int id;  //For every cashier
        int totalTransactions = 0;  //initialize total transaction count for every cashier
        List<Integer> transactions = new ArrayList<>();  //Holds the transaction types that cashier deals with.
        Set<Integer> types = new HashSet<>();  //Holds different types that cashier deals with (for max type per cashier).
        

        public Cashier(int id) {
            this.id = id;
        }
        
        
        void print() {  //It's for to see which cashier takes which transactions. It's not being used right now.
        	System.out.println("transactions");
        	for(int i =0;i<transactions.size();i++) {
        		System.out.println(transactions.get(i));
        	}
        }

        
        public double Cost(int baseCost, int transactionType, int globalProcessedCount) {  //Cost rules are all in this function
            int cost = baseCost + (globalProcessedCount / 5); //Counting inflation after every 5 customer.
            int allTransactions = transactions.size();  //To hold size
            
            boolean same = allTransactions > 0 && transactions.get(allTransactions - 1) == transactionType; //two same type rule

            
            if (allTransactions >= 2 &&  //three same type rule
                transactions.get(allTransactions - 1) == transactionType &&
                transactions.get(allTransactions - 2) == transactionType) {
                return cost * 1.5;
            }

            
            if (allTransactions > 0 && transactions.get(allTransactions - 1) > transactionType) {  //harder to simpler type rule
                return cost * 0.8;
            }

           
            if (same) {
                return 0.0;
            }
            
            return cost;
        }

        public boolean canAccept(int transactionType, int maxTypesPerCashier) {  //Controls if transaction types exceeded max type number per cashier
            return types.contains(transactionType) || types.size() < maxTypesPerCashier;
        }

        public void assign(int transactionType, double minCost) {  //assigning transaction and different types to best cashier
            if (!types.contains(transactionType)) {
                types.add(transactionType);
            }
            transactions.add(transactionType);
            totalTransactions++;
            totalCost += minCost;
        }
    }

    public static void main(String[] args) throws IOException {  //main function reads input file and then calls algorithm with parameters and creating output file.
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            int baseCost = Integer.parseInt(line.trim());
            int cashierCount = Integer.parseInt(reader.readLine().trim());
            int maxTypesPerCashier = Integer.parseInt(reader.readLine().trim());
            String[] types = reader.readLine()
            	    .replaceAll("[\\u00A0\\s]+", " ")
            	    .trim()
            	    .replaceAll("Type\\s*", "")
            	    .split(",\\s*");

            List<Integer> transactions = new ArrayList<>();
            for (String s : types) {
                //System.out.println(Integer.parseInt(s));
                transactions.add(Integer.parseInt(s));               
            }

            double result = greedy(baseCost, cashierCount, maxTypesPerCashier, transactions);  //greedy algorithm
            System.out.println(String.format("%.2f", result));  //writing results to console
            writer.write(String.format("%.2f\n", result));  //writing results to output file
        }

        reader.close();
        writer.close();
    }

    public static double greedy(int baseCost, int cashierCount, int maxTypesPerCashier, List<Integer> transactions) {  //the greedy algorithm
        List<Cashier> cashiers = new ArrayList<>();  //holds the input cashier
        for (int i = 0; i < cashierCount; i++) {
            cashiers.add(new Cashier(i));
        }

        int globalCount = 0;  //for inflation. holds customer numbers
        Cashier chosen = null;  //best cashier is null right now

        for (int t: transactions) {  //for every type, loop begins
            double minCost = Double.MAX_VALUE;
            chosen = null;

            for (Cashier c : cashiers) {  //for every type, every cashier is being processed.
            	if(c.transactions.isEmpty()) {  //if it's the first types, it assigns to cashiers at first. 
            		minCost = 0;
            		chosen = c;
            		break;
            	}else {  //for the next types
                    if (!c.canAccept(t, maxTypesPerCashier)) continue;  //Controls whether the cashier can take the type or not
                    double cost = c.Cost(baseCost, t, globalCount);  //Calculates cost
                    if (cost < minCost) {  //Chooses the best cashier
                        minCost = cost;
                        chosen = c;
                    }
            	}

            }

            if (chosen == null) {  //If there is no best cashier, returns -1
                return -1.00;
            }
            
            chosen.assign(t, minCost);  //after finding the best cashier for the type, assigns the cost and type to cashier.
            //chosen.print();
            globalCount++;  //customer increases by one
        }
        
        double total = 0;  //initializing result
        for (Cashier c : cashiers) {  
            total += c.totalCost;  //sum of all the minimum cost of all cashier
        }

        return total;
    }
}
