import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Alien {

    // image that represents the alien's position on the board
    private BufferedImage image;
    // current position of the alien on the board grid
    private Point pos;

    private int alienID;
    private static int numberOfAliens = 0;

    private static int tickMoveHor;
    private static int tickMoveVer;


    public Alien(int x, int y) {
        // load the assets
        loadImage();

        // initialize the state
        pos = new Point(x, y);

        this.alienID = ++numberOfAliens;
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
                image = ImageIO.read(new File("images/alien.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void turnRed() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/redAlien.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but 
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        g.drawImage(
                image,
                pos.x * Board.TILE_SIZE,
                pos.y * Board.TILE_SIZE,
                observer
        );
    }

    public void tick(){
        pos.translate(tickMoveHor,tickMoveVer);
    }

    public static void setTickMoveHor(int newTickMove ){
        tickMoveHor = newTickMove;
    }

    public static int getTickMoveHor() {
        return tickMoveHor;
    }

    public static void setTickMoveVer ( int newTickMove ){
        tickMoveVer = newTickMove;
    }

    public Point getPos() {
        return pos;
    }

    public int getAlienID() {
        return alienID;
    }

}