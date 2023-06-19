//Reg.Number: 1905770
/* The Covid-19 Snake Game: The purpose of the game is to entertain the users as they play it,
   but also provide them some useful guidance and messages for Covid-19 pandemic. The game is based on
   the classic snake games we all know, but with Covid-19 theme. The goal of the user is to
   destroy the Covid-19. There are 2 stages. The first one is to collect masks, and the stage
   two, to collect vaccines. As you collect masks, covid-19 objects are generated to make your
   game harder. If you collect 15 masks without getting a covid, the masks will stop spawning
   and the vaccines will start to spawn. For every vaccine you get, a covid will be destroyed.
   The game finishes when you destroy all the Covids. Each mask and vaccine worth a point. If
   you collide with a covid you lose. If the snake head touches the rest of the body, you lose
   as well. The faster you finish the game, the better score you get. Time is a part of the
   scoring system. At each five points the gel sanitizer/antiseptic is spawn on the panel. If
   you get it, you get an extra bonus point. If you do not get it on time, you must wait for
   the next 5 scored points because it will disappear (it will disappear if you get a mask or
   vaccine). At the end you can see the score you got with the time taken. You can also see the
   best 5 scores achieved. Snake object consists of three parts. The head is shown by the medical
   sign logo and the body is shown with the 2m distance icons and human icons (message to keep distance).
*/
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

//Game frame creator and starter game class
public class CE203_1905770_Ass2 {
    JFrame gameFrame = new JFrame();
    public CE203_1905770_Ass2() {
        //creating the frame of the game
        gameFrame.add(new Game());
        gameFrame.setTitle("Reg.Number: 1905770");
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.pack();
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null);
    }
    public static void main(String[] args){
        //starting the game
        new CE203_1905770_Ass2();
    }
}


