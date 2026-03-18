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
        IO.println(String.format("Genes %s", genes));

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


        System.out.println("expression = " + expression);
        var unJunked = removeJunkGenes(expression.toString());
        System.out.println("unJunked = " + unJunked);
        var phenotype = evaluateExpression(unJunked);

        return 1 / abs((goal - phenotype) + 1);
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

        // First pass: Multiplication and Division
        for (int i = 0; i < list.size(); i++) {
            String token = list.get(i);
            if (token.equals("*") || token.equals("/")) {
                float left = Float.parseFloat(list.get(i - 1));
                float right = Float.parseFloat(list.get(i + 1));
                float res = token.equals("*") ? left * right : (right != 0 ? left / right : 0);
                list.set(i - 1, String.valueOf(res));
                list.remove(i); // remove operator
                list.remove(i); // remove right operand
                i--;
            }
        }

        // Second pass: Addition and Subtraction
        float result = Float.parseFloat(list.get(0));
        for (int i = 1; i < list.size(); i += 2) {
            String op = list.get(i);
            float val = Float.parseFloat(list.get(i + 1));
            if (op.equals("+")) result += val;
            else if (op.equals("-")) result -= val;
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
