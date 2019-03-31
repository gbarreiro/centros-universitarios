package docencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import modelos.Alumno;
import modelos.Asignatura;
import modelos.Aula;
import modelos.EscribibleEnFichero;
import modelos.Persona;
import modelos.Profesor;

/**
 * Esta clase estática se encarga de interpretar las instrucciones del fichero ejecucion.txt.
 * Si el comando existe lo ejecuta, abortándose la operación y generándose el aviso correspondiente si hubiese algún error.
 * @author Guillermo Barreiro Fernández
 *
 */
public class Comandos {
	
	// La clase no se puede instanciar:
	private Comandos() {
		
	}

	/**
	 * Ejecuta la instrucción correspondiente.
	 * @param instruccion La instrucción entera, es decir, la línea correspondiente del fichero ejecucion.txt
	 * @param parametros La instrucción dividida (split) en sus parámetros.
	 */
	public static void ejecutaComando(String instruccion, String parametros[]) {
		String comando = parametros[0].toLowerCase();
		switch (comando) {
		case "insertapersona" :
			insertarPersona(parametros);
			break;
			
		case "asignagrupo": 
			asignarGrupo(parametros);
			break;
			
		case "matricula": 
			matricularAlumno(parametros);
			break;
			
		case "creagrupoasig":
			crearGrupoAsignatura(parametros);
			break;
		
		case "evalua": 
			evaluarAsignatura(parametros);
			break;
			
		case "expediente":
			obtenerExpediente(parametros);
			break;
			
		case "ocupacionaula": 
			obtenerOcupacionAula(parametros);
			break;
			
		default:
			// Si el comando no existe
			Errores.comandoErroneo(instruccion);
			break;
		
		}
		
	}
	
