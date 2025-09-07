package com.daribear.prefy.Utils.PlayIntegrity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data entity that represents the play integrity response.
 */
@Data
@AllArgsConstructor
public class IntegrityResponse {
    private Boolean success;
    private String token;


}
