package com.grupo4.pedido_service_grupo4.dto;

public record ConfiguracionDTO(
        String impuestoIgv,
        String montoMinimoEnvioGratis,
        String mensajeBienvenida,
        String datasourceUrl) {
}
