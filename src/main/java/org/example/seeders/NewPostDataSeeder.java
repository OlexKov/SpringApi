package org.example.seeders;

import lombok.RequiredArgsConstructor;
import org.example.entities.Area;
import org.example.interfaces.repositories.IAreaRepository;
import org.example.interfaces.repositories.IRegionRepository;
import org.example.interfaces.repositories.ISettlementRepository;
import org.example.interfaces.repositories.IWarehouseRepository;
import org.example.mapping.AreaModelMapper;
import org.example.mapping.RegionMadelMapper;
import org.example.mapping.SettlementModelMapper;
import org.example.mapping.WarehouseModelMapper;
import org.example.models.newpost.AreaModel;
import org.example.models.newpost.RegionModel;
import org.example.models.newpost.SettlementModel;
import org.example.models.newpost.WarehouseModel;
import org.example.services.NewPostApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class NewPostDataSeeder implements CommandLineRunner {
    private final ISettlementRepository settlementRepository;
    private final IAreaRepository areaRepository;
    private final IRegionRepository regionRepository;
    private final NewPostApiService newPostApiService;
    private final AreaModelMapper areaModelMapper;
    private final SettlementModelMapper settlementModelMapper;
    private final WarehouseModelMapper warehouseModelMapper;
    private final RegionMadelMapper regionModelMapper;
    @Override
    public void run(String... args) throws Exception {
        List<AreaModel> areas = new ArrayList<>();
        List<SettlementModel> settlements = new ArrayList<>();
        List<WarehouseModel> warehouses = new ArrayList<>();
        List<RegionModel> regions = new ArrayList<>();
        if(areaRepository.count() == 0){
            System.out.println("Завантаження даних Нової Пошти");
            areas = newPostApiService.getAreas();
            System.out.println("Області - " + areas.size());
            regions = newPostApiService.getRegions(areas.stream().map(AreaModel::getRef).toList());
            System.out.println("Райони - " + regions.size());
            settlements = newPostApiService.getSettlements();
            System.out.println("Населені пункти - " + settlements.size());
            warehouses = newPostApiService.getWarehouses();
            System.out.println("Відділення - " + warehouses.size());


            List<Area> areasEntities = new ArrayList<>();
            System.out.println("Підготовка даних до сиду");
            for (var item:areas){
                var areaEntity = areaModelMapper.fromModel(item);
                var areaRegionsModels =  regions.stream().filter(x-> Objects.equals(x.getArea(), item.getRef())).toList();
                var areaRegionsEntities = regionModelMapper.fromModel(areaRegionsModels);
                System.out.println(item.getDescription() + ": Районів - " + areaRegionsEntities.size());
                for (var areaRegionEntity:areaRegionsEntities){
                    areaRegionEntity.setArea(areaEntity);
                    var regionSettlements = settlements.stream().filter(x-> Objects.equals(x.getRegion(), areaRegionEntity.getId())).toList();
                    var regionSettlementsEntities= settlementModelMapper.fromModel(regionSettlements);
                    for(var regionSettlementsEntity:regionSettlementsEntities){
                      var settlementWarehouses =  warehouses.stream().filter(x-> Objects.equals(x.getSettlement(), regionSettlementsEntity.getId())).toList();
                      var settlementWarehouseEntities = warehouseModelMapper.fromModel(settlementWarehouses);
                      for (var settlementWarehouseEntity:settlementWarehouseEntities){
                          settlementWarehouseEntity.setSettlement(regionSettlementsEntity);
                      }
                        regionSettlementsEntity.setWarehouses(settlementWarehouseEntities);
                    }
                    areaRegionEntity.setSettlements(regionSettlementsEntities);
                }
                areaEntity.setRegions(areaRegionsEntities);
                areasEntities.add(areaEntity);
            }
            System.out.println("Cид даних Нової Пошти");
            areasEntities =  areaRepository.saveAllAndFlush(areasEntities);
            for(var area:areasEntities){
                var areaCenterIdOpt = Objects.requireNonNull(areas.stream().filter(x -> Objects.equals(x.getRef(), area.getId())).findFirst());
                if(areaCenterIdOpt.isPresent()){
                    System.out.println(areaCenterIdOpt.get().getAreasCenter());
                    var areaCenterOpt = settlementRepository.findById(areaCenterIdOpt.get().getAreasCenter());
                    areaCenterOpt.ifPresent(area::setAreaCenter);
                }
            }
            areaRepository.saveAll(areasEntities);
            var regionsEntities = regionRepository.findAll();
            for(var regionEntity:regionsEntities){
                var regionCenterIdOpt = Objects.requireNonNull(regions.stream().filter(x -> Objects.equals(x.getRef(), regionEntity.getId())).findFirst());
                if(regionCenterIdOpt.isPresent()){
                    System.out.println(regionCenterIdOpt.get().getRegionCenter());
                    var areaCenterOpt = settlementRepository.findById(regionCenterIdOpt.get().getRegionCenter());
                    areaCenterOpt.ifPresent(regionEntity::setRegionCenter);
                }
            }
            regionRepository.saveAll(regionsEntities);
            System.out.println("Cид даних Нової Пошти завершено" );
        }
    }
}
