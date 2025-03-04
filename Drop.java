import java.io.File;

public class Drop extends Entity {
    private static final int DROP_SIZE = 48;

    protected boolean isCollected = false;

    public Drop(double x, double y, String appearanceImagePath) {
        this.x = x;
        this.y = y;
        this.width = DROP_SIZE;
        this.height = DROP_SIZE;
        this.appearanceImagePath = appearanceImagePath;
        setImage(new File(appearanceImagePath));
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        isCollected = true;
    }
}
