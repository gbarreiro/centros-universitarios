package docencia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase de gestión de los errores durante la ejecución del programa.
 * Se encarga de procesar el error y guardarlo en avisos.txt.
 * Tipos de error posibles:
 * 
 * 1) Error en comando: depende de cada comando, se registra con {@link #errorComando(String, String)}
 * 2) Comando erróneo: un comando que no existe o con argumentos inválidos, se registra con {@link #comandoErroneo(String)}
 *
 * @author Guillermo Barreiro Fernández
 */
public class Errores {

	private final static String nombreFichero = "avisos.txt";
	
	/**
	 * Siglas para los errores producidos por el comando "Insertar persona".
	 */
	public final static String INSERTA_PERSONA = "IP";
	
	/**
	 * Siglas para los errores producidos por el comando "Crear grupo asignatura".
	 */
	public final static String CREAR_GRUPO_ASIGNATURA = "CGA";
	
	/**
	 * Siglas para los errores producidos por el comando "Matricular".
	 */
	public final static String MATRICULAR_ALUMNO = "MAT";
	
	/**
	 * Siglas para los errores producidos por el comando "Asignar grupo".
	 */
	public final static String ASIGNAR_GRUPO = "AGRUPO";
	
	/**
	 * Siglas para los errores producidos por el comando "Evaluar asignatura".
	 */
	public final static String EVALUAR_ASIGNATURA = "EVALUA";
	
	/**
	 * Siglas para los errores producidos por el comando "Obtener calendario ocupación aulas".
	 */
	public final static String CALENDARIO_OCUPACION_AULA = "OCUPAULA";
	
	/**
	 * Siglas para los errores producidos por el comando "Obtener expediente alumno".
	 */
	public final static String EXPEDIENTE_ALUMNO = "EXP";
	
	
	private static void nuevaLinea(String linea) {
		try {
			// Escribe una línea en el fichero de avisos
			// Si avisos.txt aún no existe, lo crea
			FileWriter fw = new FileWriter(nombreFichero, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(linea);
			bw.newLine();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// Error al leer el fichero
			e.printStackTrace();
		}
				
		
	}
	
	/**
	 * Registra un error producido durante la ejecución de un comando.
	 * @param siglas Siglas del comando, disponibles como constantes en esta misma clase.
	 * @param aviso El aviso producido.
	 */
	public static void errorComando(String siglas, String aviso) {
		nuevaLinea(siglas + " -- " + aviso);
		
	}
	
	/**
	 * Registra un error producido cuando se introduce un comando que no existe.
	 * @param comando El comando introducido.
	 */
	public static void comandoErroneo(String comando) {
		nuevaLinea("Comando incorrecto: " + comando);
	}
	
	// La clase no se puede instanciar:
	private Errores() {
		
	}
	
}
