package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.entities.*;
import org.example.exception.R2dbcExceptionUtil;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoService productoService;

    public Mono<PedidoResponseDTO> crearPedido(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCliente(dto.getCliente());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        return Flux.fromIterable(dto.getDetalles())
                .flatMap(detalle -> productoService.obtenerProducto(detalle.getProductoId())
                        .flatMap(producto -> {
                            //Revision de la cantidad en stock
                            if (producto.getStock() < detalle.getCantidad()) {
                                return Mono.error(new RuntimeException(
                                        "Stock insuficiente para el producto: " + producto.getNombre()));
                            }

                            // Calcular subtotal
                            Double subtotal = producto.getPrecio() * detalle.getCantidad();
                            // Tomar datos desde producto y la cantidad del mismo
                            DetallePedido det = new DetallePedido();
                            det.setProductoId(producto.getId());
                            det.setCantidad(detalle.getCantidad());
                            det.setPrecioUnitario(producto.getPrecio());

                            return Mono.just(new Object[]{det, subtotal});
                        })
                )
                .collectList()
                .flatMap(lista -> {
                    //Calculo del total
                    double total = lista.stream()
                            .mapToDouble(o -> (Double) o[1])
                            .sum();
                    pedido.setTotal(total);
                    return pedidoRepository.save(pedido)
                            .flatMap(saved -> Flux.fromIterable(lista)
                                    .flatMap(o -> {
                                        DetallePedido det = (DetallePedido) o[0];
                                        det.setPedidoId(saved.getId());
                                        return detallePedidoRepository.save(det)
                                                .thenReturn(det);
                                    })
                                    .collectList()
                                    .map(detallesGuardados -> {
                                        PedidoResponseDTO response = new PedidoResponseDTO();
                                        response.setId(saved.getId());
                                        response.setCliente(saved.getCliente());
                                        response.setFecha(saved.getFecha());
                                        response.setTotal(saved.getTotal());
                                        response.setEstado(saved.getEstado());
                                        response.setDetalles(
                                                detallesGuardados.stream().map(d -> {
                                                    DetallePedidoDTO ddto = new DetallePedidoDTO();
                                                    ddto.setProductoId(d.getProductoId());
                                                    ddto.setCantidad(d.getCantidad());
                                                    return ddto;
                                                }).toList()
                                        );
                                        return response;
                                    })
                            );
                })
                .onErrorMap(R2dbcExceptionUtil::handleR2dbcException);
    }

    public Flux<Pedido> obtenerPedidos() {
        return pedidoRepository.findAll();
    }

    public Mono<PedidoResponseDTO> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .flatMap(pedido -> detallePedidoRepository.findByPedidoId(id)
                        .map(det -> {
                            DetallePedidoDTO ddto = new DetallePedidoDTO();
                            ddto.setProductoId(det.getProductoId());
                            ddto.setCantidad(det.getCantidad());
                            return ddto;
                        })
                        .collectList()
                        .map(detalles -> {
                            PedidoResponseDTO dto = new PedidoResponseDTO();
                            dto.setId(pedido.getId());
                            dto.setCliente(pedido.getCliente());
                            dto.setFecha(pedido.getFecha());
                            dto.setTotal(pedido.getTotal());
                            dto.setEstado(pedido.getEstado());
                            dto.setDetalles(detalles);
                            return dto;
                        }));
    }

    public Mono<Pedido> actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id)
                .flatMap(pedido -> {
                    String estadoAnterior = pedido.getEstado();
                    //Validacion del cambio a PROCESADO
                    if (!"PROCESADO".equalsIgnoreCase(estadoAnterior)
                            && "PROCESADO".equalsIgnoreCase(nuevoEstado)) {

                        // Verificar stock
                        return detallePedidoRepository.findByPedidoId(id)
                                .flatMap(detalle ->
                                        productoService.obtenerProducto(detalle.getProductoId())
                                                .flatMap(producto -> {
                                                    if (producto.getStock() < detalle.getCantidad()) {
                                                        return Mono.error(new RuntimeException(
                                                                "No hay stock suficiente para el producto: " + producto.getNombre()));
                                                    }
                                                    return Mono.just(producto);
                                                })
                                )
                                .collectList()
                                .flatMap(productos -> {
                                    // Actualizar tanto stock como estado
                                    pedido.setEstado(nuevoEstado);
                                    return pedidoRepository.save(pedido)
                                            .flatMap(saved ->
                                                    detallePedidoRepository.findByPedidoId(id)
                                                            .flatMap(detalle ->
                                                                    productoService.obtenerProducto(detalle.getProductoId())
                                                                            .flatMap(producto -> {
                                                                                int nuevoStock = producto.getStock() - detalle.getCantidad();
                                                                                return productoService.actualizarStock(producto.getId(), nuevoStock);
                                                                            })
                                                            )
                                                            .then(Mono.just(saved))
                                            );
                                });
                    } else {
                        // Solo actualizar estado si no es PROCESADO
                        pedido.setEstado(nuevoEstado);
                        return pedidoRepository.save(pedido);
                    }
                });
    }

    public Mono<Void> eliminarPedido(Long id) {
        return detallePedidoRepository.findByPedidoId(id)
                .flatMap(det -> detallePedidoRepository.deleteById(det.getId()))
                .then(pedidoRepository.deleteById(id));
    }
}
