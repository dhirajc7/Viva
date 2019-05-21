package viva.oneplatinum.com.viva.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import viva.oneplatinum.com.viva.R;

/**
 * Created by Dell on 3/14/2016.
 */
public class GifView extends View {
    public Movie mMovie;

    public long movieStart;


    /*private InputStream gifInputStream;
    private Movie gifMovie;*/
    /*private int movieWidth,movieHeight;
    private long movieDuration;*/
    //private long movieStart;

    public GifView(Context context){
        super(context);
        initializeView();
        //init(context);

    }
    public GifView(Context context,AttributeSet attrs){
        super(context, attrs);
        //init(context);
        initializeView();
    }
    public GifView(Context context,AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        //init(context);
        initializeView();
    }

    private void initializeView(){
        InputStream is=getContext().getResources().openRawResource(+R.drawable.splash);
        mMovie=Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
           int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight() - mMovie.height());
            this.invalidate();

        }

    }
    private  int gifId;

    public  void setGifResource(int resId){
    this.gifId=resId;
        initializeView();
    }

    public int getGIFResource(){
        return this.gifId;
    }

/* private void init(Context context){
        setFocusable(true);
        gifInputStream=context.getResources().openRawResource(+R.drawable.congratulations);
        gifMovie=Movie.decodeStream(gifInputStream);
        movieWidth=gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration=gifMovie.duration();


    }

    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        setMeasuredDimension(movieWidth,movieHeight);
    }
    public int getMovieWidth(){
        return movieWidth;
    }
    public int getMovieHeight(){
        return movieHeight;
    }
    public long getMovieDuration(){
        return movieDuration;
    }
   protected void onDraw(Canvas canvas){
        long now= SystemClock.uptimeMillis();

       if(gifMovie!=null){
           int dur=gifMovie.duration();
           if(dur==0){
                dur=1000;
           }
           int relTime=(int)((now=movieStart) % dur);
           gifMovie.setTime(relTime);
           gifMovie.draw(canvas,0,0);
           invalidate();
       }
   }*/
}
