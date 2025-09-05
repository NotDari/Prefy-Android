package com.daribear.prefy.SubmitPost;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.ByteArrayOutputStream;

/**
 * Helper class which compresses images for posts to save memory and file size.
 */
public class imageCompresser {


    /**
     * Compresses a bitmap by drawing it on a white background and reducing the quality.
     * Has 50% quality, and is a JPEG
     *
     * @param bitmap image to be compressed
     * @return compressed Bitmap
     */
   public Bitmap compressBitmap(Bitmap bitmap){
       Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
       Canvas canvas = new Canvas(newBitmap);
       canvas.drawColor(Color.WHITE);
       canvas.drawBitmap(bitmap, 0, 0, null);
       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
       return newBitmap;
   }


}
