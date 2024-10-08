package org.example.models.newpost;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionModel {
    @JsonProperty("Ref")
    private String ref;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("AreasCenter")
    private String regionCenter;
    @JsonProperty("RegionType")
    private String regionType;
    private String area;
}
