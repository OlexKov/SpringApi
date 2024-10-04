package org.example.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.models.newpost.AreaModel;
import org.example.models.newpost.RegionModel;
import org.example.models.newpost.SettlementModel;
import org.example.models.newpost.WarehouseModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface INewPostApiService {
    List<AreaModel> getAreas() throws JsonProcessingException;
    List<WarehouseModel> getWarehouses() throws JsonProcessingException, ExecutionException, InterruptedException;
    List<SettlementModel> getSettlements() throws JsonProcessingException, ExecutionException, InterruptedException;
    List<RegionModel> getRegions(Iterable<String> areasIds) throws JsonProcessingException, ExecutionException, InterruptedException;
}
