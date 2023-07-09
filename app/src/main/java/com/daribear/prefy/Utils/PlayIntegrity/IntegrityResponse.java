package com.daribear.prefy.Utils.PlayIntegrity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntegrityResponse {
    private Boolean success;
    private String token;


}
