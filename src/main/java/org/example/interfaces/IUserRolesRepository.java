package org.example.interfaces;
import org.example.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRolesRepository extends JpaRepository<UserRole, Long> {
    UserRole getByName(String name);
}
