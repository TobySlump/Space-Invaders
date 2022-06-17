import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.Random;

public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    private final int DELAY = 25;
    // controls the size of the board
    public static final int TILE_SIZE = 1;
    public static final int ROWS = 600;
    public static final int COLUMNS = 900;
    public static final int ALIENSWIDE = 10;
    public static final int ALIENSDOWN = 5;
    public static final int STARTINGLIVES = 3;
    public int ticksLeft = 200;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    // objects that appear on the game board
    private Player player;
    private Lives lives;
    private LinkedList<SimpleAudioPlayer> audioPlayers = new LinkedList<>();
    private BoardStates currentState;

    private LinkedList<Alien> listOfAlien = new LinkedList<>() ;
    private LinkedList<Missile> listOfMissile = new LinkedList<>();
    private LinkedList<Explosion> listOfExplosions = new LinkedList<>();
    private LinkedList<Barrier> listOfBarrier = new LinkedList<>();

    public Board() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // set the game board size
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // set the game board background color
        setBackground(new Color(232, 232, 232));

        // initialize the game state
        player = new Player();
        currentState = BoardStates.Playing;
        lives = new Lives(STARTINGLIVES);
        for (int i = 0; i < 3; i++){
            listOfBarrier.add(new Barrier(175 + (225 * i),400));
        }

        audioPlayers.add(new SimpleAudioPlayer("sounds/short pew.wav"));
        // audioPlayers.add(new SimpleAudioPlayer("sounds/alien pew.wav"));

        for (int i = 0; i < ALIENSWIDE * ALIENSDOWN; i++){
            listOfAlien.add( new Alien(20 + ((i % ALIENSWIDE) *50),20 + (i/ALIENSWIDE)*50));
        }

        Alien.setTickMoveHor(1);

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.


        if (currentState == BoardStates.Playing) {

            player.tick();

            // can Aliens descend
            Alien.setTickMoveVer(0);
            boolean canAlienDescend = true;
            for (Alien alien : listOfAlien){
                if (alien.getPos().y >= 320){
                    canAlienDescend = false;
                    break;
                }
            }

            // bounce aliens
            for (Alien alien : listOfAlien) {
                if (alien.getPos().x == 5 && Alien.getTickMoveHor() == -1) {
                    Alien.setTickMoveHor(1);
                    if (canAlienDescend) {
                        Alien.setTickMoveVer(10);
                    }
                    break;
                } else if (alien.getPos().x >= Board.COLUMNS - 60 && Alien.getTickMoveHor() == 1) {
                    Alien.setTickMoveHor(-1);
                    if (canAlienDescend) {
                        Alien.setTickMoveVer(10);
                    }
                    break;
                }
            }

            for (Alien alien : listOfAlien) {
                alien.tick();
            }

            // remove missiles when they reach end of screen
            LinkedList<Missile> listOfMissilesToRemove = new LinkedList<>();
            for (Missile missile : listOfMissile) {
                missile.tick();
                if (missile.getPos().y < 0) {
                    listOfMissilesToRemove.add(missile);
                } else if (missile.getPos().y > 550) {
                    listOfMissilesToRemove.add(missile);
                }
            }
            for (Missile missile : listOfMissilesToRemove) {
                listOfMissile.remove(missile);
            }

            for (Explosion explosion : listOfExplosions) {
                explosion.tick();
            }

            // 1 in 500 chance of an alien to shoot every tick
            Random rand = new Random();
            int upperbound = 500;
            for (Alien alien : listOfAlien) {
                int int_random = rand.nextInt(upperbound);
                if (int_random == 1) {
                    listOfMissile.add(new Missile(alien.getPos().x + 25, alien.getPos().y + 30, 3));
                    /**
                    try {
                        audioPlayer.runSound("sounds/alien pew.wav");
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        ex.printStackTrace();
                    }
                     **/
                }
            }

            collisionCheck();

        }else if (currentState == BoardStates.Won){
            listOfMissile.clear();
            for (Explosion explosion : listOfExplosions){
                explosion.tick();
            }

            // player flies upwards off-screen
            player.victoryFlight();
            ticksLeft -= 1;
            if (ticksLeft == 0 ){
                System.exit(0);
            }

        }else if (currentState == BoardStates.Lost){

            // bounce aliens
            for (Alien alien : listOfAlien) {
                if (alien.getPos().x == 5 && Alien.getTickMoveHor() == -1) {
                    Alien.setTickMoveHor(1);
                    break;
                } else if (alien.getPos().x >= Board.COLUMNS - 60 && Alien.getTickMoveHor() == 1) {
                    Alien.setTickMoveHor(-1);
                    break;
                }
            }

            // turn aliens red when they win
            for (Alien alien : listOfAlien){
                alien.turnRed();
                alien.tick();
            }
            listOfMissile.clear();
            ticksLeft -= 1;
            if (ticksLeft == 0 ){
                System.exit(0);
            }
        }

        repaint();
    }

    public void collisionCheck(){
        LinkedList<Alien> listOfAliensToRemove = new LinkedList<>();
        LinkedList<Missile> listOfMissilesToRemove = new LinkedList<>();
        LinkedList<Barrier> listofBarrierToRemove = new LinkedList<>();

        // has missile hit alien
        for (Alien alien : listOfAlien) {
            for (Missile missile : listOfMissile) {

                if ((Math.pow(missile.getPos().x - alien.getPos().x - 25,2) +
                        Math.pow(missile.getPos().y - alien.getPos().y - 25,2)) <  1000 &&
                missile.getTickMove() < 0) {

                    listOfAliensToRemove.add(alien);
                    listOfMissilesToRemove.add(missile);
                    player.addScore(100);
                    listOfExplosions.add(new Explosion(alien.getPos().x, alien.getPos().y));

                }
            }
        }

        // if hit remove alien and missile
        for (Alien alien : listOfAliensToRemove){
            listOfAlien.remove(alien);
            if (listOfAlien.size() == 0){
                currentState = BoardStates.Won;
            }
        }
        for (Missile missile : listOfMissilesToRemove) {
            listOfMissile.remove(missile);
        }

        // has missile hit barrier
        for (Barrier barrier : listOfBarrier){
            for (Missile missile : listOfMissile){
                if (Math.abs(barrier.getPos().y - missile.getPos().y) < 5){
                    if (missile.getPos().x - barrier.getPos().x < 100 &&
                            missile.getPos().x - barrier.getPos().x > 0){
                        barrier.hit();
                        listOfMissilesToRemove.add(missile);
                        if (barrier.isDead()){
                            listofBarrierToRemove.add(barrier);
                        }
                    }
                }
            }
        }
        for (Missile missile : listOfMissilesToRemove){
            listOfMissile.remove(missile);
        }
        for (Barrier barrier : listofBarrierToRemove){
            listOfBarrier.remove(barrier);
        }

        // has missile hit player
        if (player.getPlayerState() == PlayerStates.Alive) {
            for (Missile missile : listOfMissile) {
                if ((Math.pow(missile.getPos().x - player.getPos().x - 25, 2) +
                        Math.pow(missile.getPos().y - player.getPos().y - 10, 2)) < 1000 &&
                        missile.getTickMove() > 0) {
                    if (lives.getNumberOfLives() == 1) {
                        listOfMissilesToRemove.add(missile);
                        lives.removeLife();
                        player.die();
                        currentState = BoardStates.Lost;
                    } else {
                        lives.removeLife();
                        player.respawn();
                        listOfMissilesToRemove.add(missile);
                    }
                }
            }
            for (Missile missile : listOfMissilesToRemove) {
                listOfMissile.remove(missile);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver 
        // because Component implements the ImageObserver interface, and JPanel 
        // extends from Component. So "this" Board instance, as a Component, can 
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        drawBackground(g);
        //drawScore(g);
        drawScore(g);

        player.draw(g, this);
        for (Alien alien : listOfAlien){
            alien.draw(g, this);
        }
        for (Missile missile : listOfMissile){
            missile.draw(g, this);
        }
        for (Explosion explosion : listOfExplosions){
            if (explosion.getTicksShown() < 5){
                explosion.draw(g, this);
            }
        }
        for (Barrier barrier : listOfBarrier){
            barrier.draw(g, this);
        }
        lives.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        player.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_SPACE && !Missile.getHasSpawned() &&
                player.getPlayerState() == PlayerStates.Alive) {
            listOfMissile.add(new Missile(player.getPos().x + 23, player.getPos().y - 10, -5));
            Missile.setHasSpawned();
            try {
                audioPlayers.get(0).runSound();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
        player.keyReleased(e);

        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            Missile.setHasSpawned();
        }
    }

    private void drawBackground(Graphics g) {
        // draw a checkered background
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0,0,COLUMNS*TILE_SIZE,ROWS*TILE_SIZE);
    }

    private void drawScore(Graphics g) {
        // set the text to be displayed
        String text = "SCORE: " + player.getScore();
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(30, 201, 139));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1) - 20, TILE_SIZE * (COLUMNS/2), TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

}