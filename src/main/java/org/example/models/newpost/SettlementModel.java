package org.example.models.newpost;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettlementModel {
    @JsonProperty("Ref")
    private String ref;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("SettlementTypeDescription")
    private String settlementTypeDescription;
  //  @JsonProperty("Area")
  //  private String areaId;
}
