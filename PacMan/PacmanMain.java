// Nastassja Motro

import javax.swing.JFrame;

public class PacmanMain extends JFrame {
  public PacmanMain() {
    initGUI();
  }
  private void initGUI() {
    add(new PacmanBoard());
    setTitle("Pacman Game");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(380, 420);
    setLocationRelativeTo(null);
    setVisible(true);
  }
  // finally my entry point
  public static void main(String[] args) {
    new PacmanMain();
  }
}
