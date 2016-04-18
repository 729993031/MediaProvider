package com.example.MediaProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {

    Button add;
    Button view;
    ListView show;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> descs = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        add = (Button) findViewById(R.id.add);
        view = (Button) findViewById(R.id.view);
        show = (ListView) findViewById(R.id.show);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                names.clear();
                descs.clear();
                fileNames.clear();
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    names.add(name);
                    descs.add(desc);
                    fileNames.add(new String(data, 0, data.length - 1));
                }
                List<Map<String, Object>> listItems = new ArrayList<>();
                for (int i = 0; i < names.size(); i++) {
                    Map<String, Object> listItem = new HashMap<>();
                    listItem.put("name", names.get(i));
                    listItem.put("desc", descs.get(i));
                    listItems.add(listItem);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(MyActivity.this, listItems, R.layout.line, new String[]{"name", "desc"}, new int[]{R.id.name, R.id.desc});
                show.setAdapter(simpleAdapter);
            }
        });
        show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                View viewDialog = getLayoutInflater().inflate(R.layout.view, null);
                ImageView imageView = (ImageView) viewDialog.findViewById(R.id.image);
                imageView.setImageBitmap(BitmapFactory.decodeFile(fileNames.get(position)));
                new AlertDialog.Builder(MyActivity.this).setView(viewDialog).setPositiveButton("确定", null)
                        .show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "jinta");
                values.put(MediaStore.Images.Media.DESCRIPTION, "金塔");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Bitmap bitmap = BitmapFactory.decodeResource(MyActivity.this.getResources(),
                        R.drawable.jinta);
                System.out.println("======");
                OutputStream os = null;
                try {
                    os = getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}