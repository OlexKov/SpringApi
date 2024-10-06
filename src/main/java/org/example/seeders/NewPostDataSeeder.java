package org.example.seeders;

import lombok.RequiredArgsConstructor;
import org.example.interfaces.repositories.IAreaRepository;
import org.example.services.NewPostApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@RequiredArgsConstructor
public class NewPostDataSeeder implements CommandLineRunner {

    private final IAreaRepository areaRepository;
    private final NewPostApiService newPostApiService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(areaRepository.count() == 0) {
            newPostApiService.saveToDataBase(newPostApiService.getData(),false);
        }
    }
}
