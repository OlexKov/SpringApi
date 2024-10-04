package org.example.mapping;
import org.example.entities.Warehouse;
import org.example.models.newpost.WarehouseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface WarehouseModelMapper {
    @Mapping(target = "id", source = "ref")
    @Mapping(target = "settlement", ignore = true)
    Warehouse fromModel(WarehouseModel model);
    Set<Warehouse> fromModel(Iterable<WarehouseModel> models);
}
