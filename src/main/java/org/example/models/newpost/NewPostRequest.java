package org.example.models.newpost;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewPostRequest {
    private String apiKey;
    private String modelName;
    private String calledMethod;
    private MethodProperties methodProperties;
}
