package viva.oneplatinum.com.viva.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GeneralUtil {
    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;

        return false;
    }
    public static boolean isValidUrl(String txtWebsite){
        Pattern regex = Pattern.compile("^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$");
        Matcher matcher = regex.matcher(txtWebsite);
        if (matcher.matches()) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static Bitmap rotateImage(Context context, File photoFile) {
        Bitmap scaled = null;
        try {
            FileInputStream fis = new FileInputStream(photoFile);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            Bitmap bm = BitmapFactory.decodeStream(fis);

            ExifInterface exif = new ExifInterface(
                    photoFile.getAbsolutePath());

            String orientString = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.d("TAG_orientation EXIFDATA", orientString);
            int orientation = orientString != null ? Integer
                    .parseInt(orientString)
                    : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotationAngle = 270;
            // Rotate Bitmap
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
                    (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,
                    bm.getWidth(), bm.getHeight(), matrix, true);

            int height = rotatedBitmap.getHeight(), scaledheight, scaledwidth;
            int width = rotatedBitmap.getWidth();
            float aspect;

            if (width < height) {// portrait
                aspect = ((float) height / (float) width);
                scaledwidth = 480;
                scaledheight = (int) (480 * aspect);
            } else {// landscape
                aspect = ((float) width / (float) height);
                scaledheight = 480;
                scaledwidth = (int) (480 * aspect);
            }
            scaled = Bitmap.createScaledBitmap(rotatedBitmap,
                    scaledwidth, scaledheight, false);
            // impic.setImageURI(Uri.fromFile(photoFile));

            // create a file to write bitmap data
            File f = new File(context.getCacheDir(), "scaledBitmap");
            f.createNewFile();

            // Convert bitmap to byte array
            Bitmap bitmap = scaled;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            // Equate the file to photofile
            photoFile = f;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("photofile io error", "EXif not done");
        }
        return scaled;
    }


    public static Bitmap setBlurBackground(String bitmapUrl,
                                           final ImageView back, Context context) {
        AQuery aq = new AQuery(context);
        if (bitmapUrl
                .contains("http://res.cloudinary.com/dcj2wdnva/image/upload/")) {
            String ImageUrl[] = bitmapUrl
                    .split("http://res.cloudinary.com/dcj2wdnva/image/upload/");
            String blurImageUrl = "http://res.cloudinary.com/dcj2wdnva/image/upload/e_blur:1000/"
                    + ImageUrl[1];
            Log.d("blurImageUrl", blurImageUrl);
            aq.id(back).image(blurImageUrl);
            back.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (bitmapUrl
                .contains("https://graph.facebook.com/")) {
            String ImageUrl[] = bitmapUrl.split("https://graph.facebook.com/");
            String id[] = ImageUrl[1].split("/");
            String blurProfile = "http://res.cloudinary.com/dcj2wdnva/image/facebook/e_blur:1000/"
                    + id[0];
            Log.d("blurImageUrl", blurProfile);
            aq.id(back).image(blurProfile);
            back.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
//        else if (bitmapUrl
//                .contains("https://graph.facebook.com/")) {
//            String ImageUrl[] = bitmapUrl.split("https://graph.facebook.com/");
//            String id[] = ImageUrl[1].split("/");
//            String blurProfile = "http://res.cloudinary.com/dcj2wdnva/image/facebook/e_blur:400/"
//                    + id[0];
//            Log.d("blurImageUrl", blurProfile);
//            aq.id(back).image(blurProfile);
//            back.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        }
//        else if(){
//
//        }
        return null;
    }




    public static int intToDpi(int integer, Context context) {
        return (int) (integer * context.getResources().getDisplayMetrics().density);
    }

    public static String generateRandomAlphaNumeric() {
        String ab = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rd = new Random();

        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ab.charAt(rd.nextInt(ab.length())));

        }
        return sb.toString();
    }

    public static final String generatemd5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This method checks whether mobile data is enabled.
     *
     * @param context
     * @return
     */

    public boolean isMobileDataEabled(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    /**
     * This method is used to convert String into ByteArray for storing purpose
     *
     * @param content
     * @return
     * @throws IOException
     */
    public static byte[] convertStringToByteArray(String content)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(content);
        bos.close();
        return bos.toByteArray();
    }

    public static String convertByteArrayToString(byte[] byteArray) {
        return new String(byteArray);
    }


    public static Runnable getTouchDelegateAction(final View parent,
                                                  final View delegate, final int topPadding, final int bottomPadding,
                                                  final int leftPadding, final int rightPadding) {
        return new Runnable() {
            @Override
            public void run() {

                // Construct a new Rectangle and let the Delegate set its values
                Rect touchRect = new Rect();
                delegate.getHitRect(touchRect);

                // Modify the dimensions of the Rectangle
                // Padding values below zero are replaced by zeros
                touchRect.top -= Math.max(0, topPadding);
                touchRect.bottom += Math.max(0, bottomPadding);
                touchRect.left -= Math.max(0, leftPadding);
                touchRect.right += Math.max(0, rightPadding);

                // Now we are going to construct the TouchDelegate
                TouchDelegate touchDelegate = new TouchDelegate(touchRect,
                        delegate);

                // And set it on the parent
                parent.setTouchDelegate(touchDelegate);

            }
        };
    }

    public static String getNewsTimeStamp(long timeInMillisec, String language) {
        Date date = new Date();
        long currentTime = date.getTime();
        long timeStamp = currentTime - timeInMillisec + 20700000;// add 5.45hr
        long timeInSec = timeStamp / 1000;
        long timeInMin = timeInSec / 60;
        long timeInHour = timeInMin / 60;

        if (language.equals("Engilsh")) {
            if (timeStamp <= 0) {
                return "0sec";
            }
            if (timeInMin <= 60) {
                String unit = (timeInMin > 1) ? "s" : "";
                return timeInMin + " min" + unit;
            }
            if (timeInHour <= 24) {
                String unit = (timeInHour > 1) ? "s" : "";
                return timeInHour + " hr" + unit;
            }
        } else {
            if (timeStamp <= 0) {
                return "0;]";
            }
            if (timeInMin <= 60) {

                return timeInMin + " ldg]^";
            }
            if (timeInHour <= 24) {

                return timeInHour + " #)^f";
            }

        }
        if (timeInHour > 24) {
            return new Date(timeInMillisec) + "";
        }

        return "";
    }

    public static String getNewsTimeStampOld(long timeInMillisec,
                                             String language) {
        Date date = new Date();
        long currentTime = date.getTime();
        long timeStamp = currentTime - timeInMillisec + 20700000;// add 5.45hr
        long timeInSec = timeStamp / 1000;
        long timeInMin = timeInSec / 60;
        long timeInHour = timeInMin / 60;
        long timeInDay = timeInHour / 24;
        long timeInWeek = timeInDay / 7;
        long timeInMonth = timeInDay / 30;
        long timeInYear = timeInMonth / 12;

        if (language.equals("English")) {
            if (timeStamp <= 0) {
                return "0sec";
            }
            if (timeInMin <= 60) {
                String unit = (timeInMin > 1) ? "s" : "";
                return timeInMin + " min" + unit;
            }
            if (timeInHour <= 24) {
                String unit = (timeInHour > 1) ? "s" : "";
                return timeInHour + " hr" + unit;
            }
            if (timeInDay <= 7) {
                String unit = (timeInDay > 1) ? "s" : "";
                return timeInDay + " day" + unit;
            }
            if (timeInWeek < 4) {
                String unit = (timeInWeek > 1) ? "s" : "";
                return timeInWeek + " week" + unit;
            }
            if (timeInMonth <= 12) {
                String unit = (timeInMonth > 1) ? "s" : "";
                return timeInMonth + " month" + unit;
            }
            if (timeInSec <= 60) {
                String unit = (timeInSec > 1) ? "s" : "";
                return timeInSec + " sec" + unit;
            }
            if (timeInYear > 1) {
                String unit = (timeInYear > 1) ? "s" : "";
                return timeInYear + " year" + unit;

            }
        } else {
            if (timeStamp <= 0) {
                return "0;]";
            }
            if (timeInMin <= 60) {

                return timeInMin + " ldg]^";
            }
            if (timeInHour <= 24) {

                return timeInHour + " #)^f";
            }
            if (timeInDay <= 7) {

                return timeInDay + " lbg";
            }
            if (timeInWeek < 4) {

                return timeInWeek + " xKtf";
            }
            if (timeInMonth <= 12) {

                return timeInMonth + " dlxgf";
            }
            if (timeInSec <= 60) {

                return timeInSec + " ;]";
            }
            if (timeInYear > 1) {

                return timeInYear + " jif{";

            }

        }

        return "";
    }

    /**
     * px转sp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static int px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }


    public static Bitmap getBlFurDrawable(Bitmap sentBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        // Bitmap = ((BitmapDrawable) d).getBitmap();
        int radius = 15;

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return bitmap;
    }

    public Bitmap getHexagonShape(Bitmap scaleBitmapImage) {
        // TODO Auto-generated method stub
        int targetWidth = 600;
        int targetHeight = 600;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        Path path = new Path();
        float stdW = 300;
        float stdH = 300;
        float w3 = stdW / 2;
        float h2 = stdH / 2;
        path.moveTo(0, (float) (h2 * Math.sqrt(3) / 2));
        path.rLineTo(w3 / 2, -(float) (h2 * Math.sqrt(3) / 2));
        path.rLineTo(w3, 0);
        path.rLineTo(w3 / 2, (float) (h2 * Math.sqrt(3) / 2));
        path.rLineTo(-w3 / 2, (float) (h2 * Math.sqrt(3) / 2));
        path.rLineTo(-w3, 0);
        path.rLineTo(-w3 / 2, -(float) (h2 * Math.sqrt(3) / 2));

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);
        return targetBitmap;
    }

    /**
     * Method for making rounded bitmap from rectangle bitmap
     *
     * @param loadedImage
     * @return rounded bitmap
     */


    // public static Bitmap blur(Bitmap bkg, float radius, Context context) {
    // Bitmap overlay = Bitmap.createBitmap(
    // view.getMeasuredWidth(),
    // view.getMeasuredHeight(),
    // Bitmap.Config.ARGB_8888);
    //
    // Canvas canvas = new Canvas(overlay);
    //
    // canvas.drawBitmap(bkg, -view.getLeft(),
    // -view.getTop(), null);
    //
    // RenderScript rs = RenderScript.create(context);
    //
    // Allocation overlayAlloc = Allocation.createFromBitmap(
    // rs, overlay);
    //
    // ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
    // rs, overlayAlloc.getElement());
    //
    // blur.setInput(overlayAlloc);
    //
    // blur.setRadius(radius);
    //
    // blur.forEach(overlayAlloc);
    //
    // overlayAlloc.copyTo(overlay);
    //
    // rs.destroy();
    // return overlay;
    // }

    /**
     * expand the view provided with an animation
     *
     * @param v
     */
    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;

        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {

                v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources()
                .getDisplayMetrics().density));
        a.setDuration(300);
        v.startAnimation(a);
    }

    /**
     * collapse the given view with animation
     *
     * @param v
     */
    public static void collapse(final View v) {

        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight
                            - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources()
                .getDisplayMetrics().density));
        a.setDuration(300);
        v.startAnimation(a);
    }


    public static String removeStringBetweens(String input, String from,
                                              String to) {
        Pattern p = Pattern.compile(".*?" + from + "(.*?)" + to + ".*?");
        Matcher m = p.matcher(input);
        String textToRemove = "";

        while (m.find()) {
            textToRemove = m.group(1);
        }
        return input.replace(textToRemove, "");
    }

    public static String replaceNthOcurrence(String str, String toFind,
                                             String toReplace, int ocurrence) {
        Pattern p = Pattern.compile(Pattern.quote(toFind));
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        int i = 0;
        while (m.find()) {
            if (++i == ocurrence) {
                sb.replace(m.start(), m.end(), toReplace);
                break;
            }
        }

        return sb.toString();
    }

    public static void showToastView(Context context, String message) {
        Toast toast = new Toast(context);
        TextView view = new TextView(context);
        // view.setTypeface(((NepalKhabarApplication) context
        // .getApplicationContext()).getNepali());
        view.setBackgroundColor(Color.BLACK);
        view.setText(message);
        view.setPadding(15, 15, 15, 15);
        view.setTextSize(18);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

//	public static String getMACAddress(Context context) {
//		WifiManager manager = (WifiManager) context
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = manager.getConnectionInfo();
//		System.out.println(info.getMacAddress());
//		return info.getMacAddress();
//
//	}

    public static String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static double getDistanceFromLatLonInKm(double lat1, double lon1,
                                                   double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1); // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d;
    }

    public static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static boolean compareDate(String valid_until) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm"); // 2015-06-05
        // 07:28
        Date strDate;
        try {
            strDate = sdf.parse(valid_until);
            if (System.currentTimeMillis() > strDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean compareDate1(String valid_until) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); // 2015-06-05
        // 07:28
        Date strDate;
        try {
            strDate = sdf.parse(valid_until);
            if (System.currentTimeMillis() > strDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public static String convertEventDate(String dateString) {
        String convertedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date testDate = null;
        try {
            testDate = (Date) formatter.parse(dateString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);

        SimpleDateFormat newFormat = new SimpleDateFormat(
                "EEEE dd MMM yyyy, hh:mm:a");
        convertedDate = newFormat.format(cal.getTime());

        return convertedDate;
    }
    //	public static int getCategoryPosition(ArrayList<CategoryInfo> list,
//			String value) {
//
//		for (int i = 0; i < list.size(); i++) {
//			if (value.equals(list.get(i).categoryId + "")) {
//				return i;
//			}
//
//		}
//		return 0;
//
//	}
//
//	public static int getTypePosition(ArrayList<CategoryInfo> list, String value) {
//
//		for (int i = 0; i < list.size(); i++) {
//			if (value.equals(list.get(i).typeTitle + "")) {
//				return i;
//			}
//
//		}
//		return 0;
//
//	}
    public static Bitmap decodeImageFile(String filepath, int filesize) {
        try {
            // The new size we want to scale to
            final int REQUIRED_SIZE = filesize;
            File imageFile = new File(filepath);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(imageFile), null, o);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            o.inSampleSize = Math.max(o.outWidth / REQUIRED_SIZE, o.outHeight
                    / REQUIRED_SIZE);
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(imageFile),
                    null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Bitmap getRoundedBitmap(Bitmap loadedImage) {
        int a = loadedImage.getHeight() < loadedImage.getWidth() ? loadedImage
                .getHeight() : loadedImage.getWidth();
        Bitmap circleBitmap = Bitmap
                .createBitmap(a, a, Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(loadedImage,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);

        c.drawCircle(a / 2, a / 2, a / 2, paint);

        return circleBitmap;
    }

    public static String convertDate(String dateString) {
        String convertedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date testDate = null;
        try {
            testDate = (Date) formatter.parse(dateString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);

        SimpleDateFormat newFormat = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a");
        convertedDate = newFormat.format(cal.getTime());

        return convertedDate;
    }
    public static String convertPostDate(String dateString) {
        String convertedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date testDate = null;
        try {
            testDate = (Date) formatter.parse(dateString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);

        SimpleDateFormat newFormat = new SimpleDateFormat(
                "dd MMM , hh a");
        convertedDate = newFormat.format(cal.getTime());

        return convertedDate;
    }
    public static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 7.5f;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }
}
