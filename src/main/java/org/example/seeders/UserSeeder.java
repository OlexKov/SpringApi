package org.example.seeders;

import lombok.RequiredArgsConstructor;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.interfaces.IStorageService;
import org.example.interfaces.IUserRepository;
import org.example.interfaces.IUserRolesRepository;
import org.example.models.FileFormats;
import org.example.models.Roles;
import org.springframework.boot.CommandLineRunner;
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
            System.out.println("Сид ролей!");
            List<UserRole> userRoles = new ArrayList<>();
            for (var role:roles){
                userRoles.add(new UserRole(0L,role.toString(),List.of()));
            }
            roleRepo.saveAll(userRoles);
            System.out.println("Сид ролей завершено!");
        }
        if(userRepo.count()==0){
            System.out.println("Сид юзерів!");
            var adminRole = roleRepo.getByName(Roles.Admin.toString());
            var userRole = roleRepo.getByName(Roles.User.toString());
            var users = new ArrayList<>(Arrays.asList(
                    new User(
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
                    ),
                    new User(
                            0L,
                            "Ivan",
                            passwordEncoder.encode("Ivan_1"),
                            "Іван",
                            "Вознюк",
                            "Ivan@gmail.com",
                            new GregorianCalendar(2000, Calendar.APRIL, 1).getTime(),
                            storageService.saveImage("https://picsum.photos/800/600", FileFormats.WEBP),
                            true,true,true,true,
                            List.of(userRole)
                    )));
            userRepo.saveAll(users);
            System.out.println("Сид юзерів завершено!");
        }

    }
}
