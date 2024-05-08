package ch.malbun.fulldriveworld.util;

public class TileService {
  public static int[] getPosByIndex(int size, int index) {
    int row = (int) index / size;
    int col = index % size;
    return new int[]{row, col};
  }
}
