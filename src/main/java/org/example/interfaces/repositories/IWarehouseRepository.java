package org.example.interfaces.repositories;


import org.example.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface IWarehouseRepository extends JpaRepository<Warehouse, String> {
}
