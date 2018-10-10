package connectfour;

/*
The main piece in the game. Has its own coordinates and velocity
*/
public class Disc {
	/**
	 * Represents the x-coordinate of the disc.
	 */
	private int x;
	
	/**
	 * Represents the y-coordinate of the disc.
	 */
	private int y;
	
	/**
	 * Represents the y-coordinate of where the dropping disc should stop dropping.
	 */
	private int stopY;
	
	/**
	 * Represents the velocity of the ball or speed in which the timer fires.
	 */
	private int yVelocity;
	
	/**
	 * Initializes an instance of Disc with specified values.
	 * @param x
	 * @param y
	 * @param stopY
	 * @param vel
	 */
	public Disc(int x, int y, int stopY, int vel) {
		this.x = x;
		this.y = y;
		this.stopY = stopY;
		this.yVelocity = vel;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setStopY(int stopY) {
		this.stopY = stopY;
	}
	
	public int getStopY() {
		return this.stopY;
	}
	
	public int getYVelocity(){
		return this.yVelocity;
	}
	
	public void setYVelocity(int vel) {
		this.yVelocity = vel;
	}
}
