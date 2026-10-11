package com.grupo4.pedido_service_grupo4.controller;

import com.grupo4.pedido_service_grupo4.dto.PedidoResponseDTO;
import com.grupo4.pedido_service_grupo4.dto.ResumenConsolidadoClienteDTO;
import com.grupo4.pedido_service_grupo4.service.PedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Buscar un pedido por su identificador
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Long idPedido) {

        PedidoResponseDTO pedido =
                pedidoService.buscarPorId(idPedido);

        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pedido);
    }

    // Listar pedidos de un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                pedidoService.listarPorCliente(clienteId)
        );
    }

    // Verificar el pago y completar el pedido
    @GetMapping("/verificar-y-completar/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> verificarYCompletar(
            @PathVariable Long idPedido) {

        PedidoResponseDTO pedido =
                pedidoService.verificarYCompletar(idPedido);

        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pedido);
    }

    // Consolidar pedidos y pagos de un cliente
    @GetMapping("/consolidado-cliente/{clienteId}")
    public ResponseEntity<ResumenConsolidadoClienteDTO> consolidarCliente(
            @PathVariable Long clienteId) {

        ResumenConsolidadoClienteDTO resumen =
                pedidoService.consolidarCliente(clienteId);

        return ResponseEntity.ok(resumen);
    }
}
