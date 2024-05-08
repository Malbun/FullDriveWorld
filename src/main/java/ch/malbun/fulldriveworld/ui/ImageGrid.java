package ch.malbun.fulldriveworld.ui;

import ch.malbun.fulldriveworld.App;
import ch.malbun.fulldriveworld.util.TileService;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;

public class ImageGrid extends GridPane {

  public ArrayList<TrackPlan> trackPlans = new ArrayList<>();
  public int selectedIndex = 0;

  public ImageGrid(int size) throws IOException {
    setStyle("-fx-background-color: black;");
    setHgap(1);
    setVgap(1);

    for (int i = 0; i < Math.pow(size, 2); i++) {
      TrackPlan trackPlan = new TrackPlan(i);
      trackPlans.add(trackPlan);
    }

    trackPlans.forEach(trackPlan -> {
      int[] pos = TileService.getPosByIndex(size, trackPlan.index);
      add(trackPlan, pos[1], pos[0]);
    });

    trackPlans.forEach(TrackPlan::toggleBorderOn);
    trackPlans.forEach(TrackPlan::resetBorder);

  }

  public void resetSelections() {
    trackPlans.stream().filter(trackPlan -> trackPlan.selected).forEach(trackPlan -> {
      trackPlan.resetBorder();
      trackPlan.selected = false;
    });
  }

  public void edit() throws IOException {
    Runtime.getRuntime().exec("java -jar " + App.tempDir + "\\FullDrive.jar " + selectedIndex + " "  + selectedIndex, null, App.tempDir.toFile());
  }

  public void update() {
    trackPlans.forEach(TrackPlan::update);
  }
}
