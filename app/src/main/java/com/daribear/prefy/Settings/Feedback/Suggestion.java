package com.daribear.prefy.Settings.Feedback;

import lombok.Data;

/**
 * Data entity representing a users suggestion that they suggest to us.
 */
@Data
public class Suggestion {
    private String suggestionText;
    private Double creationDate;
    private Long userId;
}
