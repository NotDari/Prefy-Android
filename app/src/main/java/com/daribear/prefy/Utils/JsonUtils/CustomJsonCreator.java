package com.daribear.prefy.Utils.JsonUtils;

import com.daribear.prefy.Comments.Comment;

import org.json.JSONObject;

import java.util.ArrayList;

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
