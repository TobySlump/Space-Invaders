import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class Lives {

    private int numberOfLives;

    private BufferedImage image;

    public Lives ( int startingLives){
        loadImage();

        numberOfLives = startingLives;
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

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        for (int i = 0; i < numberOfLives; i++){
            g.drawImage(
                    image,
                    (550 + (60 * i)) * Board.TILE_SIZE,
                    560 * Board.TILE_SIZE,
                    observer
            );
        }

    }

    public void removeLife(){
        numberOfLives -= 1;
    }

    public int getNumberOfLives(){
        return numberOfLives;
    }
}
