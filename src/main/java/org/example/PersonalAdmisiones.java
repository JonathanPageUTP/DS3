package org.example;

public class PersonalAdmisiones extends Persona {
    private String idPersonal;

    public PersonalAdmisiones(String nombre, String apellido, String cedula, String sexo, String idPersonal) {
        super(nombre, apellido, cedula, sexo);
        this.idPersonal = idPersonal;
    }

    // Métodos específicos
    public String getIdPersonal() {
        return idPersonal;
    }
}
