package be.rla.jimage.main;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.iharder.dnd.FileDrop;

public class JImageResizer {
    
    //TODO reset button : gui
    //TODO cste tick : code
    //TODO variable sensibility tick + gui (pref?)
    //TODO variable max/min + gui (pref?)
    //TODO field handler auto adjust
    //TODO progressbar 
    //TODO keep simple <--> pref/options ?
    //TODO multi thread 

    private static Boolean permitChange = Boolean.TRUE;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("jImageResizer");

        final JTextArea text = new JTextArea();
        frame.getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);

        new FileDrop(System.out, text, new FileDrop.Listener() {
            public void filesDropped(final File[] files) {
                for (final File file : files) {
                    try {
                        text.append("treating file : " + file.getCanonicalPath() + "\n");
                        final File destination = FilenameUtils.getDestination(file);
                        final BufferedImage original = ImageUtils.getImage(file);
                        final BufferedImage resized = ImageUtils.resize(original, Context.getInstance().getFactor(), Context.getInstance().isHint());
                        ImageUtils.write(resized, destination);
                        text.append("OK : " + destination.getCanonicalPath() + "\n");
                        if (text.getText().length() > 1000) {
                            text.setText("");
                        }
                    } catch (IOException e) {
                        text.append(e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                }
            }
        });

        JTextField field = new JTextField(getText(-100));
        field.setHorizontalAlignment(JTextField.CENTER);

        JComboBox<String> combo = new JComboBox<>(new String[]{"smaller", "larger"});

        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, -200, 200, -100);
        slider.setMinorTickSpacing(10);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (getPermitChange()) {
                    int value = slider.getValue();
                    field.setText(getText(value));
                    if (value < 0) {
                        if (!"smaller".equals(combo.getSelectedItem())) {
                            combo.setSelectedItem("smaller");
                        }
                    } else {
                        if (!"larger".equals(combo.getSelectedItem())) {
                            combo.setSelectedItem("larger");
                        }
                    }
                }
            }
        });

        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (getPermitChange()) {
                    setPermitChange(false);
                    int v = slider.getValue();
                    slider.setValue(-v);
                } else {
                    setPermitChange(true);
                }
            }
        });
        
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO implements handler with permitchange
                //FIXME
                System.out.println(e);
            }
        });

        JPanel south = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = .0;
        c.gridwidth = 2;
        c.gridy = 0;
        south.add(slider, c);
        c.weightx = .50;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        south.add(field, c);
        c.gridx = 1;
        south.add(combo, c);

        frame.getContentPane().add(south, BorderLayout.SOUTH);

        frame.setBounds(100, 100, 300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    protected static String getText(int value) {
        double v = value / 100.0;
        double scale = Math.pow(10, v);
        if (scale == 1.0) {
            return "no change !";
        } else {
            double scale2 = (scale < 1.0) ? 1 / scale : scale;
            return String.format("%.2f X", scale2);
        }
    }

    public static Boolean getPermitChange() {
        return permitChange;
    }

    public static void setPermitChange(Boolean permitChange) {
        JImageResizer.permitChange = permitChange;
    }

}
