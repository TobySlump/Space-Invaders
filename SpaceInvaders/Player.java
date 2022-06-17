import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    // image that represents the player's position on the board
    private BufferedImage image;
    // current position of the player on the board grid
    private Point pos;
    // keep track of the player's score
    private int score;

    private int isMoving;
    private PlayerStates playerState;
    private int ticksLeftWhilePlayerRespawning;
    private int ticksWhilePlayerFlashing;
    private boolean isModelShowing;

    public Player() {
        // load the assets
        loadImage();

        // initialize the state
        pos = new Point(425, 500);
        score = 0;
        isMoving = 0;
        playerState = PlayerStates.Alive;
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/player.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void die(){
        playerState = PlayerStates.Dead;
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/explosion.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but 
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        if (playerState == PlayerStates.Alive || playerState == PlayerStates.Dead) {
            g.drawImage(
                    image,
                    pos.x * Board.TILE_SIZE,
                    pos.y * Board.TILE_SIZE,
                    observer
            );
        }else if (playerState == PlayerStates.Respawning){
            if (ticksLeftWhilePlayerRespawning == 0){
                playerState = PlayerStates.Alive;
                g.drawImage(
                        image,
                        pos.x * Board.TILE_SIZE,
                        pos.y * Board.TILE_SIZE,
                        observer
                );
            }else {
                if (ticksWhilePlayerFlashing > 0) {
                    ticksWhilePlayerFlashing -= 1;
                    if (isModelShowing) {
                        g.drawImage(
                            image,
                            pos.x * Board.TILE_SIZE,
                            pos.y * Board.TILE_SIZE,
                            observer
                        );
                    }
                }else {
                    isModelShowing = !isModelShowing;
                    ticksWhilePlayerFlashing = 5;
                             }
            }
            ticksLeftWhilePlayerRespawning -= 1;
        }
    }

    public void keyPressed(KeyEvent e) {
        // every keyboard get has a certain code. get the value of that code from the
        // keyboard event so that we can compare it to KeyEvent constants
        int key = e.getKeyCode();

        // depending on which arrow key was pressed, we're going to move the player by
        // one whole tile for this input
        if (key == KeyEvent.VK_RIGHT) {
            isMoving = 10;
        }
        if (key == KeyEvent.VK_LEFT) {
            isMoving = -10;
        }
    }

    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();

        // depending on which arrow key was pressed, we're going to move the player by
        // one whole tile for this input
        if (key == KeyEvent.VK_RIGHT) {
            isMoving = 0;
        }
        if (key == KeyEvent.VK_LEFT) {
            isMoving = 0;
        }
    }

    public void tick() {
        // this gets called once every tick, before the repainting process happens.
        // so we can do anything needed in here to update the state of the player.

        // prevent the player from moving off the edge of the board sideways
        if (pos.x < 20) {
            pos.x = 21;
        } else if (pos.x >= Board.COLUMNS - 90) {
            pos.x = Board.COLUMNS - 91;
        }
        // prevent the player from moving off the edge of the board vertically
        pos.translate(isMoving, 0);
    }

    public void respawn() {
        playerState = PlayerStates.Respawning;
        ticksWhilePlayerFlashing = 5;
        isModelShowing = false;
        ticksLeftWhilePlayerRespawning = 80;
    }

    public void revive(){
        playerState = PlayerStates.Alive;
        isModelShowing = true;
    }

    public void victoryFlight() {
        pos.translate(0, -3);
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public void addScore(int amount) {
        score += amount;
    }

    public Point getPos() {
        return pos;
    }

    public PlayerStates getPlayerState(){
        return playerState;
    }

}