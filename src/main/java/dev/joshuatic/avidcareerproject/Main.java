package dev.joshuatic.avidcareerproject;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.net.URI;

/*
 * ============================================================
 * Path to Programmer - AVID Career Project
 * ============================================================
 *
 * Author:
 * Joshua Murr
 *
 * Overview:
 * This is a JavaFX decision-based game about the journey to becoming
 * a programmer. The player makes choices across 20 stages, and those
 * choices affect three stats:
 *
 * 1. Skill
 * 2. Knowledge
 * 3. Burnout
 *
 * The game includes:
 * - A title screen
 * - A rules popup
 * - A stat system
 * - A progress bar
 * - Scene images
 * - A final result screen
 * - A source code button at the end
 * - A 1-second cooldown between button actions
 *
 * Design philosophy:
 * This version intentionally uses a fixed window size so the layout
 * remains stable and predictable during demos and packaging.
 *
 * ============================================================
 */

public class Main extends Application {

    /*
     * ========================================================
     * CONFIGURATION SECTION
     * ========================================================
     *
     * Put constants here when you want one easy place to edit
     * values used throughout the application.
     */

    /*
     * The source code URL used by the "View Source Code" button
     * on the final screen.
     */
    private static final String SOURCE_CODE_URL = "https://github.com/joshuatic/avid-career-project";

    /*
     * This is the cooldown length between player actions.
     * The value is in milliseconds.
     *
     * 1000 ms = 1 second
     */
    private static final int ACTION_COOLDOWN_MS = 1000;

    /*
     * Fixed the application window size.
     * These are used when the main Scene is created.
     */
    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 650;

    /*
     * Fixed image size for the scene banner image shown during gameplay.
     */
    private static final int SCENE_IMAGE_WIDTH = 700;
    private static final int SCENE_IMAGE_HEIGHT = 250;

    /*
     * ========================================================
     * GAME STATE SECTION
     * ========================================================
     *
     * These fields track the player's progress.
     */

    /*
     * Current stage in the game.
     * Starts at 1 and goes up to 20.
     */
    int stage = 1;

    /*
     * Skill represents practical coding ability.
     */
    int skill = 0;

    /*
     * Knowledge represents learning and understanding.
     */
    int knowledge = 0;

    /*
     * Burnout represents stress and exhaustion.
     */
    int burnout = 0;

    /*
     * inputLocked prevents the player from clicking multiple times
     * before the game is ready for the next action.
     *
     * This is especially important because we are adding a 1-second
     * cooldown between scene choices.
     */
    boolean inputLocked = false;

    /*
     * ========================================================
     * UI COMPONENTS SECTION
     * ========================================================
     *
     * These fields store JavaFX nodes we need to reuse later.
     */

    /*
     * The one main Scene used by the application.
     * We switch the root between the title screen and the game screen.
     */
    Scene scene;

    /*
     * The main gameplay root.
     * This is the layout used after the player presses Start Game.
     */
    VBox mainRoot;

    /*
     * Labels that show the player's current stats and current stage.
     */
    Label statsLabel;
    Label progressLabel;

    /*
     * The green progress bar at the top of the gameplay screen.
     */
    ProgressBar progressBar;

    /*
     * The large text area that acts like a running game log.
     * It shows questions, choices, and final results.
     */
    TextArea textArea;

    /*
     * The two primary choice buttons used for all gameplay decisions.
     */
    Button choice1;
    Button choice2;

    /*
     * The image shown during gameplay for the current scene.
     */
    ImageView imageView;

