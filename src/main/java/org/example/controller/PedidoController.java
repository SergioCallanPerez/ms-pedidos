package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.EstadoPedidoDTO;
import org.example.dto.PedidoRequestDTO;
import org.example.dto.PedidoResponseDTO;
import org.example.entities.Pedido;
import org.example.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PedidoResponseDTO> crearPedido(@RequestBody PedidoRequestDTO dto) {
        return pedidoService.crearPedido(dto);
    }

    @GetMapping
    public Flux<Pedido> obtenerPedidos() {
        return pedidoService.obtenerPedidos();
    }

    @GetMapping("/{id}")
    public Mono<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPedidoPorId(id);
    }

    @PutMapping("/{id}/estado")
    public Mono<Pedido> actualizarEstado(
            @PathVariable Long id,
            @RequestBody EstadoPedidoDTO estadoDTO) {
        return pedidoService.actualizarEstado(id, estadoDTO.getEstado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id) {
        return pedidoService.eliminarPedido(id);
    }
}
