package org.example.mapping;

import org.example.entities.Area;
import org.example.models.newpost.AreaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AreaModelMapper {
    @Mapping(target = "id", source = "ref")
    Area fromModel(AreaModel model);
    Set<Area> fromModel(Iterable<AreaModel> models);
}
