package com.georgeneokq.dressup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.georgeneokq.dressup.util.Util;
import com.georgeneokq.dressup.util.ImageUtil;

import java.io.File;
import java.io.IOException;

public class DressUpActivity extends AppCompatActivity {

    RelativeLayout imageGallery;

    int[] imageResourceIds = {
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,
            R.drawable.kimono,

    };

    ImageView imageView;

    private static final int REQUEST_IMAGE_CAPTURE = 1,
            REQUEST_TAKE_PHOTO = 1;

    Uri currentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_up);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageGallery = (RelativeLayout) findViewById(R.id.relativeLayout);

        ImageUtil.enableHorizontalImageGallery(DressUpActivity.this, imageGallery, imageResourceIds, 20, imageOnClickListener);

        dispatchTakePictureIntent();
    }

    /*
     * Take picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = null;
                try {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.georgeneokq.dressup.fileprovider",
                            photoFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Keep global reference to newly formed URI, to be used in onActivityResult method
                currentPhotoUri = photoURI;

                // This is where the photo is saved
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                try {
                    // Load image bitmap from specified URI and load bitmap into ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri);
                    imageView.setImageBitmap(bitmap);

                    // Delete the file from storage after displaying. Cut off "my_images" from the path.
                    String imageName = currentPhotoUri.getPath().split("my_images")[1];
                    File image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + imageName);
                    boolean deleted = image.delete();

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
    }

    private View.OnClickListener imageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            Util.toast(DressUpActivity.this, "You clicked item " + Integer.toString(id));
        }
    };
}
