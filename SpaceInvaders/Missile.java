import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Missile {

    // image that represents the missile's position on the board
    private BufferedImage image;
    // current position of the missile on the board grid
    private Point pos;

    private int missileID;
    private static int numberOfBullets = 0;

    private int tickMove;
    private static boolean hasSpawned = false;

    public Missile(int x, int y, int tickMove) {
        // load the assets
        loadImage();

        // initialize the state
        pos = new Point(x, y);

        this.missileID = ++numberOfBullets;
        this.tickMove = tickMove;
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/missile.png"));
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
        pos.translate(0,tickMove);
    }

    public void setTickMove( int newTickMove ){
        tickMove = newTickMove;
    }

    public int getTickMove() {
        return tickMove;
    }

    public static boolean getHasSpawned(){
        return hasSpawned;
    }

    public static void setHasSpawned(){
        hasSpawned = !hasSpawned;
    }

    public Point getPos() {
        return pos;
    }

    public int getMissileID() {
        return missileID;
    }

}