package be.rla.jimage.main;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
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

    // TODO keep simple <--> pref/options ?
    // TODO progressbar
    // TODO multi thread
    // TODO drop directory
    // TODO reset button : gui
    // TODO variable max/min + gui (pref?)
    // TODO field handler auto adjust
    // TODO variable sensibility tick + gui (pref?)

    private static final String version = "1.0";

    private static final int MIN_VALUE = -2000;
    private static final int MAX_VALUE = 2000;
    private static final int TICK = 1000;
    private static final int INIT_VALUE = getSlideValue(Context.getInstance().getFactor());

    private static String oldField = getText(INIT_VALUE);
    private static Boolean permitChange = Boolean.TRUE;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("jImageResizer v" + version);

        final ImageIcon imageIcon = new ImageIcon(JImageResizer.class.getClassLoader().getResource("dropzone.jpg"));
        JTextArea text = new JTextArea() {
            private static final long serialVersionUID = 1L;

            Image image = imageIcon.getImage();

            Image grayImage = GrayFilter.createDisabledImage(image);
            {
                setOpaque(false);
                setEditable(false);
                setLineWrap(true);
                setToolTipText("drag images here");
            }

            public void paint(Graphics g) {
                g.drawImage(grayImage, 25, 100, this);
                super.paint(g);
            }
        };
        frame.getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);

        new FileDrop(System.out, text, new FileDrop.Listener() {
            public void filesDropped(final File[] files) {
                for (final File file : files) {
                    try {
                        text.setText("");
                        text.append("treating : " + file.getName() + "\n");
                        final File destination = FilenameUtils.getDestination(file);
                        final BufferedImage original = ImageUtils.getImage(file);
                        text.append(">>> " + destination.getName() + "\n");
                        final BufferedImage resized = ImageUtils.resize(original, Context.getInstance().getFactor(), Context.getInstance().isHint());
                        ImageUtils.write(resized, destination);
                        text.append("Ok Done :-)\n");
                    } catch (Exception e) {
                        String message = e.getMessage();
                        if (message == null || "".equals(message)) {
                            message = "Not Possible : an error occurs";
                        }
                        text.append(message + " :-( !\n");
                        e.printStackTrace();
                    }
                }
            }
        });

        JTextField field = new JTextField(getText(INIT_VALUE));
        field.setHorizontalAlignment(JTextField.CENTER);

        JComboBox<String> combo = new JComboBox<>(new String[]{"smaller", "larger"});

        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, MIN_VALUE, MAX_VALUE, INIT_VALUE);
        slider.setMinorTickSpacing(TICK);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (getPermitChange()) {
                    final int value = slider.getValue();
                    final String text = getText(value);
                    field.setText(text);
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
                    getText(-v);
                } else {
                    setPermitChange(true);
                }
            }
        });

        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldListImpl(field, combo, slider);
            }
        });
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                fieldListImpl(field, combo, slider);
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
        frame.setResizable(false);
        frame.setVisible(true);
    }

    protected static void fieldListImpl(JTextField field, JComboBox<String> combo, JSlider slider) {
        final String rawText = field.getText();
        final String regex_double = "\\s*\\d*[\\.\\,]?\\d+\\s*[xX]?\\s*";
        if (rawText.matches(regex_double)) {
            String text = rawText.replace("X", "").replace("x", "").replace(" ", "").replace(",", ".");
            try {
                final double d = Double.parseDouble(text) * ("smaller".equals(combo.getSelectedItem()) ? -1 : 1);
                final int slideValue = getSlideValue(d);
                if (slideValue >= MIN_VALUE && slideValue <= MAX_VALUE) {
                    slider.setValue(slideValue);
                    final String t = getText(slideValue);
                    oldField = t;
                }
            } catch (Exception ex) {
            }
        }
        if (!rawText.equals(oldField)) {
            field.setText(oldField);
        }
    }

    protected static int getSlideValue(double value) {
        int n = (int) Math.round(Math.log10(Math.abs(value)) * TICK * Math.signum(value));
        return n;
    }

    protected static String getText(int value) {
        double v = ((double) value) / TICK;
        double scale = Math.pow(10, v);
        Context.getInstance().setFactor(scale);
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
