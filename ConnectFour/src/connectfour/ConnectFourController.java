package connectfour;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/*
The main controller of the game that listens for mouse input. The mouse will either
drop a Red disc or allow the AI to use Alpha-Beta pruning to find the next Black move.
Contains a nested class 'Panel' that draws all the components of the board and discs
and the appropriate headings.

Alpha-Beta pruning algorithm is initiated via the run() method. Intiates with the
maximum depth for this algorithm (in this case 4). This in turn triggers the alphaBetaPruning()
method with the first depth being used. This triggers the getMax() or getMin() methods that
find the maximum and minimum score respectaviley. Each instance triggers another 
alphaBetaPrunign() algorithm with the next depth until the max depth is reached, after which
the preceding methods can find the best move that either achieve the maximum or
minimum score.
The score is calculated by figuring out if there will be a connect 4 (and rewarding
the AI with 11, or punishing the AI with 11 if it is the player who wins). In addition,
the number of lines of 3 and 2 are found and added/subtracted to the score. There
is an intervention in case the subsequent move the player makes results in a connect 4
which the score is set to a low value of -200 so that the AI will not look too far 
into the future.
*/
public class ConnectFourController implements ActionListener {
	//Represents the view or what the user sees.
	private final ConnectFourView view;
	
	//Represents the model or the data for the game.
	private final ConnectFourModel model;
        
	//Initializes the controller with view and model
	public ConnectFourController(final ConnectFourView view, final ConnectFourModel model) {
		this.view = view;
		this.model = model;
		
                //set dimensions
		int frameWidth = 2 * this.model.getMargin() + this.model.getCols() * this.model.getTileSize();
		int frameHeight = 3 * this.model.getMargin() + this.model.getRows() * this.model.getTileSize();
		
		//Setup Frame
		this.view.getContentPane().setPreferredSize(new Dimension(frameWidth, frameHeight));
		this.view.setTitle("Connect Four with Alpha-Beta Pruning");
		this.view.pack();
		this.view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.view.setLocationRelativeTo(null);
		this.view.setResizable(false);
		
		int buttonWidth = 2 * this.model.getMargin();
		int buttonHeight = this.model.getMargin() / 2;
		
		//Setup Restart Button
		JPanel restartPanel = new JPanel();
		this.view.getRestartButton().setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		restartPanel.add(this.view.getRestartButton(), BorderLayout.CENTER);
		this.view.add(restartPanel, BorderLayout.PAGE_END);
		
		//Setup Font
		this.view.setFont(new Font("Monospaced", Font.BOLD, this.model.getFontSize()));
		
		this.view.addRestartButtonListener(new RestartButtonListener());
		this.view.addMouseListener(new PanelListener());
		this.view.addMouseMotionListener(new CursorListener());
		this.view.addPanel(new Panel());
		
		this.view.setVisible(true);
	}
	
        //Paints all the components of the board incuding the discs, board, and win sequence
	class Panel extends JPanel {

                /*
                Responsible for updating the view.
		 * Draws the dropping disc if a timer is running and then draws the game board
		 * to simulate the disc dropping inside the game board.
		 * Draws a disc which follows the user's cursor if no timer is running.
		 * Draws the win sequence when a Connect Four is found.
                */
		@Override		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//Set color of dropping disc to be the current players color
			if (model.getCurrentColor() == model.RED) {
				view.setCurrIcon(view.getRedDiscIcon());
			} else {
				view.setCurrIcon(view.getBlackDiscIcon());
			}
			
			if (model.getTimer().isRunning()) {
				//Draw falling disc                                
				g.drawImage (view.getCurrIcon().getImage(), 
						model.getMargin() + model.getDroppingDisc().getX(),
						model.getDroppingDisc().getY(), 
						model.getTileSize(), 
						model.getTileSize(), 
						null);
			} else {
				//Draw disc that follows mouse cursor at top only if red disc
				if (!model.getWinSequence() && model.getCurrentColor()==1){
                                    g.drawImage (view.getCurrIcon().getImage(), 
							model.getMousePoint().x - (model.getTileSize() / 2),
							0, 
							model.getTileSize(), 
							model.getTileSize(), 
							null);
                                }					
                                else if(!model.getWinSequence() && model.getCurrentColor()==2){
                                    //Draw instruction to tell user to click anywhere to drop AI disc
                                    
                                }
			}
			
