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

    private List<SendableObject> activities = new LinkedList<>();

    public synchronized SendableObject dequeue() {
        while (this.activities.isEmpty())
            try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(BlockingQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        SendableObject activity = activities.get(0);
        activities.remove(activity);
        System.out.println(activities);
        return activity;
    }

    public synchronized void enqueue(SendableObject item) {
        activities.add(item);
        notify();
    }
}
