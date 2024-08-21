import java.io.EOFException;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.ToggleGroup;
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
    Button modifyDaily;
    Button deleteDaily;
    TableView<DailyLog> table;
    ObservableList<DailyLog> history;
    Button addSleepButton;
    Button addGeneralButton;

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

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("View Fitness History");
        history = FXCollections
                .observableArrayList(system.currentUser.getFitnessHistory().sortedHistory());

        // try {
        // };
        table = new TableView<>();

        // Set up table columns
        TableColumn<DailyLog, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(300);
        TableColumn<DailyLog, Double> improvementColumn = new TableColumn<>("Improvement %");
        improvementColumn.setMinWidth(300);
        TableColumn<DailyLog, String> exercisesColumn = new TableColumn<>("Exercises");
        exercisesColumn.setMinWidth(300);
        // TableColumn<DailyLog, Void> actionsColumn = new TableColumn<>("Actions");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDate());
        improvementColumn.setCellValueFactory(cellData -> cellData.getValue().getImprovementPercentage().asObject());
        exercisesColumn.setCellValueFactory(
                cellData -> cellData.getValue().getListExercise(system.currentUser.caloriesCalculation));
        // improvementColumn.setCellFactory(column -> new
        // TableCell<AppController.DailyLogEntry, Double>());
        // exercisesColumn.setCellValueFactory(cellData ->
        // cellData.getValue().getBMI().asObject());

        // actionsColumn.setCellFactory(column -> {
        // return new TableCell<AppController.DailyLogEntry, Void>() {
        // private final Button editButton = new Button("Edit");
        // private final Button deleteButton = new Button("Delete");
        // {
        // editButton.setOnAction(event -> {
        // AppController.DailyLogEntry entry =
        // getTableView().getItems().get(getIndex());
        // showEditDialog(entry, table);
        // });
        // deleteButton.setOnAction(event -> {
        // AppController.DailyLogEntry entry =
        // getTableView().getItems().get(getIndex());
        // controller.deleteDailyLog(entry);
        // table.setItems(controller.getFitnessHistoryData()); // Refresh the table
        // });
        // }

        // @Override
        // protected void updateItem(Void item, boolean empty) {
        // super.updateItem(item, empty);
        // if (empty) {
        // setGraphic(null);
        // } else {
        // HBox buttons = new HBox(5, editButton, deleteButton);
        // setGraphic(buttons);
        // }
        // }
        // };
        // });
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            history = null;
            table = null;
            stage.close();
        });
        table.setItems(history);
        table.getColumns().addAll(dateColumn, improvementColumn, exercisesColumn);

        // System.out.println("About to call getFitnessHistoryData");
        // // ObservableList<AppController.DailyLogEntry> data =
        // controller.getFitnessHistoryData();
        // System.out.println("Data size: " + data.size());

        // // Print out the contents of the data
        // for (AppController.DailyLogEntry entry : data) {
        // System.out.println("Entry: ID=" + entry.getId() + ", Date=" + entry.getDate()
        // +
        // ", Improvement=" + entry.getImprovementPercentage() +
        // ", Exercises=" + entry.getExercises());
        // }
        this.deleteDaily = new Button("Delete");
        deleteDaily.setOnAction(e -> {
            DailyLog selecteDailyLog = this.table.getSelectionModel().getSelectedItem();
            controller.deleteDailyLog(selecteDailyLog);
            history = FXCollections.observableArrayList(system.currentUser.getFitnessHistory().sortedHistory());
            table.setItems(history);
        });
        this.modifyDaily = new Button("Modify");
        modifyDaily.setOnAction(e -> {
            DailyLog selecteDailyLog = this.table.getSelectionModel().getSelectedItem();
            modifyForm(stage, selecteDailyLog);
            history = FXCollections.observableArrayList(system.currentUser.getFitnessHistory().sortedHistory());
            table.setItems(history);
        });
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Fitness History:"), table, deleteDaily, modifyDaily, closeButton);

        Scene scene = new Scene(layout, 900, 600);
        stage.setScene(scene);
        System.out.println("About to show stage");
        stage.show();
        System.out.println("Stage shown");
        // } catch (Exception e) {
        // System.err.println("Error in showViewFitnessHistoryWindow: " +
        // e.getMessage());
        // e.printStackTrace();
        // showAlert("Error", "An error occurred while loading fitness history: " +
        // e.getMessage(),
        // Alert.AlertType.ERROR);
        // }
    }

    // private void showEditDialog(AppController.DailyLogEntry entry,
    // TableView<AppController.DailyLogEntry> table) {
    // Stage dialog = new Stage();
    // dialog.initModality(Modality.APPLICATION_MODAL);
    // dialog.setTitle("Edit Daily Log");

    // GridPane grid = new GridPane();
    // grid.setHgap(10);
    // grid.setVgap(10);
    // grid.setPadding(new Insets(20, 150, 10, 10));

    // TextField dateField = new TextField(entry.getDate());
    // TextField improvementField = new TextField(String.format("%.14f",
    // entry.getImprovementPercentage()));
    // TextArea exercisesArea = new TextArea(entry.getExercises());

    // grid.add(new Label("Date:"), 0, 0);
    // grid.add(dateField, 1, 0);
    // grid.add(new Label("Improvement %:"), 0, 1);
    // grid.add(improvementField, 1, 1);
    // grid.add(new Label("Exercises:"), 0, 2);
    // grid.add(exercisesArea, 1, 2);

    // Button saveButton = new Button("Save");
    // saveButton.setOnAction(e -> {
    // try {
    // double newImprovement = Double.parseDouble(improvementField.getText());
    // controller.editDailyLog(entry, dateField.getText(), newImprovement,
    // exercisesArea.getText());
    // table.setItems(controller.getFitnessHistoryData()); // Refresh the table
    // dialog.close();
    // } catch (NumberFormatException ex) {
    // // Show error message
    // showAlert("Invalid Input", "Please enter a valid number for improvement
    // percentage.");
    // } catch (Exception ex) {
    // // Show error message
    // showAlert("Error", "An error occurred while saving: " + ex.getMessage());
    // }
    // });

    // grid.add(saveButton, 1, 3);

    // Scene dialogScene = new Scene(grid, 400, 300);
    // dialog.setScene(dialogScene);
    // dialog.showAndWait();
    // }
    private void modifyForm(Stage primaryStage, DailyLog dailyLog) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        // Make it modal
        stage.initModality(Modality.APPLICATION_MODAL);
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton pushUp = new RadioButton("Push-UP");
        pushUp.setToggleGroup(toggleGroup);
        RadioButton starJump = new RadioButton("Star-Jump");
        starJump.setToggleGroup(toggleGroup);
        RadioButton tricepDip = new RadioButton("Tricep Dip");
        tricepDip.setToggleGroup(toggleGroup);
        RadioButton squats = new RadioButton("Squat");
        squats.setToggleGroup(toggleGroup);
        RadioButton burpee = new RadioButton("Burpee");
        burpee.setToggleGroup(toggleGroup);
        RadioButton chinUp = new RadioButton("Chin Up");
        chinUp.setToggleGroup(toggleGroup);
        RadioButton sitUp = new RadioButton("Sit UP");
        sitUp.setToggleGroup(toggleGroup);
        RadioButton pullUp = new RadioButton("Sit UP");
        pullUp.setToggleGroup(toggleGroup);
        TextField numberToChange = new TextField();
        Button submit = new Button("submit");
        submit.setOnAction(e -> {
            ExerciseType exerciseType = ExerciseType.PUSH_UPS;
            String number = numberToChange.getText();
            if (pushUp.isSelected()) {
                exerciseType = ExerciseType.PUSH_UPS;
            } else if (starJump.isSelected()) {
                exerciseType = ExerciseType.STAR_JUMPS;
            } else if (tricepDip.isSelected()) {
                exerciseType = ExerciseType.TRICEP_DIPS;
            } else if (burpee.isSelected()) {
                exerciseType = ExerciseType.BURPEES;
            } else if (chinUp.isSelected()) {
                exerciseType = ExerciseType.CHIN_UPS;
            } else if (sitUp.isSelected()) {
                exerciseType = ExerciseType.SIT_UPS;
            } else if (squats.isSelected()) {
                exerciseType = ExerciseType.SQUATS;
            } else if (pullUp.isSelected()) {
                exerciseType = ExerciseType.PULL_UPS;
            }
            if (!number.isEmpty()) {
                controller.addupdateDailyLog(dailyLog, exerciseType, number);
                ;
                history = FXCollections.observableArrayList(system.currentUser.getFitnessHistory().sortedHistory());
                table.setItems(history);
                stage.close();
            }

        });
        VBox vBox = new VBox(5, pushUp, starJump, tricepDip, burpee, chinUp, sitUp, squats, pullUp, numberToChange,
                submit);
        Scene hi = new Scene(vBox, 300, 300);
        stage.setScene(hi);
        stage.show();

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
        Label label = new Label("Welcome, " + s.getName().getValue());
        Button addExerciseButton = new Button("Add Exercise");
        addExerciseButton.setOnAction(event -> showAddExerciseWindow(primaryStage));
        Button viewDailyLogButton = new Button("View Daily Log");
        viewDailyLogButton.setOnAction(event -> showViewDailyLogWindow());
        Button viewFitnessHistoryButton = new Button("View Fitness History");
        viewFitnessHistoryButton.setOnAction(event -> {
            System.out.println("View Fitness History button clicked");
            this.system.currentUser.viewHistory();
            showViewFitnessHistoryWindow();
        });
        addSleepButton = new Button("Add Sleep");
        addSleepButton.setOnAction(e -> {
            addSleepWindow(primaryStage);
        });
        addGeneralButton = new Button("Add General");
        addGeneralButton.setOnAction(e -> {
            addGeneralWindow(primaryStage);
        });
        HBox h = new HBox(label);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(logButton);
        h2.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(5, h, h2, addExerciseButton, addSleepButton, addGeneralButton, viewDailyLogButton,
                viewFitnessHistoryButton);
        Scene hi = new Scene(vBox, 300, 300);
        stage.setScene(hi);
        stage.show();
    }

    private void addGeneralWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        ListView<DailyLog> listing = new ListView<>();
        ObservableList<DailyLog> availableDate = FXCollections
                .observableArrayList(system.currentUser.history.getHistory());
        listing.setItems(availableDate);
        Button addButton = new Button("Add Calories");
        addButton.setOnAction(e -> {
            DailyLog dailyLog = listing.getSelectionModel().getSelectedItem();
            addGeneralWindowForm(primaryStage, dailyLog);
        });
        VBox listBox = new VBox(5, listing, addButton);
        Scene listingAccount = new Scene(listBox, 500, 500);
        stage.setScene(listingAccount);
        stage.setTitle("Add General");
        stage.show();

    }

    private void addGeneralWindowForm(Stage primaryStage, DailyLog daily) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        TextField calories = new TextField();
        calories.setPromptText("Enter your calories");
        Label label = new Label("Consume calories or Burn calories");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton add = new RadioButton("Consume");
        add.setToggleGroup(toggleGroup);
        RadioButton burn = new RadioButton("Burn");
        burn.setToggleGroup(toggleGroup);
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (add.isSelected()) {
                controller.addCalories(calories.getText(), daily);
            } else if (burn.isSelected()) {
                controller.addCalories(calories.getText(), daily);
            }
            stage.close();
        });
        VBox vbox = new VBox(calories, add, burn, submit);
        Scene scence = new Scene(vbox, 300, 300);
        stage.setScene(scence);
        stage.show();
    }

    private void addSleepWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label dateLabel = new Label("Enter Date");
        TextField dayField = new TextField();
        TextField monthField = new TextField();
        TextField yearField = new TextField();
        Label hoursLabel = new Label("Enter Hours of Sleep:");
        TextField hoursField = new TextField();
        configTextFieldForDoubles(hoursField);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            try {
                String day = dayField.getText();
                String month = monthField.getText();
                String year = yearField.getText();
                String hours = hoursField.getText();
                controller.addSleep(day, month, year, hours);
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, dateLabel, dayField, monthField, yearField, hoursLabel, hoursField,
                submitButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 300);
        stage.setScene(scene);
        stage.setTitle("Add Sleep");
        stage.show();
    }

    private void logInAccount(Stage primaryStage) {
        buttronIn.setOnAction(event -> {
            try {
                controller.logIn(signInUser.getText(), signInPassword.getText());
                info.setText("Log In SuccessFull");
                correcProperty = new SimpleBooleanProperty(true);
                // showMainMenu();
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

    // private void showMainMenu() {
    // view.getChildren().clear();
    // Label welcomeLabel = new Label("Welcome, " +
    // system.currentUser.getName().get());
    // Button addExerciseButton = new Button("Add Exercise");
    // addExerciseButton.setOnAction(event -> showAddExerciseWindow());
    // Button viewDailyLogButton = new Button("View Daily Log");
    // viewDailyLogButton.setOnAction(event -> showViewDailyLogWindow());
    // Button viewFitnessHistoryButton = new Button("View Fitness History");
    // viewFitnessHistoryButton.setOnAction(event -> {
    // System.out.println("View Fitness History button clicked");
    // showViewFitnessHistoryWindow();
    // });
    // Button logOutButton = new Button("Log Out");
    // logOutButton.setOnAction(event -> {
    // system.logOut(system.currentUser.getName());
    // view.getChildren().clear();
    // createLoginLayout();
    // });
    // view.getChildren().addAll(welcomeLabel, addExerciseButton,
    // viewDailyLogButton, viewFitnessHistoryButton,
    // logOutButton);
    // }

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

                DailyLog dailyLog = system.currentUser.getFitnessHistory().getDailyLog(day, month, year);
                logInfoArea.setText(dailyLog.viewDailyLog(system.currentUser.caloriesCalculation).get());
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
                logInfoArea);

        Scene scene = new Scene(layout, 400, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddExerciseWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
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

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton pushUp = new RadioButton("Push-UP");
        pushUp.setToggleGroup(toggleGroup);
        RadioButton starJump = new RadioButton("Star-Jump");
        starJump.setToggleGroup(toggleGroup);
        RadioButton tricepDip = new RadioButton("Tricep Dip");
        tricepDip.setToggleGroup(toggleGroup);
        RadioButton squats = new RadioButton("Squat");
        squats.setToggleGroup(toggleGroup);
        RadioButton burpee = new RadioButton("Burpee");
        burpee.setToggleGroup(toggleGroup);
        RadioButton chinUp = new RadioButton("Chin Up");
        chinUp.setToggleGroup(toggleGroup);
        RadioButton sitUp = new RadioButton("Sit UP");
        sitUp.setToggleGroup(toggleGroup);
        RadioButton pullUp = new RadioButton("Pull UP");
        pullUp.setToggleGroup(toggleGroup);

        TextField repetitionsField = new TextField();
        repetitionsField.setPromptText("Enter number of repetitions");
        TextField hoursField = new TextField();
        hoursField.setPromptText("Enter number of hours");

        Button addButton = new Button("Add Exercise");
        addButton.setOnAction(e -> {
            try {
                SimpleIntegerProperty day = new SimpleIntegerProperty(Integer.parseInt(dayField.getText()));
                SimpleIntegerProperty month = new SimpleIntegerProperty(Integer.parseInt(monthField.getText()));
                SimpleIntegerProperty year = new SimpleIntegerProperty(Integer.parseInt(yearField.getText()));

                ExerciseType exerciseType = ExerciseType.PUSH_UPS;
                if (pushUp.isSelected()) {
                    exerciseType = ExerciseType.PUSH_UPS;
                } else if (starJump.isSelected()) {
                    exerciseType = ExerciseType.STAR_JUMPS;
                } else if (tricepDip.isSelected()) {
                    exerciseType = ExerciseType.TRICEP_DIPS;
                } else if (burpee.isSelected()) {
                    exerciseType = ExerciseType.BURPEES;
                } else if (chinUp.isSelected()) {
                    exerciseType = ExerciseType.CHIN_UPS;
                } else if (sitUp.isSelected()) {
                    exerciseType = ExerciseType.SIT_UPS;
                } else if (squats.isSelected()) {
                    exerciseType = ExerciseType.SQUATS;
                } else if (pullUp.isSelected()) {
                    exerciseType = ExerciseType.PULL_UPS;
                }
                // change to radio button

                if (this.system.currentUser.caloriesCalculation == CalculateExcerciseCalories.PER_EXCERCISE) {
                    SimpleIntegerProperty repetitions = new SimpleIntegerProperty(
                            Integer.parseInt(repetitionsField.getText()));
                    controller.addExercise(day, month, year, exerciseType, repetitions);
                } else {
                    SimpleDoubleProperty hour = new SimpleDoubleProperty(Double.parseDouble(hoursField.getText()));
                    controller.addExercise(day, month, year, exerciseType, hour);
                }
                stage.close();
            } catch (NumberFormatException ex) {
            } catch (IllegalArgumentException ex) {
            }
        });

        layout.getChildren().addAll(
                new Label("Enter the date for these exercises (DD MM YYYY):"),
                dayField, monthField, yearField,
                new Label("Enter exercise type:"),
                pushUp, starJump, tricepDip, burpee, chinUp, sitUp, squats, pullUp,
                new Label("Enter number of repetitions:"),
                repetitionsField, hoursField,
                addButton);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
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