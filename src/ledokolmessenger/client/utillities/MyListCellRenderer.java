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

    private JPanel p;
    private JPanel iconPanel;
    private JLabel l;
    private JTextArea ta;

    public MyListCellRenderer() {
        p = new JPanel();
        p.setLayout(new BorderLayout());

        // icon
        iconPanel = new JPanel(new BorderLayout());
        l = new JLabel(); // <-- this will be an icon instead of a
        // text
        iconPanel.add(l, BorderLayout.NORTH);
        p.add(iconPanel, BorderLayout.WEST);

        // text
        ta = new JTextArea();
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        p.add(ta, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
            final boolean hasFocus) {

        String text = (String) value;
        ta.setText((String) value);
        
        l.setText("");
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0) {
            ta.setSize(width, Short.MAX_VALUE);
        }
        return p;

    }
}
