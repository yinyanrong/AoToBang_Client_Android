/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aotobang.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.model.DefaultModelImp.PathType;


public class ImageUtils {
	public static final int SCALE_IMAGE_WIDTH = 640;
	  public static final int SCALE_IMAGE_HEIGHT = 960;
	
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =AotoBangApp.getInstance().getCachePath(PathType.Img)+imageName;
        return path;
		
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =AotoBangApp.getInstance().getCachePath(PathType.Img)+"th"+thumbImageName;
        return path;
    }


	  public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap)
	  {
	    return getRoundedCornerBitmap(paramBitmap, 6.0F);
	  }

	  public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap, float paramFloat)
	  {
	    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
	    Canvas localCanvas = new Canvas(localBitmap);
	    int i = -12434878;
	    Paint localPaint = new Paint();
	    Rect localRect = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
	    RectF localRectF = new RectF(localRect);
	    float f = paramFloat;
	    localPaint.setAntiAlias(true);
	    localCanvas.drawARGB(0, 0, 0, 0);
	    localPaint.setColor(-12434878);
	    localCanvas.drawRoundRect(localRectF, f, f, localPaint);
	    localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    localCanvas.drawBitmap(paramBitmap, localRect, localRect, localPaint);
	    return localBitmap;
	  }

	  public static Bitmap getVideoThumbnail(String paramString, int paramInt1, int paramInt2, int paramInt3)
	  {
	    Bitmap localBitmap = null;
	    localBitmap = ThumbnailUtils.createVideoThumbnail(paramString, paramInt3);
	    localBitmap = ThumbnailUtils.extractThumbnail(localBitmap, paramInt1, paramInt2, 2);
	    return localBitmap;
	  }

	  public static String saveVideoThumb(File paramFile, int paramInt1, int paramInt2, int paramInt3)
	  {
	    Bitmap localBitmap = getVideoThumbnail(paramFile.getAbsolutePath(), paramInt1, paramInt2, paramInt3);
	    File localFile = new File(AotoBangApp.getInstance().getCachePath(PathType.Video), "th" + paramFile.getName());
	    try
	    {
	      localFile.createNewFile();
	    }
	    catch (IOException localIOException1)
	    {
	      localIOException1.printStackTrace();
	    }
	    FileOutputStream localFileOutputStream = null;
	    try
	    {
	      localFileOutputStream = new FileOutputStream(localFile);
	    }
	    catch (FileNotFoundException localFileNotFoundException)
	    {
	      localFileNotFoundException.printStackTrace();
	    }
	    localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localFileOutputStream);
	    try
	    {
	      localFileOutputStream.flush();
	    }
	    catch (IOException localIOException2)
	    {
	      localIOException2.printStackTrace();
	    }
	    try
	    {
	      localFileOutputStream.close();
	    }
	    catch (IOException localIOException3)
	    {
	      localIOException3.printStackTrace();
	    }
	    return localFile.getAbsolutePath();
	  }

	  public static Bitmap decodeScaleImage(String path, int width, int heigh)
	  {
	    BitmapFactory.Options localOptions = getBitmapOptions(path);
	    int i = calculateInSampleSize(localOptions, width, heigh);
	    //EMLog.d("img", "original wid" + localOptions.outWidth + " original height:" + localOptions.outHeight + " sample:" + i);
	    localOptions.inSampleSize = i;
	    localOptions.inJustDecodeBounds = false;
	    Bitmap localBitmap1 = BitmapFactory.decodeFile(path, localOptions);
	    int j = readPictureDegree(path);
	    Bitmap localBitmap2 = null;
	    if ((localBitmap1 != null) && (j != 0))
	    {
	      localBitmap2 = rotaingImageView(j, localBitmap1);
	      localBitmap1.recycle();
	      localBitmap1 = null;
	      return localBitmap2;
	    }
	    return localBitmap1;
	  }

	  public static Bitmap decodeScaleImage(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
	  {
	    BitmapFactory.Options localOptions = new BitmapFactory.Options();
	    localOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
	    int i = calculateInSampleSize(localOptions, paramInt2, paramInt3);
	    localOptions.inSampleSize = i;
	    localOptions.inJustDecodeBounds = false;
	    Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
	    return localBitmap;
	  }

	  public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
	  {
	    int i = paramOptions.outHeight;
	    int j = paramOptions.outWidth;
	    int k = 1;
	    if ((i > paramInt2) || (j > paramInt1))
	    {
	      int m = Math.round(i / paramInt2);
	      int n = Math.round(j / paramInt1);
	      k = m > n ? m : n;
	    }
	    return k;
	  }

	  public static String getThumbnailImage(String paramString, int paramInt)
	  {
	    Bitmap localBitmap = decodeScaleImage(paramString, paramInt, paramInt);
	    try
	    {
	      File localFile = File.createTempFile("image", ".jpg");
	      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
	      localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
	      localFileOutputStream.close();
	      //EMLog.d("img", "generate thumbnail image at:" + localFile.getAbsolutePath() + " size:" + localFile.length());
	      return localFile.getAbsolutePath();
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return paramString;
	  }

	  public static String getScaledImage(Context paramContext, String paramString)
	  {
	    File localFile1 = new File(paramString);
	    if (!localFile1.exists())
	      return paramString;
	    long l = localFile1.length();
	    //EMLog.d("img", "original img size:" + l);
	    if (l <= 102400L)
	    {
	      //EMLog.d("img", "use original small image");
	      return paramString;
	    }
	    Bitmap localBitmap = decodeScaleImage(paramString, 640, 960);
	    try
	    {
	      File localFile2 = File.createTempFile("image", ".jpg", paramContext.getFilesDir());
	      FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
	      localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
	      localFileOutputStream.close();
	      //EMLog.d("img", "compared to small fle" + localFile2.getAbsolutePath() + " size:" + localFile2.length());
	      return localFile2.getAbsolutePath();
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return paramString;
	  }

	  public static String getScaledImage(Context paramContext, String paramString, int paramInt)
	  {
	    File localFile1 = new File(paramString);
	    if (localFile1.exists())
	    {
	      long l = localFile1.length();
	      //EMLog.d("img", "original img size:" + l);
	      if (l > 102400L)
	      {
	        Bitmap localBitmap = decodeScaleImage(paramString, 640, 960);
	        try
	        {
	          File localFile2 = new File(paramContext.getExternalCacheDir(), "eaemobTemp" + paramInt + ".jpg");
	          FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
	          localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
	          localFileOutputStream.close();
	          //EMLog.d("img", "compared to small fle" + localFile2.getAbsolutePath() + " size:" + localFile2.length());
	          return localFile2.getAbsolutePath();
	        }
	        catch (Exception localException)
	        {
	          localException.printStackTrace();
	        }
	      }
	    }
	    return paramString;
	  }

	  public static Bitmap mergeImages(int paramInt1, int paramInt2, List<Bitmap> paramList)
	  {
	    Bitmap localBitmap1 = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
	    Canvas localCanvas = new Canvas(localBitmap1);
	    localCanvas.drawColor(-3355444);
	    //EMLog.d("img", "merge images to size:" + paramInt1 + "*" + paramInt2 + " with images:" + paramList.size());
	    int i;
	    if (paramList.size() <= 4)
	      i = 2;
	    else
	      i = 3;
	    int j = 0;
	    int k = (paramInt1 - 4) / i;
	    for (int m = 0; m < i; m++)
	      for (int n = 0; n < i; n++)
	      {
	        Bitmap localBitmap2 = (Bitmap)paramList.get(j);
	        Bitmap localBitmap3 = Bitmap.createScaledBitmap(localBitmap2, k, k, true);
	        Bitmap localBitmap4 = getRoundedCornerBitmap(localBitmap3, 2.0F);
	        localBitmap3.recycle();
	        localCanvas.drawBitmap(localBitmap4, n * k + (n + 2), m * k + (m + 2), null);
	        localBitmap4.recycle();
	        j++;
	        if (j == paramList.size())
	          return localBitmap1;
	      }
	    return localBitmap1;
	  }

	  public static int readPictureDegree(String paramString)
	  {
	    int i = 0;
	    try
	    {
	      ExifInterface localExifInterface = new ExifInterface(paramString);
	      int j = localExifInterface.getAttributeInt("Orientation", 1);
	      switch (j)
	      {
	      case 6:
	        i = 90;
	        break;
	      case 3:
	        i = 180;
	        break;
	      case 8:
	        i = 270;
	      case 4:
	      case 5:
	      case 7:
	      }
	    }
	    catch (IOException localIOException)
	    {
	      localIOException.printStackTrace();
	    }
	    return i;
	  }

	  public static Bitmap rotaingImageView(int rotate, Bitmap paramBitmap)
	  {
	    Matrix localMatrix = new Matrix();
	    localMatrix.postRotate(rotate);
	    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
	    return localBitmap;
	  }

	  public static BitmapFactory.Options getBitmapOptions(String paramString)
	  {
	    BitmapFactory.Options localOptions = new BitmapFactory.Options();
	    localOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(paramString, localOptions);
	    return localOptions;
	  }
	 /* public static Drawable zoomDrawable(Drawable drawable, int w, int h) {  
		    int width = drawable.getIntrinsicWidth();  
		    int height = drawable.getIntrinsicHeight();  
		    // drawable转换成bitmap  
		    Bitmap oldbmp = drawableToBitmap(drawable);  
		    // 创建操作图片用的Matrix对象  
		    Matrix matrix = new Matrix();  
		    // 计算缩放比例  
		    float sx = ((float) w / width);  
		    float sy = ((float) h / height);  
		    // 设置缩放比例  
		    matrix.postScale(sx, sy);  
		    // 建立新的bitmap，其内容是对原bitmap的缩放后的图  
		    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,  
		            matrix, true);  
		    return new BitmapDrawable(newbmp);  
		}  */
	  /*public static Bitmap drawableToBitmap(Drawable drawable) {  
	        // 取 drawable 的长宽  
	        int w = drawable.getIntrinsicWidth();  
	        int h = drawable.getIntrinsicHeight();  
	  
	        // 取 drawable 的颜色格式  
	        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
	                : Bitmap.Config.RGB_565;  
	        // 建立对应 bitmap  
	        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
	        // 建立对应 bitmap 的画布  
	        Canvas canvas = new Canvas(bitmap);  
	        drawable.setBounds(0, 0, w, h);  
	        // 把 drawable 内容画到画布中  
	        drawable.draw(canvas);  
	        return bitmap;  
	    } */
	  public static byte[] bitmap2Bytes(Bitmap bm,boolean compress) {
		           ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		          
		          
		      	int options = 100;  
		      	if(compress){
				// 如果大于100kb则再次压缩,最多压缩三次
				while (baos.toByteArray().length / 1024 > 100 && options != 10) { 
					// 清空baos
					baos.reset();  
					// 这里压缩options%，把压缩后的数据存放到baos中  
					bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
		            options -= 30;
				}
				
		      	}
		          return baos.toByteArray();
		      }
	  
	
}
