
package com.grupo4.pedido_service_grupo4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PedidoServiceGrupo4Application {

    public static void main(String[] args) {
        SpringApplication.run(
                PedidoServiceGrupo4Application.class,
                args
        );
    }
}
