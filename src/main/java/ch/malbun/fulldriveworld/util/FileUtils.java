package ch.malbun.fulldriveworld.util;

import ch.malbun.fulldriveworld.App;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {

  public static void save() throws IOException {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Speicherort wählen");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gleisplan World Datei", "*.gplw"));
    chooser.setInitialFileName("Gleisplan");
    File savedFile = chooser.showSaveDialog(new Popup());

    if (savedFile == null) return;

    FileOutputStream fos = new FileOutputStream(savedFile);
    ZipOutputStream zipOut = new ZipOutputStream(fos);

    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(App.tempDir);
    List<File> filesToZip = StreamSupport.stream(directoryStream.spliterator(), false).map(Path::toFile).filter(file -> !file.getName().split("\\.")[1].equals("jar")).toList();

    filesToZip.forEach(file -> {
      try {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
          zipOut.write(bytes, 0, length);
        }
        fis.close();

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    zipOut.close();
    fos.close();
    App.lastSaved = savedFile;


  }

  public static void load() throws IOException {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Speicherort wählen");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gleisplan World Datei", "*.gplw"));
    chooser.setInitialFileName("Gleisplan");
    File openedFile = chooser.showOpenDialog(new Popup());

    if (openedFile == null) return;


    ZipFile zipFile = new ZipFile(openedFile);

    for (int i = 0; i < 25; i++) {
      InputStream is1 = zipFile.getInputStream(zipFile.getEntry(i + ".gplx"));
      FileOutputStream fos1 = new FileOutputStream(App.tempDir + "\\" + i + ".gplx");
      fos1.write(is1.readAllBytes());
      fos1.close();

      InputStream is2 = zipFile.getInputStream(zipFile.getEntry(i + ".png"));
      FileOutputStream fos2 = new FileOutputStream(App.tempDir + "\\" + i + ".png");
      fos2.write(is2.readAllBytes());
      fos2.close();

    }

    zipFile.close();


  }

  public static void saveExist() throws IOException {
    File savedFile = App.lastSaved;

    if (savedFile == null) return;

    FileOutputStream fos = new FileOutputStream(savedFile);
    ZipOutputStream zipOut = new ZipOutputStream(fos);

    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(App.tempDir);
    List<File> filesToZip = StreamSupport.stream(directoryStream.spliterator(), false).map(Path::toFile).filter(file -> !file.getName().split("\\.")[1].equals("jar")).toList();

    filesToZip.forEach(file -> {
      try {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
          zipOut.write(bytes, 0, length);
        }
        fis.close();

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    zipOut.close();
    fos.close();
  }

}
