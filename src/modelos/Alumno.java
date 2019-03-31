package modelos;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import docencia.Arranque;

/**
 * Clase que modela a un alumno, que a su vez heredda los métodos y atributos de la clase {@link Persona}.
 * Cada conjunto de líneas del fichero "alumnos.txt" delimitado por * se puede modelar como un objeto de esta clase.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Alumno extends Persona implements EscribibleEnFichero {
	
	private GregorianCalendar fechaIngreso;
	private ArrayList<AsignaturaSuperada> superadas;
	private ArrayList<DocenciaRecibida> actuales;
	
	private static final String NOMBRE_FICHERO = "alumnos.txt";
	
	/**
	 * Constructor de la clase Alumno. Permite crear un objeto de la clase Alumno.
	 * @param dni DNI del alumno
	 * @param nombre Nombre y apellidos del alumno
	 * @param email Email del alumno. Si no tiene email se puede pasar null.
	 * @param fechaNacimiento Fecha de nacimiento del alumno, previamente comprobada y en formato d/M/yyyy
	 * @param fechaIngreso Fecha de ingreso del alumno, previamente comprobada y en formato d/M/yyyy
	 * @param asignaturasSuperadas Asignaturas superadas por el alumno, tal como vienen en el fichero alumnos.txt
	 * @param docenciaRecibida Docencia recibida por el alumno, tal como vienen en el fichero alumnos.txt
	 */
	public Alumno(String dni,String nombre,String email,String fechaNacimiento,String fechaIngreso, String asignaturasSuperadas, String docenciaRecibida) {
		super(dni,nombre,email,fechaNacimiento);

		// Asignaturas superadas:
		this.superadas = new ArrayList<AsignaturaSuperada>();
		if(asignaturasSuperadas!=null) {
			// Si el alumno tiene asignaturas superadas
			String[] materiasSuperadas = asignaturasSuperadas.split(";");

			for(int i=0;i<materiasSuperadas.length;i++) {
				String[] campos= materiasSuperadas[i].trim().split("\\s+");
				if(campos.length==3) {
					AsignaturaSuperada asignatura = new AsignaturaSuperada(campos[0], campos[1], Float.parseFloat(campos[2]));
					this.superadas.add(asignatura);
				}
			}	
		}
		

		// Docencia recibida:
		this.actuales = new ArrayList<DocenciaRecibida>();
		if(docenciaRecibida!=null) {
			// Si el alumno está recibiendo docencia
			String[] materiasActuales = docenciaRecibida.split(";");

			for(int i=0;i<materiasActuales.length;i++) {
				String[] campos= materiasActuales[i].trim().split("\\s+");
				DocenciaRecibida asignatura;
				if(campos.length==1) {
					// Matriculado pero sin grupo:
					asignatura = new DocenciaRecibida(campos[0]);
					this.actuales.add(asignatura);
				}else if(campos.length==3){
					// Matriculado con grupo
					asignatura = new DocenciaRecibida(campos[0], campos[1].charAt(0), Integer.parseInt(campos[2]));
					this.actuales.add(asignatura);
				}
				
			}	
		}
		
		this.fechaIngreso=Persona.fechaToGregorianCalendar(fechaIngreso);
		
	}	
	
	/**
	 * Devuelve la fecha de ingreso del alumno como un GregorianCalendar.
	 */
	public GregorianCalendar getFechaIngreso() {
		return fechaIngreso;
	}
	
	/**
	 * Devuelve la fecha de ingreso del alumno formateada.
	 */
	public String getFechaIngresoFormateada() {
		return dateFormat.format(fechaIngreso.getTime());
	}
	
	/**
	 * Devuelve un ArrayList con todas las asignaturas superadas por el alumno.
	 */
	public ArrayList<AsignaturaSuperada> getAsignaturasSuperadas() {
		return superadas;
	}
	
	/**
	 * Devuelve un ArrayList con todas las asignaturas recibidas actualmente por el alumno.
	 */
	public ArrayList<DocenciaRecibida> getDocenciaRecibida() {
		return actuales;
	}
	
	/**
	 * Matricula a un alumno en una asignatura, dejando vacíos su grupo e ID.
	 * Las asignaturas que está cursando actualmente un alumno se modelan mediante la clase {@link Alumno.DocenciaRecibida}
	 * @param siglas Siglas de la asignatura
	 */
	public void matricular(String siglas) {
		DocenciaRecibida dr = new DocenciaRecibida(siglas);
		this.actuales.add(dr);
	}
	
	/**
	 * Asigna un grupo a un alumno, siempre y cuando estuviese previamente matriculado en la asignatura.
	 * Las asignaturas que está cursando actualmente un alumno se modelan mediante la clase {@link Alumno.DocenciaRecibida}
	 * @param asignatura Siglas de la asignatura
	 * @param tipoGrupo A o B
	 * @param idGrupo 1,...
	 */
	public void asignarGrupo(String asignatura, char tipoGrupo, int idGrupo) {
		boolean matriculado = false;
		for(DocenciaRecibida dr: actuales) {
			if(dr.getSiglas().equals(asignatura)) {
				// Sin asignar?
				if(dr.getTipoGrupo()=='#') {
					dr.asignar(tipoGrupo, idGrupo);
					return;
				}
				
				// Ya está en un grupo de ese tipo?
				if(dr.getTipoGrupo()==tipoGrupo) {
					dr.id = idGrupo; // lo mueve de grupo
					return;
				}
				
				// Está matriculado en otro tipo de grupo?
				matriculado = true;
			}
		}
		if(matriculado) actuales.add(new DocenciaRecibida(asignatura, tipoGrupo, idGrupo));
	}
	
	/**
	 * Evalúa una asignatura cursada por el alumno, eliminándola de su docencia recibida.
	 * En caso de que la haya aprobado, se incluirá en la lista de asignaturas superadas. 
	 * @param nota Nota obtenida en la evaluación de la asignatura
	 * @param siglasAsignatura Siglas de la asignatura
	 * @param cursoAcademico Curso académico en el que se evaluó la asignatura
	 */
	public void evaluarAsignatura(float nota, String siglasAsignatura, String cursoAcademico) {
			// Aprobado o suspenso: se elimina la asignatura de la docencia recibida
			ArrayList<DocenciaRecibida> eliminadas = new ArrayList<DocenciaRecibida>();
			for(DocenciaRecibida dr: actuales) {
				// Busca los grupos de esta asignatura
				if(dr.getSiglas().equals(siglasAsignatura)) {
					eliminadas.add(dr);
				}
			}
			actuales.removeAll(eliminadas);
			
			if(nota>=5) {
				// Si la asignatura está aprobada, se incluirá en la lista de asignaturas superadas
				superadas.add(new AsignaturaSuperada(siglasAsignatura, cursoAcademico, nota));
			}
			
	}
	
	/**
	 * Convierte los atributos de este objeto a un conjunto de líneas de texto, separadas por saltos de línea (\n),
	 * para así facilitar su guardado en un fichero de texto.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 * @return Conjunto de líneas de texto con los atributos del objeto.
	 */
	public String toTexto() {
		// DNI, nombre, email, fecha de nacimiento y fecha de ingreso:
		String inicio = getDni()+ " \n" + getNombre() + " \n" + getEmail() + " \n" + this.getFechaNacimientoFormateada()
			+ " \n" +  this.getFechaIngresoFormateada() + " \n";
		
		// Asignaturas superadas (recorremos los arrays)
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<superadas.size(); i++) {
			buffer.append(superadas.get(i).toString());
			if(i<superadas.size()-1) {
				buffer.append("; ");
			}
		}
		
		buffer.append(" \n");
		
		// Docencia recibida (recorremos los arrays)
		for(int i = 0; i<actuales.size(); i++) {
			buffer.append(actuales.get(i).toString());
			if(i<actuales.size()-1) {
				buffer.append("; ");
			}
		}
		
		
		// Juntamos todo en un mismo String
		String cadena = inicio.concat(new String(buffer));
		return cadena;
		
	}
	
	/**
	 * Devuelve el nombre del fichero de texto en el que se guardan los alumnos.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 */
	public String getNombreFichero() {
		return NOMBRE_FICHERO;
	}
	
	/**
	 * Asignatura superada por el alumno. Se usa para modelar el campo correspondiente del fichero alumnos.txt.
	 * Una asignatura superada tiene unas siglas, un curso académico y una nota.
	 */
	public class AsignaturaSuperada implements Comparable<AsignaturaSuperada>{
		private String siglas;
		private String cursoAcademico;
		private float nota;
		
		/**
		 * Constructor de la clase. Crea una asignatura superada por un alumno.
		 * @param siglas Siglas de la asignatura
		 * @param cursoAcademico Curso académico en el que fue superada (p.ej: 16/17)
		 * @param nota Nota obtenida
		 */
		AsignaturaSuperada(String siglas, String cursoAcademico, float nota){
			this.siglas = siglas;
			this.cursoAcademico = cursoAcademico;
			this.nota = nota;
		}

		/**
		 * Devuelve las siglas de la asignatura.
		 */
		public String getSiglas() {
			return siglas;
		}

		/**
		 * Devuelve el curso académico en el que fue aprobada la asignatura.
		 */
		public String getCursoAcademico() {
			return cursoAcademico;
		}

		/**
		 * Devuelve la nota obtenida en la asignatura.
		 */
		public float getNota() {
			return nota;
		}
		
		/**
		 * Devuelve el curso en el que se imparte esta asignatura.
		 * 
		 */
		public int getCurso() {
			return Arranque.asignaturas.get(this.getSiglas()).getCurso();
		}
		
		/**
		 * Devuelve los atributos del objeto en una línea de texto.
		 * Se usa este método a la hora de escribir los ficheros.
		 */
		public String toString() {
			return siglas + " " + cursoAcademico + " " + nota;
		}

		@Override
		/**
		 * Compara una asignatura superada con otra.
		 * El primer criterio de comparación es el curso, y en caso de empate se ordena por orden alfabético de las siglas de la asignatura.
		 * @param o Asignatura superada a comparar
		 * @return 1 si es mayor o -1 si es menor
		 */
		public int compareTo(AsignaturaSuperada o) {
			// Empezamos ordenando por curso
			if(o.getCurso()>this.getCurso()) return -1;
			if(o.getCurso()<this.getCurso()) return 1;
			
			// Si el curso es el mismo, compara por siglas de la asignatura
			return this.getSiglas().compareTo(o.getSiglas());
		}
		
	}
	
	/**
	 * Asignatura en la que está matriculado el alumno. Se usa para modelar el campo correspondiente del fichero alumnos.txt.
	 * Si un alumno está matriculado en una asignatura pero no tiene grupo asignado, tipoGrupo = '#' e id = 0.
	 */
	public class DocenciaRecibida{
		private String siglas;
		private char tipoGrupo;
		private int id;
		
		/**
		 * Constructor de la clase. Crea una asignatura en la que esté matriculado el alumno.
		 * Este constructor se usa cuando el alumno ya tiene grupo.
		 * @param siglas Siglas de la asignatura
		 * @param tipoGrupo Tipo de grupo: A o B
		 * @param id Identificador del grupo: 1,...
		 */
		public DocenciaRecibida(String siglas, char tipoGrupo, int id){
			this.siglas = siglas;
			this.tipoGrupo = tipoGrupo;
			this.id = id;
		}
		
		/**
		 * Constructor de la clase. Crea una asignatura en la que esté matriculado el alumno.
		 * Este constructor se usa cuando el alumno no tiene grupo.
		 * @param siglas Siglas de la asignatura
		 */
		DocenciaRecibida(String siglas){
			this.siglas = siglas;
			this.tipoGrupo = '#';
			this.id = 0;
		}

		/**
		 * Devuelve las siglas de la asignatura.
		 */
		public String getSiglas() {
			return siglas;
		}

		/**
		 * Devuelve el tipo de grupo.
		 */
		public char getTipoGrupo() {
			return tipoGrupo;
		}

		/**
		 * Devuelve el ID del grupo.
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * Devuelve los atributos del objeto en una línea de texto.
		 * Se usa este método a la hora de escribir los ficheros.
		 */
		public String toString() {
			if(tipoGrupo=='#') {
				// Si el grupo no está asignado
				return siglas;
			}else {
				// Si el grupo está asignado (A o B)
				return siglas + " " + tipoGrupo + " " + id;
			}
			
		}
		
		/**
		 * Asigna un grupo a la matrícula del alumno en esta asignatura.
		 * @param tipoGrupo A o B
		 * @param id 1,...
		 */
		public void asignar(char tipoGrupo, int id) {
			this.tipoGrupo = tipoGrupo;
			this.id = id;
		}
		
	}
	
}
