package com.daribear.prefy.Popular;


import com.daribear.prefy.customClasses.StandardPost;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PopularPost extends StandardPost{
    private Double popularDate;
}
