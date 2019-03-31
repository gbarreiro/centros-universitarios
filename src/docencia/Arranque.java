package docencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modelos.*;

/**
 * Clase de arranque del proyecto. La ejecución del programa comienza con la invocación del método {@link #inicio()}.
 * Contiene los mapas estáticos con todos los modelos (alumnos, profesores...), accesibles desde cualquier parte del programa.
 * Esta clase se encarga de leer los ficheros de la base de datos y de sobreescribirlos cuando sea necesario.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Arranque {
	// Mapas
	
	/**
	 * Mapa con los alumnos registrados en la base de datos.
	 * La clave es el DNI del alumno y el valor un objeto de tipo {@link modelos.Alumno}.
	 */
	public static LinkedHashMap<String, Alumno> alumnos = new LinkedHashMap<String, Alumno>();
	
	/**
	 * Mapa con los profesores registrados en la base de datos.
	 * La clave es el DNI del profesor y el valor un objeto de tipo {@link modelos.Profesor}.
	 */
	public static LinkedHashMap<String, Profesor> profesores = new LinkedHashMap<String, Profesor>();
	
	/**
	 * Mapa con las aulas registradas en la base de datos.
	 * La clave es las siglas del aula y el valor un objeto de tipo {@link modelos.Aula}.
	 */
	public static LinkedHashMap<String, Aula> aulas = new LinkedHashMap<String, Aula>();
	
	/**
	 * Mapa con las planificaciones docentes registradas en la base de datos.
	 * La clave es la concatenación del DNI del profesor, la asignatura y el tipo de grupo.
	 * El valor es un objeto de tipo {@link modelos.Pod}.
	 */
	public static LinkedHashMap<String, Pod> pod = new LinkedHashMap<String, Pod>();
	
	/**
	 * Mapa con las asignaturas registradas en la base de datos.
	 * La clave es las siglas de la asignatura y el valor un objeto de tipo {@link modelos.Asignatura}.
	 */
	public static LinkedHashMap<String, Asignatura> asignaturas = new LinkedHashMap<String, Asignatura>();
	
	// Parámetros del curso (cursoAcademico.txt):
	
	/**
	 * Curso académico actual, en formato String. P.ej.: 17/18
	 */
	public static String cursoAcademico;
	
	/**
	 * Semana del año en la que comienza el curso académico. P.ej.: 35
	 */
	public static int semanaInicioCurso;
	
	private static ArrayList<String> instrucciones;
	private static final String FICHERO_INSTRUCCIONES	 = "ficheros/ejecucion.txt";
	
	// La clase no se puede instanciar:
	private Arranque() {
		
	}

	/**
	 * Punto de comienzo del programa.
	 * Lee los ficheros de la base de datos, carga su contenido en memoria 
	 * y a partir de ahí ejecuta todas las instrucciones del fichero ejecucion.txt.
	 * Se presupone que los ficheros de la base de datos están correctos.
	 */
	public static void inicio() {
		try {
			cargarMapas();
			
		}catch(Exception e) {
			System.out.println("Error inesperado al cargar los ficheros. Se termina la ejecución del programa");
			e.printStackTrace(System.out);
			System.exit(1);
		}

		cargarInstrucciones();
		
	}
	
	/**
	 * Lee los archivos de la base de datos y carga su contenido en los mapas estáticos de la clase Arranque.
	 * Se lee archivo a archivo, siguiendo un orden lógico, y utilizando los métodos {@link #leerArray(String)} y {@link #leerSimple(String)}.
	 * @throws IOException En caso de error leyendo alguno de los archivos (no debería de pasar en este proyecto).
	 */
	private static void cargarMapas() throws IOException{

		// 1) Curso académico: cursoAcademico.txt
		ArrayList<String> listaCursoAcademico = leerSimple("ficheros/cursoAcademico.txt");
		cursoAcademico = listaCursoAcademico.get(0);
		semanaInicioCurso = Integer.parseInt(listaCursoAcademico.get(1));
		

		/*
		 * 2) Aulas: aulas.txt
		 * Líneas:
		 * 	(0) siglas --> String
		 * 	(1) tipo grupo --> char
		 * 	(2) capacidad --> int
		 * Clave: siglas
		 */
		ArrayList<ArrayList<String>> listaAulas = leerArray("ficheros/aulas.txt");
		for (int i = 0; i < listaAulas.size(); i++) {
			// Recorre el arraylist aula a aula
			ArrayList<String> aulaActual = listaAulas.get(i);
			String clave = aulaActual.get(0); // clave = siglas
			char tipoGrupo = aulaActual.get(1).charAt(0);
			int capacidad = Integer.parseInt(aulaActual.get(2));
			Aula aula = new Aula(clave, tipoGrupo, capacidad);
			aulas.put(clave, aula); // mete el aula en el mapa correspondiente
		}
		

		/*
		 * 3) Asignaturas: asignaturas.txt
		 * Líneas:
		 * 	(0) siglas --> String
		 * 	(1) nombre --> String
		 * 	(2) curso --> int
		 * 	(3) cuatrimestre --> int
		 * 	(4) coordinador --> String (DNI)
		 * 	(5) Prerrequisitos --> String (lista de siglas separadas por ';') --! puede ser null (no hay prerrequisitos)
		 * 	(6) Duración grupos A --> int
		 * 	(7) Duración grupos B --> int
		 * 	(8) Grupos A --> String (lista de grupos separados por ';') --! puede ser null (no hay grupos A)
		 * 	(9) Grupos B --> String (lista de grupos separados por ';') --! puede ser null (no hay grupos B)
		 * Clave: siglas
		 */
		ArrayList<ArrayList<String>> listaAsignaturas = leerArray("ficheros/asignaturas.txt");
		for (int i = 0; i < listaAsignaturas.size(); i++) {
			// Recorre el arraylist asignatura a asignatura
			ArrayList<String> asignaturaActual = listaAsignaturas.get(i);
			String clave = asignaturaActual.get(0);	// clave = siglas	
			String nombre = asignaturaActual.get(1);
			int curso = Integer.parseInt(asignaturaActual.get(2));
			int cuatrimestre = Integer.parseInt(asignaturaActual.get(3));
			String dniCoordinador = asignaturaActual.get(4);
			String prerrequisitos = asignaturaActual.get(5); // OJO: puede ser null si no tiene prerrequisitos
			int duracionGrupoA = Integer.parseInt(asignaturaActual.get(6));
			int duracionGrupoB = Integer.parseInt(asignaturaActual.get(7));
			String listaGruposA = asignaturaActual.get(8);
			String listaGruposB = asignaturaActual.get(9);
			
			Asignatura asignatura = new Asignatura(clave, nombre, curso, cuatrimestre, dniCoordinador,
					prerrequisitos, duracionGrupoA, duracionGrupoB, listaGruposA, listaGruposB);
			asignaturas.put(clave, asignatura);
		}
		

		/*
		 * 4) POD: pod.txt -- alternativa 1
		 * Líneas:
		 * 	(0) dni --> String
		 * 	(1) asignatura --> String (siglas)
		 * 	(2) Tipo grupo --> char (A/B)
		 *  (3) Número de grupos --> float
		 * Clave: DNI+asignatura+tipoGrupo
		 */
		ArrayList<ArrayList<String>> listaPod = leerArray("ficheros/pod.txt");
		for (int i = 0; i < listaPod.size(); i++) {
			// Recorre el arraylist, pod a pod
			ArrayList<String> podActual = listaPod.get(i);
			String dni = podActual.get(0);
			String asignatura = podActual.get(1);
			char tipoGrupo = podActual.get(2).charAt(0);
			float numeroGrupos = Float.parseFloat(podActual.get(3));
			String clave = dni + asignatura + tipoGrupo;

			Pod miPod = new Pod(dni, asignatura, tipoGrupo, numeroGrupos);
			pod.put(clave, miPod);
		} 

		/*
		 * 5) Profesores: profesores.txt
		 * Líneas:
		 * 	(0) dni --> String
		 * 	(1) Nombre y apellidos --> String
		 * 	(2) fecha de nacimiento --> String (dd/mm/aaaa)
		 * 	(3) categoría --> String (titular/asociado)
		 * 	(4) departamento --> String
		 * 	(5) Docencia impartida --> String (lista de docencias impartidas separada por ';') --! puede ser null (no imparte docencia)
		 * Clave = DNI
		 */
		ArrayList<ArrayList<String>> listaProfesores = leerArray("ficheros/profesores.txt");
		for (int i = 0; i < listaProfesores.size(); i++) {
			// Recorre el arraylist, profesor a profesor
			ArrayList<String> profeActual = listaProfesores.get(i);
			String clave = profeActual.get(0); // clave = dni
			String nombre = profeActual.get(1);
			String fechaNacimiento = profeActual.get(2);
			String categoria = profeActual.get(3);
			String departamento = profeActual.get(4);
			String docenciaImpartida = profeActual.size()==6?profeActual.get(5):null; // puede ser null
			
			Profesor profesor = new Profesor(clave, nombre, fechaNacimiento, categoria, departamento, docenciaImpartida);
			profesores.put(clave, profesor);
			
		}

		/*
		 * 6) Alumnos: alumnos.txt
		 * Líneas:
		 * 	(0) dni --> String
		 * 	(1) Nombre y apellidos --> String
		 * 	(2) email --> String --! puede ser null (no tiene email)
		 *  (3) fecha de nacimiento --> String (dd/mm/aaaa)
		 *  (4) fecha de ingreso --> String (dd/mm/aaaa)
		 *  (5) Asignaturas superadas --> String (lista de asignaturas separada por ';') --! puede ser null (no ha superado ninguna asignatura)
		 *  (6) Docencia recibida --> String (lista de asignaturas separada por ';') --! puede ser null (no recibe docencia ninguna)
		 * Clave = DNI
		 */
		ArrayList<ArrayList<String>> listaAlumnos = leerArray("ficheros/alumnos.txt");
		for (int i = 0; i < listaAlumnos.size(); i++) {
			// Recorre el arraylist, alumno a alumno
			ArrayList<String> alumnoActual = listaAlumnos.get(i);
			String clave = alumnoActual.get(0); // clave = dni
			String nombre = alumnoActual.get(1);
			String email = alumnoActual.get(2); // puede ser null
			String fechaNacimiento = alumnoActual.get(3);
			String fechaIngreso = alumnoActual.get(4);
			String asignaturasSuperadas = alumnoActual.size()>=6?alumnoActual.get(5):null;
			String docenciaRecibida = alumnoActual.size()==7?alumnoActual.get(6):null;
			
			Alumno alumno = new Alumno(clave, nombre, email, fechaNacimiento, fechaIngreso, asignaturasSuperadas, docenciaRecibida);
			alumnos.put(clave, alumno);
			
		}

	}
	
	/**
	 * Lee el fichero ejecucion.txt y carga sus instrucciones (1 línea = 1 instrucción) en memoria.
	 */
	private static void cargarInstrucciones() {
		// 1. Leer ejecucion.txt
		try {
			instrucciones = leerSimple(FICHERO_INSTRUCCIONES);
		} catch (Exception e) {
			System.out.println("Fichero de ejecución no existente");
			System.exit(1); // finaliza el programa
		} 
		// 2. Ejecutar comando a comando
		for (String linea: instrucciones){
			if(!linea.isEmpty() && !linea.startsWith("*")) {
				
				// Si la línea es un comando, la separa en parametros, respetando las comillas dobles
				List<String> params = new ArrayList<String>();
				Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(linea);
				while (m.find())
				    params.add(m.group(1).replace("\"", "")); 
				params.remove(0); // elimina del array de parámetros el número de línea
				Comandos.ejecutaComando(linea, params.toArray(new String[0]));
			}
			
		}

	}

	
	// FUNCIONES AUXILIARES:

	/**
	 * Lee un archivo con n elementos separados por el delimitador '*'.
	 * Se emplea para los siguientes ficheros: alumnos.txt, asignaturas.txt, aulas.txt, pod.txt y profesores.txt.
	 * Si hay una línea en blanco, se guarda como null en el Array.
	 * @param archivo nombre del archivo
	 * @return ArrayList de ArrayList de líneas
	 * @throws IOException En caso de error al leer el fichero
	 */
	private static ArrayList<ArrayList<String>> leerArray(String archivo) throws IOException {

		// Declaracion de variables
		ArrayList<String> listaLineas =  new ArrayList<String>();
		ArrayList<ArrayList<String>> listaElementos = new ArrayList<ArrayList<String>>();
		String linea;
		BufferedReader lectura;

		lectura = new BufferedReader(new FileReader(archivo));
		// Lee el archivo línea a línea hasta el final
		while ((linea = lectura.readLine()) != null) {
			if (linea.trim().isEmpty()) listaLineas.add(null); // línea en blanco = null en el ArrayList
			else if (linea.toCharArray()[0] != '*') { // si no se trata de un delimitador
				listaLineas.add(linea.trim()); // se añade la línea a la lista

			} else { // si se trata de un delimitador
				// añade la lista de líneas a la lista de elementos
				listaElementos.add(listaLineas);
				listaLineas= new ArrayList<String>();

			}

		}

		listaElementos.add(listaLineas);

		// Una vez finalizada la lectura
		lectura.close();

		return listaElementos;


	}
	
	/**
	 * Lee un fichero de n lineas, devolviendo estas como cadenas de texto en un ArrayList de n elementos.
	 * Se utiliza para los ficheros cursoAcademico.txt y ejecucion.txt
	 * @param archivo Nombre del archivo
	 * @return ArrayList con las líneas leídas
	 * @throws IOException En caso de error al leer el fichero
	 */
	private static ArrayList<String> leerSimple(String archivo) throws IOException{
		ArrayList<String> lista = new ArrayList<String>(); // aquí se irán almacenando todas las líneas del fichero de texto
		BufferedReader lectura;

		lectura = new BufferedReader(new FileReader(archivo));
		String linea;
		while((linea=lectura.readLine())!=null){
			// Hasta llegar al final del fichero, va leyendo línea a línea
			if(!linea.trim().isEmpty()) lista.add(linea.trim()); // línea en blanco = null en el ArrayList
		}

		// Una vez finalizada la lectura del fichero
		lectura.close();

		return lista;
		
	}
	
	/**
	 * Vuelca el contenido del LinkedHashMap al fichero de texto correspondiente.
	 * El nombre del fichero y la representación de los objetos se obtiene a través de la interfaz modelos.EscribibleEnFichero
	 * @param mapa Mapa a escribir en el fichero correspondiente
	 */
	public static void sobreescribirFichero(LinkedHashMap<String, EscribibleEnFichero> mapa){
		// Obtenemos la lista con los objetos a escribir
		Collection<EscribibleEnFichero> coleccion = mapa.values();
		EscribibleEnFichero lista[] = coleccion.toArray(new EscribibleEnFichero[coleccion.size()]);
		
		// Abrimos el fichero correspondiente en modo sobreescritura

		FileWriter writer = null;
		try {
			writer = new FileWriter(lista[0].getNombreFichero(), false);
			BufferedWriter buff = new BufferedWriter(writer);
			PrintWriter printer = new PrintWriter(buff);

			for(int i = 0; i<lista.length; i++) {
				// Recorre todo el mapa, escribiendo sus elementos uno a uno
				printer.print(lista[i].toTexto());
				if(i<lista.length-1) { // añade un separador (*) entre elemento y elemento
					printer.print("\n*\n");
				}
			}

			// Cierra el fichero
			printer.close();
			buff.close();
			writer.close();
			
		} catch (IOException e) {
			System.out.println("Error inesperado al escribir en el fichero " + lista[0].getNombreFichero());
			e.printStackTrace();
		}


		
	}
	
}
