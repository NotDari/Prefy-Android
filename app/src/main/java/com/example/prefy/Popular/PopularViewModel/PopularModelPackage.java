package com.example.prefy.Popular.PopularViewModel;


import com.example.prefy.Popular.PopularPostSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PopularModelPackage {

    private PopularPostSet popularPostSet;
    private String retrievalType;
}
