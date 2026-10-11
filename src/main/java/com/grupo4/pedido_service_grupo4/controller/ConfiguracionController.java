package com.grupo4.pedido_service_grupo4.controller;

import com.grupo4.pedido_service_grupo4.dto.ConfiguracionDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/api/v1/pedidos")
public class ConfiguracionController {

    @Value("${ecom.impuesto-igv}")
    private String impuestoIgv;

    @Value("${ecom.monto-minimo-envio-gratis}")
    private String montoMinimoEnvioGratis;

    @Value("${ecom.mensaje-bienvenida}")
    private String mensajeBienvenida;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    // Mostrar la configuracion obtenida del Config Server
    @GetMapping("/configuracion")
    public ResponseEntity<ConfiguracionDTO> obtenerConfiguracion() {

        ConfiguracionDTO configuracion = new ConfiguracionDTO(
                impuestoIgv,
                montoMinimoEnvioGratis,
                mensajeBienvenida,
                datasourceUrl
        );

        return ResponseEntity.ok(configuracion);
    }
}
