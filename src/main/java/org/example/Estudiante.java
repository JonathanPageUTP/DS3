package org.example;
public class Estudiante extends Persona {
    private String idEstudiante;
    private String carrera;

    public Estudiante(String nombre, String apellido, String cedula, String sexo, String idEstudiante, String carrera) {
        super(nombre, apellido, cedula, sexo);
        this.idEstudiante = idEstudiante;
        this.carrera = carrera;
    }

    // Métodos específicos
    public String getIdEstudiante() {
        return idEstudiante;
    }

    public String getCarrera() {
        return carrera;
    }

    @Override
    public String toString() {
        return super.toString() + "\nID Estudiante: " + idEstudiante + "\nCarrera: " + carrera;
    }
}
