package com.samsung.mscr.gearconsolesdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsung.mscr.gearconsolesdk.library.GCUtils;

import java.io.ByteArrayOutputStream;

public class ImagesActivity extends Activity {

    private static final String TAG = ImagesActivity.class.getName();
    private ImageView mImageView;
    private Bitmap mBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        mImageView = (ImageView)findViewById(R.id.image_view);
        Button chooseImageButton = (Button)findViewById(R.id.button_choose_image);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        Button sendImageButton = (Button)findViewById(R.id.button_send_image);
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBitMap == null) {
                    Toast.makeText(ImagesActivity.this, "Please, choose image", Toast.LENGTH_SHORT).show();
                    return;
                }
                //send message to show loader before send heavy image
                Intent intent = new Intent(GCUtils.INTENT_NAME);
//                intent.putExtra("raw", "{\"loader\":true}");
                intent.putExtra("loader", true);
                getApplication().getApplicationContext().sendBroadcast(intent);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
//                Log.d("", imageEncoded);
                //здесь ограничение на размер String
                //лучше перенести в сервис или использовать Parseable
                intent = new Intent(GCUtils.INTENT_NAME);
//                intent.putExtra("raw", "{\"config\":{\"inputType\":\"default\",\"iconBase64\":\""+imageEncoded+"\"},\"eval\":\"alert(123);\"}");
                intent.putExtra("iconBase64", imageEncoded);
                getApplication().getApplicationContext().sendBroadcast(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d(TAG, "filePath="+filePath);
//                    mUrl = filePath;
                    mBitMap = getResizedBitmap(320, 320, filePath);

//                    mBitMap = BitmapFactory.decodeFile(filePath);
                    mImageView.setImageBitmap(mBitMap);
                }
        }
    }

    public Bitmap getResizedBitmap(int targetW, int targetH,  String imagePath) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //inJustDecodeBounds = true <-- will not load the bitmap into memory
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
