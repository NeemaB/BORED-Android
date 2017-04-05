package cpen391.team6.bored.Data;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

/**
 * Created by andytertzakian on 2017-03-16.
 */

public class ExternalNote extends Note {

    private Bitmap mBitmap;
    private String mFileName;
    private String mCourseCode;

    public ExternalNote(String filename, String courseCode, Bitmap bitmap){
        this.mFileName = filename;
        this.mCourseCode = courseCode;
        this.mBitmap = bitmap;
    }
    public String getFilename() {
        return mFileName;
    }
    public String getCourseCode() {
        return mCourseCode;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }
}
