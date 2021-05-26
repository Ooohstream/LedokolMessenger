package ledokolmessenger.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Exchanger;
import ledokolmessenger.client.ui.MainWindow;
import ledokolmessenger.serialized.SendableObject;

/**
 *
 * @author OMEN
 */
public class ReceiverThread extends Thread {

    Exchanger<SendableObject> exchanger = new Exchanger<>();
    ObjectInputStream inputStream;

    public ReceiverThread(ObjectInputStream inputStream, Exchanger exchanger) {
        this.inputStream = inputStream;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    SendableObject serverRespond = (SendableObject) this.inputStream.readObject();
                    
                    if(serverRespond.getType().equals("OldMessage"))
                    {
                        
                    }
                }
            } catch (IOException | ClassNotFoundException  e) {
                e.printStackTrace();
            }
        }
    }

}
