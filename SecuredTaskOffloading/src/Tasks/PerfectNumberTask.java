package Tasks;

import Contract.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Task to check if a number is a perfect number
 * A perfect number equals the sum of its proper divisors
 * Example: 6 = 1 + 2 + 3 (divisors of 6)
 * First few perfect numbers: 6, 28, 496, 8128
 */
public class PerfectNumberTask implements Task {
    
    private static final long serialVersionUID = 1L;
    
    private long number;
    
    /**
     * Constructor
     * @param number The number to check
     */
    public PerfectNumberTask(long number) {
        this.number = number;
    }
    
    /**
     * Execute the perfect number check
     * @return Result message indicating if number is perfect
     */
    @Override
    public Object execute() {
        if (number <= 0) {
            return number + " is not a perfect number (must be positive)";
        }
        
        // Find all divisors (excluding the number itself)
        List<Long> divisors = new ArrayList<>();
        long sum = 0;
        
        for (long i = 1; i <= number / 2; i++) {
            if (number % i == 0) {
                divisors.add(i);
                sum += i;
            }
        }
        
        // Check if sum equals the number
        StringBuilder result = new StringBuilder();
        result.append(number);
        
        if (sum == number) {
            result.append(" is a PERFECT number!\n");
            result.append("Divisors: ");
            for (int i = 0; i < divisors.size(); i++) {
                result.append(divisors.get(i));
                if (i < divisors.size() - 1) {
                    result.append(" + ");
                }
            }
            result.append(" = ").append(sum);
        } else {
            result.append(" is NOT a perfect number.\n");
            result.append("Sum of divisors (");
            for (int i = 0; i < divisors.size(); i++) {
                result.append(divisors.get(i));
                if (i < divisors.size() - 1) {
                    result.append(" + ");
                }
            }
            result.append(") = ").append(sum);
            result.append(" ≠ ").append(number);
        }
        
        return result.toString();
    }
    
    @Override
    public String getDescription() {
        return "Perfect Number Task: Check if " + number + " is perfect";
    }
    
    @Override
    public String toString() {
        return "PerfectNumberTask{number=" + number + "}";
    }
}
