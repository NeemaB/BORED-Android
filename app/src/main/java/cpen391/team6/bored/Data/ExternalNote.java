package cpen391.team6.bored.Data;

import android.graphics.Bitmap;

import com.google.api.client.util.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andytertzakian on 2017-03-16.
 */

public class ExternalNote extends Note {

    private String mTitle;
    private ArrayList<String> mFileNames;
    private String mCourseCode;
    private DateTime mDateTime;

    public ExternalNote(String filename, ArrayList<String> filenames, String courseCode, DateTime dateTime){
        this.mTitle = filename;
        this.mFileNames = filenames;
        this.mCourseCode = courseCode;
        this.mDateTime = dateTime;
    }
    public String getTitle() {
        return mTitle;
    }

    public ArrayList <String> getFileNames() { return mFileNames;}

    public String getCourseCode() {
        return mCourseCode;
    }

    public DateTime getDateTime(){
        return  mDateTime;
    }
}
