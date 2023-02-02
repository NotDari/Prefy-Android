package com.example.prefy.customClasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomError extends RuntimeException {
    private Integer customCode;
    private String message;

}
