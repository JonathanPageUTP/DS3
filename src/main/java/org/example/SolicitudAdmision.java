package org.example;
public abstract class SolicitudAdmision {
    protected Estudiante estudiante;

    public SolicitudAdmision(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public abstract boolean validar();
    public abstract void procesar();
    public abstract void imprimir();
}

