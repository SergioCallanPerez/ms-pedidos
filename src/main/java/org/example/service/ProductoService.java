package org.example.service;

import org.example.dto.DetallePedidoDTO;
import org.example.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductoService {
    private final WebClient webClient;

    public ProductoService(WebClient webClient){
        this.webClient= webClient;
    }

    public Mono<ProductoDTO> obtenerProducto(Long id){
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(ProductoDTO.class);
    }

    public Mono<ProductoDTO> actualizarStock(Long id, Integer cantidad) {
        DetallePedidoDTO body = new DetallePedidoDTO();
        body.setProductoId(id);
        body.setCantidad(cantidad);

        return webClient.patch()
                .uri("/{id}/stock", id)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        "Error al actualizar stock: " + response.statusCode() + " - " + errorBody
                                )))
                )
                .bodyToMono(ProductoDTO.class);
    }

}
