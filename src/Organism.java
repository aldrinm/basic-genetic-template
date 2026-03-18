import static java.lang.Math.abs;

public class Organism {

    Chromosome chromosome;
    float fitness;

    static void main() {
//        testFitness();



    }




    private static void testFitness() {
        Organism og = new Organism();

        //String genes = "01010000010"; //5+1
        //String genes = "0101000001010100"; //5+1-4
        String genes = "0101000001010100100011110110"; //5+1-4*3/6
        og.chromosome = new Chromosome(genes);

        float fitness1 = og.fitness(10);
        System.out.println("fitness1 = " + fitness1);

    }

    private float fitness(int goal) {
        if (chromosome == null) {
            return 0.0f;
        }
        String genes = chromosome.getGenes();

        if (genes == null || genes.isEmpty()) {
            return 0.0f;
        }
        //IO.println(String.format("Genes %s", genes));

        StringBuilder expression = new StringBuilder();

        int bitIndex = 0;
        while (bitIndex < genes.length()) {
            // Read the first number (4 bits).
            if (bitIndex + 4 <= genes.length()) {
                //get the next 4 bits
                String numberString = genes.substring(bitIndex, bitIndex + 4);
                expression.append(lookupNumberString(numberString));
            } else {
                expression.append("JUNK");
            }

            bitIndex += 4;

            // read the operator if any
            if (bitIndex + 2 <= genes.length()) {
                //get the next 2 bits
                String operatorString = genes.substring(bitIndex, bitIndex + 2);
                expression.append(lookupOperatorString(operatorString));
            } else {
                expression.append("JUNK");
            }
            bitIndex += 2;
        }


        var unJunked = removeJunkGenes(expression.toString());
        var phenotype = evaluateExpression(unJunked);

        float fitnessVal = 1.0f / (1.0f + abs(goal - phenotype));
        System.out.println("expression = " + expression + " unJunked = " + unJunked + " phenotype = " + phenotype + " fitness = " + fitnessVal);

        return fitnessVal;
    }

    private String removeJunkGenes(String expression) {
        //remove the string JUNK from expression and if there is an operator +,-,*,/ just before that, remove that too
        String cleaned = expression.replaceAll("[+\\-*/]?JUNK", "");
        // Also handle the case where an operator might be left at the very end of the string
        if (!cleaned.isEmpty() && "+-*/".indexOf(cleaned.charAt(cleaned.length() - 1)) != -1) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        return cleaned;
    }

    private float evaluateExpression(String expression) {
        if (expression == null || expression.isEmpty()) return 0;

        // Split by operators but keep them in the array
        String[] tokens = expression.split("(?<=[+\\-*/])|(?=[+\\-*/])");
        if (tokens.length == 0) return 0;

        java.util.List<String> list = new java.util.ArrayList<>(java.util.Arrays.asList(tokens));

        System.out.println("Before first pass, list = " + list);
        // First pass: Multiplication and Division
        for (int i = 0; i < list.size(); i++) {
            String token = list.get(i);
            if (token.equals("*") || token.equals("/")) {
                if (i == 0 || i + 1 >= list.size()) { // Invalid operator position
                    list.remove(i);
                    i--;
                    continue;
                }
                try {
                    float left = Float.parseFloat(list.get(i - 1));
                    float right = Float.parseFloat(list.get(i + 1));
                    float res = token.equals("*") ? left * right : (right != 0 ? left / right : 0);
                    list.set(i - 1, String.valueOf(res));
                    list.remove(i); // remove operator
                    list.remove(i); // remove right operand
                    i--;
                } catch (NumberFormatException e) {
                    // One of the operands is not a number, e.g. "5 * + 2"
                    list.remove(i); // remove the operator
                    i--;
                }
            }
        }

        System.out.println("Before second pass, list = " + list);
        // Second pass: Addition and Subtraction
        if (!list.isEmpty() && (list.get(0).equals("+") || list.get(0).equals("-"))) {
            list.add(0, "0");
        }

        if (list.isEmpty()) return 0;

        float result;
        try {
            result = Float.parseFloat(list.get(0));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return 0; // malformed expression
        }

        for (int i = 1; i < list.size(); i += 2) {
            String op = list.get(i);
            if (i + 1 >= list.size()) break; // trailing operator
            try {
                float val = Float.parseFloat(list.get(i + 1));
                if (op.equals("+")) result += val;
                else if (op.equals("-")) result -= val;
            } catch (NumberFormatException e) {
                // a case like "5 + -"
                break;
            }
        }

        return result;
    }

    private String lookupNumberString(String numberString) {
        return switch (numberString) {
            case "0000" -> "0";
            case "0001" -> "1";
            case "0010" -> "2";
            case "0011" -> "3";
            case "0100" -> "4";
            case "0101" -> "5";
            case "0110" -> "6";
            case "0111" -> "7";
            case "1000" -> "8";
            case "1001" -> "9";
            default -> "JUNK";
        };
    }

    private String lookupOperatorString(String operatorString) {
        return switch (operatorString) {
            case "00" -> "+";
            case "01" -> "-";
            case "10" -> "*";
            case "11" -> "/";
            default -> "JUNK";
        };
    }

    @Override
    public String toString() {
        return "Organism{" +
                "chromosome=" + chromosome +
                ", fitness=" + fitness +
                '}';
    }

    public void calculateFitness(int goal) {
        this.fitness = fitness(goal);
    }
}
