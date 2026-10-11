package com.grupo4.pedido_service_grupo4;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"ecom.impuesto-igv=0.18",
		"ecom.monto-minimo-envio-gratis=150.00",
		"ecom.mensaje-bienvenida=Bienvenido (prueba)",
		"spring.datasource.url=jdbc:h2:mem:prueba"
})
class PedidoServiceGrupo4ApplicationTests {

	@Test
	void contextLoads() {
	}

}
