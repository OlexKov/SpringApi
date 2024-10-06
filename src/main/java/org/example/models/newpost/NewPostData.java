package org.example.models.newpost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPostData {
    private List<AreaModel> areas = new ArrayList<>();
    private List<RegionModel> regions= new ArrayList<>();
    private List<SettlementModel> settlements= new ArrayList<>();
    private List<WarehouseModel> warehouses= new ArrayList<>();
}