    /*
     * ========================================================
     * APPLICATION ENTRY POINT FOR JAVAFX
     * ========================================================
     *
     * JavaFX calls start(...) automatically after launch().
     */
    @Override
    public void start(Stage stageWindow) {

        // App Icon
        var iconUrl = getClass().getResource("/images/icon.png");
        if (iconUrl != null) {
            stageWindow.getIcons().add(new Image(iconUrl.toExternalForm()));
        }

        /*
         * ----------------------------------------------------
         * Top-right rules button
         * ----------------------------------------------------
         *
         * This button is visible during gameplay.
         * It opens the rules popup at any time.
         */
        Button rulesButton = new Button("How to Play");
        rulesButton.setOnAction(e -> showRules());
        rulesButton.setStyle("""
            -fx-background-color: #444;
            -fx-text-fill: white;
            -fx-font-size: 12px;
            -fx-background-radius: 6;
        """);

        /*
         * ----------------------------------------------------
         * Main gameplay image area
         * ----------------------------------------------------
         *
         * This image changes depending on the current stage.
         * It uses a fixed width and height for consistent layout.
         */
        imageView = new ImageView();
        imageView.setFitWidth(SCENE_IMAGE_WIDTH);
        imageView.setFitHeight(SCENE_IMAGE_HEIGHT);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        /*
         * A StackPane is used so the image is centered nicely.
         */
        StackPane imageContainer = new StackPane(imageView);
        imageContainer.setAlignment(Pos.CENTER);

        /*
         * ----------------------------------------------------
         * Main text log
         * ----------------------------------------------------
         *
         * This is where all game text appears.
         * The player cannot edit it.
         */
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        /*
         * ----------------------------------------------------
         * Labels for progress and stats
         * ----------------------------------------------------
         */
        progressLabel = new Label();
        statsLabel = new Label();

        /*
         * Larger, more readable text styling.
         */
        progressLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        statsLabel.setStyle("-fx-font-size: 16px;");

        /*
         * Put both labels into a centered vertical box.
         * This keeps the top information tidy.
         */
        VBox infoBox = new VBox(8, progressLabel, statsLabel);
        infoBox.setAlignment(Pos.CENTER);

        /*
         * ----------------------------------------------------
         * Progress bar
         * ----------------------------------------------------
         *
         * This shows how far through the 20 stages the player is.
         */
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(700);
        progressBar.setPrefHeight(14);

        /*
         * Initialize UI text before the scene is shown.
         */
        updateStats();
        updateProgress();

        /*
         * ----------------------------------------------------
         * Main gameplay choice buttons
         * ----------------------------------------------------
         */
        choice1 = new Button();
        choice2 = new Button();

        /*
         * Reuse one styling method for both buttons so the UI
         * stays consistent.
         */
        styleButton(choice1);
        styleButton(choice2);

        /*
         * The two main choice buttons live in an HBox, so they
         * sit side-by-side and remain centered.
         */
        HBox buttonBox = new HBox(20, choice1, choice2);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        /*
         * ----------------------------------------------------
         * Top bar
         * ----------------------------------------------------
         *
         * Holds the rules button in the top-right corner.
         */
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.getChildren().add(rulesButton);

        /*
         * ----------------------------------------------------
         * Main gameplay root layout
         * ----------------------------------------------------
         *
         * This is the root used once the player starts the game.
         */
        mainRoot = new VBox(
                12,
                topBar,
                infoBox,
                progressBar,
                imageContainer,
                textArea,
                buttonBox
        );
        mainRoot.setAlignment(Pos.TOP_CENTER);
        mainRoot.setPadding(new Insets(20));

        /*
         * ----------------------------------------------------
         * Main Scene
         * ----------------------------------------------------
         *
         * The application uses one Scene and swaps roots
         * depending on whether the player is on the title screen
         * or in gameplay.
         */
        scene = new Scene(mainRoot, WINDOW_WIDTH, WINDOW_HEIGHT);

        /*
         * Load CSS if the file exists.
         * If not, the app still works, just without custom styling.
         */
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        /*
         * ----------------------------------------------------
         * Final stage/window setup
         * ----------------------------------------------------
         */
        stageWindow.setTitle("Path to Programmer - Career Simulation");
        stageWindow.setScene(scene);
        stageWindow.setResizable(false);
        stageWindow.show();

        /*
         * Start on the title screen instead of jumping straight into gameplay.
         */
        showTitleScreen();
    }

    /*
     * ========================================================
     * TITLE SCREEN
     * ========================================================
     *
     * This method replaces the current scene root with a clean
     * title screen layout.
     */
    void showTitleScreen() {

        /*
         * Root container for the title screen.
         */
        StackPane menuRoot = new StackPane();
        menuRoot.setStyle("-fx-background-color: white;");

        /*
         * Centered content box for title screen controls.
         */
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(true);

        /*
         * Title image shown on the menu screen.
         *
         * This is separate from the gameplay scene image.
         */
        ImageView menuImage = new ImageView();
        menuImage.setFitWidth(260);
        menuImage.setPreserveRatio(true);
        menuImage.setSmooth(true);

        /*
         * Try loading the menu image from resources.
         */
        var bgUrl = getClass().getResource("/images/menu_bg.jpg");
        if (bgUrl != null) {
            menuImage.setImage(new Image(bgUrl.toExternalForm()));
        }

        /*
         * Main title text.
         */
        Label title = new Label("PATH TO PROGRAMMER");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: black; -fx-font-weight: bold;");

        /*
         * Subtitle / tagline text.
         */
        Label subtitle = new Label("""
                Build your skills.
                Avoid burnout.
                Become a developer.
                """);
        subtitle.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Double.MAX_VALUE);

