package Tasks;

import Contract.Task;
import java.math.BigInteger;

/**
 * Task to calculate the nth Fibonacci number
 * Uses BigInteger to handle large numbers
 * Fibonacci sequence: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34...
 */
public class FibonacciTask implements Task {
    
    private static final long serialVersionUID = 1L;
    
    private int n;
    
    /**
     * Constructor
     * @param n The position in Fibonacci sequence (0-indexed)
     */
    public FibonacciTask(int n) {
        this.n = n;
    }
    
    /**
     * Execute the Fibonacci calculation
     * @return The nth Fibonacci number
     */
    @Override
    public Object execute() {
        if (n < 0) {
            return "Error: n must be non-negative";
        }
        
        if (n == 0) {
            return "Fibonacci(" + n + ") = 0";
        }
        
        if (n == 1) {
            return "Fibonacci(" + n + ") = 1";
        }
        
        // Use iterative approach with BigInteger for large numbers
        BigInteger prev = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;
        
        for (int i = 2; i <= n; i++) {
            BigInteger next = prev.add(current);
            prev = current;
            current = next;
        }
        
        return "Fibonacci(" + n + ") = " + current.toString();
    }
    
    @Override
    public String getDescription() {
        return "Fibonacci Task: Calculate " + n + "th Fibonacci number";
    }
    
    @Override
    public String toString() {
        return "FibonacciTask{n=" + n + "}";
    }
}
