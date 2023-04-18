package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Main extends Application {
	
	//variables needed for game to function	
	static int width = 20;
	static int height = 20;
	//sets speed update time (more = faster, less = slower)(attached to scoreboard!)
	static int speed = 4;
	//food vars
	static int foodcolor = 0;
	static int foodX = 0;
	static int foodY = 0;
	
	static int cornersize = 30; //sets size of all elements
	
	//stores the snake
	static List<Corner> snake = new ArrayList<>();
	//default direction where the snake will go (before user input)
	static Dir direction = Dir.left;
	//sets game state (if set to true, game will end immediately)
	static boolean gameOver = false;
	//randoom variable is made so it can be accessed by methods that require it
	static Random rando = new Random();

	
	public enum Dir { //contains directions for controls
		left,right,up,down
	}


	public static class Corner {
		int x;
		int y;

	public Corner(int x, int y) {
			this.x = x;
			this.y = y;
		}
	
	}



	@Override
	public void start(Stage primaryStage) { 
		//stage is a window that contains all objects of application
		try { //try block to ensure game works, and if it doesn't it is caught 
			
			 // new food is created at beginning of game
			newFood();
			//creating game board, vbox lays all children in a vertical line
			VBox root = new VBox();
			//dimensions of gameboard
			Canvas c = new Canvas(width*cornersize, height*cornersize);
			//makes draw calls to the canvas (updates the scene)
			GraphicsContext gc = c.getGraphicsContext2D();
			//adds canvas to the root so it can be displayed
			root.getChildren().add(c);
			
			//timer that that counts each frame while started
			new AnimationTimer() {
				long lastTick = 0;

				@Override
				public void handle (long now) {
					if(lastTick == 0) {
						lastTick = now;
						tick(gc);
						return;
					}

					if (now-lastTick > 1000000000 / speed) {
						lastTick = now;

						tick(gc);
					}
				}

			}.start(); //if everything works, the game starts

			
			//scene represents the contents of the application (scene in the stage)
			Scene scene = new Scene(root,width*cornersize,height*cornersize);

			//game is controlled via the arrowkeys on the keyboard
			//an event is added so that it recognizes when a key is pressed
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key ->{
				if(key.getCode() == KeyCode.UP) {
					direction = Dir.up;
				}

				if(key.getCode() == KeyCode.DOWN) {
					direction = Dir.down;
				}

				if(key.getCode() == KeyCode.LEFT) {
					direction = Dir.left;
				}

				if(key.getCode() == KeyCode.RIGHT) {
					direction = Dir.right;
				}


			});

		//------------------------------------------------
		//starting snake parts (where the snake will first be spawned, roughly in the middle)
		snake.add(new Corner(width/2,height/2));
		snake.add(new Corner(width/2,height/2));
		snake.add(new Corner(width/2,height/2));
		//------------------------------------------------


			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(scene); //setting of scene on stage
			primaryStage.setTitle("Snaaaaaake!"); //name of window    
			primaryStage.show(); //displays the game on screen
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		

        
	}
	
	
	

//------------------------------------------------
	//tick (or Timeline) for managing speed of game (a thread that 
	//is repetitive that updates the scene)

	public static void tick(GraphicsContext gc) {
/*	used to issue draw calls to a Canvas using a buffer
essentially used to call parameters(actions, drawing and such) so they will be rendered onto
the canvas(or screen)	*/
		
		
		
		if(gameOver) { 
			//if a game over condition is met, a game over text will appear on screen
			DropShadow ds = new DropShadow();
			ds.setOffsetY(3.0f);
			ds.setColor(Color.color(0.4f, 0, 0));

			gc.setEffect(ds);
			gc.setFill(Color.RED); //color of text
			gc.setFont(Font.font("Verdana", FontWeight.BOLD, 70)); //font and size
			gc.fillText("GAME OVER",75, 300); //text itself and placement on scene


		
			return;

		}

		for (int i = snake.size()-1;i>=1;i--) {
			snake.get(i).x = snake.get(i-1).x;
			snake.get(i).y = snake.get(i-1).y;
		}

		switch(direction) { //choosing the direction of the snake

		case up:
			snake.get(0).y--;

			if(snake.get(0).y < 0) {
				gameOver = true; // if snake goes over top border, the game ends
			}
			break;


		case down:
			snake.get(0).y++;

			if(snake.get(0).y > height) { 
				gameOver = true; // if snake goes over bottom border, the game ends
			}
			break;


		case left:
			snake.get(0).x--;

			if(snake.get(0).x < 0) {
				gameOver = true; // if snake goes over left border, the game ends
			}
			break;


		case right:
			snake.get(0).x++;

			if(snake.get(0).x > width) {
				gameOver = true; // if snake goes over right border, the game ends
			}
			break;
		}
		
		//eating food
		if (foodX == snake.get(0).x && foodY == snake.get(0).y) { // if food touches the snakes head
			snake.add(new Corner(-1,-1)); //snake gets larger by 1
			newFood(); //and a new food is made

		}

		//if snake runs into itself/wall
		for(int i = 1; i<snake.size(); i++) {
			if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;

			}			
		}
		


		//filling the background with a color
		gc.setFill(Color.DARKSEAGREEN);
		gc.fillRect(0, 0, width*cornersize, height*cornersize);
		//since we have variables already made with numbers we just use those for convenience

		//creating the scoreboard on the top
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("",30));
		gc.fillText("SCORE: "+(speed-5),10,30);
		//score goes up as food is eaten (since speed increments after each food, we use that
		//variable for convenience)
		
		
		//------------------------------------------------
		//randoom food color, and setting the size of the food
		
		Color cc = Color.WHITE;
		
		//use a switch method, which picks a number randomly from 0-4
		switch(foodcolor) {
		case 0: cc = Color.PURPLE;
		break;

		case 1: cc = Color.BLUE;
		break;

		case 2: cc = Color.LIGHTGREEN;
		break;

		case 3: cc = Color.ORANGERED;
		break;

		case 4: cc = Color.YELLOW;
		break;

		}
		//setting color and shape of food
		gc.setFill(cc);
		gc.fillOval(foodX*cornersize, foodY*cornersize,cornersize-5,cornersize-5);
		

		
		//setting the color of the snake and giving it an outline to
		//make it stand out from the background better
		
		for (Corner c:snake) {
			gc.setFill(Color.GREY);
			gc.fillRect(c.x*cornersize,c.y*cornersize, cornersize-1,cornersize-1);

			gc.setFill(Color.ORANGE);
			gc.fillRect(c.x*cornersize,c.y*cornersize, cornersize-2,cornersize-2);
		}


	} //end of tick (or updates to the game)

	//the creating food method
	public static void newFood() {
		//new food is placed somewhere on the canvas randomly
		//while the game has started
		start: while(true) {
			foodX = rando.nextInt(width); 
			foodY = rando.nextInt(height);

			//if the snake touches the food, the game continues
			for (Corner c: snake) {
				if(c.x == foodX && c.y == foodY ) {
					continue start;
				}
			}

			foodcolor = rando.nextInt(5); //the food color is randomized
			speed++; //as food is eaten, speed of snake increases
			break;
		}
	}
	
	

	public static void main(String[] args) { // launching the program
		launch(args);	
		
		
		}
	}











