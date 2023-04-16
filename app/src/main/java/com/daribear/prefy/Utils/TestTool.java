package com.daribear.prefy.Utils;

import lombok.Getter;

public class TestTool {
    private static TestTool instance;
    @Getter
    private Integer voteCount = 0;


    public static TestTool getInstance(){
        if (instance == null){
            instance = new TestTool();
        }
        return instance;
    }

    public void changeVoteCount(Integer changeAmount){
        voteCount += changeAmount;
    }

}
