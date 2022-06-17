import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Barrier {

    // image that represents the missile's position on the board
    private BufferedImage image;
    // current position of the missile on the board grid
    private Point pos;

    private int hitsLeft;
    private int imageState;

    public Barrier(int x, int y) {
        imageState = 10;
        // load the assets
        loadImage();

        // initialize the state
        pos = new Point(x, y);
        hitsLeft = 16;

    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/barrier" + Integer.toString(imageState) + ".png"));
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

    public void hit(){
        hitsLeft -= 1;
        if (hitsLeft != 0){
            if (hitsLeft % 4 == 0){
                imageState += 10;
                loadImage();
            }
        }
    }

    public boolean isDead(){
        return hitsLeft == 0;
    }

    public void tick(){;
    }

    public Point getPos() {
        return pos;
    }

}