package connectfour;

import java.awt.Point;
import javax.swing.Timer;

/*The model of the board that contains the 2d array of the board and its discs.
1 is red, 2 is black, 0 is empty. Has methods to tell who won, how many 3 in a 
row there are, 2 in a row there are. This can be used by the Alpha-Beta pruning
algorithm in the Controller class by looking at each instance of the model of 
the board and calculating score by taking these aspects in the game.
*/
public class ConnectFourModel {
	
	//Represents the gameBoard in which discs are placed.
	private int gameBoard[][];
	
	//Represents the disc which drops when the user clicks a column of the game board.
	private Disc droppingDisc;
	
	//Represents the location where the user clicks
	private Point clickPoint;
	
	//Represents the location of the mouse cursor.
	private Point mousePoint;
	
	//Represents the location of 4 discs which make up the Connect-Four.
	private Point connectFour[];
	
	//Represents whether or not to begin drawing the win Sequence.
	private boolean winSequence;
	
	//Represents timer for the dropping disc.
	private Timer timer;
	
	//Represents the size of the disc.
	private final int DISC_SIZE = 75;
	
	//Represents the size of the tile.
	private final int TILE_SIZE = 100;
	
	//Represents the distance between the game board and the window.
	private final int MARGIN = 50;
	
	//Represents the font size of the text drawn in the win sequence.
	private final int FONT_SIZE = 48;
	
	//Represents the status of a tile in the game board. In this case: an empty tile.
	public final int EMPTY = 0;
	
	//Represents the status of a tile in the game board. In this case: A red disc.
	public final int RED = 1;
	
	//Represents the status of a tile in the game board. In this case: A black disc.
	public final int BLACK = 2;
	
	//Represents the # of rows in the game board.
	private final int ROWS = 6;
	
	//Represents the # of columns in the game board.
	private final int COLS = 7;
	
	//Represents the speed in which the timer fires for the dropping disc.
	private final int Y_DISC_VELOCITY = 5;
	
	//Represents the color of the current turn.
	private int currentColor = RED;
	
	//Initializes a new instance of ConnectFourModel with default empty values.
	public ConnectFourModel() {
		this.gameBoard = new int[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				this.gameBoard[i][j] = EMPTY;
			}
		}
		
		this.connectFour = new Point[4];
		for (int i = 0; i < 4; i++) {
			this.connectFour[i]= new Point(0,0); 
		}
		
