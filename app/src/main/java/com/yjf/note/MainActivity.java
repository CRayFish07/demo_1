package com.yjf.note;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yjf.note.db.ImageDB;
import com.yjf.note.handler.IndexHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private List<String> imageList;
    private static int nowImage;
    private Bitmap bitMap;

    private ImageView imageView;
    private ImageDB imageDB;
    private IndexHandler indexHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        TextView tv = (TextView)findViewById(R.id.welcome_text);
        tv.setText("welcome "+username);
        //初始化按钮事件
        initButtonEvent();
        //初始话图片列表
        initImageList();
        indexHandler = new IndexHandler(this.getApplicationContext());
    }

    private void initButtonEvent(){
        Button add_button = (Button)findViewById(R.id.add_picture);
        Button before_button = (Button)findViewById(R.id.before_picture);
        Button after_button = (Button)findViewById(R.id.after_picture);
        add_button.setOnClickListener(this);
        before_button.setOnClickListener(this);
        after_button.setOnClickListener(this);

        Button refresh_button = (Button)findViewById(R.id.refresh);
        refresh_button.setOnClickListener(this);
    }

    private void initImageList(){
        RelativeLayout imageLayout = (RelativeLayout)findViewById(R.id.cycle);
        imageView = (ImageView)findViewById(R.id.picture_view);
        //初始化图片数据
        if(imageDB == null){
            imageDB = new ImageDB(this.getApplicationContext());
        }
        Cursor cursor = imageDB.queryAllData();
        imageList = new ArrayList<String>();
        //初始化图片索引值
        nowImage = -1;
        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(ImageDB.KEY_ADDR);
            while(!cursor.isAfterLast()){
                String path = cursor.getString(index);
                imageList.add(path);
                cursor.moveToNext();
            }
            cursor.close();
        }
        if(imageList.size()>0){
            nowImage = 0;
            showImage(nowImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // test dfsd
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //按钮点击事件回调方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
           if (intent != null) {
               Uri imageUri = intent.getData();
               if (bitMap != null) {
                    bitMap.recycle();
               }
               if(imageUri == null){
                   // 拍照的有的手机不是返回Uri
                   Bundle bundle = intent.getExtras();
                   bitMap = bundle.getParcelable("data");
               }else{
                    try {
                        bitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
               }
           String path = "";
            //表示拍照，需要存储到本地
            if(resultCode == 1) {
                path = savePhotoToSdcard();
            }else{
                //获取图片路径
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(imageUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
            }
            //将数据持久化到数据库
           imageDB.insert(path);
           imageView.setImageBitmap(bitMap);
           imageList.add(path);
           nowImage = imageList.size() - 1;
           }
    }

    //按钮点击事件
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_picture:
                startActivityForResult(new Intent(MainActivity.this,ButtonListActivity.class),1);
                break;
            case R.id.before_picture:getNextImage(0);break;
            case R.id.after_picture:getNextImage(1);break;
            case R.id.refresh:indexHandler.count(handler);break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch(data.getInt("result")){
                case 1:
                    Toast.makeText(MainActivity.this.getApplicationContext(), "登录总次数:"+data.getInt("count"), Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(MainActivity.this.getApplicationContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
            }
        }
    };

    //flag 1表示下一张，0表示上一张
    private void getNextImage(int flag){
          if(flag == 1){
              if(nowImage < imageList.size()-1 && nowImage >= 0){
                  nowImage++;
                  showImage(nowImage);
              }else{
                  Toast.makeText(this,"已经是最后一张了", Toast.LENGTH_SHORT).show();
              }
          }else{
              if(nowImage > 0 ){
                  nowImage--;
                  showImage(nowImage);
              }else{
                  Toast.makeText(this.getApplicationContext(),"已经是第一张了", Toast.LENGTH_SHORT).show();
              }
          }
    }

    //展示图片
    private void showImage(int nowImage){
        String path = imageList.get(nowImage);
        File file = new File(path);
        long size = file.length();
        if(!file.exists()){
            return;
        }
        Uri uri = Uri.fromFile(file);
        if(bitMap != null){
            bitMap.recycle();
        }
        try {
            bitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitMap);

    }

    //将图片保存到sd卡
    private String savePhotoToSdcard(){
        String path = Environment.getExternalStorageDirectory().toString()+"/note_test";
        File filePath = new File(path);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        File photoFile = new File(path,System.currentTimeMillis()+".jpg");
        OutputStream os = null;
        try {
            os = new FileOutputStream(photoFile);
            bitMap.compress(Bitmap.CompressFormat.JPEG,100,os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return photoFile.getAbsolutePath();
    }
}