			for (int i = 0; i < model.getRows(); i++) {
				for (int j = 0; j < model.getCols(); j++) {
					if (model.getGameBoard()[i][j]== model.RED) {
						//Draw Red Discs
						g.drawImage (view.getRedDiscIcon().getImage(), 
								model.getMargin() + j * model.getTileSize(),
								2 * model.getMargin() + i * model.getTileSize(), 
								model.getTileSize(), 
								model.getTileSize(), 
								null);
					} else if (model.getGameBoard()[i][j]== model.BLACK) {
						//Draw Black Discs
						g.drawImage (view.getBlackDiscIcon().getImage(), 
								model.getMargin() + j * model.getTileSize(),
								2 * model.getMargin() + i * model.getTileSize(), 
								model.getTileSize(), 
								model.getTileSize(), 
								null);
					}
					//Draw the default grid
					g.drawImage (view.getDefaultIcon().getImage(), 
							model.getMargin() + (j * model.getTileSize()),
							2 * model.getMargin() + i * model.getTileSize(), 
							model.getTileSize(), 
							model.getTileSize(), 
							null);
				}
			}
			
			if (model.getWinSequence()) drawWinSequence(g);
                        
                        if(!model.getWinSequence() && model.getCurrentColor()==2)
                            drawAItext(g);
                        
		}
	}
	
        //Restarts the game if button is clicked
	class RestartButtonListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			restart();
		}
	}
	
	class PanelListener implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}
		
		@Override
		public void mouseExited(MouseEvent e) {}

                //Updates the board. Places the disc into the board if it is the 
                //human's turn. Otherwise, waits for the player to click somewhere on
                //the screen to make the AI play a disc
		@Override
		public void mousePressed(MouseEvent e) {
                    //If game hasn't been won yet
                    if(!model.getWinSequence()){
                        //If color is red
                         if(getCurrColor()==1 ){
                        //If a disc is currently falling...return.
			if (model.getTimer().isRunning()) return;
			
			//If the user clicks outside the game board...return.
			if (e.getX() < model.getMargin() 
					|| e.getX() > model.getMargin() + model.getCols() * model.getTileSize()) return;
			
			//If in the win sequence...return.
			if (model.getWinSequence()) return;
			
			//Retrieves the location of where the user clicked.
			model.getClickPoint().y = (int) Math.floor((e.getY() - model.getMargin()) / model.getTileSize());
			model.getClickPoint().x = (int) Math.floor((e.getX() - model.getMargin()) / model.getTileSize());
			
			//If the user clicks a full column...return.
			if (model.getGameBoard()[0][model.getClickPoint().x] != model.EMPTY) return;
			
			setupDroppingDisc();
                        
			//Begin the timer.
			model.getTimer().start();
                        
                    }
                    //If it is black's turn.
                    else{
                        //Wait for the previous disc to stop falling. Run the
                        //AI alpha-beta pruning algorithm with depth of 4
                        if(!model.getTimer().isRunning())
                            run(model.getCurrentColor(), 4);
                    }
                    }
		}

		@Override
		public void mouseReleased(MouseEvent e) {                    
                }	
	}
	
	class CursorListener implements MouseMotionListener {
            
                //Allows the red disc to move along with the cursor's x position
		@Override
		public void mouseMoved(MouseEvent e) {
			model.getMousePoint().x = e.getX();
			view.repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {}
	}

	@Override
	/**
	 * Called every time the timer fires. Updates the location of the dropping
	 * disc and tells the view to redraw the screen. Stops the timer once
	 * it has reached it's stopping y-coordinate and checks for Connect-Fours
	 * otherwise it switches player turns.
	 */
	public void actionPerformed(ActionEvent e) {
		//Increment Y-coordinate of falling disc.
		this.model.getDroppingDisc().setY(this.model.getDroppingDisc().getY()+10);
		this.view.repaint();
		
		//Check if dropping disc has reached bottom of game board.
		if (this.model.getDroppingDisc().getY() >= this.model.getDroppingDisc().getStopY() + this.model.getDiscSize()) {
			int row = this.model.getDroppingDisc().getStopY() / this.model.getTileSize();
			int col = this.model.getClickPoint().x;
			//Place a disc where the falling disc landed.
			this.model.getGameBoard()[row][col] = this.model.getCurrentColor();
			//Stop the timer.
			this.model.getTimer().stop();
			
			//Check if a Connect-Four is found. Else Switch turns.
			if (checkWin() > 0)
				this.model.setWinSequence(true);
			else
				switchColor();
		}
		
	}
	
	/**
	 * highlights the connect 4 sequence and draws the winning text
	 */
	public void drawWinSequence(Graphics g) {
		//Draw Connect Four
		for (int i = 0; i < this.model.getConnectFour().length; i++) {
			g.drawImage (view.getWinFlashIcon().getImage(), 
					this.model.getMargin() + (this.model.getConnectFour()[i].y * this.model.getTileSize()),
					2 * this.model.getMargin() + this.model.getConnectFour()[i].x * this.model.getTileSize(), 
					this.model.getTileSize(), 
					this.model.getTileSize(), 
					null);
		}		
		drawWinText(g);
	}
	
	//Draws winning text
	public void drawWinText(Graphics g) {
		String str;
		g.setFont(view.getFont());
		
		if (this.model.getGameBoard()[this.model.getConnectFour()[0].x][this.model.getConnectFour()[0].y] == this.model.RED) {
			str = "Red Wins";
		} else {
			str = "Black Wins";
		}
		
		int strLength = g.getFontMetrics().stringWidth(str);
		int frameWidth = 2 * this.model.getMargin() + this.model.getCols() * this.model.getTileSize();
		
		g.drawString(str, (frameWidth / 2) - (strLength / 2), this.model.getMargin() + this.model.getMargin() / 2);
	}
        
        //Draws text telling user to click anywhere to drop AI disc
        public void drawAItext(Graphics g){
            String str = "Click Anywhere to Drop AI's Disc";
            Font myFont = new Font("Helvetica", Font.BOLD, 18);
            g.setFont(myFont);
            
            int strLength = g.getFontMetrics().stringWidth(str);
            int frameWidth = 2 * this.model.getMargin() + this.model.getCols() * this.model.getTileSize();
		
            g.drawString(str, (frameWidth / 2) - (strLength / 2), this.model.getMargin() + this.model.getMargin() / 4);
        }
        
	
	/**
	 * Responsible for preparing the dropping disc for dropping.
	 * Sets the location where it should be dropped and where it should stop dropping.
	 */
	public void setupDroppingDisc() {
		//Setup the timer.
		this.model.setTimer(new Timer(this.model.getDroppingDisc().getYVelocity(), this));
		//Set the X-Coordinate of disc as the clicked column
		this.model.getDroppingDisc().setX(this.model.getClickPoint().x * this.model.getTileSize());
		//Set Y-Coordinate as top of screen.
		this.model.getDroppingDisc().setY(0);

		//Step through each row of the clicked column.
		//Find the first empty spot and set the location of the dropping disc to stop there.
		for (int i = this.model.getRows() - 1; i >= 0; i--) {
			if (this.model.getGameBoard()[i][this.model.getClickPoint().x] == this.model.EMPTY) {
				this.model.getDroppingDisc().setStopY(i * this.model.getTileSize());
                                System.out.println("Clicked: " + this.model.getClickPoint().x);
                                System.out.println("Rows:" + this.model.getRows());
				break;
			}
		}
	}
        
        //Alternate way to drop the disc. Used when the AI drops the disc with 
        //respect to a column number 'x'
        public void setupDroppingDisc2(int x) {
            
            //Setup the column
            model.getClickPoint().x = (int) Math.floor(x);
            //Drop the disc like the human player would
            setupDroppingDisc();        
            //Begin the timer.
            model.getTimer().start();
	}
	
	/**
	 * Switches the current color or turn.
	 */
	public void switchColor() {
		if (this.model.getCurrentColor() == this.model.RED) {
			this.model.setCurrentColor(this.model.BLACK);
		} else {
			this.model.setCurrentColor(this.model.RED);
		}
	}
	
	/**
	 * Checks for Connect-Fours. Accomplishes this by comparing every possible
	 * starting location for a Connect-Four with its succeeding discs.
	 * @return The color of the connect four or 0 if none was found.
	 */
	public int checkWin() {
		int start = 0;
		//Check horizontal win
		for (int row = this.model.getRows() - 1; row >= 0; row--) {
			for (int col = 0; col < this.model.getCols() - 3; col++) {
				start = this.model.getGameBoard()[row][col];
				if (start != this.model.EMPTY 
						&& start == this.model.getGameBoard()[row][col + 1]
						&& start == this.model.getGameBoard()[row][col + 2]
						&& start == this.model.getGameBoard()[row][col + 3]) {
					for (int i = 0; i < 4; i++) {
						this.model.getConnectFour()[i] = new Point(row, col + i);
					}
					return start;
				}
			}
		}
		
		//Check vertical win
		for (int row = this.model.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.model.getCols(); col++) {
				start = this.model.getGameBoard()[row][col];
				if (start != this.model.EMPTY 
						&& start == this.model.getGameBoard()[row - 1][col]
						&& start == this.model.getGameBoard()[row - 2][col]
						&& start == this.model.getGameBoard()[row - 3][col]) {
					for (int i = 0; i < 4; i++) {
						this.model.getConnectFour()[i] = new Point(row - i, col);
					}
					return start;
				}
			}
		}
		
		//Check diagonal win from bottom left to top right
		for (int row = this.model.getRows() - 1; row >= 3; row--) {
			for (int col = 0; col < this.model.getCols() - 3; col++) {
				start = this.model.getGameBoard()[row][col];
				if (start != this.model.EMPTY 
						&& start == this.model.getGameBoard()[row - 1][col + 1]
						&& start == this.model.getGameBoard()[row - 2][col + 2]
						&& start == this.model.getGameBoard()[row - 3][col + 3]) {
					for (int i = 0; i < 4; i++) {
						this.model.getConnectFour()[i] = new Point(row - i, col + i);
					}
					return start;
				}
			}
		}
		
		//Check diagonal win from bottom right to top left
		for (int row = this.model.getRows() - 1; row >= 3; row--) {
			for (int col = this.model.getCols() - 1; col >= 3; col--) {
				start = this.model.getGameBoard()[row][col];
				if (start != this.model.EMPTY 
						&& start == this.model.getGameBoard()[row-1][col-1]
						&& start == this.model.getGameBoard()[row-2][col-2]
						&& start == this.model.getGameBoard()[row-3][col-3]) {
					for (int i = 0; i < 4; i++) {
						this.model.getConnectFour()[i] = new Point(row - i, col - i);
					}
					return start;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Restarts the game by setting all the tiles in the game board to empty and 
	 * sets the current turn to red by default.
	 */
	public void restart() {
		for (int i = 0; i < model.getRows(); i++) {
			for (int j = 0; j < model.getCols(); j++) {
				model.getGameBoard()[i][j] = model.EMPTY;
			}
		}
			
		model.setCurrentColor(model.RED);
		model.setWinSequence(false);
		view.repaint();
	}

    //This turn's color
    public int getCurrColor() {
        return this.model.getCurrentColor();
    }
    
    
    //***********************************************************************************
    //***********************Alpha-Beta Pruning Algorithm********************************
    //***********************************************************************************
    
    //Global varibale that keeps track of the AI's initial best move
    int AIbestmove;
    //The maximum depth the algorithm will to through
    double maxPly;
    
    //Starts the algorithm off
    public void run(int player, double maxPly){
        //Needs to have a depth of at least 1
        if (maxPly < 1) {
            throw new IllegalArgumentException("Maximum depth must be greater than 0.");
        }        
        this.maxPly = maxPly;
        
        //Do not want to actually make a move with the model, so make a copy of the model
        ConnectFourModel deepCopy = new ConnectFourModel();
        for(int x=0; x<this.model.getRows(); x++){
                for(int y=0; y<this.model.getCols(); y++){
                    deepCopy.getGameBoard()[x][y] = this.model.getGameBoard()[x][y];
                }
            }        
        deepCopy.setCurrentColor(this.model.getCurrentColor());
        
        //Starts the algorithm off with a negative Alpha and positive Beta
        alphaBetaPruning(player, deepCopy, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);        
        //Once algorithm completed, make the AI move onto the main board
        setupDroppingDisc2(AIbestmove);
        System.out.println("THE MOVE " + AIbestmove);        
        
    }
    
    //Where the algorithm intitiates according to the current depth. This is designed
    //to be recursive as each depth calls upon this method until finally the maximum
    //depth is reached and the final moves are made. The initial move is kept track
    //of via the global varibale 'AIbestmove'
    public int alphaBetaPruning (int player, ConnectFourModel board, double alpha, double beta, int currentPly) {
        
        //checkWin() == 0 means game has been won  OR every spot is full
        if (currentPly++ == maxPly || board.checkWin() != 0 || board.boardIsFull()) {
            //System.out.println("PRUNED with: " + score(player, board));      
            return score(player, board, currentPly);
        }

        if (board.getCurrentColor() == player) {
            return getMax(player, board, alpha, beta, currentPly);
        } else {
            return getMin(player, board, alpha, beta, currentPly);
        }
    }

    //Plays the move with the highest score.
    //If the current score while searching through the possible moves is higher than
    //the current alpha,that score becomes the current alpha
    public int getMax (int player, ConnectFourModel board, double alpha, double beta, int currentPly) {
        
        //correponds to the column of the best move
        int indexOfBestMove = -1;
        //correpodns to the row where the move is to be placed
        int rowOfBestMove = -1;
        
        //The only possible moves are the slots in each of the columns
        for(int i=0; i<board.getCols(); i++){
            
            //If column is full, then no need to look for possible moves as no 
            //discs can be inserted
            if(!board.columnIsFull(i)){
                
                //Create a copy of the board...
                ConnectFourModel modifiedBoard = new ConnectFourModel();                
                //and copy the current game board to the copy...
            for(int x=0; x<modifiedBoard.getRows(); x++){
                for(int y=0; y<modifiedBoard.getCols(); y++){
                    modifiedBoard.getGameBoard()[x][y] = board.getGameBoard()[x][y];
                }
            }
            //copy the color over as well
            modifiedBoard.setCurrentColor(board.getCurrentColor());
            
            //Starts j from the bottom
            for (int j = modifiedBoard.getRows() - 1; j >= 0; j--){
                //Once an empty slot is found (from the bottom to top) insert the disc
                if (modifiedBoard.getGameBoard()[j][i] == modifiedBoard.EMPTY) {
                    
                                modifiedBoard.getGameBoard()[j][i] = modifiedBoard.getCurrentColor();
                                modifiedBoard.switchColor();
                                //Get the score of the next depth level in the series of moves
                                int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);
                                
                                //Update the best score, alpha, and track of best move
                                if (score > alpha) {
                                      alpha = score;
                                      indexOfBestMove = i;
                                      rowOfBestMove = j;
                                 }
				break;  //Breaks when we find an empty one
			}
            }
            
                // Pruning.
                if (alpha >= beta) {
                    System.out.println("BROKEN MAX at " + i + " at ply " + currentPly);
                    break;
                }
            
            }
            
        }

        //If indexOfBestMove is not -1, then at least one viable move was found
        if (indexOfBestMove != -1) {
            //Set the best move's positon to that of the board's current color...
            board.getGameBoard()[rowOfBestMove][indexOfBestMove] = board.getCurrentColor();
            //then switch the color as it is now the other player's turn
            board.switchColor();
            
            System.out.println("Index of Best Move (MAX) " + indexOfBestMove 
                + " currentPly: " + currentPly + " score(alpha): " + alpha);
            
            //If the algorithm comes back to the first instance, that is the initial
            //move to make. Store the indexOfBestMove to the AIbestmove so that move
            //can be made
            if(currentPly==1){
                AIbestmove = indexOfBestMove;
                
                //This just prints the state of the board so that it can be compared
                //to what is being displayed, just in case a disc becomes erroneously 
                //switched
                for(int j=0; j<board.getRows(); j++){
                    for(int i=0; i<board.getCols(); i++){
                        System.out.print(" " + board.getGameBoard()[j][i]);
                    }
                    System.out.println("");
                }
                
            }
                
        }
        
        return (int)alpha;
    }

    //Plays the move with the lowest score.
    //If the current score while searching through the possible moves is lower
    //than the current beta, that score becomes the current beta
    public int getMin (int player, ConnectFourModel board, double alpha, double beta, int currentPly) {
        
        //correponds to the column of the best move
        int indexOfBestMove = -1;
        //correpodns to the row where the move is to be placed
        int rowOfBestMove = -1;
        
        //The only possible moves are the slots in each of the columns
        for(int i=0; i<board.getCols(); i++){
            
            //If column is full, then no need to look for possible moves as no 
            //discs can be inserted
            if(!board.columnIsFull(i)){
               
                //Create a copy of the board...
                ConnectFourModel modifiedBoard = new ConnectFourModel();                
                //and copy the current game board to the copy...
            for(int x=0; x<modifiedBoard.getRows(); x++){
                for(int y=0; y<modifiedBoard.getCols(); y++){
                    modifiedBoard.getGameBoard()[x][y] = board.getGameBoard()[x][y];
                }
            }
            //copy the color over as well
            modifiedBoard.setCurrentColor(board.getCurrentColor());
            
            //Starts j from the bottom
            for (int j = modifiedBoard.getRows() - 1; j >= 0; j--){
                //Once an empty slot is found (from the bottom to top) insert the disc
                if (modifiedBoard.getGameBoard()[j][i] == modifiedBoard.EMPTY) {
				
                                //Makes move, then switches color
                                modifiedBoard.getGameBoard()[j][i] = modifiedBoard.getCurrentColor();
                                modifiedBoard.switchColor();
                                //Get the score of the next depth level in the series of moves
                                int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);
                                
                                //Update the best score, alpha, and track of best move
                                if (score < beta) {
                                    beta = score;
                                    indexOfBestMove = i;
                                    rowOfBestMove = j;
                                }
				break;  //Breaks when we find an empty one
			} 
            }
            
                                // Pruning.
                                if (alpha >= beta) {
                                    break;
                                }
            
           }
            
        }

        //If indexOfBestMove is not -1, then at least one viable move was found
        if (indexOfBestMove != -1) {
            //Set the best move's positon to that of the board's current color...
            board.getGameBoard()[rowOfBestMove][indexOfBestMove] = board.getCurrentColor();
            //then switch the color as it is now the other player's turn
            board.switchColor();
        }
        return (int)beta;
    }

    //The score to be returned. Wins (connect 4) score the most points, followed
    //by rows of 3, followed by rows of 2. There is an intervention if the human
    //can win in the next following move. The score is set to -200 in that case
    //so the AI doesn't look too far ahead with the possibility that the player can
    //instantly win
    public int score (int player, ConnectFourModel Board, int currentPly) {
        
        //Initialize the score to be returned
        int theScore = 0;
        
        //If the board shows red wins, add 11 to the red score
        int redWin = 0;
        if(Board.checkWin()==Board.RED)
            redWin = 11;
        
        //If the board shows black wins, add 11 to the black score
        int blackWin =0;
        if(Board.checkWin()==Board.BLACK)
            blackWin = 11;
        
        //Add the winning score (of 0 or 11) to the number of discs that have 3 aligned and
        //multiply it by 3 and the number of discs that have 2 aligned
        int redScore = redWin + Board.checkThree(Board.RED)*3 + Board.checkTwo(Board.RED);
        int blackScore = blackWin + Board.checkThree(Board.RED)*3 + Board.checkTwo(Board.BLACK);
        
        //Decrement the redScore and Increment the blackScore to total score if the current
        //player is the Black piece. Reverse if the current player is the Red piece
        if(player == Board.BLACK){
            
            //If the opponenet (the human player) wins, that takes immediate priority
            //or else AI will think too far ahead on a future win when the human can
            //immediately end the game
            if(Board.checkWin()==Board.RED)
                return -200;
            
            theScore -= redScore;
            theScore += blackScore;
        }
        else{
            theScore += redScore;
            theScore -= blackScore;
        }
        
        return theScore;        
    }
        
}
