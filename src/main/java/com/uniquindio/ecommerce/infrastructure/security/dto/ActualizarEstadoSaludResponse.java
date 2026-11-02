package com.uniquindio.ecommerce.infrastructure.security.dto;

public class ActualizarEstadoSaludResponse {

    private String idEjemplar;
    private String estadoSalud;
    private boolean disponibleParaVenta;
    private String mensaje;

    public ActualizarEstadoSaludResponse(String idEjemplar, String estadoSalud, boolean disponibleParaVenta, String mensaje) {
        this.idEjemplar = idEjemplar;
        this.estadoSalud = estadoSalud;
        this.disponibleParaVenta = disponibleParaVenta;
        this.mensaje = mensaje;
    }

    public String getIdEjemplar() {
        return idEjemplar; }

    public String getEstadoSalud() {
        return estadoSalud; }

    public boolean isDisponibleParaVenta() {
        return disponibleParaVenta; }

    public String getMensaje() {
        return mensaje; }

}
