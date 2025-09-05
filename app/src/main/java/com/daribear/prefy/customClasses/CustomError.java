package com.daribear.prefy.customClasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A data entity representing an error. Has a message and a custom code, as this is the format the springboot database returns.
 */
@Getter
@Setter
@AllArgsConstructor
public class CustomError extends RuntimeException {
    private Integer customCode;
    private String message;

}