//main class for the game. This is where the panel is painted, objects of the panel are created, most of the objects and variables are declared, and snake movement is created
 class Game extends JPanel {
    //fixed grid sizes and object sizes
    static final int GRID_HEIGHT = 500;
    static final int GRID_WIDTH = 500;
    static final int OBJECTS_SIZE = 20;
    static final int MAX_SIZE = 500;
    //used to snake body coordinates
    static final int snakeX[] = new int[MAX_SIZE];
    static final int snakeY[] = new int[MAX_SIZE];
    int snakeBody = 5;   //starting snake body
    int masksCollected = 0, vaccinesCollected = 0, score= 0, antisepticsCollected =0;  //counts for objects collected
    int maskX, covidX, antisepticX, vaccineX;  //used for object coordinates
    int maskY, covidY, antisepticY, vaccineY;
    String startPos = "DOWN";  //starting direction of the snake
    boolean started = false;   //boolean if the game is started
    int screen = 0;   //used to now which game screen will be shown and which mouse or keyboard controls are in use (according to the screen)
    Random random;   //used to generate random coordinates
    Timer time;   //timer for the action perform of the game
    CovidMask mask;  // object for the mask->used to draw masks in the game
    CovidVaccine vaccine; // object for the vaccines->used to draw vaccines in the game
    CovidAntiseptic antiseptic; // object for the antiseptics->used to draw antiseptics in the game
    Snake snake; // object for the snake->used to draw the snake in the game
    Covid covid, covidNew;  // object for the covid->used to draw covids in the in the game
    StartGame startGame;  //used as a game starter
    EndGame endGame = new EndGame();   //object which created the end screen
    ArrayList<Covid> covidArray = new ArrayList<>();  //saving all the covid objects created
    long startTime;  //used to get start and end time time of the game
    long stopTime;
    int gameTimeCount;  //used to get the time taken for the game
    File saveScoresFile;  //file which the scores are saved
    String filename = "scoresFile.txt";  //name of the file
    FileWriter writeFile;  //object used to write the scores and times in the file
    StartScreen startScreen = new StartScreen();  //object which is used to create the startup screen (menu)
    //main game background
    static BufferedImage background;
    static {
        try {
            background = ImageIO.read(new File("Images/background.png")); //image taken form: https://static.physoc.org/app/uploads/2020/03/30091715/COVID19-video-background-942x500.jpg
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //creating panel
    public Game() {
        random = new Random();
        startGame = new StartGame(this);  //starting a new game
        // panel configuration
        this.setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
    }
    //make the snake to be able to move
    public void snakeBodyMove(){
        //used for manipulating snake movement (manipulating snake object coordinates)
        for(int i=snakeBody; i>0; i--){
            snakeX[i] = snakeX[i-1];
            snakeY[i] = snakeY[i-1];
        }
        //how the snake will start at the start of the game and when keyboard key is pressed manipulates snake object by rotating it
        if(startPos=="UP"){
            snakeY[0] = snakeY[0]-OBJECTS_SIZE;
        }
        else if(startPos=="DOWN"){
            snakeY[0] = snakeY[0]+OBJECTS_SIZE;
        }
        else if(startPos=="RIGHT"){
            snakeX[0] = snakeX[0]+OBJECTS_SIZE;
        }
        else if(startPos=="LEFT"){
            snakeX[0] = snakeX[0]-OBJECTS_SIZE;
        }
        else {
            started=false;
            stopTime = System.currentTimeMillis();
            System.out.println("Something on startPos initialization is wrong");
        }
    }
    //creating mask object
    public void createMask(){
        boolean check=true;
        while (check){
            int chk = 0;
            maskX = random.nextInt(GRID_WIDTH/OBJECTS_SIZE)*OBJECTS_SIZE;
            maskY = random.nextInt(GRID_HEIGHT/OBJECTS_SIZE)*OBJECTS_SIZE;
            for (Iterator<Covid> covidIterator = covidArray.iterator(); covidIterator.hasNext();){  //check to prevent mask to be spawn over covids and antiseptics
                covidNew = covidIterator.next();
                if (((maskX == covidNew.covidX) && (maskY == covidNew.covidY)) || ((maskX == antisepticX) && (maskY == antisepticY))){
                    chk=1;
                    break;
                }
            }
            if (chk < 1) {
                check=false;
                mask = new CovidMask(maskX, maskY, OBJECTS_SIZE, OBJECTS_SIZE);
            }
        }
    }
    //creating covid object
    public void createCovid() {
        boolean check=true;
        while (check){
            covidX = random.nextInt(GRID_WIDTH / OBJECTS_SIZE) * OBJECTS_SIZE;
            covidY = random.nextInt(GRID_HEIGHT / OBJECTS_SIZE) * OBJECTS_SIZE;
            if ((maskX == covidX) && (maskY == covidY)){   //check to prevent covids to be spawn over masks
            }
            else {
                check=false;
                if(masksCollected<=15) {
                    covid = new Covid(covidX, covidY, OBJECTS_SIZE, OBJECTS_SIZE);
                    covidArray.add(covid);
                }
            }
        }
    }
    //creating vaccine object
    public void createVaccine() {
        boolean check=true;
        while (check){
            int chk = 0;
            vaccineX = random.nextInt(GRID_WIDTH / OBJECTS_SIZE) * OBJECTS_SIZE;
            vaccineY = random.nextInt(GRID_HEIGHT / OBJECTS_SIZE) * OBJECTS_SIZE;
            for (Iterator<Covid> covidIterator = covidArray.iterator(); covidIterator.hasNext();){ //check to prevent vaccines to be spawn over covids and antiseptics
                covidNew = covidIterator.next();
                if (((vaccineX == covidNew.covidX) && (vaccineY == covidNew.covidY)) || ((antisepticX == vaccineX) && (antisepticY == vaccineY))){
                    chk=1;
                    break;
                }
            }
            if (chk < 1) {
                check=false;
                vaccine = new CovidVaccine(vaccineX, vaccineY, OBJECTS_SIZE, OBJECTS_SIZE);
            }
        }
    }
    //creating antiseptic object
    public void createAntiseptic() {
        boolean check=true;
        while (check){
            int chk = 0;
            antisepticX = random.nextInt(GRID_WIDTH / OBJECTS_SIZE) * OBJECTS_SIZE;
            antisepticY = random.nextInt(GRID_HEIGHT / OBJECTS_SIZE) * OBJECTS_SIZE;
            for (Iterator<Covid> covidIterator = covidArray.iterator(); covidIterator.hasNext();) {  //check to prevent antiseptics to be spawn over covids, masks, and vaccines
                covidNew = covidIterator.next();
                if (((antisepticX == covidNew.covidX) && (antisepticY == covidNew.covidY)) || ((antisepticX == maskX) && (antisepticY == maskY)) || ((antisepticX == vaccineX) && (antisepticY == vaccineY))) {
                    chk = 1;
                    break;
                }
            }
            if (chk < 1) {
                check=false;
                antiseptic = new CovidAntiseptic(antisepticX, antisepticY, OBJECTS_SIZE, OBJECTS_SIZE);
            }
        }
    }
    //destroying covid object
    public void destroyCovid(){
        if(covidArray.size()!=0) {
            covidArray.remove(0);
            repaint();
        }
        //game is ended if all covids are destroyed
        else {
            started = false;
            time.stop();
            stopTime = System.currentTimeMillis();

        }
    }
    //paintComponent for drawing objects on the panel
    public void paintComponent(Graphics g){
        Font font;
        if(screen == 0 ){
            startScreen.drawMenu(g,this);
        }
        else if(screen == 1) {
            if (started) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, GRID_WIDTH, GRID_HEIGHT, null);
                if (masksCollected < 15) {   //painting new masks until collected masks are 15
                    mask.draw(g);
                }
                for (Iterator<Covid> covidIterator = covidArray.iterator(); covidIterator.hasNext(); ) {
                    covidNew = covidIterator.next();
                    covidNew.draw(g);
                }
                //draw antiseptic when you get 5 masks or vaccines--->works as a bonus score
                if (masksCollected + vaccinesCollected != 0) {
                    if ((masksCollected + vaccinesCollected + antisepticsCollected) % 5 == 0) {   //painting aniseptics at every +5 score
                        antiseptic.draw(g);
                    }
                }
                snake.draw(g);
                if (masksCollected >= 15) {  //if 15 masks collected, vaccines start to draw
                    vaccine.draw(g);
                }
                //Score on gameplay screen
                font = new Font(Font.MONOSPACED, Font.BOLD, 25);
                g.setColor(new Color(0xFF0020));
                g.setFont(font);
                FontMetrics metric = g.getFontMetrics(font);
                g.drawString("Score: " + score, (GRID_WIDTH - metric.stringWidth("Score:  ")) / 2, GRID_HEIGHT / 10);
                //finish game cases
            } else {
                super.paintComponent(g);
                if (covidArray.size() != 0) {
                    try {
                        screen = 2;
                        endGame.endGame(g, this);  //going to the game over screen
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        screen = 2;
                        endGame.winGame(g, this);  //going to the win screen
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
//Reference: For basic coding for the snake a video was used for help (movement of the snake): https://www.youtube.com/watch?v=bI6e6qjJ8JQ&t=1276s


//StartScreen class is used to draw the fist menu screen of the game
 class StartScreen {
    //drawing all the images,backgrounds, and messages to display on the screen
    public void drawMenu(Graphics g, Game game){   // game passed through to allow for game manipulation and graphics for drawing the panel
        BufferedImage background = null;
        BufferedImage coolEarth = null;
        try {
            background = ImageIO.read(new File("Images/background.png"));  //image taken form: https://static.physoc.org/app/uploads/2020/03/30091715/COVID19-video-background-942x500.jpg
            coolEarth = ImageIO.read(new File("Images/coolEarth.png"));   //image taken from: https://png.pngtree.com/png-clipart/20200401/original/pngtree-save-world-from-coronavirus-with-mask-png-image_5337141.jpg
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background,0,0,500,500,null);
        g.drawImage(coolEarth,0,200,200,200,null);
        g.drawImage(coolEarth,300,200,200,200,null);
        Font fontCovid, fontSaveWorld, fontSnake,fontSave;
        fontCovid = new Font(Font.MONOSPACED, Font.BOLD, 40);
        fontSnake = new Font("Ink free", Font.BOLD, 40);
        fontSave = new Font("Ink free", Font.BOLD, 25);
        fontSaveWorld = new Font("Ink free", Font.BOLD, 20);
        g.setColor(new Color(0xFF0020));
        g.setFont(fontCovid);
        FontMetrics metric = g.getFontMetrics(fontCovid);
        FontMetrics metric2 = g.getFontMetrics(fontSnake);
        FontMetrics metric3 = g.getFontMetrics(fontSaveWorld);
        FontMetrics metric4 = g.getFontMetrics(fontSave);
        g.drawString("COVID-19", (Game.GRID_WIDTH - metric.stringWidth("COVID-19")) / 2, Game.GRID_HEIGHT - 450);
        g.setFont(fontSnake);
        g.drawString("SNAKE GAME", (Game.GRID_WIDTH - metric2.stringWidth("SNAKE GAME")) / 2, Game.GRID_HEIGHT - 400);

        g.setFont(fontSave);
        g.setColor(new Color(56,186,238));
        g.drawString("SAVE THE WORLD", (Game.GRID_WIDTH - metric4.stringWidth("SAVE THE WORLD")) / 2, Game.GRID_HEIGHT - 275);
        g.drawString("FROM", (Game.GRID_WIDTH - metric4.stringWidth("FROM")) / 2, Game.GRID_HEIGHT - 225);
        g.drawString("COVID-19", (Game.GRID_WIDTH - metric4.stringWidth("COVID-19")) / 2, Game.GRID_HEIGHT - 175);

        g.setFont(fontSaveWorld);
        g.setColor(new Color(20, 134, 203));
        g.drawString("CLICK OR PRESS SPACE TO START", (Game.GRID_WIDTH - metric3.stringWidth("CLICK OR PRESS SPACE TO START")) / 2, Game.GRID_HEIGHT - 75);

        //adding a keyboard listener
        game.addKeyListener(new KeyboardListener(game));
        //adding mouse listener
        game.addMouseListener(new MouseClickListener(game));
    }
}


//StartGame class is used as a game starter
 class StartGame {
    Game game;
    public StartGame(Game game){    // game passed through to allow for game manipulation
        this.game=game;
    }
    //start game. Creating a new file or use existing one, initialise the game objects again for a new game and create a timer with actionListener
    public void snakeGameStart(){
        try {
            game.saveScoresFile = new File(game.filename);
            if (game.saveScoresFile.createNewFile()) {
                game.writeFile = new FileWriter(game.filename);
            } else {
                game.writeFile = new FileWriter(game.filename,true);
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        //reinitializing objects for playing again
        game.covidArray.removeAll(game.covidArray);
        game.score = 0;
        game.masksCollected = 0;
        game.vaccinesCollected = 0;
        game.antisepticsCollected = 0;
        game.snakeBody = 5;
        game.snake = new Snake(game.snakeX,game.snakeY,game.OBJECTS_SIZE,game.OBJECTS_SIZE,game.snakeBody);  //initializing snake object
        game.createMask();
        game.createCovid();
        game.started = true;  //game start running
        game.startTime = System.currentTimeMillis();
        game.time = new Timer(100, new ActionPerformed(game));
        game.time.start(); //starting the game timer with the Action Performed
    }
}


//EndGame class is used to display the end screens of the game(win screen and lose screen)
 class EndGame{
    BufferedReader reader;
    ArrayList<GameScores> gameScores;
    ArrayList<GameScores> gameScoresForEach;
    //screen if you lose game, drawing messages and scores
    public  void endGame(Graphics g, Game game) throws IOException {   // game passed through to allow for game manipulation and graphics for panel drawing
        game.gameTimeCount = (int) (game.stopTime - game.startTime);
        saveScoresFile(game);
        readScoresFile(game);

        BufferedImage background = null;
        try {
            background = ImageIO.read(new File("Images/background.png")); //image taken form: https://static.physoc.org/app/uploads/2020/03/30091715/COVID19-video-background-942x500.jpg
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background,0,0,500,500,null);

        Font fontLost, fontScoreboard, fontPlayAgain ;
        fontLost = new Font("Ink free",Font.BOLD, 40);
        fontScoreboard = new Font(Font.MONOSPACED,Font.BOLD, 20);
        fontPlayAgain = new Font("Ink free",Font.BOLD, 20);
        g.setColor(new Color(0xFF0020));
        g.setFont(fontLost);
        FontMetrics metric = g.getFontMetrics(fontLost);
        FontMetrics metric2 = g.getFontMetrics(fontScoreboard);
        FontMetrics metric3 = g.getFontMetrics(fontPlayAgain);
        g.drawString("Game Over", (Game.GRID_WIDTH-metric.stringWidth("Game Over"))/2, Game.GRID_HEIGHT-450);
        g.drawString("You Got Covid-19", (Game.GRID_WIDTH-metric.stringWidth("You Got Covid-19"))/2, Game.GRID_HEIGHT-400);
        g.drawString("Score Board", (Game.GRID_WIDTH-metric.stringWidth("Score Board"))/2, Game.GRID_HEIGHT-250);
        g.setFont(fontPlayAgain);
        g.setColor(new Color(0x3636FF));
        g.drawString("Click or Press Space to Play Again",(Game.GRID_WIDTH-metric3.stringWidth("Click or Press Space to Play Again"))/2, Game.GRID_HEIGHT-300);
        g.setFont(fontScoreboard);
        g.setColor(new Color(0x00FFA8));
        g.drawString("Score: "+game.score + " Time: " + (game.gameTimeCount/1000)/60 + "m "+(game.gameTimeCount/1000)%60 + "s " + (game.gameTimeCount%1000) + "ms",
                (Game.GRID_WIDTH-metric2.stringWidth("Score: "+game.score + " Time: " + (game.gameTimeCount/1000)/60 + "m "+(game.gameTimeCount/1000)%60 + "s " + (game.gameTimeCount%1000) + "ms"))/2,
                Game.GRID_HEIGHT-350);
        //drawing the scoreboard with the 5 best scores
        int count =0, counter =1, count2= 200;
        for (GameScores scores : gameScores){
            g.drawString(counter+") "+scores.scoreString +" "+scores.score +" "+scores.timeString+" "+(scores.timeTaken/1000)/60 + "m "+(scores.timeTaken/1000)%60 + "s " + (scores.timeTaken%1000) + "ms",
                    (Game.GRID_WIDTH-metric2.stringWidth(counter+") "+scores.scoreString +" "+scores.score +" "+scores.timeString+" "+(scores.timeTaken/1000)/60 + "m "+(scores.timeTaken/1000)%60 + "s " + (scores.timeTaken%1000) + "ms")-30)/2,
                    Game.GRID_HEIGHT-count2);
            count++;
            counter++;
            count2-=35;
            if(count == 5){
                break;
            }
        }
    }

    //screen if you win game, drawing messages on panel and scores
    public void winGame(Graphics g, Game game) throws IOException {  // game passed through to allow for game manipulation and graphics for panel drawing
        game.gameTimeCount = (int) (game.stopTime - game.startTime);
        saveScoresFile(game);
        readScoresFile(game);
        BufferedImage background = null;
        try {
            background = ImageIO.read(new File("Images/background.png")); //image taken form: https://static.physoc.org/app/uploads/2020/03/30091715/COVID19-video-background-942x500.jpg
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background,0,0,500,500,null);

        Font fontWin ,fontWin2, fontPlayAgain;
        fontWin = new Font("Ink free",Font.BOLD, 30);
        fontWin2 = new Font(Font.MONOSPACED,Font.BOLD, 20);
        fontPlayAgain = new Font("Ink free",Font.BOLD, 20);
        g.setColor(new Color(0xFF0020));
        g.setFont(fontWin);
        FontMetrics metric = g.getFontMetrics(fontWin);
        FontMetrics metric2 = g.getFontMetrics(fontWin2);
        FontMetrics metric3 = g.getFontMetrics(fontPlayAgain);
        g.drawString("You Won", (Game.GRID_WIDTH-metric.stringWidth("You Won"))/2, Game.GRID_HEIGHT-450);
        g.drawString("You Stayed Safe", (Game.GRID_WIDTH-metric.stringWidth("You Stayed Safe"))/2, Game.GRID_HEIGHT-400);
        g.drawString("From Covid-19", (Game.GRID_WIDTH-metric.stringWidth("From Covid-19"))/2, Game.GRID_HEIGHT-350);
        g.drawString("Score Board", (Game.GRID_WIDTH-metric.stringWidth("Score Board"))/2, Game.GRID_HEIGHT-200);
        g.setFont(fontPlayAgain);
        g.setColor(new Color(0x3636FF));
        g.drawString("Click or Press Space to Play Again",(Game.GRID_WIDTH-metric3.stringWidth("Click or Press Space to Play Again"))/2, Game.GRID_HEIGHT-250);
        g.setFont(fontWin2);
        g.setColor(new Color(0x00FFA8));
        g.drawString("Score: "+game.score + " Time: " + (game.gameTimeCount/1000)/60 + "m "+(game.gameTimeCount/1000)%60 + "s " + (game.gameTimeCount%1000) + "ms",
                (Game.GRID_WIDTH-metric2.stringWidth("Score: "+game.score + " Time: " + (game.gameTimeCount/1000)/60 + "m "+(game.gameTimeCount/1000)%60 + "s " + (game.gameTimeCount%1000) + "ms"))/2,
                Game.GRID_HEIGHT-300);
        //drawing the scoreboard with the 5 best scores
        int count =0, counter =1, count2= 150;
        for (GameScores scores : gameScores){
            g.drawString(counter+") "+scores.scoreString +" "+scores.score +" "+scores.timeString+" "+(scores.timeTaken/1000)/60 + "m "+(scores.timeTaken/1000)%60 + "s " + (scores.timeTaken%1000) + "ms",
                    (Game.GRID_WIDTH-metric2.stringWidth(counter+") "+scores.scoreString +" "+scores.score +" "+scores.timeString+" "+(scores.timeTaken/1000)/60 + "m "+(scores.timeTaken/1000)%60 + "s " + (scores.timeTaken%1000) + "ms")-30)/2,
                    Game.GRID_HEIGHT-count2);
            count++;
            counter++;
            count2-=35;
            if(count == 5){
                break;
            }
        }
    }
    //saving scores each time the user plays in a file
    public void saveScoresFile(Game game) throws IOException {   // game passed through to allow for game manipulation
        gameScoresForEach = new ArrayList<>();
        gameScoresForEach.add(new GameScores("Score:", game.score , "Time:",  game.gameTimeCount));
        for (GameScores scores : gameScoresForEach) {
            game.writeFile.write(scores.scoreString);
            game.writeFile.write(" " + scores.score);
            game.writeFile.write(" " + scores.timeString);
            game.writeFile.write(" " + scores.timeTaken);
            game.writeFile.write('\n');
        }
        game.writeFile.flush();
    }
    //reading the file and saving file entries in a new list which will be displayed at the end of the game
    public void readScoresFile(Game game) throws IOException {   // game passed through to allow for game manipulation
        reader = new BufferedReader(new FileReader(game.filename));
        gameScores = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            String[] scoreDetails = line.split(" ");
            String scoreString = scoreDetails[0];
            int score = Integer.parseInt(scoreDetails[1]);
            String timeString = scoreDetails[2];
            int timeTaken = Integer.parseInt(scoreDetails[3]);
            gameScores.add(new GameScores(scoreString, score, timeString, timeTaken));
            line = reader.readLine();
        }
        Collections.sort(gameScores, new scoresCompare());  //sorting
        //creating a new FileWriter to overwrite the previous one with the sorted scores
        game.writeFile = new FileWriter(game.filename);
        for (GameScores scores : gameScores) {
            game.writeFile.write(scores.scoreString);
            game.writeFile.write(" " + scores.score);
            game.writeFile.write(" " + scores.timeString);
            game.writeFile.write(" " + scores.timeTaken+" " + "ms");
            game.writeFile.write('\n');
        }
        game.writeFile.close();
        reader.close();
    }
}


//GameScores class is used for the scoring system
 class GameScores {
    String scoreString;
    int score;
    String timeString;
    int timeTaken;
    //saving the game scores
    public GameScores(String scoreString, int score, String timeString, int timeTaken){
        this.scoreString = scoreString;
        this.score = score;
        this.timeString = timeString;
        this.timeTaken = timeTaken;
    }
    //toString method to represent GameScores objects to String
    @Override
    public String toString() {
        return  scoreString +" "+ score + " " + timeString + " "+ timeTaken;
    }
}


//sorting them first by score and then by time taken
 class scoresCompare implements Comparator<GameScores> {
    @Override
    public int compare(GameScores score1, GameScores score2) {
        int result;
        result = score2.score - score1.score;
        if(result == 0){
            result = score1.timeTaken - score2.timeTaken;
        }
        return result;
    }
}


//Game Object class. All the objects override the draw method and get the grid sizes as well
 abstract class GameObjects {
    protected int sizeX;
    protected int sizeY;
    public GameObjects(int sizeX, int sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
    //method to draw the objects
    public abstract void draw(Graphics g);
}


//Snake object class. Used to create an new object of snake and draw it. Extends GameObjects abstract class
 class Snake extends GameObjects {
    int snakeX[];
    int snakeY[];
    int snakeBody;
    static BufferedImage doctor;
    static BufferedImage human;
    static BufferedImage distance;
    static {
        try {
            doctor = ImageIO.read(new File("Images/doctor.png"));   //image taken from: https://www.pngkey.com/png/full/33-337638_medicine-logo-png-1-medical-logo.png
            human = ImageIO.read(new File("Images/human.png"));     //image taken from: https://www.freeiconspng.com/img/1927
            distance = ImageIO.read(new File("Images/distancing.png")); //image taken from: https://cdn2.iconfinder.com/data/icons/the-new-normal-2/64/social_distancing_2m_distance_user-128.png
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Snake(int snakeX[],int snakeY[], int sizeX, int sizeY, int snakeBody) {
        super(sizeX, sizeY);
        this.snakeX = snakeX;
        this.snakeY = snakeY;
        this.snakeBody = snakeBody;
    }

    @Override
    public void draw(Graphics g) {
        for(int i=0; i<snakeBody;i++){
            //image of logo sign
            if(i==0){
                g.drawImage(doctor, snakeX[0],snakeY[0],sizeX,sizeY,null);
            }
            else{
                //for image person
                if(i%2!= 0){
                    g.drawImage(human, snakeX[i],snakeY[i],sizeX,sizeY,null);
                    //for image distance
                }else{
                    g.drawImage(distance, snakeX[i],snakeY[i],sizeX,sizeY,null);
                }

            }
        }
    }
}


//Covid object class. Used to create an new object of covid and draw it. Extends GameObjects abstract class
 class Covid extends GameObjects {
    int covidX;
    int covidY;
    static BufferedImage covid;
    static {
        try {
            covid = ImageIO.read(new File("Images/covid.png"));  //image taken from: https://freepikpsd.com/wp-content/uploads/2020/02/coronavirus_PNG-Images-Clipart.png
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Covid(int covidX, int covidY, int sizeX, int sizeY) {
        super(sizeX, sizeY);
        this.covidX = covidX;
        this.covidY = covidY;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(covid, covidX,covidY,sizeX,sizeY,null);
    }
}


//Antiseptic object class. Used to create an new object of antiseptic and draw it. Extends GameObjects abstract class
 class CovidAntiseptic extends GameObjects {
    int antisepticX;
    int antisepticY;
    static BufferedImage antiseptic;
    static {
        try {
            antiseptic = ImageIO.read(new File("Images/antiseptic.png"));  //image taken from: https://cdn0.iconfinder.com/data/icons/covid-19-37/512/UseHandSanitizer-handantiseptic-handdisinfectant-covid19-disinfection-hygiene-prevention-512.png
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public CovidAntiseptic(int antisepticX, int antisepticY, int sizeX, int sizeY) {
        super(sizeX, sizeY);
        this.antisepticX=antisepticX;
        this.antisepticY=antisepticY;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(antiseptic, antisepticX,antisepticY,sizeX,sizeY,null);
    }
}


//Mask object class. Used to create an new object of mask and draw it. Extends GameObjects abstract class
 class CovidMask extends GameObjects {
    int maskX;
    int maskY;
    static BufferedImage mask;
    static {
        try {
            mask = ImageIO.read(new File("Images/mask.png"));  //image taken from: https://i.pinimg.com/originals/4e/c1/19/4ec119f7a6cf0088dce44614696f15bb.png
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public CovidMask(int maskX, int maskY, int sizeX, int sizeY) {
        super(sizeX,sizeY);
        this.maskX =maskX;
        this.maskY =maskY;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(mask,maskX,maskY,sizeX,sizeY,null);
    }
}


//Vaccine object class. Used to create an new object of vaccine and draw it. Extends GameObjects abstract class
 class CovidVaccine extends GameObjects {
    int vaccineX;
    int vaccineY;
    static BufferedImage vaccine;
    static {
        try {
            vaccine = ImageIO.read(new File("Images/vaccine.png"));  //image taken from: https://images.squarespace-cdn.com/content/5a7adf55f6576ee0160b5a58/1523964614321-ZTGQTIY55KJNYQDNFVJZ/vaccine.png?content-type=image%2Fpng
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public CovidVaccine(int vaccineX, int vaccineY, int sizeX, int sizeY) {
        super(sizeX, sizeY);
        this.vaccineX=vaccineX;
        this.vaccineY=vaccineY;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(vaccine, vaccineX,vaccineY,sizeX,sizeY,null);
    }
}


/*this class is used for the collisions of the game objects
  When mask is collected new covid is created. When vaccine
  is collected, one covid is destroyed. Antiseptic are used
  for bonus points */
 class GameCollisions {
    Game game;
    public GameCollisions(Game game){ // game passed through to allow for game manipulation
        this.game=game;
    }
    //collision if the snake eats itself
    public void snakeBodyHit(){
        for(int i = game.snakeBody; i>0; i--){
            if((Game.snakeX[0] == Game.snakeX[i]) && (Game.snakeY[0] == Game.snakeY[i])){
                game.started=false;
                game.time.stop();
                game.stopTime = System.currentTimeMillis();

            }
        }
    }
    //collision with the borders to make the snake reappear from the opposite panel grid
    public void snakeBorderHit(){
        if(Game.snakeX[0] > Game.GRID_WIDTH -1){
            Game.snakeX[0] = 0;
        }
        else if(Game.snakeX[0] < -1){
            Game.snakeX[0]= Game.GRID_WIDTH;
        }
        else if(Game.snakeY[0] > Game.GRID_HEIGHT -1){
            Game.snakeY[0]=0;
        }
        else if(Game.snakeY[0] < -1){
            Game.snakeY[0] = Game.GRID_HEIGHT;
        }
    }
    //collision with all kinds of objects(masks,vaccines,antiseptics,covids)
    public void objectCollision(){
        for(int i = 0; i<game.snakeBody; i++) {
            //collision with masks
            if ((Game.snakeX[i] == game.maskX) && (Game.snakeY[i] == game.maskY)) {
                if (game.masksCollected < 15) {
                    game.snakeBody++;
                    game.masksCollected++;
                    game.score++;
                    game.snake = new Snake(Game.snakeX, Game.snakeY, Game.OBJECTS_SIZE, Game.OBJECTS_SIZE, game.snakeBody);  //drawing the snake with the new body length
                    game.createMask();
                    game.createCovid();
                    if (game.masksCollected == 15) {
                        game.createVaccine();
                    }
                    if(game.masksCollected == 5){
                        game.createAntiseptic();
                    }
                }
            }
            //collision with covid
            else {
                for (Iterator<Covid> covidIterator = game.covidArray.iterator(); covidIterator.hasNext(); ) {
                    Covid covidNew = covidIterator.next();
                    if ((Game.snakeX[i] == covidNew.covidX) && (Game.snakeY[i] == covidNew.covidY)) {
                        game.started = false;
                        game.time.stop();
                        game.stopTime = System.currentTimeMillis();

                    }
                }
            }
            //collision with vaccines and destroying covid
            if((Game.snakeX[i] == game.vaccineX) && (Game.snakeY[i] == game.vaccineY)) {
                if (game.masksCollected >= 15) {
                    game.vaccinesCollected++;
                    game.score++;
                    game.createVaccine();
                    game.destroyCovid();
                }
            }
            //collision with antiseptic
            if(game.masksCollected+game.vaccinesCollected != 0){
                if ((game.masksCollected + game.vaccinesCollected + game.antisepticsCollected) % 5 == 0) {    //create antiseptics at each scored 5 points
                    if ((Game.snakeX[i] == game.antisepticX) && (Game.snakeY[i] == game.antisepticY)) {
                        game.antisepticsCollected++;
                        game.score++;
                    }
                }
            }
        }
    }
}


//KeyBoard Listener is used to play the game with the keyboard (control snake, start and play again game)
 class KeyboardListener implements KeyListener {
    private Game game; // game passed through to allow for game manipulation
    boolean up=true, down=true, right=true, left=true;
    public KeyboardListener(Game game) {
        this.game = game;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                //Cases to make sure that a no 180 degree can be made and not continuously press of a key can happen
                if(game.startPos != "DOWN"){
                    if(up==true) {
                        down=true;
                        right=true;
                        left=true;
                        game.startPos = "UP";
                        game.snakeBodyMove();
                        up=false;
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                if(game.startPos != "UP") {
                    if (down == true) {
                        up = true;
                        right = true;
                        left = true;
                        game.startPos = "DOWN";
                        game.snakeBodyMove();
                        down=false;
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                if(game.startPos != "RIGHT") {
                    if (left == true) {
                        down = true;
                        right = true;
                        up = true;
                        game.startPos = "LEFT";
                        game.snakeBodyMove();
                        left = false;
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(game.startPos != "LEFT"){
                    if (right == true) {
                        down = true;
                        left = true;
                        up = true;
                        game.startPos = "RIGHT";
                        game.snakeBodyMove();
                        right = false;
                    }
                }
                break;
            //allow space to be used only on the start screen and on the end screen to play the game again
            case KeyEvent.VK_SPACE:
                if(game.screen == 0){
                    game.screen = 1;
                    game.startGame.snakeGameStart();
                }else if(game.screen == 2){
                    game.screen = 1;
                    game.startGame.snakeGameStart();
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }
}


//Mouse Listener is used to start or play again the game
 class MouseClickListener implements MouseListener {
    private Game game; // game passed through to allow for game manipulation
    public MouseClickListener(Game game) {
        this.game = game;
    }
    //mouse is used to start the game from start screen or to play again at the end screen
    @Override
    public void mouseClicked(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)){
            if(game.screen == 0){
                game.screen = 1;
                game.startGame.snakeGameStart();
            }else if(game.screen == 2){
                game.screen = 1;
                game.startGame.snakeGameStart();
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
}


//ActionPerformed class is used to "activate" the game screen
 class ActionPerformed implements ActionListener {
    Game game;
    GameCollisions collisions;
    public ActionPerformed(Game game){  // game passed through to allow for game manipulation
        this.game=game;
    }
    //if the game is started collisions are activated
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        collisions = new GameCollisions(game);
        if(game.started){
            game.snakeBodyMove();
            collisions.snakeBorderHit();
            collisions.snakeBodyHit();
            collisions.objectCollision();
        }
        game.repaint();
    }
}