	/**
	 * Introduce un nuevo alumno o profesor en el sistema.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void insertarPersona(String[] parametros) {
		
		String nombre = parametros[3];
		
		// Comprobamos el tipo de persona
		if(parametros[1].contains("profesor")) { // el contains evita problemas con los espacios
			if(parametros.length==7) {
				// Comprobamos el DNI y la fecha de nacimiento
				String dni = parametros[2];
				if(!Persona.comprobarDNI(dni)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Dni incorrecto");
					return;
				}
				
				String fechaNacimiento = parametros[4];
				if(!Persona.comprobarFechaNacimiento(fechaNacimiento)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Fecha incorrecta");
					return;
				}
				
				// DNI ya existente?
				if(Arranque.profesores.containsKey(dni)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Profesor ya existente");
					return;
				}
				
				// Llegados hasta aquí el profesor se puede registrar
				String categoria = parametros[5];
				String departamento = parametros[6];
				
				Profesor profesor = new Profesor(dni, nombre, fechaNacimiento, categoria, departamento, null);
				Arranque.profesores.put(dni, profesor);
				Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.profesores));
				
			}else {
				Errores.errorComando(Errores.INSERTA_PERSONA, "Número de argumentos incorrecto");
			}
			
		}
		
		if(parametros[1].contains("alumno")) {
			if(parametros.length==6) {
				// Comprobamos el DNI y la fecha de nacimiento
				String dni = parametros[2];
				if(!Persona.comprobarDNI(dni)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Dni incorrecto");
					return;
				}
				
				String fechaNacimiento = parametros[4];
				if(!Persona.comprobarFechaNacimiento(fechaNacimiento)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Fecha incorrecta");
					return;
				}
				
				// Fecha de ingreso correcta?
				String fechaIngreso = parametros[5];
				if(!Persona.comprobarFechaIngreso(fechaNacimiento, fechaIngreso)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Fecha de ingreso incorrecta");
					return;
				}
				
				// DNI ya existente?
				if(Arranque.alumnos.containsKey(dni)) {
					Errores.errorComando(Errores.INSERTA_PERSONA, "Alumno ya existente");
					return;
				}
				
				// Llegados hasta aquí el alumno se puede registrar
				Alumno alumno = new Alumno(dni, nombre, null, fechaNacimiento, fechaIngreso, null, null);
				Arranque.alumnos.put(dni, alumno);
				Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.alumnos));
			}
			else {
				Errores.errorComando(Errores.INSERTA_PERSONA, "Número de argumentos incorrecto");
			}
		}
	}
	
	/**
	 * Asigna un grupo de una asignatura a un profesor o alumno.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void asignarGrupo(String[] parametros) {
		if(parametros.length!=6) {
			// Número de parámetros incorrecto?
			Errores.errorComando(Errores.ASIGNAR_GRUPO, "Número de argumentos incorrecto");
			return;
		}
		
		// Separamos los parámetros
		String perfil = parametros[1];
		String dni = parametros[2];
		String asignatura = parametros[3];
		char tipoGrupo = parametros[4].charAt(0);
		int grupo = Integer.parseInt(parametros[5]);
		
		// Comprobamos posibles errores
		
		// Existe el alumno / profesor?
		if(perfil.equals("alumno")) {
			if(!Arranque.alumnos.containsKey(dni)) {
				// No existe el alumno
				Errores.errorComando(Errores.ASIGNAR_GRUPO, "Alumno inexistente");
				return;
			}
		}else {
			if(!Arranque.profesores.containsKey(dni)) {
				// No existe el profesor
				Errores.errorComando(Errores.ASIGNAR_GRUPO, "Profesor inexistente");
				return;
			}
		}
		
		// Existe la asignatura?
		if(!Arranque.asignaturas.containsKey(asignatura)) {
			// No existe la asignatura
			Errores.errorComando(Errores.ASIGNAR_GRUPO, "Asignatura inexistente");
			return;
		}
		
		// Tipo de grupo: solo A o B
		if(tipoGrupo!='A' && tipoGrupo!='B') {
			// Tipo de grupo incorrecto
			Errores.errorComando(Errores.ASIGNAR_GRUPO, "Tipo de grupo incorrecto");
			return;
		}
		
		// Grupos de la asignatura
		if(Arranque.asignaturas.get(asignatura).getGrupo(grupo, tipoGrupo)==null) {
			Errores.errorComando(Errores.ASIGNAR_GRUPO, "Grupo inexistente");
			return;
		}
		
		// Grupo ya asignado?
		if(perfil.equals("profesor")) {
			// Comprobamos todos los profesores, uno a uno
			for(Profesor profe: Arranque.profesores.values()) {
				for(Profesor.DocenciaImpartida di: profe.getDocenciaImpartida()) {
					if(di.getSiglas().equals(asignatura)) {
						// Mismo grupo y asignatura?
						if(di.getIdGrupo()==grupo && di.getTipoGrupo()==tipoGrupo) {
							// El grupo ya estaba asignado
							Errores.errorComando(Errores.ASIGNAR_GRUPO, "Grupo ya asignado");
							return;
						}
					}
				}
			}
		}
		
		// Alumno no matriculado?
		if(perfil.equals("alumno")) {
			// Obtenemos las asignaturas en las que está matriculado el alumno
			ArrayList<Alumno.DocenciaRecibida> docencia = Arranque.alumnos.get(dni).getDocenciaRecibida();
			boolean matriculado = false;
			for(Alumno.DocenciaRecibida dr: docencia) {
				if(dr.getSiglas().equals(asignatura)) {
					matriculado = true;
					break;
				}
			}
			
			if(!matriculado) {
				Errores.errorComando(Errores.ASIGNAR_GRUPO, "Alumno no matriculado");
				return;
			}
		}
		
		// Comprobaciones POD
		if(perfil.equals("profesor")) {
			// Presente en el pod del profesor?
			String clave = dni + asignatura + tipoGrupo;
			if(!Arranque.pod.containsKey(clave)) {
				Errores.errorComando(Errores.ASIGNAR_GRUPO, "Asignatura/tipo-grupo no presente en el POD del profesor");
				return;
			}
			
			// Número de grupos válido?
			int numeroGruposImpartidos = 0;
			for(Profesor.DocenciaImpartida di: Arranque.profesores.get(dni).getDocenciaImpartida()) {
				if(di.getSiglas().equals(asignatura)&&di.getTipoGrupo()==tipoGrupo) numeroGruposImpartidos++;
				
			}
			float maximo = Arranque.pod.get(dni+asignatura+tipoGrupo).getNumeroGrupos();
			if(numeroGruposImpartidos>=maximo){
				// Número de grupos máximos alcanzado
				Errores.errorComando(Errores.ASIGNAR_GRUPO, "Número de grupos superior al contemplado en el POD");
				return;
			}
		}
		
		// Solapes: se comprueban todas los grupos A y B en las que está inscrito un profesor o alumno, en busca de posibles solapamientos
		
		// Duración de una clase del grupo que estamos intentando asignar	(grupo1)
		int duracion1 = tipoGrupo=='A'?
				Arranque.asignaturas.get(asignatura).getDuracionGrupoA():
				Arranque.asignaturas.get(asignatura).getDuracionGrupoB();

		Asignatura.Grupo grupo1 = Arranque.asignaturas.get(asignatura).getGrupo(grupo, tipoGrupo);

		// Horas ocupadas por el grupo1
		int horas1[] = new int[duracion1];
		for(int i=0; i<duracion1; i++) {
			horas1[i] = grupo1.getHoraInicio()+i;
		}
		
		// Solape alumno
		if(perfil.equals("alumno")) {
					
			ArrayList<Alumno.DocenciaRecibida> docenciaRecibida = Arranque.alumnos.get(dni).getDocenciaRecibida();
			for(Alumno.DocenciaRecibida dr: docenciaRecibida) {
				// Comprueba una a una las asignaturas en las que está matriculado un alumno, en busca de un posible solapamiento
				if(dr.getTipoGrupo()=='#') continue; // si no tiene grupo asignado, se salta esta asignatura
				
				// Buscamos la hora a la que tiene lugar el/los grupo(s) de la asignatura en el/los que está el alumno: grupo2
				Asignatura mAsignatura = Arranque.asignaturas.get(dr.getSiglas());
				Asignatura.Grupo grupo2 = mAsignatura.getGrupo(dr.getId(), dr.getTipoGrupo());
				
				// Duración de una clase del grupo a analizar
				int duracion2 = dr.getTipoGrupo()=='A'?
						Arranque.asignaturas.get(dr.getSiglas()).getDuracionGrupoA():
						Arranque.asignaturas.get(dr.getSiglas()).getDuracionGrupoB();	
								
				// Coinciden el mismo día?
				if(grupo2.getDia()==grupo1.getDia()){
					// Entonces posible solape
					int horas2[] = new int[duracion2];
					for(int i=0; i<duracion2; i++) {
						horas2[i] = grupo2.getHoraInicio()+i;
					}
					
					// ahora recorremos los dos arrays en busca de horas que se solapen
					for(int i = 0; i<duracion1; i++) {
						for(int k = 0; i<duracion2; i++) {
							if(horas1[i]==horas2[k]) {
								// Solape detectado!
								Errores.errorComando(Errores.ASIGNAR_GRUPO, "Solape alumno");
								return;
							}
						}
					}
				}
			}
		}
		
		// Solape profesor
		if(perfil.equals("profesor")) {
			ArrayList<Profesor.DocenciaImpartida> docenciaImpartida = Arranque.profesores.get(dni).getDocenciaImpartida();
			for(Profesor.DocenciaImpartida di: docenciaImpartida) {
				// Comprueba una a una las asignaturas que imparte un profesor, en busca de un posible solapamiento
				// Buscamos la hora a la que tiene lugar el/los grupo(s) de la asignatura en el/los que está el profesor: grupo2
				Asignatura mAsignatura = Arranque.asignaturas.get(di.getSiglas());
				Asignatura.Grupo grupo2 = mAsignatura.getGrupo(di.getIdGrupo(), di.getTipoGrupo());
				
				// Duración de una clase del grupo a analizar
				int duracion2 = di.getTipoGrupo()=='A'?
						Arranque.asignaturas.get(di.getSiglas()).getDuracionGrupoA():
						Arranque.asignaturas.get(di.getSiglas()).getDuracionGrupoB();	
				
				// Coinciden el mismo día?
				if(grupo2.getDia()==grupo1.getDia()){
					// Entonces posible solape
					int horas2[] = new int[duracion2];
					for(int i=0; i<duracion2; i++) {
						horas2[i] = grupo2.getHoraInicio()+i;
					}
					
					// ahora recorremos los dos arrays en busca de horas que se solapen
					for(int i = 0; i<duracion1; i++) {
						for(int k = 0; i<duracion2; i++) {
							if(horas1[i]==horas2[k]) {
								// Solape detectado!
								Errores.errorComando(Errores.ASIGNAR_GRUPO, "Solape profesor");
								return;
							}
						}
					}		
				
			}
		}
		}
		
		// Aula completa?
		if(perfil.equals("alumno")) {
			// Capacidad máxima del aula
			int capacidad = Math.min(Arranque.aulas.get(grupo1.getAula()).getCapacidad(), tipoGrupo=='A'?40:20);
			int contador = 0; // número de alumnos en el mismo grupo
			
			// Comprobamos alumno a alumno, buscando aquellos que estén en el mismo grupo
			for(Alumno alumno: Arranque.alumnos.values()) {
				for(Alumno.DocenciaRecibida dr: alumno.getDocenciaRecibida()) {
					if(dr.getSiglas().equals(asignatura) && dr.getId()==grupo && dr.getTipoGrupo() == tipoGrupo) {
						// Alumno en el mismo grupo
						contador++;
						if(contador>=capacidad) {
							Errores.errorComando(Errores.ASIGNAR_GRUPO, "Aula completa");
							return;
						}
					}
				}
			}
		}
		
		// Llegados hasta aquí, podemos asignar el grupo
		if(perfil.equals("alumno")) {
			// Asigna el grupo al alumno
			Alumno alumno = Arranque.alumnos.get(dni);
			alumno.asignarGrupo(asignatura, tipoGrupo, grupo);
			Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.alumnos));
		}
		
		if(perfil.equals("profesor")) {
			// Asigna el grupo al profesor
			Profesor profesor = Arranque.profesores.get(dni);
			profesor.asignarGrupo(asignatura, tipoGrupo, grupo);
			Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.profesores));
		
		}
		
	}

	
	/**
	 * Enrola a un alumno en una asignatura.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void matricularAlumno(String[] parametros) {
		if(parametros.length!=3) {
			// Número de parámetros incorrecto?
			Errores.errorComando(Errores.MATRICULAR_ALUMNO, "Número de argumentos incorrecto");
			return;
		}
		
		// Obtenemos el dni y la asignatura
		String dni = parametros[1];
		String asignatura = parametros[2];
		
		// Existe el alumno?
		if(!Arranque.alumnos.containsKey(dni)) {
			Errores.errorComando(Errores.MATRICULAR_ALUMNO, "Alumno inexistente");
			return;
		}
		
		// Existe la asignatura?
		if(!Arranque.asignaturas.containsKey(asignatura)) {
			Errores.errorComando(Errores.MATRICULAR_ALUMNO, "Asignatura inexistente");
			return;
		}
		
		// Ya matriculado?
		ArrayList<Alumno.DocenciaRecibida> docencia = Arranque.alumnos.get(dni).getDocenciaRecibida();
		for(Alumno.DocenciaRecibida dr: docencia) {
			if(dr.getSiglas().equals(asignatura)) {
				// Si está ya matriculado se aborta la operación
				Errores.errorComando(Errores.MATRICULAR_ALUMNO, "Ya es alumno de la asignatura indicada");
				return;
			}
		}
		
		// Prerrequisitos
		String[] prerrequisitos = Arranque.asignaturas.get(asignatura).getPrerrequisitos();
		ArrayList<Alumno.AsignaturaSuperada> superadas = Arranque.alumnos.get(dni).getAsignaturasSuperadas();
		boolean cumple = false;
		for(String pr: prerrequisitos) {
			// Comprueba los prerrequisitos, uno a uno
			cumple = false;
			for(Alumno.AsignaturaSuperada as: superadas) {
				if(as.getSiglas().equals(pr)) {
					cumple = true;
					break;
				}
			}
			if(!cumple) {
				// Hay un prerrequisito que no se está cumpliendo
				Errores.errorComando(Errores.MATRICULAR_ALUMNO, "No cumple requisitos");
				return;
			}
		}
		
		// Llegados aquí se puede matricular al alumno
		Arranque.alumnos.get(dni).matricular(asignatura);
		Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.alumnos));
		
	}
	
	/**
	 * Crea un grupo A o B para una asignatura que ya exista.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void crearGrupoAsignatura(String[] parametros) {
		if(parametros.length!=7) {
			// Número de parámetros incorrectos?
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Número de argumentos incorrecto");
			return;
		}
		
		// Parámetros del comando
		String siglasAsignatura = parametros[1];
		char tipoGrupo = parametros[2].charAt(0);
		int idGrupo = Integer.parseInt(parametros[3]);
		char dia = parametros[4].charAt(0);
		int horaInicio = Integer.parseInt(parametros[5]);
		String aula = parametros[6];
		
		// Existe la asignatura?
		if(!Arranque.asignaturas.containsKey(siglasAsignatura)) {
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Asignatura inexistente");
			return;
		}
		
		Asignatura asignatura = Arranque.asignaturas.get(siglasAsignatura);
		
		// Es correcto el tipo de grupo?
		if(tipoGrupo!='A' && tipoGrupo!='B') {
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Tipo de grupo incorrecto");
			return;
		}
		
		// Existe ya el grupo?
		if(asignatura.getGrupo(idGrupo, tipoGrupo)!=null) {
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Grupo ya existente");
			return;
		}
		
		// Día correcto?
		if(dia!='L' && dia!='M' && dia!= 'X' && dia!='J' && dia!='V') {
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Día incorrecto");
			return;
		}
		
		// Existe el aula?
		if(!Arranque.aulas.containsKey(aula)) {
			Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Aula no existente");
			return;
		}
		
		// Posible solapamiento?
		// horas1: horas ocupadas por el grupo que estamos intentando crear
		int duracion1 = tipoGrupo=='A'?
				asignatura.getDuracionGrupoA():
					asignatura.getDuracionGrupoB();
		int horas1[] = new int[duracion1];
		for(int i=0;i<duracion1;i++) horas1[i] = horaInicio+i;
		
		
		for(Asignatura asg: Arranque.asignaturas.values()) {
			for(Asignatura.Grupo grupo: asg.getGruposTodos()) {
				if(grupo.getAula().equals(aula)&&grupo.getDia()==dia) {
					// Posible solape
					// horas2: horas ocupadas por el grupo que podría estar solapando
					int duracion2 = grupo.getTipo()=='A'?
							asg.getDuracionGrupoA():asg.getDuracionGrupoB();
					int horas2[] = new int[duracion2];
					for(int i=0;i<duracion2;i++) horas2[i] = grupo.getHoraInicio()+i;
					
					// Se solapan?
					for(int i=0; i<duracion1; i++) {
						for(int k=0; k<duracion2; k++) {
							if(horas1[i]==horas2[k]) {
								// Hay solapamiento
								Errores.errorComando(Errores.CREAR_GRUPO_ASIGNATURA, "Solape de aula");
								return;
							}
						}
					}
				}
				
			}
			
		}
		
		// Llegados hasta aquí ya se puede crear el grupo
		asignatura.crearGrupoAsignatura(tipoGrupo, idGrupo, dia, horaInicio, aula);
		Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.asignaturas));
		
	}
	
	/**
	 * Introduce las notas de una asignatura, asignándoselas a todos los alumnos que la estén cursando
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void evaluarAsignatura(String[] parametros) {
		if(parametros.length!=4) {
			// Número de parámetros incorrectos
			Errores.errorComando(Errores.EVALUAR_ASIGNATURA, "Número de argumentos incorrecto");
			return;
		}
		
		// Argumentos del comando
		String siglasAsignatura = parametros[1];
		String ficheroNotasA = parametros[2];
		String ficheroNotasB = parametros[3];
		String cursoAcademico = Arranque.cursoAcademico;
		
		// Existe la asignatura?
		if(!Arranque.asignaturas.containsKey(siglasAsignatura)) {
			Errores.errorComando(Errores.EVALUAR_ASIGNATURA, "Asignatura inexistente");
			return;
		}
		
		// Asignatura ya evaluada?
		for(Alumno alumno: Arranque.alumnos.values()) {
			for(Alumno.AsignaturaSuperada as: alumno.getAsignaturasSuperadas()) {
				if(as.getSiglas().equals(siglasAsignatura) && as.getCursoAcademico().equals(cursoAcademico)) {
					// La asignatura ya había sido evaluada
					Errores.errorComando(Errores.EVALUAR_ASIGNATURA, "Asignatura ya evaluada en este curso académico");
					return;
				}
			}
		}
		
		// Leemos los ficheros y cargamos su contenido en un HashMap, donde la clave será el DNI del alumno y el valor su nota
		LinkedHashMap<String, Float> notasA = new LinkedHashMap<String, Float>();
		LinkedHashMap<String, Float> notasB = new LinkedHashMap<String, Float>();
		
		// Lectura del fichero de notas A
		try {
			BufferedReader lectura = new BufferedReader(new FileReader(ficheroNotasA));
			String linea;
			while((linea=lectura.readLine())!=null){
				// Hasta llegar al final del fichero, va leyendo línea a línea
				if(!linea.isEmpty()) {
					String[] params = linea.trim().split("\\s+");
					String dni = params[0].trim();
					float nota = Float.parseFloat(params[1].trim());
					notasA.put(dni, nota);
				}
			}

			// Una vez finalizada la lectura del fichero
			lectura.close();

			// Lectura del fichero de notas B
			lectura = new BufferedReader(new FileReader(ficheroNotasB));
			while((linea=lectura.readLine())!=null){
				// Hasta llegar al final del fichero, va leyendo línea a línea
				if(!linea.isEmpty()) {
					String[] params = linea.trim().split("\\s+");
					String dni = params[0].trim();
					float nota = Float.parseFloat(params[1].trim());
					notasB.put(dni, nota);
				}
			}
		}catch(IOException e) {
			System.out.println("Error al leer los ficheros. Se aborta la operación.");
			return;
		}
		
		Collection<String> dniAlumnos = notasA.keySet();
		
		int linea = 0;
		for(String dni: dniAlumnos) {
			// Evaluamos todos los alumnos del fichero, uno a uno
			linea++;
			String error = "Error en línea " + linea + ": ";
			
			// Existe el alumno?
			if(!Arranque.alumnos.containsKey(dni)) {
				Errores.errorComando(Errores.EVALUAR_ASIGNATURA, error + "Alumno inexistente: " + dni);
				continue;
			}
			
			// El alumno está matriculado en la asignatura?
			Alumno alumno = Arranque.alumnos.get(dni);
			boolean matriculado = false;
			for(Alumno.DocenciaRecibida dr: alumno.getDocenciaRecibida()) {
				if(dr.getSiglas().equals(siglasAsignatura)) {
					matriculado = true;
					break;
				}
			}
			
			if(!matriculado) {
				Errores.errorComando(Errores.EVALUAR_ASIGNATURA, error + "Alumno no matriculado: " + dni);
				continue;
			}
			
			
			// Notas correctas?
			float notaA = notasA.get(dni);
			float notaB = notasB.get(dni);
			if(notaA>5 || notaA<0 || notaB>5 || notaB<0) {
				Errores.errorComando(Errores.EVALUAR_ASIGNATURA, error + "Nota grupo A/B incorrecta");
				continue;
			}
			
			// Llegados hasta aquí ya se puede evaluar la asignatura
			float notaTotal = notaA + notaB; // nota entre 0 y 10
			alumno.evaluarAsignatura(notaTotal, siglasAsignatura, cursoAcademico);
			
		}
		
		// Actualizamos el fichero alumnos.txt
		Arranque.sobreescribirFichero(new LinkedHashMap<String, EscribibleEnFichero>(Arranque.alumnos));
		
	}

	/**
	 * Genera el expediente de un alumno. 
	 * Se trata de una relación ordenada, por curso y asignatura, de todas las asignaturas aprobadas.
	 * Se guarda en un fichero de texto.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void obtenerExpediente(String[] parametros) {
		if(parametros.length!=3) {
			// Número de argumentos incorrecto
			Errores.errorComando(Errores.EXPEDIENTE_ALUMNO, "Número de argumentos incorrecto");
			return;
		}
		
		String dniAlumno = parametros[1];
		String nombreSalida = parametros[2];
		
		// Existe el alumno?
		if(!Arranque.alumnos.containsKey(dniAlumno)) {
			Errores.errorComando(Errores.EXPEDIENTE_ALUMNO, "Alumno inexistente");
			return;
		}
		
		// Escribe el fichero con las notas obtenidas por el alumno en todas sus asignaturas aprobadas
		try {
			float sumaNotas = 0; int asignaturas = 0;
			BufferedWriter bw = new BufferedWriter(new FileWriter(nombreSalida, false));
			ArrayList<Alumno.AsignaturaSuperada> superadas = new ArrayList<Alumno.AsignaturaSuperada>(Arranque.alumnos.get(dniAlumno).getAsignaturasSuperadas());
			Collections.sort(superadas); // ordena las asignaturas aprobadas

			for(Alumno.AsignaturaSuperada as: superadas) {
				// Recorre todas las asignaturas aprobadas por el alumno
				asignaturas++;
				sumaNotas += (as.getNota());
				String linea = as.getCurso() + "; " + as.getSiglas() + "; " + as.getNota() + "; " + as.getCursoAcademico();
				bw.write(linea);
				bw.newLine();
			}
			
			// Nota media del expediente
			float notaMedia = sumaNotas/asignaturas;
			DecimalFormat formato = new DecimalFormat("#.00");
			bw.write("Nota media del expediente: " + formato.format(notaMedia));
			bw.newLine();
			
			bw.close();
			
		}catch(IOException e) {
			// Error en la escritura del fichero
			System.out.println("Error al escribir en el fichero. Se aborta la operación.");
			return;
		}
		
	}
		
	/**
	 * Genera el calendario de ocupación semanal un aula.
	 * Si se especifica * como aula, se mostrará el calendario de todas.
	 * La salida se realiza por pantalla.
	 * @param parametros El comando de ejecución separado en parámetros
	 */
	private static void obtenerOcupacionAula(String[] parametros) {
		if(parametros.length!=2) {
			// Número de argumentos incorrecto?
			Errores.errorComando(Errores.CALENDARIO_OCUPACION_AULA, "Número de argumentos incorrecto");
			return;
		}
		
		String siglasAula = parametros[1];
		
		if(siglasAula.equals("*")) {
			// Se genera el calendario de todas las aulas
			for(Aula aula: Arranque.aulas.values()) {
				aula.obtenerCalendarioOcupacion();
				System.out.println(); System.out.println(); // doble salto de línea
			}
		}else {
			// Se genera solo el calendario del aula especificada
			// Existe el aula?
			if(!Arranque.aulas.containsKey(siglasAula)) {
				// No existe el aula
				Errores.errorComando(Errores.CALENDARIO_OCUPACION_AULA, "No existe el aula " + siglasAula);
				return;
			}
			Arranque.aulas.get(siglasAula).obtenerCalendarioOcupacion();
		}
		
		
	}
	
	
}
