package com.georgeneokq.dressup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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

    /*
     * For image dragging
     */
    RelativeLayout playground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_up);

        imageView = findViewById(R.id.imageView);
        imageGallery = findViewById(R.id.imageGallery);
        playground = findViewById(R.id.playground);

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

            // Cast to ImageView type
            ImageView image = (ImageView) v;

            // Get image drawable
            Drawable drawable = image.getDrawable();

            // Create a new ImageView to be appended to the playground
            ImageView newImage = new ImageView(DressUpActivity.this);

            // Set new image's drawable
            newImage.setImageDrawable(drawable);

            // Get resourceID from tag
            int resourceId = Integer.parseInt((String) image.getTag());

            // Get image original dimensions in pixels
            ImageUtil.ImageDimensions imageDimensions = ImageUtil.getImageDimensionsFromResource(DressUpActivity.this, resourceId);

            int imgOriginalWidth = imageDimensions.getWidth();
            int imgOriginalHeight = imageDimensions.getHeight();

            // Calculate suitable width and height to use
            int newHeight = playground.getHeight() / 2; // Let image height be half of container's height
            float ratio = imgOriginalHeight / newHeight;
            int newWidth = (int) (imgOriginalWidth / ratio + 0.5f); // Adjust width according to image dimensions ratio

            // Setup layout parameters.
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);

            // Align to parent start and parent top to set image coordinates according to top and left
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

            // Set layout parameters
            newImage.setLayoutParams(params);

            // Previous point refers to the original point the finger touches
            final PointF previousPoint = new PointF();

            // Set image drag listener
            newImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

                    switch(event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            previousPoint.x = event.getX();
                            previousPoint.y = event.getY();
                            break;

                        case MotionEvent.ACTION_MOVE:

                            float dx = event.getX() - previousPoint.x;
                            float dy = event.getY() - previousPoint.y;

                            params.leftMargin += dx;
                            params.topMargin += dy;

                            // Check for boundaries, do not let image get out of bounds
                            if(params.leftMargin < 0)
                                params.leftMargin = 0;

                            if(params.leftMargin + v.getWidth() > playground.getWidth())
                                params.leftMargin = playground.getWidth() - v.getWidth();

                            if(params.topMargin < 0)
                                params.topMargin = 0;

                            if(params.topMargin + v.getHeight() > playground.getHeight())
                                params.topMargin = playground.getHeight() - v.getHeight();

                            break;

                        default:
                            break;
                    }

                    v.setLayoutParams(params);

                    return true;
                }
            });

            // Append to playground
            playground.addView(newImage);
        }
    };
}
