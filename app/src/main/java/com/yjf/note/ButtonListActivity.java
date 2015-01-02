package com.yjf.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/12/26 0026.
 */
public class ButtonListActivity extends Activity implements OnClickListener {

    private Button btn_photo, btn_picture, btn_off;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_button);
        intent = getIntent();
        btn_photo = (Button) this.findViewById(R.id.photo_button);
        btn_picture = (Button) this.findViewById(R.id.picture_button);
        btn_off = (Button) this.findViewById(R.id.off_button);

        LinearLayout layout = (LinearLayout) findViewById(R.id.pop_layout);

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", Toast.LENGTH_SHORT).show();
            }
        });

        btn_photo.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_off.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.off_button:
                finish();
                break;
            case R.id.photo_button:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.picture_button:
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(resultCode != RESULT_OK){
           return;
       }
       switch (requestCode){
           case 1:
               if (data.getExtras() != null){
                   intent.putExtras(data.getExtras());
               }
               if(data.getData() != null){
                   intent.setData(data.getData());
               }
               setResult(1,intent);
               break;
           case 2:
               if (data.getExtras() != null){
                   intent.putExtras(data.getExtras());
               }
               if(data.getData() != null){
                   intent.setData(data.getData());
               }
               setResult(2,intent);
               break;
       }
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
