package ledokolmessenger.client.utillities;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

/**
 *
 * @author OMEN
 */
public class MyListCellRenderer implements ListCellRenderer{

    private JPanel panel;
    private JPanel iconPanel;
    private JLabel label;
    private JTextArea textArea;

    public MyListCellRenderer() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        iconPanel = new JPanel(new BorderLayout());
        label = new JLabel();
        
        iconPanel.add(label, BorderLayout.NORTH);
        panel.add(iconPanel, BorderLayout.WEST);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        panel.add(textArea, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
            final boolean hasFocus) {

        String text = (String) value;
        textArea.setText((String) value);
        
        label.setText("");
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0) {
            textArea.setSize(width, Short.MAX_VALUE);
        }
        return panel;

    }
}
