package modelos;

/**
 * Interfaz común a todos los modelos que se carguen y guarden en un fichero de texto.
 * Ofrece un método para convertir el objeto a una "descripción" en texto que permita volver a crearlo posteriormente,
 * y un método para obtener el nombre del fichero de texto en el que se van a guardar los objetos.
 * @author Guillermo Barreiro Fernández
 *
 */
public interface EscribibleEnFichero {
	
	/**
	 * Convierte el objeto a una "descripción" en texto que permita volver a crearlo posteriormente.
	 * Cada línea de texto es un atributo del objeto.
	 * @return El texto que se guardará en el fichero.
	 */
	public String toTexto();
	
	/**
	 * Obtiene el nombre del fichero de texto en el que se van a guardar los objetos.
	 * @return El nombre del fichero de texto.
	 */
	public String getNombreFichero();

}
