import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class MazeNode extends Canvas {
    private Maze maze;
    private Canvas canvas;
    private double cellSizePx;
    private double lineWidthPx;

    public MazeNode(Maze maze, double cellSizePx, double lineWidthPx) {
        super(maze.getWidth() * cellSizePx + lineWidthPx, maze.getHeight() * cellSizePx + lineWidthPx);
        this.maze = maze;
        this.cellSizePx = cellSizePx;
        this.lineWidthPx = lineWidthPx;
        draw();
    }

    public Maze getMaze() {
        return maze;
    }
    
	public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, cellSizePx*maze.getWidth() + lineWidthPx, cellSizePx*maze.getHeight() + lineWidthPx);
		gc.setFill(Color.BLACK);

		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				if (maze.hasTopWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, cellSizePx, lineWidthPx);
				}
                if (maze.hasLeftWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                }
                if (x == maze.getWidth() - 1) {
                    if (maze.hasRightWall(x, y)) {
                        gc.fillRect((x + 1) * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                    }
                }
                if (y == maze.getHeight() - 1) {
                    if (maze.hasBottomWall(x, y)) {
                        gc.fillRect(x * cellSizePx, (y + 1) * cellSizePx, cellSizePx, lineWidthPx);
                    }
                }
			}
		}
	}
}
