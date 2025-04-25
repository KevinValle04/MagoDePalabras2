import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String nombre;
    private List<Character> letras;
    private int puntaje;
    private List<String> palabrasUsadas;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.letras = new ArrayList<>();
        this.puntaje = 0;
        this.palabrasUsadas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setLetras(List<Character> letras) {
        this.letras = letras;
    }

    public List<Character> getLetras() {
        return letras;
    }

    public void agregarPalabra(String palabra) {
        palabrasUsadas.add(palabra);
    }

    public List<String> getPalabrasUsadas() {
        return palabrasUsadas;
    }

    public void agregarPuntaje(int puntos) {
        puntaje += puntos;
    }

    public int getPuntaje() {
        return puntaje;
    }
}
