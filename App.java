
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log In Page");
        Systems system = new Systems();
        AppController controller = new AppController(system);
        AppView page = new AppView(system, primaryStage, controller);
        Scene scene = new Scene(page.asParent(), 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
