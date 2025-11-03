package org.example.repository;

import org.example.entities.DetallePedido;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DetallePedidoRepository extends ReactiveCrudRepository<DetallePedido, Long> {
    Flux<DetallePedido> findByPedidoId(Long pedidoID);
}
