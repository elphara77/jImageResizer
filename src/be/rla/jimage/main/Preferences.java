package be.rla.jimage.main;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Preferences {

    public void prefs() {
        JFrame frame = new JFrame("jImageResizer Preferences");

        LayoutManager layout = new GridLayout(3, 2);
        frame.setLayout(layout);

        JLabel factorL = new JLabel("Factor");
        JTextField factorF = new JTextField(new Double(Context.getInstance().getFactor()).toString());

        frame.add(factorL);
        frame.add(factorF);

        JLabel hintL = new JLabel("hint");
        JTextField hintF = new JTextField(new Boolean(Context.getInstance().isHint()).toString());

        frame.add(hintL);
        frame.add(hintF);

        JButton saveB = new JButton("Save");
        saveB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                boolean close = true;
                String text = factorF.getText();
                if (text.matches("\\d*(\\.\\d+)?")) {
                    Context.getInstance().setFactor(Double.parseDouble(text));
                } else {
                    close = false;
                }
                text = hintF.getText();
                if (close && text.matches("true|false|1|0")) {
                    if ("1".equals(text)) {
                        text = "true";
                    } else if ("0".equals(text)) {
                        text = "false";
                    }
                    Context.getInstance().setHint(Boolean.parseBoolean(text));
                } else {
                    close = false;
                }
                if (close) {
                    frame.dispose();
                }
            }
        });

        JButton cancelB = new JButton("Cancel");
        cancelB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                frame.dispose();
            }
        });

        frame.add(cancelB);
        frame.add(saveB);

        frame.setBounds(100, 100, 300, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
