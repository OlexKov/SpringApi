package org.example.interfaces;

import org.example.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    User getByUsername(String useName);
    User getByEmail(String useName);
}
