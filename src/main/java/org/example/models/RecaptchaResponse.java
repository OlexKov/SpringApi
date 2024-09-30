package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecaptchaResponse {
    private boolean success;
    private double score;
    private String action;
    private String challenge_ts; // Timestamp of the challenge
    private String hostname;
    private List<String> errorCodes;
}
