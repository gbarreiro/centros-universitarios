package modelos;

import java.util.Arrays;
import java.util.List;

import docencia.Arranque;

/**
 * Clase que modela un aula.
 * Cada conjunto de líneas del fichero "aulas.txt" delimitado por * se puede modelar como un objeto de esta clase.
 * @author Guillermo Barreiro Fernández
 */
public class Aula implements EscribibleEnFichero{

	private String siglas;
	private char tipoGrupo; // A o B
	private int capacidad;
	
	private static final String NOMBRE_FICHERO = "aulas.txt";
	
	/**
	 * Constructor de la clase Aula. Permite crear un objeto de la clase Aula.
	 * @param siglas Siglas del aula
	 * @param tipo_Grupo Tipo de clases que se impartirán en esta aula: A o B
	 * @param capacidad Número máximo de alumnos que caben en el aula. Máximo de 20 para grupos B y de 40 para grupos A.
	 */
	public Aula(String siglas, char tipo_Grupo, int capacidad) {
		
		this.siglas=siglas;
		this.tipoGrupo=tipo_Grupo;
		this.capacidad=capacidad;
		
	}
	
	/**
	 * Muestra en pantalla el calendario de ocupación de esta aula.
	 */
	public void obtenerCalendarioOcupacion() {
		// Crea las matrices en la que se almacenarán los grupos, asignaturas y profesores del horario
		// Matrices 5x10: 5 días, 10 horas
		Asignatura.Grupo clases[][] = new Asignatura.Grupo[5][10];
		Asignatura asignaturas[][] = new Asignatura[5][10];
		String profesores[][] = new String[5][10];
		List<Character> diasSemana = Arrays.asList('L','M','X','J','V');
		
		for(Asignatura as: Arranque.asignaturas.values()) {
			for(Asignatura.Grupo ag: as.getGruposTodos()) {
				// Recorre todos los grupos de todas las asignaturas, en busca de sesiones que se impartan en esta aula
				if(ag.getAula().equals(this.siglas)) {
					// Si el grupo ag se imparte en esta aula...
					int duracion = ag.getTipo()=='A'?
							as.getDuracionGrupoA():as.getDuracionGrupoB(); // horas que dura el grupo
					int dia = diasSemana.indexOf(ag.getDia()); //L = 0; M = 1...
					
					// Hay clase, por lo que debemos obtener el profesor que la imparte
					String siglasProfe = null;
					bucleProfesor:
					for(Profesor profe: Arranque.profesores.values()) {
						for(Profesor.DocenciaImpartida di: profe.getDocenciaImpartida()) {
							// Recorre todos los profesores y sus docencias impartidas hasta encontrar este grupo
							if(di.getSiglas().equals(as.getSiglas()) && di.getIdGrupo()==ag.getId() && di.getTipoGrupo() == ag.getTipo()) {
								// Es este profesor el que imparte la asignatura
								siglasProfe = profe.getSiglasProfesor();
								break bucleProfesor;
							}
						}
					}
					
					for(int i = 0;i<duracion;i++) {
						// OJO: si hay una Exception aquí es porque hay clases que están mal configuradas (después de las 18h!)
						clases[dia][ag.getHoraInicio()-9+i] = ag; // se guarda este grupo en la matriz
						asignaturas[dia][ag.getHoraInicio()-9+i] = as; // se guarda esta asignatura en la matriz
						profesores[dia][ag.getHoraInicio()-9+i] = siglasProfe; // se guarda este profesor en la matriz
					}
				}
			}
		}
		
		// Imprime la cabecera de la tabla
		System.out.println("AULA: " + this.siglas);
		System.out.println();
		System.out.println("Hora \t\t L \t\t M \t\t X \t\t J \t\t V");
		System.out.println();
		for(int hora=0;hora<10;hora++) {
			// Imprime el horario, fila a fila
			System.out.printf((9+hora) + "-" + (10+hora) + " \t\t"); // columna de la hora
			
			// Imprime la línea con la asignatura y el grupo
			for(int dia=0;dia<5;dia++) {
				if(hora!=5) {
					// Comprueba si hay algún grupo ese día a esa hora
					Asignatura.Grupo grupo = clases[dia][hora];
					Asignatura asignatura = asignaturas[dia][hora];
					if(grupo!=null) {
						// Imprime la asignatura y el grupo
						System.out.printf("%s-%c%d \t\t", asignatura.getSiglas(), grupo.getTipo(), grupo.getId());
					}else {
						// Imprime un espacio
						System.out.printf("\t\t");
					}
				}else {
					// Hora de la comida
					System.out.printf("XXXXXX \t\t");
				}
				
			}
			
			System.out.println(); // siguiente línea para las siglas de los profesores
			
			// Imprime la línea con las siglas del profesor
			System.out.printf("\t\t");
			for(int dia=0;dia<5;dia++) {
				if(hora!=5) {
					String profesor = profesores[dia][hora];
					if(profesor!=null) {
						// Imprime sus siglas
						System.out.printf(profesor+"\t\t");
					}else {
						// Imprime un espacio
						System.out.printf("\t\t");
					}
				}
				
			}
			
			System.out.println();
		}
	}
	
	/**
	 * Devuelve las siglas del aula.
	 */
	public String getSiglas() {
		return siglas;
	}
	
	/**
	 * Devuelve el tipo de grupo que se imparte en esta aula.
	 */
	public char getTipoGrupo() {
		return tipoGrupo;
	}
	
	/**
	 * Devuelve la capacidad (número máximo de alumnos) del aula.
	 */
	public int getCapacidad() {
		return capacidad;
	}
	
	/**
	 * Convierte los atributos de este objeto a un conjunto de líneas de texto, separadas por saltos de línea (\n),
	 * para así facilitar su guardado en un fichero de texto.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 * @return (String): conjunto de líneas de texto con los atributos del objeto.
	 */
	public String toTexto() {
		// Siglas, tipo grupo y capacidad:
		return siglas + " \n" + tipoGrupo + " \n" + capacidad; 
		
	}
	
	/**
	 * Devuelve el nombre del fichero de texto en el que se guardan los profesores.
	 * Implementado por la interfaz {@link EscribibleEnFichero}.
	 */
	public String getNombreFichero() {
		return NOMBRE_FICHERO;
	}
	
}
