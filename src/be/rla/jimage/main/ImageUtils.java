package be.rla.jimage.main;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

    public static BufferedImage getImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static BufferedImage resize(BufferedImage image, double factor, boolean hint) {
        final int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

        final int width = (int) Math.round(image.getWidth() * factor);
        final int height = (int) Math.round(image.getHeight() * factor);

        final BufferedImage resizedImage = new BufferedImage(width, height, type);
        final Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        if (hint) {
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        return resizedImage;
    }

    public static void write(BufferedImage image, File dest) throws IOException {
        final String name = dest.getName();
        final String ext = name.substring(name.lastIndexOf(".") + 1, name.length());
        ImageIO.write(image, ext, dest);
    }

}
