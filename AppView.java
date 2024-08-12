import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextFormatter.Change;

public class AppView {
    private VBox view;
    TextField signInUser;
    TextField signInPassword;
    public Button buttronIn;
    private Button signUpButton;
    Label info;
    private Systems system;
    public SimpleBooleanProperty correcProperty = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty correct = new SimpleBooleanProperty(false);
    Goal goal;
    CalculateExcerciseCalories calculate;
    AppController controller;

    public AppView(Systems system, Stage primaryStage, AppController controller) {
        this.controller = controller;
        this.system = system;
        createAndConfigurePane();
        createLayout();
        info.setText("Enter your email and password");
        logInAccount(primaryStage);
        signUpAccount(primaryStage);
    }

    public Parent asParent() {
        return view;
    }

    public void inAccount(Stage primaryStage, Systems s) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);

        // Make it modal
        stage.initModality(Modality.APPLICATION_MODAL);
        Button logButton = new Button("Log Out");
        logButton.setOnAction(event -> {
            s.logOut(s.currentUser.username);
            info.setText("Enter your email and password");
            stage.close();
        });
        Label label = new Label(s.getName().getValue());
        HBox h = new HBox(label);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(logButton);
        h2.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(5, h, h2);
        Scene hi = new Scene(vBox, 300, 300);
        stage.setScene(hi);
        stage.show();
    }

    private void logInAccount(Stage primaryStage) {
        buttronIn.setOnAction(event -> {
            try {
                controller.logIn(signInUser.getText(), signInPassword.getText());
                info.setText("Log In SuccessFull");
                correcProperty = new SimpleBooleanProperty(true);
                inAccount(primaryStage, system);
            } catch (Error e) {
                info.setText("Log In unsuccessful");
                correcProperty = new SimpleBooleanProperty(false);
            }
        });
    }

    private void signUpAccount(Stage primaryStage) {
        signUpButton.setOnAction(event -> {
            signUpWindow(primaryStage);
        });
    }

    private void signUpWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        TextField username = new TextField();
        TextField password = new TextField();
        TextField email = new TextField();
        TextField height = new TextField();
        configTextFieldForDoubles(height);
        TextField weight = new TextField();
        configTextFieldForDoubles(weight);
        Button Submion = new Button("Submit");
        Label inform = new Label();
        Submion.setOnAction(event -> {
            if (goal != null && calculate != null) {
                try {
                    controller.createAccount(username.getText(), password.getText(), email.getText(), weight.getText(),
                            height.getText(), goal, calculate);
                    stage.close();
                } catch (Exception e) {
                    inform.setText("Error");
                }
            } else {
                inform.setText("Error");
            }

        });
        RadioButton goal1 = new RadioButton("Gain Weight");
        goal1.setOnAction(event -> {
            goal = Goal.GAIN_WEIGHT;
        });
        RadioButton goal2 = new RadioButton("Maitain Weight");
        goal2.setOnAction(event -> {
            goal = Goal.MAINTAIN_WEIGHT;
        });
        RadioButton goal3 = new RadioButton("Loose Weight");
        goal3.setOnAction(event -> {
            goal = Goal.LOSE_WEIGHT;
        });
        RadioButton cal1 = new RadioButton("Duration");
        cal1.setOnAction(event -> {
            calculate = CalculateExcerciseCalories.DURATION_OF_EXCERCISE;
        });
        RadioButton cal2 = new RadioButton("Per Excercise");
        cal2.setOnAction(event -> {
            calculate = CalculateExcerciseCalories.PER_EXCERCISE;
        });
        HBox choiceGoal = new HBox(5, goal1, goal2, goal3);
        choiceGoal.setAlignment(Pos.CENTER);
        HBox choiceCal = new HBox(5, cal1, cal2);
        choiceCal.setAlignment(Pos.CENTER);
        VBox sign = new VBox(5, username, password, email, weight, height, choiceGoal, choiceCal, Submion, inform);
        Scene signUp = new Scene(sign, 500, 500);
        stage.setScene(signUp);
        stage.show();
    }

    public void createLayout() {
        signInUser = new TextField();
        signInPassword = new TextField();
        buttronIn = new Button("Sign In");
        info = new Label();
        signUpButton = new Button("Sign Up");
        HBox signInRow = new HBox(5, signInUser, signInPassword);
        signInRow.setAlignment(Pos.CENTER);
        HBox signInBut = new HBox(5, buttronIn);
        signInBut.setAlignment(Pos.CENTER);
        HBox information = new HBox(5, info);
        information.setAlignment(Pos.CENTER);
        HBox signUp = new HBox(5, signUpButton);
        signUp.setAlignment(Pos.CENTER);
        view.getChildren().addAll(signInRow, signInBut, information, signUp);
    }

    private void createAndConfigurePane() {
        view = new VBox(5);
        view.setAlignment(Pos.CENTER);
    }

    private void configTextFieldForDoubles(TextField field) {
        field.setTextFormatter(new TextFormatter<Double>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) {
                return c;
            }
            return null;
        }));
    }

}
