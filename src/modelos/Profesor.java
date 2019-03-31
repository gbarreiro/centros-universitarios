package modelos;

import java.util.ArrayList;

/**
 * Clase que modela a un profesor, que a su vez heredda los métodos y atributos de la clase Persona.
 * Cada conjunto de líneas del fichero "profesores.txt" delimitado por * se puede modelar como un objeto de esta clase.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Profesor extends Persona implements EscribibleEnFichero {

	private boolean puedeCoordinar = false;
	private String departamento;
	private ArrayList<DocenciaImpartida> docenciaImpartida;
	
	private static final String NOMBRE_FICHERO = "profesores.txt";
	
	/**
	 * Constructor de la clase Profesor. Permite crear un objeto de la clase Profesor.
	 * @param dni DNI del profesor
	 * @param nombre Nombre y apellidos del profesor
	 * @param fechaNacimiento Fecha de nacimiento del profesor, previamente comprobada y en formato d/M/yyyy
	 * @param categoria Categoría del profesor: titular o asociado
	 * @param departamento Departamento en el que imparte docencia
	 * @param docenciaImpartida Docencia impartida por el profesor, tal como vienen en el fichero alumnos.txt
	 */
	public Profesor(String dni, String nombre, String fechaNacimiento, String categoria, String departamento, String docenciaImpartida) {
		super(dni,nombre,fechaNacimiento);

		// Docencia impartida:
		this.docenciaImpartida = new ArrayList<DocenciaImpartida>();
		if(docenciaImpartida!=null) {
			// Si el profesor imparte docencia
			String[] docencia = docenciaImpartida.split(";");
			
			for(int i=0;i<docencia.length;i++) {
				String[] campos= docencia[i].trim().split("\\s+");
				DocenciaImpartida di;
				if(campos.length==3) {
					// Si la docencia impartida son 3 campos
					di = new DocenciaImpartida(campos[0], campos[1].charAt(0), Integer.parseInt(campos[2]));
				}else {
					// suponemos que son 2 campos, no 3 (tipo e ID de grupo juntos en un solo campo)
					di = new DocenciaImpartida(campos[0], campos[1].charAt(0), Character.getNumericValue(campos[1].charAt(1)));
				}
				this.docenciaImpartida.add(di);	
			
		}}
		
		if(categoria.equalsIgnoreCase("titular")) this.puedeCoordinar = true; 
		this.departamento = departamento;
		
	}
	
	/**
	 * Le asigna un grupo de docencia determinado al profesor.
	 * Los grupos de docencia se modelan mediante la clase {@link Profesor.DocenciaImpartida}
	 * @param siglas Siglas de la asignatura
	 * @param tipoGrupo Tipo de grupo: A o B
	 * @param idGrupo Identificador del grupo: 1,...
	 */
	public void asignarGrupo(String siglas, char tipoGrupo, int idGrupo) {
		this.docenciaImpartida.add(new DocenciaImpartida(siglas, tipoGrupo, idGrupo));
	}
	
	/**
	 * Devuelve true si el profesor es coordinador, false si no lo es.
	 */
	public boolean getIsCoordinador() {
		return puedeCoordinar;
	}
	
	/**
	 * Devuelve el departamento al que pertenece el profesor.
	 */
	public String getDepartamento() {
		return departamento;
	}
	
	/**
	 * Devuelve un ArrayList con la docencia impartida por el profesor.
	 */
	public ArrayList<DocenciaImpartida> getDocenciaImpartida() {
		return this.docenciaImpartida;
	}
	
	/**
	 * Devuelve las siglas de un profesor, es decir, las iniciales de su nombre y apellidos separadas por un punto.
	 */
	public String getSiglasProfesor() {
		String[] siglas = this.getNombre().split("\\s+");
		StringBuffer buffer = new StringBuffer();
		for(String parte: siglas) {
			if(!parte.trim().equals(","))
				buffer.append(parte.trim().charAt(0)+".");
		}
		return new String(buffer);
	}
	
	/**
	 * Convierte los atributos de este objeto a un conjunto de líneas de texto, separadas por saltos de línea (\n),
	 * para así facilitar su guardado en un fichero de texto.
	 * @return Conjunto de líneas de texto con los atributos del objeto
	 */
	public String toTexto() {
		// DNI, nombre, fecha de nacimiento, categoria y departamento:
		String categoria = puedeCoordinar?"titular":"asociado";
		String inicio = getDni()+ " \n" + getNombre() + " \n" + this.getFechaNacimientoFormateada()
			+ " \n" +  categoria + " \n" + this.departamento + " \n";
		
		// Docencia impartida (recorremos los arrays)
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<docenciaImpartida.size(); i++) {
			buffer.append(docenciaImpartida.get(i).toString());
			if(i<docenciaImpartida.size()-1) {
				buffer.append("; ");
			}
			
		}
		
		// Juntamos todo en un mismo String
		String cadena = inicio.concat(new String(buffer));
		return cadena;
		
		
	}
	
	/**
	 * Devuelve el nombre del fichero de texto en el que se guardan los profesores.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 */
	public String getNombreFichero() {
		return NOMBRE_FICHERO;
	}
	
	/**
	 * Docencia impartida por el profesor (asignatura y grupo). 
	 * Se usa para modelar el campo correspondiente del fichero profesores.txt.
	 *
	 */
	public class DocenciaImpartida{
		
		private String siglas;
		private char tipoGrupo;
		private int idGrupo;
		
		/**
		 * Constructor de la clase. Crea una asignatura en la que un profesor imparte docencia.
		 * @param siglas Siglas de la asignatura
		 * @param tipoGrupo Tipo de grupo: A o B
		 * @param idGrupo Identificador del grupo: 1,...
		 */
		DocenciaImpartida(String siglas, char tipoGrupo, int idGrupo){
			this.siglas = siglas;
			this.tipoGrupo = tipoGrupo;
			this.idGrupo = idGrupo;
		}
		
		/**
		 * Devuelve las siglas de la asignatura impartida.
		 */
		public String getSiglas() {
			return siglas;
		}

		/**
		 * Devuelve el tipo de grupo: A o B.
		 */
		public char getTipoGrupo() {
			return tipoGrupo;
		}

		/**
		 * Devuelve el ID del grupo.
		 */
		public int getIdGrupo() {
			return idGrupo;
		}
		
		/**
		 * Devuelve los atributos del objeto en una línea de texto.
		 * Se usa este método a la hora de escribir los ficheros.
		 */
		public String toString() {
			return siglas + " " + tipoGrupo + " " + idGrupo;
		}
		
		
	}

	
}
