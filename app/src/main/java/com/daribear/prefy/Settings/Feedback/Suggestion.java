package com.daribear.prefy.Settings.Feedback;

import lombok.Data;

@Data
public class Suggestion {
    private String suggestionText;
    private Double creationDate;
    private Long userId;
}
