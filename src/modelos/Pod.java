package modelos;

/**
 * Clase que modela la planificación docente de las asignaturas.
 * Cada POD es la docencia que puede impartir un determinado profesor de una determinada asignatura.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Pod implements EscribibleEnFichero {
	
	private String dni; 
	private String asignatura;
	private char tipoGrupo; //A o B
	private float numeroGrupos; // numero real de maximo 2 digitos y 2 decimales
	
	private static final String NOMBRE_FICHERO = "pod.txt";
	
	/**
	 * Constructor de la clase Pod. Permite crear un objeto de la clase Pod.
	 * @param dni DNI del profesor
	 * @param asignatura Siglas de la asignatura impartida
	 * @param tipoGrupo Tipo de grupo: A o B
	 * @param numeroGrupos Número de grupos a los que puede dar clase de esa asignatura
	 */
	public Pod(String dni, String asignatura, char tipoGrupo, float numeroGrupos) {
		
		this.dni = dni;
		this.asignatura = asignatura;
		this.tipoGrupo = tipoGrupo;
		this.numeroGrupos = numeroGrupos;
	}
	
	/**
	 * Devuelve el DNI del profesor
	 */
	public String getDni() {
		return dni;
	}
	
	/**
	 * Devuelve las siglas de la asignatura
	 */
	public String getAsignatura() {
		return asignatura;
	}
	
	/**
	 * Devuelve el tipo de grupo: A o B
	 */
	public char getTipoGrupo() {
		return tipoGrupo;
	}
	
	/**
	 * Devuelve el número de grupos al que da clase el profesor
	 */
	public float getNumeroGrupos() {
		return numeroGrupos;
	}
	
	/**
	 * Convierte los atributos de este objeto a un conjunto de líneas de texto, separadas por saltos de línea (\n),
	 * para así facilitar su guardado en un fichero de texto.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 * @return (String): conjunto de líneas de texto con los atributos del objeto.
	 */
	public String toTexto() {
		// Siglas, tipo grupo y capacidad:
		return dni + "\n" + asignatura + "\n" + tipoGrupo + "\n" + numeroGrupos; 
		
		
	}
	
	/**
	 * Devuelve el nombre del fichero de texto en el que se guardan los alumnos.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 */
	public String getNombreFichero() {
		return NOMBRE_FICHERO;
	}


}
