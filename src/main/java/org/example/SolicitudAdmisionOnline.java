package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SolicitudAdmisionOnline extends SolicitudAdmision {

    public SolicitudAdmisionOnline(Estudiante estudiante) {
        super(estudiante);
    }

    @Override
    public boolean validar() {
        // Validaciones específicas
        return estudiante.getNombre() != null && estudiante.getApellido() != null && estudiante.getCedula() != null;
    }

    @Override
    public void procesar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si el estudiante ya existe
            String sqlCheckEstudiante = "SELECT COUNT(*) FROM estudiante WHERE id_estudiante = ?";
            PreparedStatement pstmtCheckEstudiante = conn.prepareStatement(sqlCheckEstudiante);
            pstmtCheckEstudiante.setString(1, estudiante.getIdEstudiante());
            ResultSet rsEstudiante = pstmtCheckEstudiante.executeQuery();
            rsEstudiante.next();
            if (rsEstudiante.getInt(1) == 0) {
                // Insertar el estudiante en la tabla estudiante
                String sqlEstudiante = "INSERT INTO estudiante (id_estudiante, nombre, apellido, cedula, sexo, carrera) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmtEstudiante = conn.prepareStatement(sqlEstudiante);
                pstmtEstudiante.setString(1, estudiante.getIdEstudiante());
                pstmtEstudiante.setString(2, estudiante.getNombre());
                pstmtEstudiante.setString(3, estudiante.getApellido());
                pstmtEstudiante.setString(4, estudiante.getCedula());
                pstmtEstudiante.setString(5, estudiante.getSexo());
                pstmtEstudiante.setString(6, estudiante.getCarrera());
                pstmtEstudiante.executeUpdate();
            }

            // Verificar si la solicitud de admisión ya existe
            String sqlCheckSolicitud = "SELECT COUNT(*) FROM solicitud_admision WHERE id_estudiante = ?";
            PreparedStatement pstmtCheckSolicitud = conn.prepareStatement(sqlCheckSolicitud);
            pstmtCheckSolicitud.setString(1, estudiante.getIdEstudiante());
            ResultSet rsSolicitud = pstmtCheckSolicitud.executeQuery();
            rsSolicitud.next();
            if (rsSolicitud.getInt(1) == 0) {
                // Insertar la solicitud de admisión en la tabla solicitud_admision
                String sqlSolicitud = "INSERT INTO solicitud_admision (id_estudiante, estado) VALUES (?, ?)";
                PreparedStatement pstmtSolicitud = conn.prepareStatement(sqlSolicitud);
                pstmtSolicitud.setString(1, estudiante.getIdEstudiante());
                pstmtSolicitud.setString(2, "Pendiente");
                pstmtSolicitud.executeUpdate();
                JOptionPane.showMessageDialog(null, "Solicitud procesada correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "El estudiante ya tiene una solicitud de admisión registrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @Override
    public void imprimir() {
        JOptionPane.showMessageDialog(null, "Información del Estudiante:\n" + estudiante.toString());
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM solicitud_admision WHERE id_estudiante = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, estudiante.getIdEstudiante());
            ResultSet rs = pstmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID Solicitud: ").append(rs.getInt("id"))
                        .append("\nFecha Solicitud: ").append(rs.getTimestamp("fecha_solicitud"))
                        .append("\nEstado: ").append(rs.getString("estado"))
                        .append("\n\n");
            }
            JOptionPane.showMessageDialog(null, "Información de Solicitudes:\n" + sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al recuperar las solicitudes: " + e.getMessage());
        }
    }

    public void evaluar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sqlSolicitud = "SELECT * FROM solicitud_admision WHERE id_estudiante = ?";
            PreparedStatement pstmtSolicitud = conn.prepareStatement(sqlSolicitud);
            pstmtSolicitud.setString(1, estudiante.getIdEstudiante());
            ResultSet rsSolicitud = pstmtSolicitud.executeQuery();
            StringBuilder sbSolicitud = new StringBuilder();
            while (rsSolicitud.next()) {
                sbSolicitud.append("ID Solicitud: ").append(rsSolicitud.getInt("id"))
                        .append("\nFecha Solicitud: ").append(rsSolicitud.getTimestamp("fecha_solicitud"))
                        .append("\nEstado: ").append(rsSolicitud.getString("estado"))
                        .append("\n\n");
            }

            String sqlDetalle = "SELECT * FROM detalle WHERE id_estudiante = ?";
            PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
            pstmtDetalle.setString(1, estudiante.getIdEstudiante());
            ResultSet rsDetalle = pstmtDetalle.executeQuery();
            StringBuilder sbDetalle = new StringBuilder();
            while (rsDetalle.next()) {
                sbDetalle.append("ID Detalle: ").append(rsDetalle.getInt("id_detalle"))
                        .append("\nCalificación: ").append(rsDetalle.getString("calificacion"))
                        .append("\nPuntaje: ").append(rsDetalle.getInt("puntaje"))
                        .append("\n\n");
            }

            JTextArea textArea = new JTextArea(sbSolicitud.toString() + "\nInformación de Detalles:\n" + sbDetalle.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

            int opcion = JOptionPane.showConfirmDialog(null, scrollPane, "Evaluar Admisión", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                String sqlUpdate = "UPDATE solicitud_admision SET estado = 'Aceptada' WHERE id_estudiante = ?";
                PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
                pstmtUpdate.setString(1, estudiante.getIdEstudiante());
                pstmtUpdate.executeUpdate();
                JOptionPane.showMessageDialog(null, "Solicitud aceptada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al evaluar la solicitud: " + e.getMessage());
        }
    }

    public void pagarMatricula() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String montoPagoStr = JOptionPane.showInputDialog("Ingrese el monto del pago de matrícula:");
            double montoPago = Double.parseDouble(montoPagoStr);

            String sqlUpdate = "UPDATE estudiante SET pago = TRUE, monto_pago = ? WHERE id_estudiante = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setDouble(1, montoPago);
            pstmtUpdate.setString(2, estudiante.getIdEstudiante());
            pstmtUpdate.executeUpdate();
            JOptionPane.showMessageDialog(null, "Pago de matrícula realizado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al realizar el pago: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Monto de pago inválido.");
        }
    }

    public static void mostrarEstudiantes(String filtro) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT estudiante.id_estudiante, estudiante.nombre, solicitud_admision.estado, estudiante.monto_pago FROM estudiante " +
                    "JOIN solicitud_admision ON estudiante.id_estudiante = solicitud_admision.id_estudiante " +
                    "WHERE solicitud_admision.estado = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filtro);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID Estudiante: ").append(rs.getString("id_estudiante"))
                        .append("\nNombre: ").append(rs.getString("nombre"))
                        .append("\nEstado de Admisión: ").append(rs.getString("estado"))
                        .append("\nMonto de Pago: ").append(rs.getDouble("monto_pago"))
                        .append("\n\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "Estudiantes", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al recuperar los estudiantes: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        while (true) {
            String[] options = {"Crear nueva admisión", "Evaluar admisión", "Mostrar estudiantes (aceptados)", "Mostrar estudiantes (pendientes)", "Pagar matrícula", "Salir"};
            int choice = JOptionPane.showOptionDialog(null, "Seleccione una opción:", "Menú", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            switch (choice) {
                case 0:
                    String nombre = JOptionPane.showInputDialog("Ingrese el nombre:");
                    String apellido = JOptionPane.showInputDialog("Ingrese el apellido:");
                    String cedula = JOptionPane.showInputDialog("Ingrese la cédula:");
                    String sexo = JOptionPane.showInputDialog("Ingrese el sexo (M/F):");
                    String idEstudiante = JOptionPane.showInputDialog("Ingrese el ID del estudiante:");
                    String carrera = JOptionPane.showInputDialog("Ingrese la carrera:");

                    Estudiante estudiante = new Estudiante(nombre, apellido, cedula, sexo, idEstudiante, carrera);
                    SolicitudAdmisionOnline solicitud = new SolicitudAdmisionOnline(estudiante);

                    if (solicitud.validar()) {
                        solicitud.procesar();
                    } else {
                        JOptionPane.showMessageDialog(null, "Datos inválidos, por favor verifique la información.");
                    }
                    break;
                case 1:
                    idEstudiante = JOptionPane.showInputDialog("Ingrese el ID del estudiante para evaluar la admisión:");
                    // Asumiendo que puedes recuperar la información del estudiante de alguna manera
                    estudiante = obtenerEstudiantePorId(idEstudiante);
                    if (estudiante != null) {
                        solicitud = new SolicitudAdmisionOnline(estudiante);
                        solicitud.evaluar();
                    } else {
                        JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
                    }
                    break;
                case 2:
                    mostrarEstudiantes("Aceptada");
                    break;
                case 3:
                    mostrarEstudiantes("Pendiente");
                    break;
                case 4:
                    idEstudiante = JOptionPane.showInputDialog("Ingrese el ID del estudiante para pagar la matrícula:");
                    estudiante = obtenerEstudiantePorId(idEstudiante);
                    if (estudiante != null) {
                        solicitud = new SolicitudAdmisionOnline(estudiante);
                        solicitud.pagarMatricula();
                    } else {
                        JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
                    }
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.");
            }
        }
    }

    // Método simulado para obtener estudiante por ID (debes implementarlo según tu lógica de negocio)
    private static Estudiante obtenerEstudiantePorId(String idEstudiante) {
        // Aquí debes implementar la lógica para obtener un estudiante de la base de datos utilizando el idEstudiante
        // Este es solo un ejemplo simulado
        return new Estudiante("Simulado", "Estudiante", "X-XX", "F", idEstudiante, "Ingeniería");
    }
}
