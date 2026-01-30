package Contract;

import java.io.Serializable;

/**
 * Base interface for all compute tasks
 * Tasks must be Serializable to be sent over network
 */
public interface Task extends Serializable {
    /**
     * Execute the task and return the result
     * @return The result of the computation
     */
    Object execute();
    
    /**
     * Get a description of this task
     * @return String description
     */
    String getDescription();
}
