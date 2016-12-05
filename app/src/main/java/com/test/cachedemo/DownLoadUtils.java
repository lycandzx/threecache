package com.test.cachedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInput;

/**
 * Created by Administrator on 2016/12/5.
 */

public class DownLoadUtils {

    Context context;

    //保存进入内存
    LruCache<String, Bitmap> lruCache;
    File externalFilesDir;

    ImageLoadListener loadListener;

    public DownLoadUtils(Context context, ImageLoadListener loadListener) {
        this.loadListener = loadListener;
        this.context = context;
        externalFilesDir = context.getExternalFilesDir(null);
        Log.e("----", "path===" + externalFilesDir.getPath());
        int memory = (int) (Runtime.getRuntime().maxMemory() / 8);
        lruCache = new LruCache<String, Bitmap>(memory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }


    public void cacheToLrucache(String name, Bitmap bitmap) {
        lruCache.put(name, bitmap);
    }

    //从内存获取
    public Bitmap getFromCache(String name) {

        return lruCache.get(name);
    }

    //保存进入文件

    public void cacheToFile(Bitmap bitmap, String name) {

        File file = new File(externalFilesDir.getParent(), name);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //从文件获取
    public Bitmap getFromFile(final String na) {
        Log.e("----", "name==" + na);
        File[] files = externalFilesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (na.equals(name)) {
                    Log.e("----", "file==" + name);
                    return BitmapFactory.decodeFile(file.getPath());
                }
            }
            return null;
        }

        return null;
    }
    //从网络获取

    public void getFromNet(String url, final String name) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                cacheToLrucache(name, response);
                cacheToFile(response, name);
                loadListener.loadBitmap(response);
            }
        }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(imageRequest);
    }

    //获取图片
    Bitmap bitmap;

    public void downLoad(String url) {
        String name = getBitmapName(url);
        bitmap = getFromCache(name);
        if (null != bitmap) {
            loadListener.loadBitmap(bitmap);
            return;
        }
        bitmap = getFromFile(name);
        if (bitmap != null) {
            loadListener.loadBitmap(bitmap);
            return;
        }
        getFromNet(url, name);

    }

    @NonNull
    private String getBitmapName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index);
    }

}
