
package com.grupo4.pedido_service_grupo4.service;

import com.grupo4.pedido_service_grupo4.client.PagoFeignClient;
import com.grupo4.pedido_service_grupo4.dto.DetallePedidoDTO;
import com.grupo4.pedido_service_grupo4.dto.PagoDTO;
import com.grupo4.pedido_service_grupo4.dto.PedidoResponseDTO;
import com.grupo4.pedido_service_grupo4.dto.ResumenConsolidadoClienteDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final Map<Long, PedidoResponseDTO> pedidos;
    private final PagoFeignClient pagoFeignClient;

    public PedidoService(PagoFeignClient pagoFeignClient) {
        this.pagoFeignClient = pagoFeignClient;
        this.pedidos = new HashMap<>();

        // Primer pedido de ejemplo
        List<DetallePedidoDTO> detalles1 = new ArrayList<>();

        detalles1.add(new DetallePedidoDTO(
                1L, "Teclado", 2, 50.00, 100.00
        ));

        detalles1.add(new DetallePedidoDTO(
                2L, "Mouse", 1, 30.00, 30.00
        ));

        PedidoResponseDTO pedido1 = new PedidoResponseDTO(
                1L,
                "PED-001",
                LocalDateTime.now(),
                10L,
                "PENDIENTE",
                detalles1,
                calcularTotal(detalles1)
        );

        pedidos.put(pedido1.getIdPedido(), pedido1);

        // Segundo pedido de ejemplo
        List<DetallePedidoDTO> detalles2 = new ArrayList<>();

        detalles2.add(new DetallePedidoDTO(
                3L, "Monitor", 1, 450.00, 450.00
        ));

        PedidoResponseDTO pedido2 = new PedidoResponseDTO(
                2L,
                "PED-002",
                LocalDateTime.now(),
                10L,
                "PENDIENTE",
                detalles2,
                calcularTotal(detalles2)
        );

        pedidos.put(pedido2.getIdPedido(), pedido2);
    }

    // Calcular el total del pedido
    public Double calcularTotal(List<DetallePedidoDTO> detalles) {
        return detalles.stream()
                .mapToDouble(detalle ->
                        detalle.getCantidad() * detalle.getPrecioUnitario())
                .sum();
    }

    // Buscar un pedido por su identificador
    public PedidoResponseDTO buscarPorId(Long idPedido) {
        PedidoResponseDTO pedido = pedidos.get(idPedido);

        if (pedido == null) {
            return null;
        }

        // Recalcular el total a partir de los detalles
        pedido.setMontoTotalCalculado(
                calcularTotal(pedido.getListaDetalles())
        );

        return pedido;
    }

    // Listar pedidos de un cliente
    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        return pedidos.values().stream()
                .filter(pedido ->
                        pedido.getClienteId().equals(clienteId))
                .toList();
    }

    // Actualizar manualmente el estado de un pedido
    public PedidoResponseDTO actualizarEstado(
            Long idPedido, String nuevoEstado) {

        PedidoResponseDTO pedido = pedidos.get(idPedido);

        if (pedido != null) {
            pedido.setEstadoPedido(nuevoEstado);
        }

        return pedido;
    }

    // Verificar el pago y completar el pedido
    public PedidoResponseDTO verificarYCompletar(Long idPedido) {

        PedidoResponseDTO pedido = buscarPorId(idPedido);

        if (pedido == null) {
            return null;
        }

        PagoDTO pago = pagoFeignClient.buscarPagoPorPedido(idPedido);

        if (pago != null
                && pago.getMontoPagado() != null
                && pago.getEstadoPago() != null
                && Double.compare(
                pago.getMontoPagado(),
                pedido.getMontoTotalCalculado()
        ) == 0
                && "APROBADO".equalsIgnoreCase(pago.getEstadoPago())) {

            pedido.setEstadoPedido("COMPLETADO");
        }

        return pedido;
    }

    // Consolidar pedidos y pagos de un cliente
    public ResumenConsolidadoClienteDTO consolidarCliente(Long clienteId) {

        // Obtener los pedidos del cliente
        List<PedidoResponseDTO> historialPedidos =
                listarPorCliente(clienteId);

        // Consultar los pagos del cliente mediante Feign
        List<PagoDTO> historialPagos =
                pagoFeignClient.listarPagosPorCliente(clienteId);

        // Calcular el monto total de los pedidos
        double montoTotal = historialPedidos.stream()
                .mapToDouble(pedido ->
                        calcularTotal(pedido.getListaDetalles()))
                .sum();

        // Verificar que cada pedido tenga un pago aprobado
        // por el importe exacto
        boolean todosPagados = !historialPedidos.isEmpty()
                && historialPedidos.stream().allMatch(pedido ->
                historialPagos.stream().anyMatch(pago ->
                        pago.getIdPedido() != null
                                && pago.getIdPedido().equals(
                                pedido.getIdPedido())
                                && pago.getMontoPagado() != null
                                && pago.getEstadoPago() != null
                                && Double.compare(
                                pago.getMontoPagado(),
                                calcularTotal(
                                        pedido.getListaDetalles())
                        ) == 0
                                && "APROBADO".equalsIgnoreCase(
                                pago.getEstadoPago())
                )
        );

        // Construir el resumen del cliente
        ResumenConsolidadoClienteDTO resumen =
                new ResumenConsolidadoClienteDTO();

        resumen.setClienteId(clienteId);

        resumen.setNombreCliente(
                clienteId.equals(10L)
                        ? "Cliente de prueba"
                        : "Cliente " + clienteId
        );

        resumen.setTotalPedidosRealizados(historialPedidos.size());
        resumen.setMontoTotalAcumulado(montoTotal);
        resumen.setHistorialPedidos(historialPedidos);
        resumen.setHistorialPagos(historialPagos);
        resumen.setEstadoCuenta(
                todosPagados ? "AL_DIA" : "CON_DEUDAS"
        );

        return resumen;
    }
}
