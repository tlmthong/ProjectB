
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log In Page");
        Systems system = new Systems();
        system.addAccount("1", "1", "1",
                80, 1.8, Goal.LOSE_WEIGHT,
                CalculateExcerciseCalories.DURATION_OF_EXCERCISE);
        AdminUser admin = new AdminUser(system, "Admin", "abcd-1234", "1");
        AppController controller = new AppController(system, admin);
        AppView page = new AppView(system, primaryStage, controller, admin);
        Scene scene = new Scene(page.asParent(), 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
