package com.xuyinan.barcodesearcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xuyinan.barcodesearcher.util.KuaidiUtil;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(albumIntent, 0);
        } else {
            Intent cameraIntent = new Intent(this, CameraActivity.class);
            startActivityForResult(cameraIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == -1) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                Image barcode = new Image(width, height, "RGB4");
                barcode.setData(pixels);
                new KuaidiTask().execute(barcode);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {
            byte[] bytes = data.getByteArrayExtra("data");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            Image barcode = new Image(width, height, "RGB4");
            barcode.setData(pixels);
            new KuaidiTask().execute(barcode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class KuaidiTask extends AsyncTask<Image, Void, String> {
        @Override
        protected String doInBackground(Image... barcode) {
            ImageScanner scanner = new ImageScanner();
            int result = scanner.scanImage(barcode[0].convert("Y800"));
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    return getDatas(sym.getData());
                }
            }
            return null;
        }

        private String getDatas(String param) {
            try {
                return KuaidiUtil.getResult(param);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            TextView text = (TextView) findViewById(R.id.text1);
            text.setText(s);
            super.onPostExecute(s);
        }
    }
}
