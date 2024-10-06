package org.example.models.newpost;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MethodProperties {
    @JsonProperty("Page")
    private int page;
    @JsonProperty("Limit")
    private int limit;
    @JsonProperty("AreaRef")
    private String areaId ;
    @JsonProperty("RegionRef")
    private String regionId;
}
