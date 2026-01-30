package Tasks;

import Contract.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Task to find prime factorization of a number
 * Example: 60 = 2 × 2 × 3 × 5
 */
public class FactorizationTask implements Task {
    
    private static final long serialVersionUID = 1L;
    
    private long number;
    
    /**
     * Constructor
     * @param number The number to factorize
     */
    public FactorizationTask(long number) {
        this.number = number;
    }
    
    /**
     * Execute the factorization
     * @return List of prime factors
     */
    @Override
    public Object execute() {
        List<Long> factors = new ArrayList<>();
        long n = number;
        
        // Handle factor 2
        while (n % 2 == 0) {
            factors.add(2L);
            n = n / 2;
        }
        
        // Handle odd factors from 3 onwards
        for (long i = 3; i <= Math.sqrt(n); i += 2) {
            while (n % i == 0) {
                factors.add(i);
                n = n / i;
            }
        }
        
        // If n is still greater than 2, it's a prime factor
        if (n > 2) {
            factors.add(n);
        }
        
        // Return result as string for easy display
        if (factors.isEmpty()) {
            return number + " is prime (no factors)";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("Prime factorization of ").append(number).append(" = ");
        for (int i = 0; i < factors.size(); i++) {
            result.append(factors.get(i));
            if (i < factors.size() - 1) {
                result.append(" × ");
            }
        }
        
        return result.toString();
    }
    
    @Override
    public String getDescription() {
        return "Factorization Task: Find prime factors of " + number;
    }
    
    @Override
    public String toString() {
        return "FactorizationTask{number=" + number + "}";
    }
}
