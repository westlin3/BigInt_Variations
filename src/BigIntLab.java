import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.MathContext;

public class BigIntLab {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */

    static long MAXVALUE =  200000000;

    static long MINVALUE = -200000000;

    static int numberOfTrials = 100;
    static int MAXINPUTSIZE  = (int) Math.pow(2,12);
    static int MININPUTSIZE  =  1;

    static String ResultsFolderPath = "/home/curtis/Bean/LAB6/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    static void runFullExperiment(String resultsFileName) {

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize*=2) {
//            String test1 = getNumericString(inputSize);
//            String test2 = getNumericString(inputSize);
//            BigInt TEST1 = new BigInt(test1);
//            BigInt TEST2 = new BigInt(test2);
            // progress message...
            System.out.println("Running test for input size " + inputSize + " ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */
            //System.gc();

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves



            BatchStopwatch.start(); // comment this line if timing trials individually


            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                fibFormulaBig(inputSize);
            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f\n", inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }
    }

    public static void main(String[] args) {
        runFullExperiment("FibonacciFormulaBig-Exp1.txt");
        runFullExperiment("FibonacciFormulaBig-Exp2.txt");
        runFullExperiment("FibonacciFormulaBig-Exp3.txt");
//        for (int i = 10; i<80; i+=10) {
//            System.out.printf("Fibonacci number for %d\n", i);
//            System.out.println("FibLoopBig");
//            System.out.println(fibLoopBig(i).ToString());
//            System.out.println("fibFormula");
//            System.out.println(fibFormula(i));
//            System.out.println("fibFormulaBig");
//            System.out.println(fibFormulaBig(i));
//            System.out.println("fibMatrix");
//            System.out.println(fibMatrixBig(i).ToString());
//            System.out.println();
//        }
    }

    static String getNumericString(int n)
    {
        // chose a Character random from this String
        String NumericString = "0123456789";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(NumericString.length()
                    * Math.random());
            // add Character one by one in end of sb
            sb.append(NumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public static BigInt fibLoopBig(int iterations) {
        BigInt temp_1 = new BigInt("1");
        BigInt temp_2 = new BigInt("0");
        BigInt fibonacci = new BigInt("1");
        for (int i = 0; i < iterations; i++) {
            fibonacci = temp_1.findSum(temp_2);
            temp_1 = temp_2;
            temp_2 = fibonacci;
        }
        return fibonacci;
    }

    // Wrapper function for Fibonacci matrix
    public static BigInt fibMatrixBig(int n) {
        BigInt one = new BigInt("1");
        if (n == 0)
            return one;
        if (n == 1)
            return one;

        return PowerMatrix(n);

    }

    public static BigInt[][] MultiplyMatrices(BigInt matrix[][]) {
        // multiply the matrix  {0 1}
        //                      {1 1}
        BigInt zero = new BigInt("0");
        BigInt one = new BigInt("1");
        BigInt x1 = matrix[0][0].Multiply(zero).findSum(matrix[0][1].Multiply(one));
        BigInt x2 = matrix[0][0].Multiply(one).findSum(matrix[0][1].Multiply(one));
        BigInt y1 = matrix[1][0].Multiply(zero).findSum(matrix[1][1].Multiply(one));
        BigInt y2 = matrix[1][0].Multiply(one).findSum(matrix[1][1].Multiply(one));

        //   returnMatrix  {x1 x2}
        //                 {y1 y2}
        BigInt returnMatrix[][] = {{x1, x2},{y1, y2}};

        return returnMatrix;
    }

    public static BigInt PowerMatrix(int n) {
        BigInt zero = new BigInt("0");
        BigInt one = new BigInt("1");

        BigInt matrix[][] = new BigInt[][]{{zero,one},{one,one}};
        for (int i = 2; i <= n; i++) {
            matrix = MultiplyMatrices(matrix);
        }
        return matrix[1][0];
    }

    public static long fibFormula(int n){
        double phi = (1 + Math.sqrt(5))/2;
        return (long) Math.round(Math.pow(phi, n)/Math.sqrt(5));
    }

    public static BigDecimal fibFormulaBig(int n){
        MathContext m = new MathContext(20);
        BigDecimal _a = new BigDecimal(2);
        BigDecimal _b = new BigDecimal(Math.sqrt(5));
        BigDecimal phi = BigDecimal.ONE.add(_b).divide(_a);
        phi = phi.pow(n);
        return phi.divideToIntegralValue(_b).round(m).stripTrailingZeros();
    }
}
