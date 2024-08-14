import java.io.EOFException;

import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    Button adminButton;
    AdminUser admin;
    ListView<FitnessUser> listView;
    ObservableList<FitnessUser> createdAccount;

    public AppView(Systems system, Stage primaryStage, AppController controller, AdminUser admin) {
        this.controller = controller;
        this.system = system;
        this.admin = admin;
        createAndConfigurePane();
        createLayout();
        info.setText("Enter your email and password");
        logInAccount(primaryStage);
        signUpAccount(primaryStage);
        goToAdmin(primaryStage);
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

    private void goToAdmin(Stage primaryStage) {
        adminButton.setOnAction(e -> {
            adminWindow(primaryStage);
        });
    }

    private void adminWindow(Stage primaryStage) {
        listView = new ListView<>();
        createdAccount = FXCollections.observableArrayList(admin.getAllLoggedInAccount());
        listView.setItems(createdAccount);
        Stage stage = new Stage();
        stage.initOwner(primaryStage);

        // Make it modal
        stage.initModality(Modality.APPLICATION_MODAL);
        Button removeButton = new Button("Remove Account");
        removeButton.setOnAction(e -> {
            removeAccoutWindow(primaryStage);
        });
        HBox remove = new HBox(removeButton);
        remove.setAlignment(Pos.CENTER);
        VBox listAccount = new VBox(5, listView, remove);
        Scene listingAccount = new Scene(listAccount, 500, 500);
        stage.setScene(listingAccount);
        stage.setTitle("Created accounts");
        stage.show();
    }

    private void removeAccoutWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);

        // Make it modal
        stage.initModality(Modality.APPLICATION_MODAL);
        Label idLabel = new Label("Please enter the id you want to remove");
        TextField idField = new TextField();
        configTextFieldForDoubles(idField);
        Label keyLabel = new Label("Please enter the your key");
        TextField keyField = new TextField();
        Button Submit = new Button();
        Submit.setOnAction(e -> {
            if (controller.verifyKey(keyField.getText())) {
                try {
                    controller.removeAccout(idField.getText());
                } catch (Error except) {
                    createdAccount.setAll(admin.getAllLoggedInAccount());
                    listView.setItems(createdAccount);
                    stage.close();
                }
            }

        });
        VBox removeBox = new VBox(5, idLabel, idField, keyLabel, keyField, Submit);
        Scene listingAccount = new Scene(removeBox, 500, 500);
        stage.setScene(listingAccount);
        stage.show();

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
        adminButton = new Button("Go to admin mode");
        HBox signInRow = new HBox(5, signInUser, signInPassword);
        signInRow.setAlignment(Pos.CENTER);
        HBox signInBut = new HBox(5, buttronIn);
        signInBut.setAlignment(Pos.CENTER);
        HBox information = new HBox(5, info);
        information.setAlignment(Pos.CENTER);
        HBox signUp = new HBox(5, signUpButton);
        signUp.setAlignment(Pos.CENTER);
        view.getChildren().addAll(signInRow, signInBut, information, signUp, adminButton);
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
