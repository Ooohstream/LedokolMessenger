package ledokolmessenger.client;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.client.ui.EntranceFrame;

/**
 *
 * @author OMEN
 */
public class StartClient {

    public static void main(String[] args) {
        try {
            new EntranceFrame().setVisible(true);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(StartClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
