package be.rla.jimage.main;

import java.io.File;

public class FilenameUtils {

    public static File getDestination(final File file) {
        final File p = file.getParentFile();
        File dest = file;
        while (dest.exists()) {
            final String filename = dest.getName();
            final String name = filename.substring(0, filename.lastIndexOf("."));
            final String ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            final String newFilename = name + "_resized." + ext;
            dest = new File(p, newFilename);
        }
        return dest;
    }
}
