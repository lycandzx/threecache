package com.test.cachedemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements ImageLoadListener {

    Button downLoad;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        downLoad = (Button) findViewById(R.id.download);
        imageView = (ImageView) findViewById(R.id.img);

        downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownLoadUtils downLoadUtils = new DownLoadUtils(MainActivity.this, MainActivity.this);
                downLoadUtils.downLoad("http://p.nanrenwo.net/uploads/allimg/161020/8426-161020150643.png");
            }
        });
    }

    @Override
    public void loadBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
