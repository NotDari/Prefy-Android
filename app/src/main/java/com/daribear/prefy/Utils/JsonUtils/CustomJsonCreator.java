package com.daribear.prefy.Utils.JsonUtils;

import com.daribear.prefy.Comments.Comment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Converts a list of long into a json, for sending to the backend.
 */
public class  CustomJsonCreator {

    /**
     * Covnerts a list of longs into a json.
     * @param list list to be converted from json
     * @return json string
     */
    public static String createArrayStringFromLong(ArrayList<Long> list){
        if (list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < list.size(); x++) {
            sb.append(list.get(x)).append(",");
        }
        sb.setLength(sb.length() - 1);
        String newString = sb.toString();
        return newString;
    }
}
