import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Event;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PacmanBoard extends JPanel implements ActionListener {
  private Dimension dim;
  
  private final Color dots = new Color(255, 234, 16); // color of the dots on the maze
  private Color mColor; // color of the maze
  private final Font font = new Font("Serif", Font.BOLD, 16); // font to be used for the score and introduction screen
  
  // all the images
  private Image image;
  private Image ghost;
  private Image pacman;
  private Image pacmanUp, pacmanDown, pacmanRight, pacmanLeft;
  private Image pacman2Up, pacman2Down, pacman2Right, pacman2Left;
  private Image pacman3Up, pacman3Down, pacman3Right, pacman3Left;
  
  private boolean dead = false; // always false until the player dies
  private boolean inGame = false; // always false until player initiates the game in the introduciton screen
  private int pacsLeft; // more than one life
  private int score;
  
  // setting position and movement for the ghosts and pacman
  private int[] ghostX, ghostY, ghostdX, ghostdY;
  private int pacmanX, pacmanY, pacmandX, pacmandY;
  private int[] dx, dy;
  private int recdX, recdY;
  private int viewdX, viewdY;
  
  private int GHOST_NUMBER = 6; // how many ghosts are there
  private int GHOST_MAX = 12; // how many ghosts can there be in total
  
  // the animation stuff is for pacman
  private final int ANIMATION_DELAY = 2;
  private final int ANIMATION_COUNT = 4;
  private int pacmanCount = ANIMATION_DELAY;
  private int pacmanPosition = 0;
  private int pacmanDirection = 1; //can only be facing or going one direction at a time
  
  // setting up speeds
  private short[] ghostSpeed = new short[GHOST_NUMBER];
  private final int PACMAN_SPEED = 6;
  private final int speeds[] = {1, 2, 3, 4, 6, 8};
  private final int maxSpeed = 6;
  private int currentSpeed = 3;
  
  private final int BLOCKS = 24;
  private final int NUMBER_OF_BLOCKS = 15;
  private final int SCREEN = BLOCKS * NUMBER_OF_BLOCKS;
  private final short level[] = {
    19,26,26,22, 9,12,19,26,22, 9,12,19,26,26,22,
    37,11,14,17,26,26,20,15,17,26,26,20,11,14,37,
    17,26,26,20,11, 6,17,26,20, 3,14,17,26,26,20,
    21, 3, 6,25,22, 5,21, 7,21, 5,19,28, 3, 6,21,
    21, 9, 8,14,21,13,21, 5,21,13,21,11, 8,12,21,
    25,18,26,18,24,18,28, 5,25,18,24,18,26,18,28,
    6,21, 7,21, 7,21,11, 8,14,21, 7,21, 7,21,03,
    4,21, 5,21, 5,21,11,10,14,21, 5,21, 5,21, 1,
    12,21,13,21,13,21,11,10,14,21,13,21,13,21, 9,
    19,24,26,24,26,16,26,18,26,16,26,24,26,24,22,
    21, 3, 2, 2, 6,21,15,21,15,21, 3, 2, 2,06,21,
    21, 9, 8, 8, 4,17,26, 8,26,20, 1, 8, 8,12,21,
    17,26,26,22,13,21,11, 2,14,21,13,19,26,26,20,
    37,11,14,17,26,24,22,13,19,24,26,20,11,14,37,
    25,26,26,28, 3, 6,25,26,28, 3, 6,25,26,26,28
  };
  
  private short[] sData;
  private Timer timer;
  
  public PacmanBoard() {
    loadImages();
    initBoard();
    initVar();
  }
  
  // initializing the board setup
  public void initBoard() {
    addKeyListener(new TAdapter());
    setBackground(Color.BLACK);
    setFocusable(true); //lets component (JPanel) gain power of getting focused
    setDoubleBuffered(true); //double buffer creates an image off screen then displays it all at once
  }
  
  // initiates variables in the game
  public void initVar() {
    dim = new Dimension(400, 400);
    mColor = new Color(13, 255, 71);
    ghostX = new int[GHOST_MAX];
    ghostY = new int[GHOST_MAX];
    ghostdX = new int[GHOST_MAX];
    ghostdY = new int[GHOST_MAX];
    dx = new int[4];
    dy = new int[4];
    sData = new short[NUMBER_OF_BLOCKS * NUMBER_OF_BLOCKS];
    timer = new Timer(40, this);
    timer.start();
  }
  
  @Override
  public void addNotify() {
    super.addNotify();
    initGame();
  }
  
  // initiating game as a whole with starting game variables
  public void initGame() {
    score = 0;
    currentSpeed = 3;
    GHOST_NUMBER = 6;
    pacsLeft = 3;
    initLevel();
  }
  
  // initializing each level
  public void initLevel() {
    for(int i = 0; i < NUMBER_OF_BLOCKS * NUMBER_OF_BLOCKS; i++) {
        sData[i] = level[i];
      }
    contLevel();
  }
  
  // continuing the level after initializing
  public void contLevel() {
    short i;
    int dx = 1;
    int random;
    for(i = 0; i < GHOST_NUMBER; i++) {
        ghostX[i] = BLOCKS * 4;
        ghostY[i] = BLOCKS * 4;
        ghostdX[i] = dx;
        ghostdY[i] = 0;
        dx = -dx;
        random = (int) (Math.random() * (currentSpeed + 1));
        if(random > currentSpeed) {
          random = currentSpeed;
        }
        ghostSpeed[i] = (short) speeds[random];
    }
    pacmanX = BLOCKS * 7;
    pacmanY = BLOCKS * 11;
    pacmandX = 0;
    pacmandY = 0;
    recdX = 0;
    recdY = 0;
    viewdX = -1;
    viewdY = 0;
    dead = false;
  }
  
  // loading all the images using ImageIcon
  // there's three different pacmans because you have three lives
  private void loadImages() {
    ghost = new ImageIcon("images/ghost.png").getImage();
    pacman = new ImageIcon("images/pacman.png").getImage();
    pacmanUp = new ImageIcon("images/pacmanUp.png").getImage();
    pacmanDown = new ImageIcon("images/pacmanDown.png").getImage();
    pacmanRight = new ImageIcon("images/pacmanRight.png").getImage();
    pacmanLeft = new ImageIcon("images/pacmanLeft.png").getImage();
    pacman2Up = new ImageIcon("images/pacman2Up.png").getImage();
    pacman2Down = new ImageIcon("images/pacman2Down.png").getImage();
    pacman2Right = new ImageIcon("images/pacman2Right.png").getImage();
    pacman2Left = new ImageIcon("images/pacman2Left.png").getImage();
    pacman3Up = new ImageIcon("images/pacman3Up.png").getImage();
    pacman3Down = new ImageIcon("images/pacman3Down.png").getImage();
    pacman3Right = new ImageIcon("images/pacman3Right.png").getImage();
    pacman3Left = new ImageIcon("images/pacman3Left.png").getImage();
  }
  
  private void play(Graphics2D g2D) {
    if (dead) {
      death();
    } else {
      check();
      ghostMovement(g2D);
      pacMovement();
      drawPac(g2D);
    }
  }
  
  /* there's an introduction screen that pops up before the player is able to play the game
     it prompts the user to press the 's' key in order to start the game in which it then proceeds to the actual game screen
  */
  private void introScreen(Graphics2D g2D) {
    g2D.setColor(new Color(23, 46, 255));
    g2D.fillRect(50, SCREEN / 2 - 20, SCREEN - 100, 50);
    g2D.setColor(Color.WHITE);
    g2D.drawRect(50, SCREEN / 2 - 20, SCREEN - 100, 50);
    String s = "Press s to start.";
    Font smallF = new Font("Serif", Font.BOLD, 16);
    FontMetrics met = this.getFontMetrics(smallF);
    g2D.setColor(Color.WHITE);
    g2D.setFont(smallF);
    g2D.drawString(s, (SCREEN - met.stringWidth(s)) / 2, SCREEN / 2);
  }
  
  // this draws up a game score at the bottom right hand corner of the screen
  private void gameScore(Graphics2D g) {
    String s;
    g.setFont(font);
    g.setColor(new Color(255, 25, 42));
    s = "Score: " + score;
    g.drawString(s, SCREEN / 2 + 96, SCREEN + 16);
    for (int i = 0; i < pacsLeft; i++) {
      g.drawImage(pacman2Left, i * 28 + 8, SCREEN + 1, this);
    }
  }
  
  // it's in the name, you get hit by a ghost and you "die". three deaths and that's the end of the game
  private void death() {
    pacsLeft --;
    if(pacsLeft == 0) {
      inGame = false;
    }
    contLevel();
  }
  
  /*
  this method checks to see if the maze that drawn is actually finished so basically if it's done drawing then
  it initializes the game and proceeds with the rest of the code. Otherwise it continues to draw the maze until it's done
  */
  private void check() {
    boolean done = true;
    short i = 0;
    while (i < NUMBER_OF_BLOCKS * NUMBER_OF_BLOCKS && done) {
      if((sData[i] & 48) != 0) {
        done = false;
      }
      i++;
    }
    if(done) {
      score += 50;
      if(GHOST_NUMBER < GHOST_MAX) {
        GHOST_NUMBER++;
      }
      if(currentSpeed < maxSpeed) {
        currentSpeed ++;
      }
      initLevel();
    }
  }
  
  /* draws the maze. the different if statements represent the different numbers that can be seen
  in the array of level[]. The numbers provide information on what is a corner or not in the game.
  the numbers used are 0, 1, 2, 4, 8, and 16. 0 represents a space or nothing in the maze, 1 is a
  left corner, 2 is a top corner, 4 is a right corner, 8 is a bottom corner and 16 represents a point
  */
  private void drawMaze(Graphics2D g2D) {
    short z = 0;
    for(int y = 0; y < SCREEN; y += BLOCKS) {
      for(int x = 0; x < SCREEN; x += BLOCKS) {
        g2D.setStroke(new BasicStroke(1));
        g2D.setColor(mColor);
        // left corner
        if((sData[z] & 1) != 0) {
          g2D.drawLine(x, y, x, y + BLOCKS - 1);
        }
        // top corner
        if((sData[z] & 2) != 0) {
          g2D.drawLine(x, y, x + BLOCKS - 1, y);
        }
        // right corner
        if((sData[z] & 4) != 0) {
          g2D.drawLine(x + BLOCKS - 1, y, x + BLOCKS - 1, y + BLOCKS - 1);
        }
        // bottom corner
        if((sData[z] & 8) != 0) {
          g2D.drawLine(x, y + BLOCKS - 1, x + BLOCKS - 1, y + BLOCKS - 1);
        }
        // point and also the dots that pacman collects around the screen
        if((sData[z] & 16) != 0) {
          g2D.setColor(dots);
          g2D.fillRect(x + 11, y + 11, 2, 2);
        }
        z++;
      }
    }
  }
  
  // the easiest line of code you will find throughout this entire thing
  private void drawGhost(Graphics g2D, int x, int y) {
    g2D.drawImage(ghost, x, y, this);
  }
  
  /* 
  my attempt at making somewhat random movement for the ghosts, but it's not really that random i guess
  basically what's supposed to happen is that the ghost moves one square and then it's supposed to decide
  if it needs to change direction or not (if it hits a wall)
  */
  private void ghostMovement(Graphics g2D) {
    int number;
    int position;
    short i;
    // since I'm not that good with enhanced for loops and there's a lot in this loop, i decided to keep it normal
    for (i = 0; i < GHOST_NUMBER; i++) {
      if (ghostX[i] % BLOCKS == 0 && ghostY[i] % BLOCKS == 0) { // this just checks if the ghost has moved yet, and if they have it'll then decide what to do
        position = ghostX[i] / BLOCKS + NUMBER_OF_BLOCKS * (int) (ghostY[i] / BLOCKS); // this figures out the location of the ghost
        number = 0;
        // if there's nothing on the left and the ghost isn't moving right, then it'll move left
        if((sData[position] & 1) == 0 && ghostX[i] != 1) {
          dx[number] = -1;
          dy[number] = 0;
          number++;
        }
        // if there's nothing on the right and the ghost isn't moving left, then it'll move right
        if((sData[position] & 4) == 0 && ghostX[i] != -1) {
          dx[number] = 1;
          dy[number] = 0;
          number++;
        }
        // if there's nothing towards the top and the ghost isn't moving down, then it'll move up
        if((sData[position] & 2) == 0 && ghostY[i] != 1) {
          dx[number] = 0;
          dy[number] = -1;
          number++;
        }
        // if there's nothing towards the bottom and the ghost isn't moving up, then it'll move down
        if((sData[position] & 8) == 0 && ghostY[i] != -1) {
          dx[number] = 0;
          dy[number] = 1;
          number++;
        }
        // if there's nothing there
        if (number == 0) {
          // meaning that there was a point but after pacman "picked it up" and it's no longer there, becoming an empty space
          if((sData[position] & 15) == 15) {
            ghostdX[i] = 0;
            ghostdY[i] = 0;
          } else {
            ghostdX[i] = -ghostdX[i];
            ghostdY[i] = -ghostdY[i];
          }
        } else {
          number = (int) (Math.random() * number);
          if (number > 3) {
            number = 3;
          }
          ghostdX[i] = dx[number];
          ghostdY[i] = dy[number];
        }
      }
      ghostX[i] = ghostX[i] + (ghostdX[i] * ghostSpeed[i]);
      ghostY[i] = ghostY[i] + (ghostdY[i] * ghostSpeed[i]);
      drawGhost(g2D, ghostX[i] + 1, ghostY[i] + 1);
      // collision detection for pacman and the ghosts
      if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12) && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12) && inGame) {
        dead = true;
      }
    }
  }
          
  // this is the main or general method that draws pacman
  private void drawPac(Graphics2D g2D) {
    if(viewdY == -1) {
      drawUp(g2D);
    } else if(viewdY == 1) {
      drawDown(g2D);
    } else if(viewdX == 1) {
      drawRight(g2D);
    } else {
      drawLeft(g2D);
    }
  }
          
  // draws pacman facing up and it moves up
  private void drawUp(Graphics2D g2D) {
    switch (pacmanPosition) {
      case 1:
        g2D.drawImage(pacmanUp, pacmanX + 1, pacmanY + 1, this);
        break;
      case 2:
        g2D.drawImage(pacman2Up, pacmanX + 1, pacmanY + 1, this);
        break;
      case 3:
        g2D.drawImage(pacman3Up, pacmanX + 1, pacmanY, this);
        break;
      default:
        g2D.drawImage(pacman, pacmanX + 1, pacmanY, this);
        break;
    }
  }
          
  // draws pacman facing down
  private void drawDown(Graphics2D g2D) {
    switch (pacmanPosition) {
      case 1:
        g2D.drawImage(pacmanDown, pacmanX + 1, pacmanY + 1, this);
        break;
      case 2:
        g2D.drawImage(pacman2Down, pacmanX + 1, pacmanY + 1, this);
        break;
      case 3:
        g2D.drawImage(pacman3Down, pacmanX + 1, pacmanY, this);
        break;
      default:
        g2D.drawImage(pacman, pacmanX + 1, pacmanY, this);
        break;
    }
  }
          
  // draws pacman facing right
  private void drawRight(Graphics2D g2D) {
    switch (pacmanPosition) {
      case 1:
        g2D.drawImage(pacmanRight, pacmanX + 1, pacmanY + 1, this);
        break;
      case 2:
        g2D.drawImage(pacman2Right, pacmanX + 1, pacmanY + 1, this);
        break;
      case 3:
        g2D.drawImage(pacman3Right, pacmanX + 1, pacmanY, this);
        break;
      default:
        g2D.drawImage(pacman, pacmanX + 1, pacmanY, this);
        break;
    }
  }
          
  // draws pacman facing left
  private void drawLeft(Graphics2D g2D) {
    switch (pacmanPosition) {
      case 1:
        g2D.drawImage(pacmanLeft, pacmanX + 1, pacmanY + 1, this);
        break;
      case 2:
        g2D.drawImage(pacman2Left, pacmanX + 1, pacmanY + 1, this);
        break;
      case 3:
        g2D.drawImage(pacman3Left, pacmanX + 1, pacmanY, this);
        break;
      default:
        g2D.drawImage(pacman, pacmanX + 1, pacmanY, this);
        break;
    }
  }
          
  // complex and painful movement for pacman. i don't know if this even works
  // recdX and recdY can be found within the adapter class
  private void pacMovement() {
    short i;
    int position;
    if(recdX == -pacmandX && recdY == - pacmandY) {
      pacmandX = recdX;
      pacmandY = recdY;
      viewdX = pacmandX;
      viewdY = pacmandY;
    }
    if(pacmanX % BLOCKS == 0 && pacmanY % BLOCKS == 0) { // again sees if pacman has moved a square
      position = pacmanX / BLOCKS + NUMBER_OF_BLOCKS * (int) (pacmanY / BLOCKS); // determines pacman's position
      i = sData[position];
      // this basically means that once pacman moves to a position with a point (dot on maze) then we remove that point and increase the score
      if((i & 16) != 0) {
        sData[position] = (short) (i & 15);
        score++;
      }
      if(recdX != 0 || recdY != 0) {
        if(!((recdX == -1 && recdY == 0 && (i & 1) != 0) || (recdX == 1 && recdY == 0 && (i & 4) != 0)
           || (recdX == 0 && recdY == -1 && (i & 2) != 0) || (recdX == 0 && recdY == 1 && (i & 8) != 0))) {
           pacmandX = recdX;
           pacmandY = recdY;
           viewdX = pacmandX;
           viewdY = pacmandY;
         }
      }
      if((pacmandX == -1 && pacmandY == 0 && (i & 1) != 0) || (pacmandX == 1 && pacmandY == 0 && (i & 4) != 0) || (pacmandX == 0 && pacmandY == -1 && (i & 2) != 0) || (pacmandX == 0 && pacmandY == 1 && (i & 8) != 0)) {
         pacmandX = 0;
         pacmandY = 0;
      }
    }
    pacmanX = pacmanX + PACMAN_SPEED * pacmandX;
    pacmanY = pacmanY + PACMAN_SPEED * pacmandY;
  }
             
  // animiation crap for pacman opening and closing his mouth
  // the delay is there so pacman doesn't open and close his mouth insanely fast that you can barely if at all see him doing that
  private void animation() {
    pacmanCount --;
    if(pacmanCount <= 0) {
      pacmanCount = ANIMATION_DELAY;
      pacmanPosition = pacmanPosition + pacmanDirection;
      if(pacmanPosition == (ANIMATION_COUNT - 1) || pacmanPosition == 0) {
        pacmanDirection = -pacmanDirection;
      }
    }
  }
             
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    doTheDraw(g);
  }
  
  // finally drawing up or pulling everything up that was created before hand that makes up the main screen of the game (maze, score, etc.)          
  private void doTheDraw(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;
    g2D.setColor(Color.BLACK);
    g2D.fillRect(0, 0, dim.width, dim.height);
    drawMaze(g2D);
    gameScore(g2D);
    animation();
    if(inGame) {
      play(g2D);
    } else {
      introScreen(g2D);
    }
    g2D.drawImage(image, 5, 5, this);
    Toolkit.getDefaultToolkit().sync();
    g2D.dispose();
  }
  
  // adapters provide empty implementation of another interface           
  class TAdapter extends KeyAdapter {  
    @Override
    public void keyPressed(KeyEvent e) {
      int k = e.getKeyCode();
      if(inGame) {
        if(k == KeyEvent.VK_UP) {
          recdX = 0;
          recdY = -1;
        } else if(k == KeyEvent.VK_DOWN) {
          recdX = 0;
          recdY = 1;
        } else if(k == KeyEvent.VK_RIGHT) {
          recdX = 1;
          recdY = 0;
        } else if(k == KeyEvent.VK_LEFT) {
          recdX = -1;
          recdY = 0;
        } else if(k == KeyEvent.VK_PAUSE) {
          if(timer.isRunning()) {
            timer.stop();
          } else {
            timer.start();
          }
        } else if(k == KeyEvent.VK_ESCAPE && timer.isRunning()) {
          inGame = false;
        }
      } else {
        if(k == 'S'|| k == 's') {
          inGame = true;
          initGame();
        }
      }
    }
                  
    @Override
    public void keyReleased(KeyEvent e) {
      int k = e.getKeyCode();
      if(k == Event.UP || k == Event.DOWN || k == Event.RIGHT || k == Event.LEFT) {
        recdX = 0;
        recdY = 0;
      }
    }
  }
                  
  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }
}
