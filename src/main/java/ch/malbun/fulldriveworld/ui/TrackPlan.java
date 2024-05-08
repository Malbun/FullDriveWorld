package ch.malbun.fulldriveworld.ui;

import ch.malbun.fulldriveworld.App;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TrackPlan extends HBox {

  public int index;
  private ImageView imageView = new ImageView();
  public boolean selected = false;

  public TrackPlan(int index) throws IOException {
    this.index = index;

    imageView.setFitHeight(1000);
    imageView.setFitWidth(1000);
    getChildren().add(imageView);

    File gplxFile = new File(App.tempDir + "/" + index + ".gplx");
    gplxFile.deleteOnExit();
    FileOutputStream fos = new FileOutputStream(gplxFile);
    String toWrite = "{\"count\":0}";
    fos.write(toWrite.getBytes());
    fos.flush();
    fos.close();

    File defaultImageFile = new File(App.tempDir + "/" + index + ".png");
    defaultImageFile.deleteOnExit();
    FileOutputStream fos2 = new FileOutputStream(defaultImageFile);
    InputStream fis = App.class.getResourceAsStream("default.png");
    fos2.write(fis.readAllBytes());
    fos2.flush();
    fos2.close();

    update();

    setOnMouseClicked(e -> {
      App.imageGrid.resetSelections();
      selected = true;
      App.imageGrid.selectedIndex = index;
      toggleBorderOn();
    });

  }

  public void toggleBorderOn() {
    BorderStrokeStyle selectedBorderStrokeStyle = new BorderStrokeStyle(
            StrokeType.INSIDE,
            StrokeLineJoin.MITER,
            StrokeLineCap.BUTT,
            10,
            0,
            null
    );
    BorderStroke selectedBorderStroke = new BorderStroke(Color.VIOLET, selectedBorderStrokeStyle, new CornerRadii(0), new BorderWidths(1));
    setBorder(new Border(selectedBorderStroke));
  }

  public void update() {
    Image loadImg = null;
    try (InputStream is = new FileInputStream(App.tempDir + "/" + index + ".png")) {
      assert is != null;
      loadImg = new Image(is);
    } catch (IOException ignored) {}
    imageView.setImage(loadImg);
  }


  public void resetBorder() {
    BorderStrokeStyle borderStrokeStyle = new BorderStrokeStyle(
            StrokeType.INSIDE,
            StrokeLineJoin.MITER,
            StrokeLineCap.BUTT,
            10,
            0,
            null
    );
    BorderStroke borderStroke = new BorderStroke(Color.BLACK, borderStrokeStyle, new CornerRadii(0), new BorderWidths(1));
    Border standardBorder = new Border(borderStroke);
    setBorder(standardBorder);
  }
}
