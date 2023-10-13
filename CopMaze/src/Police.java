

public class Police {
	public Coordinate c;
	public boolean stop;
	
	Coordinate[] movement  = new Coordinate[4];
    Police(Coordinate c) {
        this.c = c;
        
    }
    
    
    public Coordinate startMove() {
    	Coordinate newPos = new Coordinate(0, 0);
 
    	if((int)(Math.random()*4) == 1) {
    		newPos.x = c.x;
    		newPos.y = c.y + 1;
    		System.out.println("CASE 1");
		} else if ((int)(Math.random()*4) == 2) {
			newPos.x = c.x;
    		newPos.y = c.y - 1;
    		System.out.println("CASE 2");
		} else if ((int)(Math.random()*4) == 3) {
			newPos.x = c.x + 1;
    		newPos.y = c.y;
    		System.out.println("CASE 3");
		} else if ((int)(Math.random()*4) == 0) {
			newPos.x = c.x -1;
    		newPos.y = c.y;
    		System.out.println("CASE 0");
		}
    	
    	return newPos;
//    	c.x = newPos.x;
//    	c.y = newPos.y;
    }
}
