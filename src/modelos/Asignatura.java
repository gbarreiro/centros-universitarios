package modelos;

import java.util.ArrayList;

/**
 * Clase que modela una asignatura.
 * Cada conjunto de líneas del fichero "asignaturas.txt" delimitado por * se puede modelar como un objeto de esta clase.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Asignatura implements EscribibleEnFichero {

	private String siglas;
	private String nombre;
	private int curso;
	private int cuatrimestre;
	private String dniCoordinador;
	private String[] preRequisitos;
	private int duracionGrupoA;
	private int duracionGrupoB;
	private ArrayList<Grupo> gruposA;
	private ArrayList<Grupo> gruposB;
	
	private static final String NOMBRE_FICHERO = "asignaturas.txt";

	/**
	 * Constructor de la clase Asignatura. Permite crear un objeto de la clase Asignatura.
	 * @param siglas Siglas de la asignatura
	 * @param nombre Nombre de la asignatura
	 * @param curso Curso en el que se imparte la asignatura
	 * @param cuatrimestre Cuatrimestre en el que se imparte la asignatura
	 * @param dniCoordinador DNI del profesor coordinador
	 * @param preRequisitos Prerrequisitos: asignaturas que es necesario haber cursado antes
	 * @param duracionGrupoA Duración de una sesión de un grupo A de esta asignatura
	 * @param duracionGrupoB Duración de una sesión de un grupo B de esta asignatura
	 * @param gruposA Grupos A de la asignatura, tal como vienen en el fichero asignaturas.txt
	 * @param gruposB Grupos B de la asignatura, tal como vienen en el fichero asignaturas.txt
	 */
	public Asignatura(String siglas, String nombre, int curso, int cuatrimestre, String dniCoordinador, String preRequisitos, int duracionGrupoA, int duracionGrupoB, String gruposA, String gruposB) {
	
		this.siglas = siglas;
		this.nombre = nombre;
		this.curso = curso;
		this.cuatrimestre = cuatrimestre;
		this.dniCoordinador = dniCoordinador;
		if(preRequisitos!=null) {
			this.preRequisitos=preRequisitos.split(";");
		}else {
			// Si no hay prerrequisitos se crea un array vacío
			this.preRequisitos= new String[0];
		}
		
		this.duracionGrupoA = duracionGrupoA;
		this.duracionGrupoB = duracionGrupoB;
		
		// Grupos A:
		this.gruposA = new ArrayList<Grupo>();
		
		if(gruposA!=null) {
			String[] listaGA = gruposA.split(";");
			for(int i=0;i<listaGA.length; i++) {
				String campos[] = listaGA[i].trim().split("\\s+");
				Grupo grupo = new Grupo(Integer.parseInt(campos[0]), campos[1].charAt(0), 
						Integer.parseInt(campos[2]), campos[3], 'A');
				this.gruposA.add(grupo);

			}
		}
		
		
		// Grupos B:
		this.gruposB = new ArrayList<Grupo>();
		
		if(gruposB!=null) {
			String[] listaGB = gruposB.split(";");
			for(int i=0;i<listaGB.length; i++) {
				String campos[] = listaGB[i].trim().split("\\s+");
				Grupo grupo = new Grupo(Integer.parseInt(campos[0]), campos[1].charAt(0), 
						Integer.parseInt(campos[2]), campos[3], 'B');
				this.gruposB.add(grupo);

			}
		}
		
		
	}
	
	/**
	 * Devuelve las siglas de la asignatura.
	 */
	public String getSiglas() {
		return siglas;
	}
	
	/**
	 * Devuelve el nombre de la asignatura.
	 */
	public String getNombre() {
		return nombre;
	}
	
	/**
	 * Devuelve el curso en el que se imparte la asignatura.
	 */
	public int getCurso() {
		return curso;
	}
	
	/**
	 * Devuelve el cuatrimestre en el que se imparte la asignatura.
	 */
	public int getCuatrimestre() {
		return cuatrimestre;
	}
	
	/**
	 * Devuelve el DNI del coordinador de la asignatura.
	 */
	public String getDni_Coordinador() {
		return dniCoordinador;
	}
	
	/**
	 * Devuelve las siglas de las asignaturas que es necesario haber cursado anteriormente.
	 */
	public String[] getPrerrequisitos() {
		return preRequisitos;
	}
	
	/**
	 * Devuelve la duración de un grupo A.
	 */
	public int getDuracionGrupoA() {
		return duracionGrupoA;
	}
	
	/**
	 * Devuelve la duración de un grupo B.
	 */
	public int getDuracionGrupoB() {
		return duracionGrupoB;
	}
	
	/**
	 * Devuelve un ArrayList con los grupos A de la asignatura.
	 */
	public ArrayList<Grupo> getGruposA() {
		return gruposA;
	}
	
	/**
	 * Devuelve un ArrayList con los grupos B de la asignatura.
	 */
	public ArrayList<Grupo> getGruposB() {
		return gruposB;
	}
	
	/**
	 * Devuelve un ArrayList con los grupos A y B de la asignatura.
	 */
	public ArrayList<Grupo> getGruposTodos(){
		ArrayList<Grupo> al = new ArrayList<Grupo>(gruposA);
		al.addAll(gruposB);
		return al;
	}
	
	/**
	 * Devuelve el grupo de la asignatura del tipo e ID especificados
	 * @param id ID del grupo
	 * @param tipo A o B
	 * @return Grupo si se encuentra, null si no existe el grupo buscado.
	 */
	public Grupo getGrupo(int id, char tipo) {
		if(tipo=='A') {
			for(Grupo gr: this.gruposA) {
				if(gr.id==id) return gr;
			}
		}
		if(tipo=='B') {
			for(Grupo gr: this.gruposB) {
				if(gr.id==id) return gr;
			}
		}
		return null; // si no se encontrase el grupo
		
	}
	
	/**
	 * Crea un grupo nuevo en la asignatura.
	 * @param tipo A o B
	 * @param id Número de grupo
	 * @param dia Día de la semana
	 * @param horaInicio Hora a la que empieza el grupo
	 * @param aula Aula en la que tiene lugar el grupo
	 */
	public void crearGrupoAsignatura(char tipo, int id, char dia, int horaInicio, String aula) {
		Grupo grupo = new Grupo(id, dia, horaInicio, aula, tipo);
		if(tipo=='A') gruposA.add(grupo);
		else if(tipo=='B') gruposB.add(grupo);
	}
	
	/**
	 * Convierte los atributos de este objeto a un conjunto de líneas de texto, separadas por saltos de línea (\n),
	 * para así facilitar su guardado en un fichero de texto.
	 * @return Conjunto de líneas de texto con los atributos del objeto.
	 */
	public String toTexto() {
		// Siglas, nombre, curso, cuatrimestre y coordinador (DNI):
		String inicio = siglas + " \n" + nombre + " \n" + curso
			+ " \n" +  cuatrimestre + " \n" + dniCoordinador + " \n"; 
		
		// Prerrequisitos
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<preRequisitos.length; i++) {
			buffer.append(preRequisitos[i]);
			if(i<preRequisitos.length-1) buffer.append(";");
		}
		buffer.append(" \n");
		
		
		// Duración grupos A y B
		buffer.append(duracionGrupoA + " \n" + duracionGrupoB + " \n");
		
		// Grupos A
		for(int i = 0; i<gruposA.size(); i++) {
			buffer.append(gruposA.get(i).toTexto());
			if(i<gruposA.size()-1) buffer.append("; ");
		}
		
		buffer.append(" \n");
		
		// Grupos B
		for(int i = 0; i<gruposB.size(); i++) {
			buffer.append(gruposB.get(i).toTexto());
			if(i<gruposB.size()-1) buffer.append("; ");
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
	 * Grupo de docencia de una asignatura. 
	 * Puede ser grupo A (teoría) o grupo B (práctica).
	 *
	 */
	public class Grupo{
		
		private int id;
		private char dia;
		private int horaInicio;
		private String aula;
		private char tipo; // A o B
		
		Grupo(int id, char dia, int horaInicio, String aula, char tipo){
			this.id = id;
			this.dia = dia;
			this.horaInicio = horaInicio;
			this.aula = aula;
			this.tipo = tipo;
		}

		/**
		 * Devuelve el ID del grupo.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Devuelve el día de la semana en el que se imparte este grupo.
		 */
		public char getDia() {
			return dia;
		}

		/**
		 * Devuelve la hora de inicio de este grupo.
		 */
		public int getHoraInicio() {
			return horaInicio;
		}

		/**
		 * Devuelve el aula en la que se imparte este grupo.
		 */
		public String getAula() {
			return aula;
		}

		/**
		 * Devuelve el tipo de grupo: A o B
		 */
		public char getTipo() {
			return tipo;
		}
		
		/**
		 * Devuelve los atributos del objeto en una línea de texto.
		 * Se usa este método a la hora de escribir los ficheros.
		 */
		public String toTexto() {
			return id + " " + dia + " " + horaInicio + " " + aula;
		}
		
	}
	
}