        /*
         * Menu buttons.
         */
        Button startBtn = new Button("Start Game");
        Button rulesBtn = new Button("Rules");

        styleButton(startBtn);
        styleButton(rulesBtn);

        /*
         * Start button behavior:
         * - Switch back to the gameplay root
         * - Reset all game states
         * - Clear previous text/image state
         * - Begin stage 1
         */
        startBtn.setOnAction(e -> {
            scene.setRoot(mainRoot);

            stage = 1;
            skill = 0;
            knowledge = 0;
            burnout = 0;
            inputLocked = false;

            textArea.clear();
            imageView.setImage(null);

            updateStats();
            updateProgress();
            nextScene();
        });

        /*
         * Rules button on the title screen.
         */
        rulesBtn.setOnAction(e -> showRules());

        /*
         * Add all title screen content in order.
         */
        centerBox.getChildren().addAll(menuImage, title, subtitle, startBtn, rulesBtn);

        /*
         * Put the centered content onto the title screen root.
         */
        menuRoot.getChildren().add(centerBox);

        /*
         * Swap the current root to the title screen.
         */
        scene.setRoot(menuRoot);
    }

    /*
     * ========================================================
     * RULES POPUP
     * ========================================================
     *
     * Opens an information dialog that explains how the game works.
     */
    void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("How to Play");
        alert.setHeaderText("Path to Programmer - Rules");

        alert.setContentText(
                """
                Welcome to Path to Programmer!

                Goal:
                Make choices that improve your Skill and Knowledge
                while avoiding too much Burnout.

                How to Play:
                - Read each scenario carefully
                - Choose one of the two options
                - Each choice affects your stats

                Stats:
                Skill = coding ability
                Knowledge = understanding of concepts
                Burnout = stress level

                Winning:
                High Skill + Low Burnout = Success

                Good luck!
                """
        );

        alert.showAndWait();
    }

    /*
     * ========================================================
     * BUTTON STYLING
     * ========================================================
     *
     * Applies the same visual style to buttons used in the app.
     */
    void styleButton(Button button) {
        button.setPrefWidth(200);
        button.setStyle("""
            -fx-background-color: #2d89ef;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-background-radius: 6;
        """);
    }

    /*
     * ========================================================
     * LOG SYSTEM
     * ========================================================
     *
     * Adds text to the main gameplay text area.
     *
     * If a line begins with '>' it is treated like a player choice
     * and is indented slightly for readability.
     */
    void log(String text) {
        if (text.startsWith(">")) {
            textArea.appendText("   " + text + "\n\n");
        } else {
            textArea.appendText(text + "\n\n");
        }

        /*
         * Automatically scroll to the bottom after new text is added.
         */
        textArea.positionCaret(textArea.getText().length());
    }

    /*
     * ========================================================
     * IMAGE LOADER
     * ========================================================
     *
     * Loads a scene image from /images inside resources.
     * If the image is missing, the image area is cleared.
     */
    void setImage(String file) {
        var resource = getClass().getResource("/images/" + file);

        if (resource != null) {
            imageView.setImage(new Image(resource.toExternalForm()));
        } else {
            imageView.setImage(null);
        }
    }

    /*
     * ========================================================
     * UI UPDATE METHODS
     * ========================================================
     *
     * These methods refresh the top information display.
     */

    /*
     * Updates the stat label.
     */
    void updateStats() {
        statsLabel.setText(
                "Skill: " + skill +
                        "    Knowledge: " + knowledge +
                        "    Burnout: " + burnout
        );
    }

    /*
     * Updates the stage label and animates the progress bar.
     */
    void updateProgress() {
        progressLabel.setText("Stage " + stage + " / 20");

        double target = stage / 20.0;

        Timeline t = new Timeline(
                new KeyFrame(
                        Duration.millis(250),
                        new KeyValue(progressBar.progressProperty(), target)
                )
        );

        t.play();
    }

    /*
     * ========================================================
     * SCENE SYSTEM
     * ========================================================
     *
     * Shows the current stage's text, image, and choices.
     * Each case corresponds to a stage in the game.
     */
    void nextScene() {

        /*
         * Add a visual separator before each new question.
         */
        log("────────────");

        /*
         * Unlock input because a new scene is now ready.
         */
        inputLocked = false;
        choice1.setDisable(false);
        choice2.setDisable(false);

        switch (stage) {
            case 1 -> {
                setImage("school.png");
                log("HIGH SCHOOL\nDo you take Computer Science?");
                setChoices(
                        "Take the class",
                        "Skip it",
                        () -> {
                            skill += 2;
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 2 -> {
                setImage("study.jpg");
                log("You have homework.\nDo you complete it?");
                setChoices(
                        "Yes",
                        "No",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 3 -> {
                setImage("club.png");
                log("Join a coding club?");
                setChoices(
                        "Join",
                        "Ignore",
                        () -> {
                            skill += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 4 -> {
                setImage("youtube.png");
                log("Watch tutorials or scroll?");
                setChoices(
                        "Learn",
                        "Scroll",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 5 -> {
                setImage("project.jpg");
                log("Build your first project?");
                setChoices(
                        "Build",
                        "Skip",
                        () -> {
                            skill += 3;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 6 -> {
                setImage("java.png");
                log("Learn programming seriously?");
                setChoices(
                        "Commit",
                        "Quit",
                        () -> {
                            knowledge += 3;
                            advance();
                        },
                        () -> {
                            burnout += 2;
                            advance();
                        }
                );
            }

            case 7 -> {
                setImage("bug.jpg");
                log("You hit a bug.\nKeep trying?");
                setChoices(
                        "Keep going",
                        "Give up",
                        () -> {
                            skill += 2;
                            advance();
                        },
                        () -> {
                            burnout += 2;
                            advance();
                        }
                );
            }

            case 8 -> {
                setImage("docs.jpg");
                log("Read documentation?");
                setChoices(
                        "Read it",
                        "Guess",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 9 -> {
                setImage("practice.jpg");
                log("Practice coding daily?");
                setChoices(
                        "Yes",
                        "Sometimes",
                        () -> {
                            skill += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 10 -> {
                setImage("help.jpg");
                log("Help other students?");
                setChoices(
                        "Help",
                        "Ignore",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 11 -> {
                setImage("realproject.png");
                log("Build a real-world project?");
                setChoices(
                        "Yes",
                        "No",
                        () -> {
                            skill += 3;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 12 -> {
                setImage("git.png");
                log("Learn version control?");
                setChoices(
                        "Learn Git",
                        "Ignore",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 13 -> {
                setImage("debug.png");
                log("Debug for hours?");
                setChoices(
                        "Push through",
                        "Quit",
                        () -> {
                            skill += 2;
                            burnout += 1;
                            advance();
                        },
                        () -> {
                            burnout += 2;
                            advance();
                        }
                );
            }

            case 14 -> {
                setImage("language.png");
                log("Try a new language?");
                setChoices(
                        "Try it",
                        "Stay comfortable",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 15 -> {
                setImage("hackathon.jpg");
                log("Join a hackathon?");
                setChoices(
                        "Join",
                        "Skip",
                        () -> {
                            skill += 3;
                            burnout += 1;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 16 -> {
                setImage("job.png");
                log("Apply for jobs?");
                setChoices(
                        "Apply",
                        "Wait",
                        () -> {
                            knowledge += 2;
                            advance();
                        },
                        () -> {
                            burnout += 1;
                            advance();
                        }
                );
            }

            case 17 -> {
                setImage("interview.jpg");
                log("Prepare for interviews?");
                setChoices(
                        "Prepare",
                        "Wing it",
                        () -> {
                            skill += 2;
                            advance();
                        },
                        () -> {
                            burnout += 2;
                            advance();
                        }
                );
            }

            case 18 -> {
                setImage("offer.jpg");
                log("Accept a low offer or wait?");
                setChoices(
                        "Accept",
                        "Wait",
                        () -> {
                            burnout += 1;
                            advance();
                        },
                        () -> {
                            knowledge += 2;
                            advance();
                        }
                );
            }

            case 19 -> {
                setImage("work.png");
                log("Work overtime or balance life?");
                setChoices(
                        "Overtime",
                        "Balance",
                        () -> {
                            skill += 2;
                            burnout += 2;
                            advance();
                        },
                        () -> {
                            burnout -= 1;
                            advance();
                        }
                );
            }

            case 20 -> {
                setImage("growth.png");
                log("Continue learning after getting a job?");
                setChoices(
                        "Yes",
                        "No",
                        () -> {
                            skill += 3;
                            endGame();
                        },
                        () -> {
                            burnout += 1;
                            endGame();
                        }
                );
            }

            default -> log("Error: unknown stage " + stage);
        }
    }

    /*
     * ========================================================
     * STAGE ADVANCEMENT
     * ========================================================
     *
     * Moves to the next stage or ends the game if all stages are complete.
     */
    void advance() {
        stage++;
        updateStats();
        updateProgress();

        if (stage > 20) {
            endGame();
        } else {
            nextScene();
        }
    }

    /*
     * ========================================================
     * END GAME
     * ========================================================
     *
     * Clears the gameplay log, shows the final results, and adds
     * action buttons for restart, exit, and viewing source code.
     */
    void endGame() {
        textArea.clear();

        String result;

        if (burnout >= 10) {
            result = "You burned out.";
        } else if (skill >= 25) {
            result = "You became a successful programmer.";
        } else {
            result = "You need more practice.";
        }

        log("FINAL RESULTS");
        log("Skill: " + skill);
        log("Knowledge: " + knowledge);
        log("Burnout: " + burnout);
        log(result);

        /*
         * Build a new temporary button row for the ending screen.
         * This replaces the normal two-choice gameplay layout.
         */
        Button restartButton = new Button("Restart");
        Button exitButton = new Button("Exit");
        Button sourceButton = new Button("View Source Code");

        styleButton(restartButton);
        styleButton(exitButton);
        styleButton(sourceButton);

        restartButton.setOnAction(e -> restartGame());
        exitButton.setOnAction(e -> System.exit(0));
        sourceButton.setOnAction(e -> openSourceCode());

        /*
         * Replace the current bottom button row with an end-screen row.
         */
        HBox endButtonBox = new HBox(20, restartButton, sourceButton, exitButton);
        endButtonBox.setAlignment(Pos.CENTER);
        endButtonBox.setPadding(new Insets(10));

        /*
         * The original gameplay buttons still exist in memory, but here
         * we swap the root's last child so the player sees the end buttons.
         */
        mainRoot.getChildren().set(mainRoot.getChildren().size() - 1, endButtonBox);
    }

    /*
     * ========================================================
     * RESET GAME
     * ========================================================
     *
     * Resets all stats and restores the normal gameplay button row.
     */
    void restartGame() {
        stage = 1;
        skill = 0;
        knowledge = 0;
        burnout = 0;
        inputLocked = false;

        textArea.clear();
        imageView.setImage(null);

        /*
         * Rebuild the standard gameplay button row, because the ending
         * screen replaced it with a 3-button row.
         */
        styleButton(choice1);
        styleButton(choice2);

        HBox buttonBox = new HBox(20, choice1, choice2);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        mainRoot.getChildren().set(mainRoot.getChildren().size() - 1, buttonBox);

        updateStats();
        updateProgress();
        nextScene();
    }

    /*
     * ========================================================
     * SOURCE CODE BUTTON ACTION
     * ========================================================
     *
     * Attempts to open the configured source code URL in the system browser.
     */
    void openSourceCode() {
        try {
            Desktop.getDesktop().browse(new URI(SOURCE_CODE_URL));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Source Code");
            alert.setHeaderText("Unable to open source code link");
            alert.setContentText("Please make sure the source code URL is valid.");
            alert.showAndWait();
        }
    }

    /*
     * ========================================================
     * BUTTON ACTION BINDER
     * ========================================================
     *
     * Sets text and actions for the two gameplay buttons.
     *
     * This method also adds the 1-second cooldown. Once the player clicks,
     * input is locked immediately, the choice is logged, and the selected
     * action runs only after the cooldown completes.
     */

    @SuppressWarnings("DuplicatedCode") // Remove IntelliJ warnings about duplicated code.
    void setChoices(String t1, String t2, Runnable a1, Runnable a2) {
        choice1.setText(t1);
        choice2.setText(t2);

        choice1.setOnAction(e -> {
            if (inputLocked) {
                return;
            }

            inputLocked = true;
            choice1.setDisable(true);
            choice2.setDisable(true);

            log("> " + t1);

            PauseTransition pause = new PauseTransition(Duration.millis(ACTION_COOLDOWN_MS));
            pause.setOnFinished(event -> a1.run());
            pause.play();
        });

        choice2.setOnAction(e -> {
            if (inputLocked) {
                return;
            }

            inputLocked = true;
            choice1.setDisable(true);
            choice2.setDisable(true);

            log("> " + t2);

            PauseTransition pause = new PauseTransition(Duration.millis(ACTION_COOLDOWN_MS));
            pause.setOnFinished(event -> a2.run());
            pause.play();
        });
    }

    /*
     * ========================================================
     * STANDARD JAVA MAIN METHOD
     * ========================================================
     *
     * JavaFX apps still use a normal main method that calls launch().
     */

    @SuppressWarnings("unused") // Suppresses my IDE (IntelliJ)'s warning about unused code.
    public static void main(String[] args) {
        launch();
    }
}