import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthDrop extends Drop {
    private int healthAmount;

    private static final String FOOD_VEGETABLE_DIRECTORY = "assets/Foods/Vegetables/";
    private static final String FOOD_FRUIT_DIRECTORY = "assets/Foods/Fruits/";
    private static String[] availableFoodImages;
    private static Random random = new Random();
    
    static {
        loadAvailableFoodImages();
    }

    public HealthDrop(double x, double y) {
        this(x, y, (int)(Math.random() * 21) + 20);
    }

    public HealthDrop(double x, double y, int healthAmount) {
        super(x, y, getRandomFoodImagePath());
        this.healthAmount = healthAmount;
        this.width = 64;
        this.height = 64;
    }
    
    public int getHealthAmount() {
        return healthAmount;
    }

    private static void loadAvailableFoodImages() {
        // Use List to collect paths as we don't know the final size
        List<String> foodImagePaths = new ArrayList<>();
        
        // Load vegetable images
        loadImagesFromDirectory(FOOD_VEGETABLE_DIRECTORY, foodImagePaths);
        
        // Load fruit images
        loadImagesFromDirectory(FOOD_FRUIT_DIRECTORY, foodImagePaths);
        
        // Convert list to array
        if (!foodImagePaths.isEmpty()) {
            availableFoodImages = foodImagePaths.toArray(new String[0]);
        } else {
            // Fallback if no images found
            availableFoodImages = new String[]{"assets/default.png"};
            System.err.println("No food images found, using default health image");
        }
    }
    
    private static void loadImagesFromDirectory(String directoryPath, List<String> imageList) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Food directory not found: " + directoryPath);
            return;
        }
        File[] files = directory.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            System.err.println("No food images found in " + directoryPath);
        }
        for (File file : files) {
            imageList.add(directoryPath + file.getName());
        }
    }

    private static String getRandomFoodImagePath() {
        if (availableFoodImages == null || availableFoodImages.length == 0) {
            loadAvailableFoodImages();
        }

        if (availableFoodImages.length > 0) {
            return availableFoodImages[random.nextInt(availableFoodImages.length)];
        }
        
        // Fallback
        return "assets/default.png";
    }
}
