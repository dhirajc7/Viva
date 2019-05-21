package viva.oneplatinum.com.viva.image;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements the singleton design pattern to manage a cache filled
 * by two parts : one hard and one soft.
 *
 * The hard part of the cache is initialized with 50% of its max capacity and
 * gets bigger on-the-fly. When max capacity is reached, the least-recently
 * accessed object is kicked out from the hard part to the soft part.
 *
 * The soft part of the cache is also initialized with 50% of the max capacity
 * of the hard part and gets bigger on-the-fly as needed.
 *
 * The object's references in hard part are not affected by the Garbage
 * Collector while the soft part references are too aggressively cleared when it
 * takes place.
 *
 * @author Rafael Simionato
 */
public class BitmapCache {

    private static BitmapCache mBitmapCache = null;

    // Defines the maximum number of references for the hard part of the cache
    private static final int MAX_HARD_CAPACITY = 100;

    // Hard cache, with a fixed maximum capacity
    private final HashMap<String, Bitmap> hardCache;

    // Soft cache for bitmaps kicked out of hard cache
    private final ConcurrentHashMap<String, SoftReference<Bitmap>> softCache;

    private BitmapCache() {

        // It creates a new HashMap maintaining the ordering based on the last
        // accessed element (from least-recently accessed to most-recently
        // accessed)
        hardCache = new LinkedHashMap<String, Bitmap>(MAX_HARD_CAPACITY / 2, 0.75f, true) {

            private static final long serialVersionUID = 6153694168085284990L;

            /**
             * As soon as the max capacity is reached, the least-recently
             * accessed object is transfered from the hard to the soft part of
             * the cache
             */
            @Override
            protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
                if (size() > MAX_HARD_CAPACITY) {
                    softCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                    return true;
                } else {
                    return false;
                }
            }
        };

        // Soft part of the cache for bitmaps kicked out from the hard part
        softCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(MAX_HARD_CAPACITY / 2);
    }

    /**
     * It returns the single instance for the bitmap cache object.
     */
    public static BitmapCache getInstance() {
        if (mBitmapCache == null) {
            mBitmapCache = new BitmapCache();
        }
        return mBitmapCache;
    }

    /**
     * It adds the bitmap referred by the URL string to the cache.
     *
     * @param url
     *            key to refer the entry bitmap
     * @param bitmap
     *            entry data to be stored in the cache
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (hardCache) {
                hardCache.put(url, bitmap);
            }
        }
    }

    /**
     * It checks for a cached bitmap and returns null if it is not found.
     *
     * @param url
     *            key used to retrieved a bitmap from the cache
     */
    public Bitmap getBitmap(String url) {

        Bitmap bitmap;

        synchronized (hardCache) {

            // First try find it in the hard part
            bitmap = hardCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                hardCache.remove(url);
                hardCache.put(url, bitmap);
                return bitmap;
            }

            // Then try find it in the soft part
            SoftReference<Bitmap> bitmapReference = softCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    // Bitmap found in soft cache
                    // To avoid losing this bitmap reference, move it back to
                    // first position in hard part and remove it from soft part
                    hardCache.put(url, bitmap);
                    softCache.remove(url);
                    return bitmap;
                } else {
                    // Soft reference has been Garbage Collected
                    softCache.remove(url);
                }
            }

        }
        return null;
    }

}