package org.example.seeders;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.interfaces.IStorageService;
import org.example.interfaces.IUserRepository;
import org.example.interfaces.IUserRolesRepository;
import org.example.models.FileFormats;
import org.example.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {
    private final IUserRolesRepository roleRepo;
    private final IUserRepository userRepo;
    private final IStorageService storageService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var roles = Roles.values();
        if(roleRepo.count() < roles.length){
            List<UserRole> userRoles = new ArrayList<>();
            for (var role:roles){
                userRoles.add(new UserRole(0L,role.toString(),List.of()));
            }
            roleRepo.saveAll(userRoles);
        }
        if(userRepo.count()==0){
            var adminRole = roleRepo.getByName(Roles.Admin.toString());
           userRepo.save(new User(
                    0L,
                    "Admin",
                    passwordEncoder.encode("Admin_1"),
                    "Alex",
                    "Fox",
                    "Admin_1@gmail.com",
                    new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime(),
                    storageService.saveImage("https://picsum.photos/800/600", FileFormats.WEBP),
                    true,true,true,true,
                    List.of(adminRole)
            ) );
        }

    }
}
