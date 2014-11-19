package be.rla.jimage.main;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.iharder.dnd.FileDrop;

public class JImageResizer {

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

        JButton button = new JButton("Preferences");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Preferences prefs = new Preferences();
                prefs.prefs();
            }
        });

        frame.getContentPane().add(button, BorderLayout.SOUTH);

        frame.setBounds(100, 100, 300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
