package com.georgeneokq.dressup.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil extends Util {

    /*
     * Turns a RelativeLayout nested within a HorizontalScrollView into a scrollable image gallery.
     *
     * @param ctx Activity Context
     * @param imageGallery relativelayout that is the first child of a HorizontalScrollView.
     * @param resources array of drawable resource IDs to insert into image gallery
     * @param imageMargin integer value to specify the margin between each image in DP
     * @param onClickListener onclick listener to be triggered when the image is clicked
     */
    public static void enableHorizontalImageGallery(final Context ctx, final RelativeLayout imageGallery, final int[] resourceIds,
                                                    final int imageMargin, final View.OnClickListener onClickListener) {

        // Listen for when layout is drawn so that dynamic width and height calculations can be made
        ViewTreeObserver vto = imageGallery.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Keep track of latest dynamically generated ID
                int latestLayoutID = 0;

                // Dynamically insert ImageViews using the drawables arraylist
                for(int i = 0; i < resourceIds.length; i++) {

                    int resourceId = resourceIds[i];

                    Drawable drawable = ctx.getResources().getDrawable(resourceId, null);

                    // Create new ImageView
                    ImageView image = new ImageView(ctx);

                    // Set image drawable
                    image.setImageDrawable(drawable);

                    // Get width and height of the image in pixels
                    ImageDimensions imageDimensions = getImageDimensionsFromResource(ctx, resourceId);

                    // Image original width and height
                    int imgWidth = imageDimensions.getWidth();
                    int imgHeight = imageDimensions.getHeight();

                    // Adjust size of the image - height should be equivalent to relativelayout's height
                    int layoutHeight = imageGallery.getHeight();

                    /*
                     Adjust size of the image - width should be adjusted according to layout height
                     so the original width:height ratio is maintained
                     */
                    float ratio = imgHeight / layoutHeight;

                    // New dimensions for the generated ImageView
                    int newHeight = layoutHeight;
                    int newWidth = (int) (imgWidth / ratio + 0.5f); // Round off to nearest integer

                    // Set image dimensions
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(newWidth, newHeight);

                    // If there was an item inserted before the current one, align beside the previous item and add a margin
                    if(i != 0) {
                        params.addRule(RelativeLayout.RIGHT_OF, latestLayoutID);
                        params.leftMargin = Util.convertDpToPixels(ctx, imageMargin);
                    }

                    // Center image vertically
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                    // Assign a unique ID to the image
                    int newUniqueID = View.generateViewId();
                    image.setId(newUniqueID);

                    // Keep track of latest added layout's ID
                    latestLayoutID = newUniqueID;

                    // Set layout parameters for the image
                    image.setLayoutParams(params);

                    // Set onClickListener for the image
                    image.setOnClickListener(onClickListener);

                    // Append imageview to relativelayout
                    imageGallery.addView(image);
                }

            }
        });
    }

    /*
     * Creates an app-private image file.
     *
     * @param ctx Activity context
     * @return newly created File object
     * @throws IOException thrown if file creation fails
     */

    public static File createImageFile(Context ctx) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /*
     * Get width and height of image from a specified drawable resource, in pixels
     */

    public static ImageDimensions getImageDimensionsFromResource(Context ctx, int resource) {

        Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), resource);
        int width = bm.getWidth();
        int height = bm.getHeight();
        return new ImageDimensions(width, height);
    }

    // Object containing an image's width and height - to be used when getting dimensions from drawable
    public static class ImageDimensions {

        private int width;
        private int height;

        private ImageDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /*
         * Read-only properties
         */

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }

}
