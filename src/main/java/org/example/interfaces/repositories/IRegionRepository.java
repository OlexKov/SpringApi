package org.example.interfaces.repositories;

import org.example.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRegionRepository extends JpaRepository<Region, String> {
}
