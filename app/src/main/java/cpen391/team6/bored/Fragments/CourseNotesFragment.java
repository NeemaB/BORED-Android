package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import cpen391.team6.bored.Adapters.NoteImageAdapter;
import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudImageCRUD;
import cpen391.team6.bored.Utility.CloudStorage;
import cpen391.team6.bored.Utility.CredentialBuilder;

/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */

public class CourseNotesFragment extends Fragment {

    private ListView mCourseNotesListView;
    private List<ExternalNote> mCourseNotesList;
    private NoteImageAdapter mAdapter;

    private static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    private static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";

    private ArrayList<String> mFilenames;
    private Credential mCred;
    private CloudStorage mCloudStorage;
    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.course_notes_fragment_layout, container, false);

        mCourseNotesListView = (ListView) view.findViewById(R.id.course_notes_list);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mCourseNotesList = new ArrayList<ExternalNote>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mCred = CredentialBuilder
                            .setup(getActivity().getApplicationContext(), R.raw.key, APP_CLOUD_ACCOUNT_ID)
                            .transporter(new NetHttpTransport())
                            .scope(CredentialBuilder.CredentialScope.DEVSTORAGE_READ_WRITE)
                            .build();

                    mCloudStorage = CloudStorage.build(APP_CLOUD_BUCKET_NAME, mCred);

                } catch (Exception e) {
                    Log.d("TEST", e.getMessage());
                }
            }

        });

        thread.start();

        while(thread.getState() != Thread.State.TERMINATED){
            //Waiting for above thread to finish
            //Log.d("TEST", "Waiting for init");
        }

        getFileNames();
        getCourseNotes();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getCourseNotes() {

        if(mCourseNotesList != null){
            mCourseNotesList.clear();
        }

        String courseCode = mSharedPrefs.getString("classCodePref", "");
        ArrayList<String> files = getMatchingStrings(mFilenames, courseCode + "/[a-z]+-[0-9]+.bmp");

        if(files == null){
            return;
        }

        ArrayList<String> remaining = new ArrayList<String>(files);
        for (String file : files) {
            if(remaining.contains(file)){
                String name = file.substring(courseCode.length() + 1, file.length() - 6);
                ArrayList<String> fileSegs = getMatchingStrings(files, courseCode + "/" + name + "-[0-9]+.bmp");
                remaining.removeAll(fileSegs);

                mCourseNotesList.add(getExternalNote(fileSegs, courseCode));
            }
        }

        Log.d("TEST", "# of pics: " + Integer.toString(mCourseNotesList.size()));
    }

    private ExternalNote getExternalNote(final ArrayList<String> filenames, String coursecode) {

        for(int i = 0; i < filenames.size(); i++){
            if(i == 0){
                getCloudFile(filenames.get(i), true);
            }else{
                getCloudFile(filenames.get(i), false);
                combineFiles();
            }
        }

        Bitmap extBitmap = getBitmap();

        return new ExternalNote(
                filenames.get(0).substring(coursecode.length() + 1, filenames.get(0).length() - 6),
                filenames,
                coursecode,
//                extBitmap,
                dateTime
                );
    }

    private File cloudFile1;
    private File cloudFile2;
    private DateTime dateTime;
    public void getCloudFile(final String filename, final boolean first) {

        dateTime = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(first){
                        cloudFile1 = CloudImageCRUD.readCloudImageSegment(getActivity().getApplicationContext(), mCloudStorage, filename);
                        dateTime = CloudImageCRUD.getFileDateTime(mCloudStorage, APP_CLOUD_BUCKET_NAME, filename);
                    }else{
                        cloudFile2 = CloudImageCRUD.readCloudImageSegment(getActivity().getApplicationContext(), mCloudStorage, filename);
                    }
                } catch (Exception e) {
                    Log.d("TEST", e.getMessage());
                }
            }
        });

        thread.start();

        while(thread.getState() != Thread.State.TERMINATED){
            //Log.d("TEST", "Waiting to get cloudfile: " + filename);
        }
    }

    private void combineFiles(){
        try{

            Log.d("TEST", "Before: " + Long.toString(cloudFile1.length()));

            OutputStream os = new FileOutputStream(cloudFile1, true);
            InputStream is = new FileInputStream(cloudFile2);

            byte[] b = new byte[(int)cloudFile2.length()];
            is.read(b);
            os.write(b);

            os.close();
            is.close();

            Log.d("TEST", "After: " +  Long.toString(cloudFile1.length()));

        }catch(Exception e){
            Log.d("TEST", e.getMessage());
        }
    }

    private Bitmap getBitmap(){

        Bitmap bitmap = null;
        try {
            InputStream is = new FileInputStream(cloudFile1);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.d("TEST", e.getMessage());
        }

        return bitmap;
    }

    private void getFileNames() {

        if (mCloudStorage == null) {
            return;
        }

        Boolean test;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mFilenames = CloudImageCRUD.listBucketContents(mCloudStorage, APP_CLOUD_BUCKET_NAME);
                } catch (Exception e) {
                    Log.d("TEST", e.getMessage());
                }
            }
        });

        thread.start();
        while(thread.getState() != Thread.State.TERMINATED){
            //wait for thread to finish
            //Log.d("TEST", "Waiting for getFileNames");
        }

        for(String file : mFilenames){
            Log.d("TEST", file);
        }
    }

    ArrayList<String> getMatchingStrings(ArrayList<String> list, String regex) {

        if(list == null){
            return null;
        }

        ArrayList<String> matches = new ArrayList<String>();

        Pattern p = Pattern.compile(regex);

        for (String s : list) {
            if (p.matcher(s).matches()) {
                matches.add(s);
            }
        }

        return matches;
    }

}
