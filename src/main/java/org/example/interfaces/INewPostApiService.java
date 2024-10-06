package org.example.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.models.newpost.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface INewPostApiService {
    List<AreaModel> getAreas() throws JsonProcessingException;
    List<WarehouseModel> getWarehouses() throws JsonProcessingException, ExecutionException;
    List<SettlementModel> getSettlements() throws JsonProcessingException, ExecutionException;
    List<RegionModel> getRegions(Set<String> areasIds) throws JsonProcessingException, ExecutionException;
    NewPostData getData() throws JsonProcessingException, ExecutionException;
    void saveToDataBase(NewPostData data,boolean update )throws JsonProcessingException, ExecutionException;
}
