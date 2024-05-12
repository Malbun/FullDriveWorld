package ch.malbun.fulldriveworld;

import ch.malbun.fulldriveworld.ui.ImageGrid;
import ch.malbun.fulldriveworld.util.FileUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

  public static Path tempDir;
  public static ImageGrid imageGrid;
  public static File lastSaved;

  @Override
  public void start(Stage stage) throws IOException {
    tempDir = Files.createTempDirectory("temp");

    InputStream executable = App.class.getResourceAsStream("FullDrive.jar");
    File executableFile = new File(tempDir.toFile().getAbsolutePath() + "/FullDrive.jar");
    executableFile.deleteOnExit();
    FileOutputStream fos = new FileOutputStream(executableFile);
    assert executable != null;
    fos.write(executable.readAllBytes());
    fos.flush();
    fos.close();

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: black;");
    Scene scene = new Scene(root, 800, 800);
    scene.setFill(Color.BLACK);

    ScrollPane mainScrollPane = new ScrollPane();
    mainScrollPane.pannableProperty().set(true);
    mainScrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
    mainScrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
    mainScrollPane.setPrefSize(1500, 950);
    mainScrollPane.setStyle("-fx-background-color: black;");

    imageGrid = new ImageGrid(5);

    mainScrollPane.setContent(imageGrid);

    root.setCenter(mainScrollPane);

    MenuBar menuBar = new MenuBar();
    root.setTop(menuBar);

    Menu file = new Menu("Datei");
    menuBar.getMenus().add(file);

    MenuItem save = new MenuItem("Speichern");
    save.setOnAction(e -> {
      try {
        FileUtils.save();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });
    file.getItems().add(save);

    MenuItem load = new MenuItem("Laden");
    load.setOnAction(e -> {
      try {
        FileUtils.load();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });
    file.getItems().add(load);

    MenuItem export = new MenuItem("Als Bild exportieren");
    file.getItems().add(export);
    export.setOnAction(e -> {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Speichern");
      chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG-Bild", "*.png"));
      chooser.setInitialFileName("Gleisplan");
      File exportFile = chooser.showSaveDialog(new Popup());

      imageGrid.resetSelections();

      WritableImage snapshoted = imageGrid.snapshot(new SnapshotParameters(), null);
      BufferedImage bfImage = SwingFXUtils.fromFXImage(snapshoted, null);
      try {
        ImageIO.write(bfImage, "png", exportFile);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });

    Menu edit = new Menu("Bearbeiten");
    menuBar.getMenus().add(edit);

    MenuItem editTrack = new MenuItem("Plan bearbeiten");
    edit.getItems().add(editTrack);
    editTrack.setOnAction(e -> {
      try {
        imageGrid.edit();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });

    Menu help = new Menu("Hilfe");
    menuBar.getMenus().add(help);

    MenuItem website = new MenuItem("Website");
    help.getItems().add(website);
    website.setOnAction(e -> {
      Desktop desktop = Desktop.getDesktop();
      try {
        desktop.browse(new URI("https://malbun.github.io/FullDriveWorld"));
      } catch (IOException | URISyntaxException ex) {
        throw new RuntimeException(ex);
      }
    });

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        imageGrid.update();
      }
    };

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(timerTask, 0, 2000);

    Timer saveTimer = new Timer();
    saveTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (lastSaved != null) {
          try {
            FileUtils.saveExist();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }, 0, 30000);

    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (keyEvent.getCode() == KeyCode.E){
        try {
          imageGrid.edit();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else if (keyEvent.getCode() == KeyCode.U) {
        imageGrid.update();
      } else if (keyEvent.getCode() == KeyCode.A) {
        if (imageGrid.getScaleX() == 1) return;
        imageGrid.setScaleX(imageGrid.getScaleX() + 0.05);
        imageGrid.setScaleY(imageGrid.getScaleY() + 0.05);
      } else if (keyEvent.getCode() == KeyCode.S) {
        if (imageGrid.getScaleX() <= 0.15) return;
        imageGrid.setScaleX(imageGrid.getScaleX() - 0.05);
        imageGrid.setScaleY(imageGrid.getScaleY() - 0.05);
      }
    });

    stage.setScene(scene);
    stage.setResizable(true);
    stage.setOnCloseRequest(Event::consume);
    Image icon = null;
    try (InputStream is = App.class.getResourceAsStream( "dkw2.png")) {
      assert is != null;
      icon = new Image(is);
    } catch (IOException ignored) {}
    stage.getIcons().add(icon);

    stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
      Stage requestCloseStage = new Stage();
      VBox main = new VBox();

      Text information1Text = new Text("Wollen sie FullDrive wirklich schliessen?");
      information1Text.setTextAlignment(TextAlignment.CENTER);
      main.getChildren().add(information1Text);

      Text information2Text = new Text("Ungespeicherte Änderungen gehen verloren!");
      information2Text.setTextAlignment(TextAlignment.CENTER);
      main.getChildren().add(information2Text);

      HBox buttonBox = new HBox();

      Button yes = new Button("Ja");
      yes.setAlignment(Pos.CENTER_LEFT);
      yes.setOnAction(actionEvent -> {
        Platform.exit();
        System.exit(0);
      });
      buttonBox.getChildren().add(yes);

      buttonBox.setAlignment(Pos.CENTER);
      buttonBox.getChildren().add(new Separator());

      Button no = new Button("Nein");
      no.setAlignment(Pos.CENTER_RIGHT);
      no.setOnAction(actionEvent -> {
        requestCloseStage.close();
        windowEvent.consume();
      });
      no.setCancelButton(true);
      buttonBox.getChildren().add(no);

      main.getChildren().add(buttonBox);

      Image icon2 = null;
      try (InputStream is = App.class.getResourceAsStream( "weiche2.png")) {
        assert is != null;
        icon2 = new Image(is);
      } catch (IOException ignored) {}

      Scene closeScene = new Scene(main);
      requestCloseStage.setScene(closeScene);
      requestCloseStage.setResizable(false);
      requestCloseStage.setTitle("Wirklich Schliessen");
      requestCloseStage.getIcons().add(icon2);
      requestCloseStage.show();
    });
    stage.setTitle("FullDriveWorld");
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}