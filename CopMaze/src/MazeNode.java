import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class MazeNode extends Pane {
    private Maze maze;
    private double cellSizePx;
    private double lineWidthPx;
    private double cellContentPx;
    private GemNode[] gemNodes;
    private CharacterNode characterNode;
    private Canvas canvas;
    private Pane mazeContentPane;

    public MazeNode(Maze maze, double cellSizePx, double lineWidthPx) {
        this.canvas = new Canvas(maze.getWidth() * cellSizePx + lineWidthPx, maze.getHeight() * cellSizePx + lineWidthPx);
        mazeContentPane = new Pane();
        this.maze = maze;
        this.cellSizePx = cellSizePx;
        this.lineWidthPx = lineWidthPx;
        this.cellContentPx = cellSizePx - lineWidthPx;
        generateCharacterNode();
        generateGemNodes();
        draw();
        this.getChildren().addAll(canvas, mazeContentPane);
        this.maze.setOnChangeCallback(() -> {
            update();
        });
    }

    private void generateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode = new CharacterNode(character.spriteId, (int)cellContentPx, (int)cellContentPx);
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
        mazeContentPane.getChildren().add(characterNode);
    }

    public void updateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
    }

    private void generateGemNodes() {
        Gem[] gems = maze.getGems();
        gemNodes = new GemNode[gems.length];
        for (int i = 0; i < gems.length; i++) {
            gemNodes[i] = new GemNode(i, (int)cellContentPx, (int)cellContentPx);
            gemNodes[i].setX(gems[i].c.x * cellSizePx + lineWidthPx);
            gemNodes[i].setY(gems[i].c.y * cellSizePx + lineWidthPx);
            gemNodes[i].setVisible(!gems[i].collected);
            mazeContentPane.getChildren().add(gemNodes[i]);
        }
    }

    private void updateGemNodes() {
        Gem[] gems = maze.getGems();
        for (int i = 0; i < gems.length; i++) {
            gemNodes[i].setX(gems[i].c.x * cellSizePx + lineWidthPx);
            gemNodes[i].setY(gems[i].c.y * cellSizePx + lineWidthPx);
            gemNodes[i].setVisible(!gems[i].collected);
        }
    }

    public void update() {
        updateCharacterNode();
        updateGemNodes();
    }

    public Maze getMaze() {
        return maze;
    }
    
	public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
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
