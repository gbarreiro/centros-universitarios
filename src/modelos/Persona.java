package modelos;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Clase que modela a una persona. No se pueden crear objetos de Persona (clase abstracta), sino de Alumno o Profesor.
 * Proporciona una serie de atributos y métodos comunes a sus dos clases hijas: Alumno y Profesor.
 * @author Guillermo Barreiro Fernández
 *
 */
public abstract class Persona implements EscribibleEnFichero {

	private String dni;
	private String nombre;
	private GregorianCalendar fechaNacimiento;
	private String email;
	
	/**
	 * Permite procesar las fechas en el formato d/M/yyyy; p.ej: 18/9/1998, 3/12/2011, 04/1/2010
	 */
	protected final static DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
	
	/**
	 * Constructor de la clase Persona. No se pueden instanciar objetos de esta clase,
	 * por lo que solo se usa de forma interna en los constructores de sus clases hijas.
	 * @param dni DNI de la persona, con ocho dígitos y una letra
	 * @param nombre Nombre y apellidos de la persona
	 * @param fechaNacimiento Fecha de nacimiento en formato d/M/yyyy
	 */
	public Persona(String dni,String nombre,String fechaNacimiento) {
		this.dni=dni;
		this.nombre=nombre;
		this.fechaNacimiento= fechaToGregorianCalendar(fechaNacimiento);
	}
	
	/**
	 * Constructor de la clase Persona. No se pueden instanciar objetos de esta clase,
	 * por lo que solo se usa de forma interna en los constructores de sus clases hijas.
	 * @param dni DNI de la persona, con ocho dígitos y una letra
	 * @param nombre Nombre y apellidos de la persona
	 * @param email Email de la persona
	 * @param fechaNacimiento Fecha de nacimiento en formato d/M/yyyy
	 */
	public Persona(String dni,String nombre,String email,String fechaNacimiento) {
		this.dni=dni;
		this.nombre=nombre;
		this.fechaNacimiento= fechaToGregorianCalendar(fechaNacimiento);
		this.email=email;
	}
	
	/**
	 * Devuelve el email de la persona, o una cadena vacía si no tiene uno asignado.
	 * @return Email de la persona
	 */
	public String getEmail() {
		if(email!=null) return email;
		else return " ";
	}
	
	/**
	 * Devuelve el DNI de la persona.
	 * @return DNI
	 */
	public String getDni() {
		return dni;
	}
	
	/**
	 * Devuelve el nombre y los apellidos de la persona, todo en una misma cadena.
	 * @return Nombre y apellidos
	 */
	public String getNombre() {
		return nombre;
	}
	
	/**
	 * Devuelve la fecha de nacimiento de la persona como un GregorianCalendar.
	 * @return GregorianCalendar con la fecha de nacimiento.
	 */
	public GregorianCalendar getFechaNacimiento() {
		return fechaNacimiento;
	}
	
	/**
	 * Devuelve la fecha de nacimiento de la persona como una cadena de texto,
	 * en formato d/M/yyyy.
	 * @return Fecha de nacimiento formateada
	 */
	public String getFechaNacimientoFormateada() {
		return dateFormat.format(this.fechaNacimiento.getTime());
	}
	
	// MÉTODOS AUXILIARES:
	
	/**
	 * Comprueba si un DNI es válido o no. 
	 * Para ello verifica su longitud y que sus ocho primeros dígitos sean numéricos.
	 * @param dni Número y letra de DNI
	 * @return true si es válido, false si no lo es.
	 */
	public static boolean comprobarDNI(String dni) {
		// Longitud correcta?
		if(dni.length()!=9) return false;
		
		// 8 primeros caracteres numericos?
		for (int i=0;i<8;i++) {
			if (!Character.isDigit(dni.charAt(i))) return false;
		}
		
		// Último dígito letra en mayúsculas?
		if(!Character.isUpperCase(dni.charAt(8))) return false;

		// Si cumple los tres requisitos anteriores:
		return true;
	}
	
	/**
	 * Comprueba si una fecha de nacimiento es válida o no.
	 * Para ello verifica que su formato sea correcto y que esté acotada entre 1960 y 2018.
	 * @param fecha Fecha en formato d/M/yyyy
	 * @return true si es correcta, false si no lo es.
	 */
	public static boolean comprobarFechaNacimiento(String fecha) {
		
		try {
			dateFormat.setLenient(false); // desactivamos el lenient para poder comprobar si una fecha no existe (p.ej.: 35 de abril)
			Date date = dateFormat.parse(fecha); // transforma la fecha de formato String a Date

			// fecha acotada?
			Date fechaMinima = dateFormat.parse("01/01/1960");
			Date fechaMaxima = dateFormat.parse("01/01/2018");
			if(date.before(fechaMinima) || date.after(fechaMaxima)) return false;
			
		} catch (ParseException e) {
			
			//Si hay un error al convertir la fecha es que su formato es incorrecto o la fecha es incorrecta
			return false;
		}
		
		// Si se ha llegado hasta aquí es que la fecha tiene el formato correcto
		return true;
		
	}
	
	/**
	 * Comprueba que la fecha de ingreso sea correcta, y que sea en una edad entre los 16 y los 60 años.
	 * @param fechaNacimiento Fecha de nacimiento formateada
	 * @param fechaIngreso Fecha de ingreso formateada
	 * @return true si es correcta, false si no lo es.
	 */
	public static boolean comprobarFechaIngreso(String fechaNacimiento, String fechaIngreso) {

		try {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
			LocalDate nacimiento  =LocalDate.parse(fechaNacimiento,fmt);
			LocalDate ingreso = LocalDate.parse(fechaIngreso,fmt);
			
			Period periodo = Period.between(nacimiento, ingreso);
			if(periodo.getYears() < 16 || periodo.getYears() > 60) {
				return false;
			}
			
		} catch (DateTimeParseException e) {
			return false; // el formato de la fecha era incorrecto
		}
		
		// Si se ha llegado hasta aquí es que la fecha tiene el formato correcto
		return true;
		
	}
	
	/**
	 * Convierte una fecha formateada a un GregorianCalendar.
	 * @param fecha Fecha formateada. Es necesario comprobarla antes con {@link #comprobarFechaNacimiento(String)} de convertirla.
	 * @return GregorianCalendar con la fecha formateada.
	 */
	protected static GregorianCalendar fechaToGregorianCalendar(String fecha) {
		try {
			Date date = dateFormat.parse(fecha);
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			return gc;
		} catch (ParseException e) {
			// Deberia de haberse comprobado previamente con #comprobarFecha()
			return null;
		}

	}
}
