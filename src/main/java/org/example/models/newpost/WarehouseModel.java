package org.example.models.newpost;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseModel {
    @JsonProperty("Ref")
    private String ref;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("SettlementRef")
    private String settlement;
}
