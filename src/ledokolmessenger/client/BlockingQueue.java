package ledokolmessenger.client;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.SendableObject;

/**
 *
 * @author OMEN
 */
public class BlockingQueue {

    private final List<SendableObject> activities = new LinkedList<>();

    public synchronized SendableObject dequeue() {
        while (this.activities.isEmpty())
            try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(BlockingQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        SendableObject activity = activities.remove(0);
        return activity;
    }

    public synchronized void enqueue(SendableObject item) {
        activities.add(item);
        notify();
    }
    
    public synchronized SendableObject getFirst()
    {
        if(!activities.isEmpty())
            return activities.get(0);
        else return null;
    }

    @Override
    public String toString() {
        return "BlockingQueue{" + "activities=" + activities + '}';
    }
    
    
}
