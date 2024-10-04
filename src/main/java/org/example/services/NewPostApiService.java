package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.entities.Area;
import org.example.entities.Settlement;
import org.example.entities.Warehouse;
import org.example.interfaces.INewPostApiService;
import org.example.mapping.AreaModelMapper;
import org.example.mapping.SettlementModelMapper;
import org.example.mapping.WarehouseModelMapper;
import org.example.models.newpost.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewPostApiService implements INewPostApiService {
    @Value("${novaposhta.api.url}")
    private String newPostApiUrl;
    @Value("${novaposhta.api.key}")
    private String newPostApiKey;
    private final AreaModelMapper areaModelMapper;
    private final WarehouseModelMapper warehouseModelMapper;
    private final SettlementModelMapper settlementModelMapper;
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public List<AreaModel> getAreas() throws JsonProcessingException {
        var jsonResponse = newPostRequest("Address","getSettlementAreas",1,200);
        var response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<AreaModel>>() {});
        if(response != null && response.isSuccess() && !response.getData().isEmpty()){
            List<AreaModel> areas = response.getData();
            return new ArrayList<>(areas.stream()
                    .collect(Collectors.toMap(
                            AreaModel::getRef,
                            area ->area,
                            (existing, replacement) -> existing
                    )).values());
        }
        return null;
    }

    @Override
    public List<WarehouseModel> getWarehouses() throws JsonProcessingException, ExecutionException, InterruptedException {
        List<WarehouseModel> result = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<Future<NewPostResponse<WarehouseModel>>> futures = new ArrayList<>();
        int page = 1;
        do {
//            var jsonResponse =  newPostRequest("Address","getWarehouses",page,200);
//            var response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<WarehouseModel>>() {});
//            if(response != null && response.isSuccess()){
//                result.addAll(response.getData());
//                page++;
//            }
//            else{
//                break;
//            }
            final int currentPage = page;
            futures.add(executor.submit(() -> {
                String jsonResponse = newPostRequest("Address", "getWarehouses", currentPage, 35000);
                return objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<WarehouseModel>>() {});
            }));
        }while (++page <= 1);
        for (var future : futures) {
            var response = future.get();
            if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                result.addAll(response.getData());
            }
        }
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(
                        WarehouseModel::getRef,
                        warehouse ->warehouse,
                        (existing, replacement) -> existing
                )).values());
    }

    @Override
    public List<SettlementModel> getSettlements() throws JsonProcessingException, ExecutionException, InterruptedException {
        List<SettlementModel> result = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(20); // Визначте кількість потоків
        List<Future<NewPostResponse<SettlementModel>>> futures = new ArrayList<>();
        int page = 1;
        do {
            // var jsonResponse =  newPostRequest("Address","getSettlements",page,1000);
            // var response = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<SettlementModel>>() {});
            // if(response != null && response.isSuccess() && !response.getData().isEmpty()){
            //    result.addAll(response.getData());
            //    page++;
            //}
            // else{
            //     break;
            // }
            final int currentPage = page;
            futures.add(executor.submit(() -> {
                String jsonResponse = newPostRequest("Address", "getSettlements", currentPage, 500);
                return objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<SettlementModel>>() {
                });
            }));
            page++;
        } while (page <= 54);
        for (var future : futures) {
            var response = future.get();
            if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                result.addAll(response.getData());
            }
        }
        return  new ArrayList<>(result.stream()
                .collect(Collectors.toMap(
                        SettlementModel::getRef,
                        settlement ->settlement,
                        (existing, replacement) -> existing
                )).values());
    }

    @Override
    public List<RegionModel> getRegions(Iterable<String> areasIds) throws JsonProcessingException, ExecutionException, InterruptedException {
        List<RegionModel> result = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(20); // Визначте кількість потоків
        List<Future<NewPostResponse<RegionModel>>> futures = new ArrayList<>();
        for(var areaId:areasIds) {
            futures.add(executor.submit(() -> {
                String jsonResponse = newPostRequest("Address", "getSettlementCountryRegion", 1, 200, areaId);
                var areas = objectMapper.readValue(jsonResponse, new TypeReference<NewPostResponse<RegionModel>>() {});
                for(var area:areas.getData()){area.setArea(areaId);}
                return areas;
            }));
        }

        for (var future : futures) {
            var response = future.get();
            if (response != null && response.isSuccess() && !response.getData().isEmpty()) {
                result.addAll(response.getData());
            }
        }

        return  new ArrayList<>(result.stream()
                .collect(Collectors.toMap(
                        RegionModel::getRef,
                        settlement ->settlement,
                        (existing, replacement) -> existing
                )).values());
    }

    private String newPostRequest(String modelName,String calledMethod,int page,int limit) throws JsonProcessingException {
        MethodProperties properties = new MethodProperties(page,limit,"");
        return newPostRequest(modelName,calledMethod,   properties);
    }

    private String newPostRequest(String modelName,String calledMethod,int page,int limit,String areaId) throws JsonProcessingException {
        MethodProperties properties = new MethodProperties(page,limit,areaId);
        return newPostRequest(modelName,calledMethod,   properties);
    }

    private String newPostRequest(String modelName,String calledMethod,MethodProperties properties) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        objectMapper = new ObjectMapper();
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