		this.droppingDisc = new Disc(0, 0, 0, Y_DISC_VELOCITY);
		this.winSequence = false;
		this.timer = new Timer(0, null);
		this.clickPoint = new Point(0,0);
		this.mousePoint = new Point(0,0);
	}
	
	public int getRows() {
		return this.ROWS;
	}
	
	public int getCols() {
		return this.COLS;
	}
	
	public int getMargin() {
		return this.MARGIN;
	}
	public int getDiscSize() {
		return this.DISC_SIZE;
	}
	
	public int getTileSize() {
		return this.TILE_SIZE;
	}
	
	public int getFontSize() {
		return this.FONT_SIZE;
	}
	
	public int[][] getGameBoard() {
		return this.gameBoard;
	}
	
	public int getCurrentColor() {
		return this.currentColor;
	}
	
	public void setCurrentColor(int color) {
		this.currentColor = color;
	}
	
	public Disc getDroppingDisc() {
		return this.droppingDisc;
	}
	
	public Timer getTimer(){
		return this.timer;
	}
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public Point getClickPoint() {
		return this.clickPoint;
	}
	
	public Point getMousePoint() {
		return this.mousePoint;
	}
	
	public boolean getWinSequence() {
		return this.winSequence;
	}
	
	public void setWinSequence(boolean winSequence) {
		this.winSequence = winSequence;
	}
        
	public Point[] getConnectFour() {
		return this.connectFour;
	}
        
        //Checks if there is a Connect 4 win
        public int checkWin() {
		int start = 0;
		//Check horizontal win
		for (int row = this.getRows() - 1; row >= 0; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start != this.EMPTY 
						&& start == this.getGameBoard()[row][col + 1]
						&& start == this.getGameBoard()[row][col + 2]
						&& start == this.getGameBoard()[row][col + 3]) {
					for (int i = 0; i < 4; i++) {
						this.getConnectFour()[i] = new Point(row, col + i);
					}
					return start;
				}
			}
		}
		
		//Check vertical win
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols(); col++) {
				start = this.getGameBoard()[row][col];
				if (start != this.EMPTY 
						&& start == this.getGameBoard()[row - 1][col]
						&& start == this.getGameBoard()[row - 2][col]
						&& start == this.getGameBoard()[row - 3][col]) {
					for (int i = 0; i < 4; i++) {
						this.getConnectFour()[i] = new Point(row - i, col);
					}
					return start;
				}
			}
		}
		
		//Check diagonal win from bottom left to top right
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start != this.EMPTY 
						&& start == this.getGameBoard()[row - 1][col + 1]
						&& start == this.getGameBoard()[row - 2][col + 2]
						&& start == this.getGameBoard()[row - 3][col + 3]) {
					for (int i = 0; i < 4; i++) {
						this.getConnectFour()[i] = new Point(row - i, col + i);
					}
					return start;
				}
			}
		}
		
		//Check diagonal win from bottom right to top left
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = this.getCols() - 1; col >= 3; col--) {
				start = this.getGameBoard()[row][col];
				if (start != this.EMPTY 
						&& start == this.getGameBoard()[row-1][col-1]
						&& start == this.getGameBoard()[row-2][col-2]
						&& start == this.getGameBoard()[row-3][col-3]) {
					for (int i = 0; i < 4; i++) {
						this.getConnectFour()[i] = new Point(row - i, col - i);
					}
					return start;
				}
			}
		}
		
		return 0;
	}
        
        
        //Checks how many lines of 3 there are. Increments the counter by checking
        //vertically, horizontally, diagonally
        public int checkThree(int theColor){
            int start = 0;
            int counter = 0;
		//Check horizontal win
		for (int row = this.getRows() - 1; row >= 0; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row][col + 1]
						&& start == this.getGameBoard()[row][col + 2]) {
					counter++;
				}
			}
		}
		
		//Check vertical win
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols(); col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row - 1][col]
						&& start == this.getGameBoard()[row - 2][col]) {
					counter++;
				}
			}
		}
		
		//Check diagonal win from bottom left to top right
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row - 1][col + 1]
						&& start == this.getGameBoard()[row - 2][col + 2]) {
					counter++;
				}
			}
		}
		System.out.println("THE IT IS " + start);
		//Check diagonal win from bottom right to top left
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = this.getCols() - 1; col >= 3; col--) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row-1][col-1]
						&& start == this.getGameBoard()[row-2][col-2]) {
					counter++;
				}
			}
		}
		
		return counter;
        }
        
        //Checks how many lines of 2 there are. Increments the counter by checking
        //vertically, horizontally, diagonally
        public int checkTwo(int theColor){
            int start = 0;
            int counter = 0;
		//Check horizontal win
		for (int row = this.getRows() - 1; row >= 0; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row][col + 1]) {
					counter++;
				}
			}
		}
		
		//Check vertical win
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols(); col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row - 1][col]) {
					counter++;
				}
			}
		}
		
		//Check diagonal win from bottom left to top right
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.getCols() - 3; col++) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row - 1][col + 1]) {
					counter++;
				}
			}
		}
		System.out.println("THE IT IS " + start);
		//Check diagonal win from bottom right to top left
		for (int row = this.getRows() - 1; row >= 3; row--) {
			for (int col = this.getCols() - 1; col >= 3; col--) {
				start = this.getGameBoard()[row][col];
				if (start == theColor
						&& start == this.getGameBoard()[row-1][col-1]) {
					counter++;
				}
			}
		}
		
		return counter;
        }
        
        public void switchColor() {
		if (this.getCurrentColor() == this.RED) {
			this.setCurrentColor(this.BLACK);
		} else {
			this.setCurrentColor(this.RED);
		}
	}
        
        public boolean boardIsFull(){
            
            for(int x=0; x<getRows(); x++){
                for(int y=0; y<getCols(); y++){
                    if(getGameBoard()[x][y] == EMPTY)
                        return false;                    
                }
            }
            
            return true;           
            
        }
        
        //If 0 (top) row is not empty, it is safe to assume that column is full
        public boolean columnIsFull(int i){
            if(getGameBoard()[0][i] != 0){
                return true;
            }
            else{
                return false;
            }
        }
        
        
	
}
