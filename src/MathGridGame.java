import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MathGridGame extends Application {
    // Constantes para el tamaño de la cuadrícula y el rango de números
    private static final int GRID_SIZE = 8; // Tamaño de la cuadrícula (8x8)
    private static final int MAX_NUMBER = 99; // Valor máximo de los números
    private static final int MIN_NUMBER = 10; // Valor mínimo de los números
    private static final int TIMER_SECONDS = 15; // Duración del temporizador en segundos
    private final int[][] gridNumbers = new int[GRID_SIZE][GRID_SIZE]; // Matriz de números
    private final Button[][] gridButtons = new Button[GRID_SIZE][GRID_SIZE]; // Matriz de botones
    private int selectedCount = 0; // Contador de casillas seleccionadas
    private int firstNumber; // Primer número seleccionado
    private int secondNumber; // Segundo número seleccionado
    private int firstRow; // Fila del primer botón
    private int firstCol; // Columna del primer botón
    private int secondRow; // Fila del segundo botón
    private int secondCol; // Columna del segundo botón
    private String operation; // Operación matemática (+ o -)
    private int correctResult; // Resultado correcto de la operación
    private int currentPlayer = 0; // Jugador actual (0, 1 o 2)
    private final int[] playerScores = new int[3]; // Puntuaciones de los 3 jugadores
    private final boolean[] playerAttempts = new boolean[3]; // Intentos de los jugadores
    private final Label statusLabel; // Etiqueta para mensajes de estado
    private final Label playerLabel; // Etiqueta para puntuaciones
    private final GridPane answerGrid; // Panel para opciones de respuesta
    private final VBox root; // Contenedor principal
    private volatile boolean isProcessing = false; // Bandera para evitar clics múltiples
    private Timeline timer; // Temporizador para limitar tiempo de respuesta
    private int timeLeft; // Tiempo restante en segundos

    // Constructor: inicializa componentes de la interfaz
    public MathGridGame() {
        statusLabel = new Label("Jugador 1: Selecciona una casilla");
        playerLabel = new Label("Puntuaciones: Jugador 1: 0 | Jugador 2: 0 | Jugador 3: 0");
        answerGrid = new GridPane();
        root = new VBox(10.0);
    }

    // Método principal para iniciar la aplicación JavaFX
    @Override
    public void start(Stage stage) {
        // Inicializa la cuadrícula con números aleatorios
        initializeGrid();
        // Configura el contenedor principal
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10.0));
        // Crea la cuadrícula de botones
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5.0);
        gridPane.setVgap(5.0);
        gridPane.setAlignment(Pos.CENTER);

        // Crea y configura los botones de la cuadrícula
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button button = new Button("?");
                button.setMinSize(50.0, 50.0);
                button.setStyle("-fx-font-size: 14;");
                gridButtons[row][col] = button;
                final int currentRow = row;
                final int currentCol = col;
                // Asigna acción al botón para manejar clics
                button.setOnAction(event -> {
                    if (!isProcessing) {
                        isProcessing = true;
                        handleButtonClick(currentRow, currentCol);
                    }
                });
                gridPane.add(button, col, row);
            }
        }

        // Configura el panel para las opciones de respuesta
        answerGrid.setHgap(5.0);
        answerGrid.setVgap(5.0);
        answerGrid.setAlignment(Pos.CENTER);
        // Agrega componentes a la interfaz
        root.getChildren().addAll(gridPane, answerGrid, statusLabel, playerLabel);
        // Configura la escena y la muestra
        Scene scene = new Scene(root, 500.0, 600.0);
        stage.setTitle("MathGrid: Desafio Numerico");
        stage.setScene(scene);
        stage.show();
    }

    // Inicializa la cuadrícula con números aleatorios entre MIN_NUMBER y MAX_NUMBER
    private void initializeGrid() {
        Random random = new Random();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridNumbers[row][col] = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
            }
        }
    }

    // Maneja el clic en un botón de la cuadrícula
    private void handleButtonClick(int row, int col) {
        if (!gridButtons[row][col].getText().equals("?") || selectedCount >= 2) {
            isProcessing = false;
            return;
        }
        gridButtons[row][col].setText(String.valueOf(gridNumbers[row][col]));
        selectedCount++;
        if (selectedCount == 1) {
            // Guarda el primer número y su posición
            firstNumber = gridNumbers[row][col];
            firstRow = row;
            firstCol = col;
            statusLabel.setText("Jugador " + (currentPlayer + 1) + ": Selecciona otra casilla");
            isProcessing = false;
        } else if (selectedCount == 2) {
            // Guarda el segundo número, genera operación y muestra opciones
            secondNumber = gridNumbers[row][col];
            secondRow = row;
            secondCol = col;
            generateOperation();
            statusLabel.setText("Jugador " + (currentPlayer + 1) + ": Encuentra: " + 
                firstNumber + " " + operation + " " + secondNumber + " = ? (" + TIMER_SECONDS + "s)");
            disableGridButtons();
            showAnswerOptions();
        }
    }

    // Genera una operación aleatoria (+ o -) y calcula el resultado correcto
    private void generateOperation() {
        Random random = new Random();
        int op = random.nextInt(2);
        operation = op == 0 ? "+" : "-";
        correctResult = op == 0 ? firstNumber + secondNumber : firstNumber - secondNumber;
    }

    // Muestra las opciones de respuesta (una correcta, tres incorrectas)
    private void showAnswerOptions() {
        answerGrid.getChildren().clear();
        ArrayList<Integer> options = new ArrayList<>();
        options.add(correctResult);
        Random random = new Random();
        // Genera opciones incorrectas únicas
        while (options.size() < 4) {
            int wrongAnswer = correctResult + random.nextInt(21) - 10;
            if (wrongAnswer != correctResult && !options.contains(wrongAnswer) && 
                wrongAnswer >= MIN_NUMBER && wrongAnswer <= MAX_NUMBER) {
                options.add(wrongAnswer);
            }
        }
        Collections.shuffle(options);
        // Crea botones para cada opción de respuesta
        for (int i = 0; i < options.size(); i++) {
            Button answerButton = new Button(String.valueOf(options.get(i)));
            answerButton.setMinSize(50.0, 50.0);
            answerButton.setStyle("-fx-font-size: 14;");
            int finalAnswer = options.get(i);
            answerButton.setOnAction(event -> {
                if (!isProcessing) {
                    isProcessing = true;
                    stopTimer();
                    handleAnswerClick(finalAnswer);
                }
            });
            answerGrid.add(answerButton, i, 0);
        }
        startTimer();
    }

    // Inicia un temporizador de 15 segundos para la respuesta
    private void startTimer() {
        if (timer != null) {
            timer.stop(); // Detiene cualquier temporizador previo
        }
        timeLeft = TIMER_SECONDS;
        statusLabel.setText("Jugador " + (currentPlayer + 1) + ": Encuentra: " + 
            firstNumber + " " + operation + " " + secondNumber + " = ? (" + timeLeft + "s)");
        timer = new Timeline(new KeyFrame(Duration.seconds(1.0), event -> {
            timeLeft--;
            if (timeLeft >= 0) {
                statusLabel.setText("Jugador " + (currentPlayer + 1) + ": Encuentra: " + 
                    firstNumber + " " + operation + " " + secondNumber + " = ? (" + timeLeft + "s)");
            } else {
                stopTimer();
                handleAnswerClick(-999); // Respuesta incorrecta si se acaba el tiempo
            }
        }));
        timer.setCycleCount(TIMER_SECONDS + 1);
        timer.play();
    }

    // Detiene el temporizador si está activo
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null; // Libera el temporizador para evitar acumulación
        }
        isProcessing = false;
    }

    // Maneja la selección de una respuesta
    private void handleAnswerClick(int selectedAnswer) {
        playerAttempts[currentPlayer] = true;
        if (selectedAnswer == correctResult) {
            // Suma puntos y resalta en verde si es correcto
            playerScores[currentPlayer] += 10;
            gridButtons[firstRow][firstCol].setStyle("-fx-background-color: lightgreen;");
            gridButtons[secondRow][secondCol].setStyle("-fx-background-color: lightgreen;");
            statusLabel.setText("Jugador " + (currentPlayer + 1) + ": ¡Correcto!");
        } else {
            // Resalta en rojo si es incorrecto
            gridButtons[firstRow][firstCol].setStyle("-fx-background-color: lightcoral;");
            gridButtons[secondRow][secondCol].setStyle("-fx-background-color: lightcoral;");
            statusLabel.setText("Jugador " + (currentPlayer + 1) + ": ¡Incorrecto!");
        }
        updatePlayerLabel();
        Platform.runLater(this::nextPlayer);
    }

    // Pasa al siguiente jugador o termina el juego
    private void nextPlayer() {
        answerGrid.getChildren().clear();
        selectedCount = 0;
        // Restablece los botones seleccionados
        gridButtons[firstRow][firstCol].setText("?");
        gridButtons[firstRow][firstCol].setStyle("");
        gridButtons[secondRow][secondCol].setText("?");
        gridButtons[secondRow][secondCol].setStyle("");
        enableGridButtons();
        currentPlayer = (currentPlayer + 1) % 3;
        statusLabel.setText("Jugador " + (currentPlayer + 1) + ": Selecciona una casilla");
        // Verifica si todos los jugadores han intentado
        for (boolean attempt : playerAttempts) {
            if (!attempt) {
                isProcessing = false;
                return;
            }
        }
        endGame();
    }

    // Actualiza la etiqueta de puntuaciones
    private void updatePlayerLabel() {
        playerLabel.setText("Puntuaciones: Jugador 1: " + playerScores[0] + 
            " | Jugador 2: " + playerScores[1] + " | Jugador 3: " + playerScores[2]);
    }

    // Desactiva todos los botones de la cuadrícula
    private void disableGridButtons() {
        for (Button[] row : gridButtons) {
            for (Button button : row) {
                button.setDisable(true);
            }
        }
    }

    // Activa los botones de la cuadrícula que aún muestran "?"
    private void enableGridButtons() {
        for (Button[] row : gridButtons) {
            for (Button button : row) {
                if (button.getText().equals("?")) {
                    button.setDisable(false);
                }
            }
        }
    }

    // Finaliza el juego y muestra las puntuaciones finales
    private void endGame() {
        statusLabel.setText("¡Juego terminado! Puntuaciones finales: Jugador 1: " + 
            playerScores[0] + " | Jugador 2: " + playerScores[1] + " | Jugador 3: " + playerScores[2]);
        disableGridButtons();
        isProcessing = false;
    }

    // Punto de entrada principal para ejecutar la aplicación
    public static void main(String[] args) {
        launch(args);
    }
}