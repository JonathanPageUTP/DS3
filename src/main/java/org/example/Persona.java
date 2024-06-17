package org.example;
public abstract class Persona {
    protected String nombre;
    protected String apellido;
    protected String cedula;
    protected String sexo;

    public Persona(String nombre, String apellido, String cedula, String sexo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.sexo = sexo;
    }

    // Métodos comunes
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public String getSexo() {
        return sexo;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + "\nApellido: " + apellido + "\nCédula: " + cedula + "\nSexo: " + sexo;
    }
}
