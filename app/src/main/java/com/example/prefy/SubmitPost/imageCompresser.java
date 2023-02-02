package com.example.prefy.SubmitPost;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class imageCompresser {


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
