package org.example.mapping;

import org.example.entities.Region;
import org.example.models.newpost.RegionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RegionMadelMapper {
    @Mapping(target = "area", ignore = true)
    @Mapping(target = "regionCenter", ignore = true)
    @Mapping(target = "id", source = "ref")
    @Mapping(target = "regionType", source = "regionType")
    Region fromModel(RegionModel model);
    Set<Region> fromModel(Iterable<RegionModel> models);
}
