package org.example.repository;

import org.example.entities.Pedido;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends ReactiveCrudRepository<Pedido, Long> {
}
