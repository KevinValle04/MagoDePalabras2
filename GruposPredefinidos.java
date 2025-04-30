import java.io.*;
import java.util.*;

public class GruposPredefinidos {
    private List<List<Character>> grupos;

    public GruposPredefinidos(String archivo) {
        grupos = new ArrayList<>();
        cargarGruposDesdeArchivo(archivo);
    }

    private void cargarGruposDesdeArchivo(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Grupo:")) {
                    String letras = linea.substring(7).trim();
                    List<Character> grupo = new ArrayList<>();
                    for (char c : letras.toCharArray()) {
                        grupo.add(c);
                    }
                    grupos.add(grupo);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar grupos predefinidos: " + e.getMessage());
        }
    }

    public List<Character> obtenerGrupoAleatorio(Random random) {
        if (grupos.isEmpty()) return null;
        return new ArrayList<>(grupos.get(random.nextInt(grupos.size())));
    }
}
