import java.io.EOFException;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private void showViewFitnessHistoryWindow() {
        System.out.println("Entering showViewFitnessHistoryWindow method");
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Fitness History");
    
            TableView<AppController.DailyLogEntry> table = new TableView<>();
            
            // Set up table columns
            TableColumn<AppController.DailyLogEntry, Integer> idColumn = new TableColumn<>("No.");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            TableColumn<AppController.DailyLogEntry, String> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            TableColumn<AppController.DailyLogEntry, Double> improvementColumn = new TableColumn<>("Improvement %");
            improvementColumn.setCellValueFactory(new PropertyValueFactory<>("improvementPercentage"));
            improvementColumn.setCellFactory(column -> new TableCell<AppController.DailyLogEntry, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.14f%%", item));
                    }
                }
            });
            
            TableColumn<AppController.DailyLogEntry, String> exercisesColumn = new TableColumn<>("Exercises");
            exercisesColumn.setCellValueFactory(new PropertyValueFactory<>("exercises"));
    
            TableColumn<AppController.DailyLogEntry, Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(column -> {
                return new TableCell<AppController.DailyLogEntry, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    {
                        editButton.setOnAction(event -> {
                            AppController.DailyLogEntry entry = getTableView().getItems().get(getIndex());
                            showEditDialog(entry, table);
                        });
                        deleteButton.setOnAction(event -> {
                            AppController.DailyLogEntry entry = getTableView().getItems().get(getIndex());
                            controller.deleteDailyLog(entry);
                            table.setItems(controller.getFitnessHistoryData()); // Refresh the table
                        });
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, editButton, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            });
            
            table.getColumns().addAll(idColumn, dateColumn, improvementColumn, exercisesColumn, actionsColumn);
            
            System.out.println("About to call getFitnessHistoryData");
            ObservableList<AppController.DailyLogEntry> data = controller.getFitnessHistoryData();
            System.out.println("Data size: " + data.size());
            
            // Print out the contents of the data
            for (AppController.DailyLogEntry entry : data) {
                System.out.println("Entry: ID=" + entry.getId() + ", Date=" + entry.getDate() + 
                                   ", Improvement=" + entry.getImprovementPercentage() + 
                                   ", Exercises=" + entry.getExercises());
            }
            
            table.setItems(data);
    
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));
            layout.getChildren().addAll(new Label("Fitness History:"), table);
    
            Scene scene = new Scene(layout, 800, 600);
            stage.setScene(scene);
            System.out.println("About to show stage");
            stage.show();
            System.out.println("Stage shown");
        } catch (Exception e) {
            System.err.println("Error in showViewFitnessHistoryWindow: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading fitness history: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showEditDialog(AppController.DailyLogEntry entry, TableView<AppController.DailyLogEntry> table) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Daily Log");
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField dateField = new TextField(entry.getDate());
        TextField improvementField = new TextField(String.format("%.14f", entry.getImprovementPercentage()));
        TextArea exercisesArea = new TextArea(entry.getExercises());
    
        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Improvement %:"), 0, 1);
        grid.add(improvementField, 1, 1);
        grid.add(new Label("Exercises:"), 0, 2);
        grid.add(exercisesArea, 1, 2);
    
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                double newImprovement = Double.parseDouble(improvementField.getText());
                controller.editDailyLog(entry, dateField.getText(), newImprovement, exercisesArea.getText());
                table.setItems(controller.getFitnessHistoryData()); // Refresh the table
                dialog.close();
            } catch (NumberFormatException ex) {
                // Show error message
                showAlert("Invalid Input", "Please enter a valid number for improvement percentage.");
            } catch (Exception ex) {
                // Show error message
                showAlert("Error", "An error occurred while saving: " + ex.getMessage());
            }
        });
    
        grid.add(saveButton, 1, 3);
    
        Scene dialogScene = new Scene(grid, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
                showMainMenu();
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

    private void showMainMenu() {
        view.getChildren().clear();
        Label welcomeLabel = new Label("Welcome, " + system.currentUser.getName().get());
        Button addExerciseButton = new Button("Add Exercise");
        addExerciseButton.setOnAction(event -> showAddExerciseWindow());
        Button viewDailyLogButton = new Button("View Daily Log");
        viewDailyLogButton.setOnAction(event -> showViewDailyLogWindow());
        Button viewFitnessHistoryButton = new Button("View Fitness History");
        viewFitnessHistoryButton.setOnAction(event -> {
            System.out.println("View Fitness History button clicked");
            showViewFitnessHistoryWindow();
        });
        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(event -> {
            system.logOut(system.currentUser.getName());
            view.getChildren().clear();
            createLoginLayout();
        });
        view.getChildren().addAll(welcomeLabel, addExerciseButton, viewDailyLogButton, viewFitnessHistoryButton, logOutButton);
    }

        private void showViewDailyLogWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("View Daily Log");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField dayField = new TextField();
        dayField.setPromptText("Day");
        TextField monthField = new TextField();
        monthField.setPromptText("Month");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        TextArea logInfoArea = new TextArea();
        logInfoArea.setEditable(false);
        logInfoArea.setWrapText(true);

        Button viewButton = new Button("View Log");
        viewButton.setOnAction(e -> {
            try {
                SimpleIntegerProperty day = new SimpleIntegerProperty(Integer.parseInt(dayField.getText()));
                SimpleIntegerProperty month = new SimpleIntegerProperty(Integer.parseInt(monthField.getText()));
                SimpleIntegerProperty year = new SimpleIntegerProperty(Integer.parseInt(yearField.getText()));

                DailyLog dailyLog = controller.viewDailyLog(day, month, year);
                String logInfo = controller.formatDailyLogInfo(dailyLog);
                logInfoArea.setText(logInfo);
            } catch (NumberFormatException ex) {
                logInfoArea.setText("Invalid input. Please enter valid numbers for the date.");
            } catch (Exception ex) {
                logInfoArea.setText("Error viewing daily log: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(
            new Label("Enter the date to view (DD MM YYYY):"),
            dayField, monthField, yearField,
            viewButton,
            logInfoArea
        );

        Scene scene = new Scene(layout, 400, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void createLoginLayout() {
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

    private void showAddExerciseWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Exercise");
    
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
    
        TextField dayField = new TextField();
        dayField.setPromptText("Day");
        TextField monthField = new TextField();
        monthField.setPromptText("Month");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
    
        TextField exerciseTypeField = new TextField();
        exerciseTypeField.setPromptText("Enter exercise type (e.g., PUSH_UPS)");
    
        TextField repetitionsField = new TextField();
        repetitionsField.setPromptText("Enter number of repetitions");
    
        Button addButton = new Button("Add Exercise");
        addButton.setOnAction(e -> {
            try {
                SimpleIntegerProperty day = new SimpleIntegerProperty(Integer.parseInt(dayField.getText()));
                SimpleIntegerProperty month = new SimpleIntegerProperty(Integer.parseInt(monthField.getText()));
                SimpleIntegerProperty year = new SimpleIntegerProperty(Integer.parseInt(yearField.getText()));
                
                String exerciseTypeStr = exerciseTypeField.getText().toUpperCase();
                ExerciseType exerciseType = ExerciseType.valueOf(exerciseTypeStr);
                
                SimpleIntegerProperty repetitions = new SimpleIntegerProperty(Integer.parseInt(repetitionsField.getText()));
    
                controller.addExercise(day, month, year, exerciseType, repetitions);
                showAlert("Success", "Exercise added successfully.", Alert.AlertType.INFORMATION);
                stage.close();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input. Please enter valid numbers for day, month, year, and repetitions.", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException ex) {
                showAlert("Error", "Invalid exercise type. Please enter a valid exercise type (e.g., PUSH_UPS, SIT_UPS, etc.).", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Error", "Error adding exercise: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    
        layout.getChildren().addAll(
            new Label("Enter the date for these exercises (DD MM YYYY):"),
            dayField, monthField, yearField,
            new Label("Enter exercise type:"),
            exerciseTypeField,
            new Label("Enter number of repetitions:"),
            repetitionsField,
            addButton
        );
    
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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