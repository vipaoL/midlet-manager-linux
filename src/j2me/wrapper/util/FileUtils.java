/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j2me.wrapper.util;

import j2me.wrapper.J2meWrapper;
import j2me.wrapper.MIDletInstaller;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author vipaol
 */
public class FileUtils {
    public static File createTempFile(String prefix) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(prefix, "");
            tmpFile.deleteOnExit();
        } catch (IOException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
            // TODO: error message "could not create temp file"
        }
        return tmpFile;
    }

    public static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, (ClassLoader) null)) {
            Path fileToExtract = fileSystem.getPath(fileName);
            System.out.println("extracting " + fileToExtract.toUri() + " to " + outputFile.toAbsolutePath());
            Files.copy(fileToExtract, outputFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void replaceInFile(Path path, String[][] mask) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        for (int i = 0; i < mask.length; i++) {
            content = content.replaceAll(mask[i][0], mask[i][1]);
        }
        Files.write(path, content.getBytes(charset));
    }
    
      /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName
     * @param outPath extract to
     * @throws java.io.FileNotFoundException
     * @throws java.net.URISyntaxException
     * @throws IOException
     */
    public static void exportResource(String resourceName, String outPath) throws FileNotFoundException, IOException, URISyntaxException {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = (new FileUtils()).getClass().getResourceAsStream(resourceName); //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new NullPointerException("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(outPath);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
    
    public static ImageIcon getScaledIcon(File iconFile, int sizePx) throws IOException {
        Image icon = ImageIO.read(iconFile).getScaledInstance(sizePx, sizePx, 0);
        return new ImageIcon(icon);
    }
    
    public static Attributes getJarManifestValues(Path pathToJar) throws IOException, NullPointerException {
        try {
            JarFile jarfile = new JarFile(pathToJar.toFile());
            return jarfile.getManifest().getMainAttributes();
        } catch (IOException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("Could not read jar");
        } catch (NullPointerException ex) {
            throw new NullPointerException("Could not read manifest");
        }
    }
    
    public static File[] getInstalledJars() {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        };
        File appsDir = new File(J2meWrapper.INSTALLATION_DIR + "apps");
        return appsDir.listFiles(filter);
    }
}
