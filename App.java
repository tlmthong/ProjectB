
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log In Page");
        Systems system = new Systems();
        system.addAccount(new SimpleStringProperty("1"), new SimpleStringProperty("1"), new SimpleStringProperty("1"),
                new SimpleDoubleProperty(80), new SimpleDoubleProperty(1.8), Goal.LOSE_WEIGHT,
                CalculateExcerciseCalories.DURATION_OF_EXCERCISE);
        AdminUser admin = new AdminUser(system, new SimpleStringProperty("1"), new SimpleStringProperty("1"),
                new SimpleStringProperty("1"));
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
