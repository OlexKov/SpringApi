package org.example.mapping;

import org.example.entities.Settlement;
import org.example.models.newpost.SettlementModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SettlementModelMapper {
    @Mapping(target = "id", source = "ref")
    @Mapping(target = "region", ignore = true)
    Settlement fromModel(SettlementModel model);
    Set<Settlement> fromModel(Iterable<SettlementModel> models);
}
