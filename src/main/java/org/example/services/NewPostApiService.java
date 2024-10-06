package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.entities.Area;
import org.example.entities.Region;
import org.example.entities.Settlement;
import org.example.entities.Warehouse;
import org.example.interfaces.INewPostApiService;
import org.example.mapping.AreaModelMapper;
import org.example.mapping.RegionMadelMapper;
import org.example.mapping.SettlementModelMapper;
import org.example.mapping.WarehouseModelMapper;
import org.example.models.newpost.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NewPostApiService implements INewPostApiService {
    @Value("${novaposhta.api.url}")
    private String newPostApiUrl;
    @Value("${novaposhta.api.key}")
    private String newPostApiKey;
    private final AreaModelMapper areaModelMapper;
    private final SettlementModelMapper settlementModelMapper;
    private final WarehouseModelMapper warehouseModelMapper;
    private final RegionMadelMapper regionModelMapper;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<AreaModel> getAreas() throws JsonProcessingException {
        List<AreaModel> areas = new ArrayList<>();
        var jsonResponse = newPostRequest("Address","getSettlementAreas",1,200);
        var response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<AreaModel>>() {});
        if(response != null && response.isSuccess() && !response.getData().isEmpty()){
            areas = new ArrayList<>(response.getData().parallelStream()
                    .collect(Collectors.toMap(
                            AreaModel::getRef,
                            area ->area,
                            (existing, replacement) -> existing
                    )).values());
        }
        return areas;
    }

    @Override
    public List<WarehouseModel> getWarehouses() throws JsonProcessingException, ExecutionException {
        List< WarehouseModel> result = new ArrayList<>();
        int page = 1;
        while(true) {
            NewPostResponse<WarehouseModel> response = null;
            try {
                var jsonResponse = newPostRequest("Address", "getWarehouses", page++, 1000);
                response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<WarehouseModel>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                result.addAll(response.getData());
            }
            else{
                break;
            }
        }
        return new ArrayList<>(result.parallelStream()
                .collect(Collectors.toMap(
                        WarehouseModel::getRef,
                        area ->area,
                        (existing, replacement) -> existing
                )).values());
    }

    @Override
    public List<SettlementModel> getSettlements() throws JsonProcessingException, ExecutionException {
        List<SettlementModel> result = new  ArrayList<>();
        int page = 1;
        while(true){
            NewPostResponse<SettlementModel> response = null;
            try {
                var jsonResponse = newPostRequest("Address", "getSettlements", page++, 200);
                response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<SettlementModel>>(){});
            } catch (JsonProcessingException  e) {
                throw new RuntimeException(e);
            }
            if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                result.addAll(response.getData());
            }
            else{
                break;
            }
        }
        return  new ArrayList<>(result.parallelStream()
                .collect(Collectors.toMap(
                        SettlementModel::getRef,
                        area ->area,
                        (existing, replacement) -> existing
                )).values());
    }

    @Override
    public List<RegionModel> getRegions(Set<String> areasIds) throws JsonProcessingException, ExecutionException {
        ConcurrentMap<String, RegionModel> resultMap = new ConcurrentHashMap<>();
        areasIds.parallelStream().forEach(areaId-> {
            try {
                String jsonResponse = newPostRequest("Address", "getSettlementCountryRegion", 1, 200, areaId);
                var response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<RegionModel>>() {
                });
                if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                    var regions = response.getData();
                    regions.parallelStream().forEach(region -> {
                        region.setArea(areaId);
                        resultMap.putIfAbsent(region.getRef(),region);
                    });
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        });
        return  new ArrayList<>(resultMap.values());
    }

    @Override
    public NewPostData getData() throws JsonProcessingException, ExecutionException {
        System.out.println("Завантаження даних Нової Пошти");
        final NewPostData data = new NewPostData();
        data.setAreas(getAreas());
        System.out.println("Області - " + data.getAreas().size());
        IntStream.rangeClosed(1,3).parallel().forEach(x->{
            try {
                switch (x){
                    case 1:
                        final var areasIds = data.getAreas().parallelStream().map(AreaModel::getRef).collect(Collectors.toSet());
                        data.setRegions(getRegions(areasIds));
                        System.out.println("Райони - " + data.getRegions().size());
                        break;
                    case 2:
                        data.setSettlements(getSettlements());
                        System.out.println("Населені пункти - " + data.getSettlements().size());
                        break;
                    case 3:
                        data.setWarehouses(getWarehouses());
                        System.out.println("Відділення - " + data.getWarehouses().size());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + x);
                }
            }
            catch (JsonProcessingException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return data;
    }

    @Override
    public void saveToDataBase(NewPostData data, boolean update) throws JsonProcessingException, ExecutionException {
        System.out.println("Підготовка даних до сиду");
        for(var item:data.getAreas()){
            var areaEntity = areaModelMapper.fromModel(item);
            var areaRegionsModels =  data.getRegions().parallelStream().filter(x-> Objects.equals(x.getArea(), item.getRef())).toList();
            var areaRegionsEntities = new HashSet<Region>();
            AtomicInteger settlementsCount = new AtomicInteger();
            AtomicInteger warehousesCount = new AtomicInteger();

            areaRegionsModels.parallelStream().forEach(areaRegionModel->{
                var areaRegionEntity = regionModelMapper.fromModel(areaRegionModel);
                areaRegionEntity.setArea(areaEntity);
                var regionSettlementModels = data.getSettlements()
                        .parallelStream()
                        .filter(x->Objects.equals(x.getRegion(), areaRegionEntity.getId()))
                        .toList();
                Set<Settlement> regionSettlementsEntities = new HashSet<>();
                regionSettlementModels.parallelStream().forEach(regionSettlementModel->{
                    var regionSettlementsEntity = settlementModelMapper.fromModel(regionSettlementModel);
                    regionSettlementsEntity.setRegion(areaRegionEntity);
                    var settlementWarehouses =  data.getWarehouses().parallelStream().filter(x-> Objects.equals(x.getSettlement(), regionSettlementsEntity.getId())).toList();
                    var settlementWarehouseEntities = warehouseModelMapper.fromModel(settlementWarehouses);
                    settlementWarehouseEntities.parallelStream().forEach(settlementWarehouseEntity->{
                        settlementWarehouseEntity.setSettlement(regionSettlementsEntity);
                    });
                    regionSettlementsEntity.setWarehouses(settlementWarehouseEntities);
                    warehousesCount.addAndGet(settlementWarehouseEntities.size());
                    regionSettlementsEntities.add(regionSettlementsEntity);

                    if(Objects.equals(regionSettlementsEntity.getId(), areaRegionModel.getRegionCenter())) {
                        areaRegionEntity.setRegionCenter(regionSettlementsEntity);
                    }
                });
                areaRegionEntity.setSettlements(regionSettlementsEntities);
                settlementsCount.addAndGet(regionSettlementsEntities.size());
                areaRegionsEntities.add(areaRegionEntity);
            });
            var areaCenter = settlementModelMapper.fromModel(data.getSettlements().parallelStream().filter(x->Objects.equals(x.getRef(),item.getAreasCenter())).findFirst().orElse(null));//??
            areaEntity.setAreaCenter(areaCenter);//???
            areaEntity.setRegions(areaRegionsEntities);
            if(update){
                entityManager.merge(areaEntity);
            }
            else{
                entityManager.persist(areaEntity);
            }
            System.out.println(item.getDescription() + ": Районів - " + areaRegionsEntities.size() + " Населених пунктів - "+settlementsCount + " Відділень - "+warehousesCount);
        }
        System.out.println("Cид даних Нової Пошти");
        entityManager.flush();
        entityManager.clear();
        entityManager.close();
        System.out.println("Cид даних Нової Пошти завершено" );
    }

    private String newPostRequest(String modelName,String calledMethod,int page,int limit) throws JsonProcessingException {
        MethodProperties properties = new MethodProperties(page,limit,"","");
        return newPostRequest(modelName,calledMethod,   properties);
    }

    private String newPostRequest(String modelName,String calledMethod,int page,int limit,String areaId) throws JsonProcessingException {
        MethodProperties properties = new MethodProperties(page,limit,areaId,"");
        return newPostRequest(modelName,calledMethod,   properties);
    }

    private String newPostRequest(String modelName,String calledMethod,MethodProperties properties) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        NewPostRequest request = new NewPostRequest(newPostApiKey, modelName, calledMethod,properties);
        String requestJson = objectMapper.writeValueAsString(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);
        var response = restTemplate.exchange(newPostApiUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        return null;
    }
}
