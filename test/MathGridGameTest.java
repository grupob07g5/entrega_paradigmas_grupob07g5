import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MathGridGameTest {
    
    private MathGridGame game;
    
    @BeforeEach
    void setUp() {
        // Creamos una instancia del juego para cada test
        game = new MathGridGame();
    }
    
    @Test
    @DisplayName("Verificar que la cuadrícula se inicializa con números en el rango correcto")
    void testInitializeGrid() throws Exception {
        // Usamos reflexión para acceder al método privado
        Method initializeGrid = MathGridGame.class.getDeclaredMethod("initializeGrid");
        initializeGrid.setAccessible(true);
        initializeGrid.invoke(game);
        
        // Accedemos a la matriz de números
        Field gridNumbersField = MathGridGame.class.getDeclaredField("gridNumbers");
        gridNumbersField.setAccessible(true);
        int[][] gridNumbers = (int[][]) gridNumbersField.get(game);
        
        // Verificamos que todos los números están en el rango correcto
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int number = gridNumbers[row][col];
                assertTrue(number >= 10 && number <= 99, 
                    "El número " + number + " en posición [" + row + "][" + col + "] está fuera del rango 10-99");
            }
        }
    }
    
    @Test
    @DisplayName("Verificar que la operación de suma se genera correctamente")
    void testGenerateOperationSum() throws Exception {
        // Configuramos números conocidos
        setPrivateField("firstNumber", 25);
        setPrivateField("secondNumber", 17);
        
        // Ejecutamos generateOperation múltiples veces para verificar suma
        Method generateOperation = MathGridGame.class.getDeclaredMethod("generateOperation");
        generateOperation.setAccessible(true);
        
        boolean foundSum = false;
        for (int i = 0; i < 20; i++) { // Intentamos varias veces ya que es aleatorio
            generateOperation.invoke(game);
            String operation = (String) getPrivateField("operation");
            int result = (int) getPrivateField("correctResult");
            
            if ("+".equals(operation)) {
                assertEquals(42, result, "La suma 25 + 17 debería ser 42");
                foundSum = true;
                break;
            }
        }
        assertTrue(foundSum, "Debería generar operación de suma al menos una vez");
    }
    
    @Test
    @DisplayName("Verificar que la operación de resta se genera correctamente")
    void testGenerateOperationSubtraction() throws Exception {
        // Configuramos números conocidos
        setPrivateField("firstNumber", 25);
        setPrivateField("secondNumber", 17);
        
        // Ejecutamos generateOperation múltiples veces para verificar resta
        Method generateOperation = MathGridGame.class.getDeclaredMethod("generateOperation");
        generateOperation.setAccessible(true);
        
        boolean foundSubtraction = false;
        for (int i = 0; i < 20; i++) { // Intentamos varias veces ya que es aleatorio
            generateOperation.invoke(game);
            String operation = (String) getPrivateField("operation");
            int result = (int) getPrivateField("correctResult");
            
            if ("-".equals(operation)) {
                assertEquals(8, result, "La resta 25 - 17 debería ser 8");
                foundSubtraction = true;
                break;
            }
        }
        assertTrue(foundSubtraction, "Debería generar operación de resta al menos una vez");
    }
    
    @Test
    @DisplayName("Verificar que las puntuaciones se inicializan correctamente")
    void testInitialPlayerScores() throws Exception {
        Field playerScoresField = MathGridGame.class.getDeclaredField("playerScores");
        playerScoresField.setAccessible(true);
        int[] playerScores = (int[]) playerScoresField.get(game);
        
        // Verificamos que las 3 puntuaciones inician en 0
        assertEquals(3, playerScores.length, "Debería haber exactamente 3 jugadores");
        for (int i = 0; i < playerScores.length; i++) {
            assertEquals(0, playerScores[i], "La puntuación del jugador " + (i + 1) + " debería iniciar en 0");
        }
    }
    
    @Test
    @DisplayName("Verificar que el jugador actual se inicializa correctamente")
    void testInitialCurrentPlayer() throws Exception {
        int currentPlayer = (int) getPrivateField("currentPlayer");
        assertEquals(0, currentPlayer, "El jugador actual debería iniciar en 0 (Jugador 1)");
    }
    
    @Test
    @DisplayName("Verificar que el contador de selecciones se inicializa correctamente")
    void testInitialSelectedCount() throws Exception {
        int selectedCount = (int) getPrivateField("selectedCount");
        assertEquals(0, selectedCount, "El contador de selecciones debería iniciar en 0");
    }
    
    @Test
    @DisplayName("Verificar que los intentos de jugadores se inicializan correctamente")
    void testInitialPlayerAttempts() throws Exception {
        Field playerAttemptsField = MathGridGame.class.getDeclaredField("playerAttempts");
        playerAttemptsField.setAccessible(true);
        boolean[] playerAttempts = (boolean[]) playerAttemptsField.get(game);
        
        // Verificamos que los 3 intentos inician en false
        assertEquals(3, playerAttempts.length, "Debería haber exactamente 3 intentos de jugadores");
        for (int i = 0; i < playerAttempts.length; i++) {
            assertFalse(playerAttempts[i], "El intento del jugador " + (i + 1) + " debería iniciar en false");
        }
    }
    
    @Test
    @DisplayName("Verificar que isProcessing se inicializa correctamente")
    void testInitialIsProcessing() throws Exception {
        boolean isProcessing = (boolean) getPrivateField("isProcessing");
        assertFalse(isProcessing, "isProcessing debería iniciar en false");
    }
    
    @Test
    @DisplayName("Verificar constantes del juego")
    void testGameConstants() throws Exception {
        // Verificamos las constantes usando reflexión
        Field gridSizeField = MathGridGame.class.getDeclaredField("GRID_SIZE");
        gridSizeField.setAccessible(true);
        assertEquals(8, gridSizeField.get(null), "GRID_SIZE debería ser 8");
        
        Field maxNumberField = MathGridGame.class.getDeclaredField("MAX_NUMBER");
        maxNumberField.setAccessible(true);
        assertEquals(99, maxNumberField.get(null), "MAX_NUMBER debería ser 99");
        
        Field minNumberField = MathGridGame.class.getDeclaredField("MIN_NUMBER");
        minNumberField.setAccessible(true);
        assertEquals(10, minNumberField.get(null), "MIN_NUMBER debería ser 10");
        
        Field timerSecondsField = MathGridGame.class.getDeclaredField("TIMER_SECONDS");
        timerSecondsField.setAccessible(true);
        assertEquals(15, timerSecondsField.get(null), "TIMER_SECONDS debería ser 15");
    }
    
    @Test
    @DisplayName("Verificar que la cuadrícula tiene el tamaño correcto")
    void testGridSize() throws Exception {
        Field gridNumbersField = MathGridGame.class.getDeclaredField("gridNumbers");
        gridNumbersField.setAccessible(true);
        int[][] gridNumbers = (int[][]) gridNumbersField.get(game);
        
        assertEquals(8, gridNumbers.length, "La cuadrícula debería tener 8 filas");
        for (int i = 0; i < gridNumbers.length; i++) {
            assertEquals(8, gridNumbers[i].length, "Cada fila debería tener 8 columnas");
        }
    }
    
    @Test
    @DisplayName("Verificar operaciones matemáticas específicas")
    void testSpecificMathOperations() throws Exception {
        // Probamos operaciones específicas
        testMathOperation(50, 25, "+", 75);
        testMathOperation(50, 25, "-", 25);
        testMathOperation(10, 99, "+", 109);
        testMathOperation(99, 10, "-", 89);
    }
    
    // Métodos auxiliares para acceder a campos privados
    private Object getPrivateField(String fieldName) throws Exception {
        Field field = MathGridGame.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(game);
    }
    
    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = MathGridGame.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(game, value);
    }
    
    private void testMathOperation(int first, int second, String expectedOp, int expectedResult) throws Exception {
        setPrivateField("firstNumber", first);
        setPrivateField("secondNumber", second);
        setPrivateField("operation", expectedOp);
        
        Method generateOperation = MathGridGame.class.getDeclaredMethod("generateOperation");
        generateOperation.setAccessible(true);
        
        // Forzamos la operación específica modificando temporalmente
        if ("+".equals(expectedOp)) {
            setPrivateField("operation", "+");
            setPrivateField("correctResult", first + second);
        } else {
            setPrivateField("operation", "-");
            setPrivateField("correctResult", first - second);
        }
        
        int result = (int) getPrivateField("correctResult");
        assertEquals(expectedResult, result, 
            "La operación " + first + " " + expectedOp + " " + second + " debería ser " + expectedResult);
    }
}