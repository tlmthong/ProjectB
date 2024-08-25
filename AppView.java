import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextFormatter.Change;

public class AppView {
    private VBox view;
    private TextField signInUser;
    private TextField signInPassword;
    private Button buttronIn;
    private Button signUpButton;
    private Label info;
    private Systems system;
    private Goal goal;
    private CalculateExcerciseCalories calculate;
    private AppController controller;
    private Button adminButton;
    private AdminUser admin;
    private ListView<FitnessUser> listView;
    private ObservableList<FitnessUser> createdAccount;
    private Button modifyDaily;
    private Button deleteDaily;
    private TableView<DailyLog> table;
    private ObservableList<DailyLog> history;
    private Button addSleepButton;
    private Button addGeneralButton;
    private CaloriesCalulator caloriesCalulator = new CaloriesCalulator();

    public AppView(Systems system, Stage primaryStage, AppController controller, AdminUser admin) {
        this.controller = controller;
        this.controller.setCalculator(caloriesCalulator);
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
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("View Fitness History");
        history = FXCollections
                .observableArrayList(system.getCurrentUser().getFitnessHistory().sortedHistory());

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
                cellData -> cellData.getValue().getListExercise(system.getCurrentUser().getCaloriesCalculation()));
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            history = null;
            table = null;
            stage.close();
        });
        table.setItems(history);
        table.getColumns().addAll(dateColumn, improvementColumn, exercisesColumn);
        this.deleteDaily = new Button("Delete");
        deleteDaily.setOnAction(e -> {
            DailyLog selecteDailyLog = this.table.getSelectionModel().getSelectedItem();
            controller.deleteDailyLog(selecteDailyLog);
            history = FXCollections.observableArrayList(system.getCurrentUser().getFitnessHistory().sortedHistory());
            table.setItems(history);
        });
        this.modifyDaily = new Button("Modify");
        modifyDaily.setOnAction(e -> {
            DailyLog selecteDailyLog = this.table.getSelectionModel().getSelectedItem();
            modifyForm(stage, selecteDailyLog);
            history = FXCollections.observableArrayList(system.getCurrentUser().getFitnessHistory().sortedHistory());
            table.setItems(history);
        });
        VBox layout = new VBox(10);
        HBox choice = new HBox(deleteDaily, modifyDaily, closeButton);
        choice.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Fitness History:"), table, choice);
        Scene scene = new Scene(layout, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

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
        numberToChange.setPromptText("Enter repetition or hours");
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
                history = FXCollections
                        .observableArrayList(system.getCurrentUser().getFitnessHistory().sortedHistory());
                table.setItems(history);
                stage.close();
            }

        });
        HBox choice1 = new HBox(pushUp, starJump, tricepDip);
        choice1.setAlignment(Pos.CENTER);
        HBox choice2 = new HBox(burpee, chinUp, sitUp);
        choice2.setAlignment(Pos.CENTER);
        HBox choice3 = new HBox(squats, pullUp);
        choice3.setAlignment(Pos.CENTER);
        HBox numBox = new HBox(numberToChange);
        numBox.setAlignment(Pos.CENTER);
        HBox submitBox = new HBox(submit);
        Label label = new Label("Choose the excercise");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);
        submitBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(5, labelBox, choice1, choice2, choice3, numBox, submitBox);
        Scene hi = new Scene(vBox, 600, 600);
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
            s.logOut(s.getCurrentUser().username.get());
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
            this.system.getCurrentUser().viewHistory();
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
        Button calculatorButton = new Button("Estimate your calories");
        calculatorButton.setOnAction(e -> {
            chooseExcerciseWindow(primaryStage);
        });
        HBox head = new HBox(label);
        head.setAlignment(Pos.CENTER);
        HBox head2 = new HBox(logButton);
        head2.setAlignment(Pos.CENTER);
        HBox addExeriseBox = new HBox(addExerciseButton);
        addExeriseBox.setAlignment(Pos.CENTER);
        HBox addSleepeBox = new HBox(addSleepButton);
        addSleepeBox.setAlignment(Pos.CENTER);
        HBox addGenBox = new HBox(addGeneralButton);
        addGenBox.setAlignment(Pos.CENTER);
        HBox viewBox = new HBox(viewDailyLogButton);
        viewBox.setAlignment(Pos.CENTER);
        HBox calBox = new HBox(calculatorButton);
        calBox.setAlignment(Pos.CENTER);
        HBox historyBox = new HBox(viewFitnessHistoryButton);
        historyBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(5, head, addExeriseBox, addSleepeBox, addGenBox, viewBox,
                calBox,
                historyBox, head2);
        Scene scene = new Scene(vBox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void chooseExcerciseWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
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
        RadioButton pullUp = new RadioButton("Pull UP");
        pullUp.setToggleGroup(toggleGroup);
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            ExerciseType exerciseType;
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
            } else {
                exerciseType = ExerciseType.PULL_UPS;
            }
            controller.changeCalculatorExercise(exerciseType);
            controller.changeCalculatorWeight();
            calculateCaloriesWindow(primaryStage);
        });
        HBox choice1 = new HBox(pushUp, starJump, tricepDip);
        choice1.setAlignment(Pos.CENTER);
        HBox choice2 = new HBox(burpee, chinUp, sitUp);
        choice2.setAlignment(Pos.CENTER);
        HBox choice3 = new HBox(squats, pullUp);
        choice3.setAlignment(Pos.CENTER);
        submit.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(5, new Label("Choose the excercise"), choice1, choice2, choice3, submit);
        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void calculateCaloriesWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField hourField = new TextField();
        hourField.setPromptText("Enter hour");
        TextField repField = new TextField();
        repField.setPromptText("Enter repetition");
        Label resultHour = new Label();
        Label resultRep = new Label();
        repField.textProperty().addListener((observe, old, newText) -> {
            controller.changeCalculatorRep(newText);
        });

        hourField.textProperty().addListener((obs, old, newText) -> {
            controller.changeCalculatorHour(newText);
        });
        this.caloriesCalulator.getHour().addListener((obs, old, newNum) -> {
            updateIfNeeded(newNum, hourField);
        });
        this.caloriesCalulator.getRep().addListener((obs, old, newNum) -> {
            updateIfNeeded(newNum, repField);
        });
        resultHour.textProperty().bind(caloriesCalulator.getCalorieHour().asString());
        resultRep.textProperty().bind(caloriesCalulator.getCalorieRep().asString());
        HBox hourResult = new HBox(new Label("Calories burns from duration: "), resultHour);
        hourResult.setAlignment(Pos.CENTER);
        HBox repResult = new HBox(new Label("Calories burns from repetitions: "), resultRep);
        repResult.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(5, hourField, repField, hourResult, repResult);
        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void updateIfNeeded(Number value, TextField field) {
        String s = value.toString();
        if (!field.getText().equals(s)) {
            field.setText(s);
        }
    }

    private void addGeneralWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        ListView<DailyLog> listing = new ListView<>();
        ObservableList<DailyLog> availableDate = FXCollections
                .observableArrayList(system.getCurrentUser().getFitnessHistory().getHistory());
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
        VBox vbox = new VBox(calories, label, add, burn, submit);
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
        dayField.setPromptText("Day");
        TextField monthField = new TextField();
        monthField.setPromptText("Month");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
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
                System.out.println("Error");
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
                // showMainMenu();
                inAccount(primaryStage, system);
            } catch (Error e) {
                info.setText("Log In unsuccessful");
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
        Button Submit = new Button("Remove");
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
        username.setPromptText("Enter your username");
        TextField password = new TextField();
        password.setPromptText("Enter your password");
        TextField email = new TextField();
        email.setPromptText("Enter your email");
        TextField height = new TextField();
        height.setPromptText("Enter your height");
        configTextFieldForDoubles(height);
        TextField weight = new TextField();
        weight.setPromptText("Enter your weight");
        configTextFieldForDoubles(weight);
        Button Submion = new Button("Submit");
        Label inform = new Label();
        ToggleGroup goalToggle = new ToggleGroup();
        RadioButton goal1 = new RadioButton("Gain Weight");
        goal1.setToggleGroup(goalToggle);
        RadioButton goal2 = new RadioButton("Maitain Weight");
        goal2.setToggleGroup(goalToggle);
        RadioButton goal3 = new RadioButton("Loose Weight");
        goal3.setToggleGroup(goalToggle);

        ToggleGroup calToggle = new ToggleGroup();
        RadioButton cal1 = new RadioButton("Duration");
        cal1.setToggleGroup(calToggle);
        RadioButton cal2 = new RadioButton("Per Excercise");
        cal2.setToggleGroup(calToggle);
        Submion.setOnAction(event -> {
            if (goal1.isSelected()) {
                this.goal = Goal.GAIN_WEIGHT;
            } else if (goal2.isSelected()) {
                this.goal = Goal.LOSE_WEIGHT;
            } else if (goal3.isSelected()) {
                this.goal = Goal.MAINTAIN_WEIGHT;
            }

            if (cal1.isSelected()) {
                this.calculate = CalculateExcerciseCalories.DURATION_OF_EXCERCISE;
            } else if (cal2.isSelected()) {
                this.calculate = CalculateExcerciseCalories.PER_EXCERCISE;
            }
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
        signInUser.setPromptText("Enter your username");
        signInPassword = new TextField();
        signInPassword.setPromptText("Enter your password");
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
        view.getChildren().addAll(information, signInRow, signInBut, signUp, adminButton);

    }

    private void showViewDailyLogWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("View Daily Log");

        VBox layout = new VBox(10);

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
                int day = controller.convertStringToInt(dayField.getText());
                int month = controller.convertStringToInt(monthField.getText());
                int year = controller.convertStringToInt(yearField.getText());

                DailyLog dailyLog = system.getCurrentUser().getFitnessHistory().getDailyLog(day, month, year);
                logInfoArea.setText(dailyLog.viewDailyLog(system.getCurrentUser().getCaloriesCalculation()).get());
            } catch (NumberFormatException ex) {
                logInfoArea.setText("Invalid input. Please enter valid numbers for the date.");
            } catch (Exception ex) {
                logInfoArea.setText("Error viewing daily log: Log not found");
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

        // view: create and display the UI elements
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Exercise");

        VBox layout = new VBox(10);

        // view: create input fields and buttons
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

        // view: set up event handling
        addButton.setOnAction(e -> {
            try {
                // controller: convert input to appropriate data types
                int day = controller.convertStringToInt(dayField.getText());
                int month = controller.convertStringToInt(monthField.getText());
                int year = controller.convertStringToInt(yearField.getText());

                // controller: determine selected exercise type
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

                // controller: add exercise based on calculation type
                if (this.system.getCurrentUser().getCalculate() == CalculateExcerciseCalories.PER_EXCERCISE) {
                    SimpleIntegerProperty repetitions = new SimpleIntegerProperty(
                            controller.convertStringToInt(repetitionsField.getText()));
                    // controller: call method to add exercise
                    controller.addExercise(day, month, year, exerciseType, repetitions);
                } else {
                    SimpleDoubleProperty hour = new SimpleDoubleProperty(Double.parseDouble(hoursField.getText()));
                    // controller: call method to add exercise
                    controller.addExercise(day, month, year, exerciseType, hour);
                }
                // view: close the window after adding exercise
                stage.close();
            } catch (IllegalArgumentException ex) {
                System.out.println("Error");
            }
        });

        // view: add UI elements to layout
        layout.getChildren().addAll(
                new Label("Enter the date for these exercises (DD MM YYYY):"),
                dayField, monthField, yearField,
                new Label("Enter exercise type:"),
                pushUp, starJump, tricepDip, burpee, chinUp, sitUp, squats, pullUp,
                new Label("Enter number of repetitions:"),
                repetitionsField, hoursField,
                addButton);

        // view: set up and show the scene
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
