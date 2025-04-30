import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ElMagoDeLasPalabras extends JFrame {
    private List<Jugador> jugadores;
    private Diccionario diccionario;
    private HashSet<String> palabrasUsadasGlobal;
    private Random random;
    private boolean modoExperto;
    private JTextArea areaJuego;
    private JTextField campoPalabra;
    private int rondaActual = 1;
    private int turno = 0;
    private List<Character> letrasRonda;

    public ElMagoDeLasPalabras() {
        jugadores = new ArrayList<>();
        diccionario = new Diccionario();
        palabrasUsadasGlobal = new HashSet<>();
        random = new Random();
        configurarGUI();
    }

    private void configurarGUI() {
        setTitle("El Mago de las Palabras");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Centrar la ventana

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(240, 240, 240));

        // Área de juego
        areaJuego = new JTextArea();
        areaJuego.setEditable(false);
        areaJuego.setFont(new Font("Monospaced", Font.BOLD, 16));
        areaJuego.setForeground(new Color(0, 0, 0));
        areaJuego.setBackground(new Color(255, 255, 255));
        JScrollPane scrollAreaJuego = new JScrollPane(areaJuego);
        panelPrincipal.add(scrollAreaJuego, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        panelInferior.setBackground(new Color(245, 245, 245));

        // Campo para ingresar palabra
        campoPalabra = new JTextField();
        campoPalabra.setFont(new Font("Arial", Font.PLAIN, 24));
        campoPalabra.setBackground(new Color(255, 255, 255));
        campoPalabra.setForeground(new Color(0, 0, 0));
        campoPalabra.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
        panelInferior.add(campoPalabra, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 3, 10, 10));
        panelBotones.setBackground(new Color(245, 245, 245));

        // Botones
        JButton botonEnviar = crearBoton("Enviar");
        JButton botonAgregarDiccionario = crearBoton("Agregar al Diccionario");
        JButton botonPasar = crearBoton("Pasar Turno");

        panelBotones.add(botonEnviar);
        panelBotones.add(botonAgregarDiccionario);
        panelBotones.add(botonPasar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        // Eventos de botones
        botonEnviar.addActionListener(e -> procesarPalabra(false));
        botonAgregarDiccionario.addActionListener(e -> procesarPalabra(true));
        botonPasar.addActionListener(e -> {
            actualizarTexto(jugadores.get(turno).getNombre() + " pasó su turno.");
            avanzarTurno();
        });

        mostrarVentanaInicio();
        setVisible(true);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBackground(new Color(100, 149, 237));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void mostrarVentanaInicio() {
        String[] opciones = {"Normal", "Experto"};
        int modo = JOptionPane.showOptionDialog(this, "Selecciona el modo de juego",
                "Modo de Juego", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opciones, opciones[0]);
        modoExperto = (modo == 1);

        int cantidad = 0;
        while (cantidad < 2 || cantidad > 4) {
            try {
                cantidad = Integer.parseInt(JOptionPane.showInputDialog("Cantidad de jugadores (2 a 4):"));
            } catch (Exception ignored) {}
        }

        for (int i = 0; i < cantidad; i++) {
            String nombre = JOptionPane.showInputDialog("Nombre del jugador " + (i + 1) + ":");
            jugadores.add(new Jugador(nombre));
        }

        iniciarRonda();
    }

    private void iniciarRonda() {
        palabrasUsadasGlobal.clear();
        letrasRonda = generarLetrasCompartidas();
        for (Jugador j : jugadores) {
            j.setLetras(new ArrayList<>(letrasRonda));
        }
        actualizarTexto("\n\n--- RONDA " + rondaActual + " ---\nLetras: " + letrasRonda);
        turno = 0;
        siguienteTurno();
    }

    private void siguienteTurno() {
        Jugador actual = jugadores.get(turno);
        actualizarTexto("\nTurno de " + actual.getNombre() +
                "\nLetras: " + actual.getLetras() +
                "\nPalabras usadas: " + palabrasUsadasGlobal);
        campoPalabra.setText("");
        campoPalabra.requestFocus();
    }

    private void procesarPalabra(boolean agregarAlDiccionario) {
        String palabra = campoPalabra.getText().trim().toLowerCase();
        if (palabra.isEmpty()) {
            actualizarTexto("¡No escribiste ninguna palabra!");
            return;
        }

        Jugador jugador = jugadores.get(turno);

        if (palabrasUsadasGlobal.contains(palabra)) {
            actualizarTexto("¡Palabra ya usada!");
        } else if (!puedeFormarPalabra(palabra, jugador.getLetras())) {
            int penalizacion = modoExperto ? -10 : -5;
            jugador.agregarPuntaje(penalizacion);
            actualizarTexto("¡No puedes formar esa palabra! " + penalizacion + " puntos.");
        } else if (diccionario.esValida(palabra) || agregarAlDiccionario) {
            if (agregarAlDiccionario && !diccionario.esValida(palabra)) {
                diccionario.agregarPalabra(palabra);
                actualizarTexto("Palabra añadida al diccionario.");
            }
            int puntos = calcularPuntos(palabra);
            jugador.agregarPuntaje(puntos);
            jugador.agregarPalabra(palabra);
            palabrasUsadasGlobal.add(palabra);
            actualizarTexto("¡Palabra válida! +" + puntos + " puntos.");
        } else {
            int penalizacion = modoExperto ? -10 : -5;
            jugador.agregarPuntaje(penalizacion);
            actualizarTexto("¡Palabra inválida! " + penalizacion + " puntos.");
        }

        avanzarTurno();
    }

    private void avanzarTurno() {
        turno++;
        if (turno >= jugadores.size()) {
            mostrarResumenRonda();
            rondaActual++;
            if (rondaActual <= 3) iniciarRonda();
            else mostrarGanador();
        } else {
            siguienteTurno();
        }
    }

    private void mostrarResumenRonda() {
        StringBuilder resumen = new StringBuilder("\n--- Resumen Ronda " + (rondaActual) + " ---\n");
        for (Jugador j : jugadores) {
            resumen.append(j.getNombre())
                    .append(" | Puntos: ").append(j.getPuntaje())
                    .append(" | Palabras: ").append(j.getPalabrasUsadas())
                    .append("\n");
        }
        actualizarTexto(resumen.toString());
    }

    private void mostrarGanador() {
        Jugador ganador = Collections.max(jugadores, Comparator.comparingInt(Jugador::getPuntaje));
        JOptionPane.showMessageDialog(this,
                "El ganador es " + ganador.getNombre() + " con " + ganador.getPuntaje() + " puntos!");
        System.exit(0);
    }

    private void actualizarTexto(String texto) {
        areaJuego.append("\n" + texto);
    }

    private List<Character> generarLetrasCompartidas() {
        List<Character> letras = new ArrayList<>();
        String vocales = "aeiou";
        String consonantes = "lnrstgmpbcdfhjkvz";
        int cantidadVocales = modoExperto ? 3 : (4 + random.nextInt(2));

        for (int i = 0; i < cantidadVocales; i++) {
            letras.add(vocales.charAt(random.nextInt(vocales.length())));
        }
        while (letras.size() < 10) {
            letras.add(consonantes.charAt(random.nextInt(consonantes.length())));
        }
        Collections.shuffle(letras);
        return letras;
    }

    private boolean puedeFormarPalabra(String palabra, List<Character> disponibles) {
        Map<Character, Long> conteoPalabra = palabra.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        Map<Character, Long> conteoDisponibles = disponibles.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        return conteoPalabra.entrySet().stream()
                .allMatch(e -> conteoDisponibles.getOrDefault(e.getKey(), 0L) >= e.getValue());
    }

    private int calcularPuntos(String palabra) {
        int puntos = 0;
        for (char c : palabra.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                puntos += modoExperto ? 3 : 5;
            } else {
                puntos += modoExperto ? 5 : 3;
            }
        }
        return puntos;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ElMagoDeLasPalabras::new);
    }
}
