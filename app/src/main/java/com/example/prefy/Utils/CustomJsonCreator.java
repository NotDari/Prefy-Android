package com.example.prefy.Utils;

import com.example.prefy.Comments.Comment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class  CustomJsonCreator {

    public static JSONObject createObjFromComment(Comment comment){
        JSONObject jsonObject = new JSONObject();
        /**
        try {
            //jsonObject.put("a",comment.);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
         */
        return null;
    }

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
