import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Diccionario {
    private HashMap<String, Integer> palabrasValidas;

    public Diccionario() {
        palabrasValidas = new HashMap<>();
        cargarPalabrasDesdeArchivo("diccionario.txt");
    }

    private void cargarPalabrasDesdeArchivo(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String palabra;
            while ((palabra = br.readLine()) != null) {
                palabra = palabra.trim().toLowerCase();
                if (!palabra.isEmpty() && palabra.matches("[a-záéíóúñü]+")) {
                    int puntaje = calcularPuntajePalabra(palabra);
                    palabrasValidas.put(palabra, puntaje);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de palabras: " + e.getMessage());
        }
    }

    private int calcularPuntajePalabra(String palabra) {
        int puntaje = 0;
        for (char c : palabra.toCharArray()) {
            if ("aeiouáéíóúü".indexOf(c) >= 0) {
                puntaje += 5;
            } else {
                puntaje += 3;
            }
        }
        return puntaje;
    }

    public boolean esValida(String palabra) {
        return palabrasValidas.containsKey(palabra);
    }

    public int getPuntajePalabra(String palabra) {
        return palabrasValidas.getOrDefault(palabra, 0);
    }

    public Set<String> getPalabras() {
        return palabrasValidas.keySet();
    }
}
