package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.joanzapata.iconify.widget.IconTextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import cpen391.team6.bored.Adapters.ExternalNoteAdapter;
import cpen391.team6.bored.BoredApplication;
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
//    private List<ExternalNote> BoredApplication.mCourseNotes;
    private ExternalNoteAdapter mAdapter;

    private FetchFileNamesTask mFileNamesTask;
    private InitCloudStorageTask mCloudStorageTask;

    private static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    private static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";

    private String mCourseCode;
    private ArrayList<String> mFilenames;
    private SharedPreferences mSharedPrefs;

    private TextView mNoNotesMessage;
    private IconTextView mNoNotesIcon;
    private RelativeLayout mNoCourseNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.course_notes_fragment_layout, container, false);

        mCourseNotesListView = (ListView) view.findViewById(R.id.course_notes_list);
        mNoNotesMessage = (TextView) view.findViewById(R.id.no_course_notes_message);
        mNoNotesIcon = (IconTextView) view.findViewById(R.id.no_course_notes_icon);
        mNoCourseNotes = (RelativeLayout) view.findViewById(R.id.no_course_notes);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mCourseCode = mSharedPrefs.getString("classCodePref", "");


        mFileNamesTask = new FetchFileNamesTask() {
            @Override
            public void onPostExecute(Void result) {

                if(BoredApplication.mCourseNotes.isEmpty()){
                    mCourseNotesListView.setVisibility(View.GONE);
                    mNoCourseNotes.setVisibility(View.VISIBLE);

                }else {
                    mCourseNotesListView.setVisibility(View.VISIBLE);
                    mNoCourseNotes.setVisibility(View.GONE);
                    mAdapter = new ExternalNoteAdapter(getContext(), R.layout.note_list_item, mCourseCode, BoredApplication.mCourseNotes);
                    mCourseNotesListView.setAdapter(mAdapter);
                }
            }
        };

        if(BoredApplication.mCloudStorage == null) {

            mCloudStorageTask = new InitCloudStorageTask(){

                @Override
                public void onPostExecute(Void result) {
                    mFileNamesTask.execute();

                }
            };

            mCloudStorageTask.execute();

        }else if (BoredApplication.mCourseNotes == null){

            mFileNamesTask.execute();

        }else{

            if(BoredApplication.mCourseNotes.isEmpty()){
                mCourseNotesListView.setVisibility(View.GONE);
                mNoCourseNotes.setVisibility(View.VISIBLE);

            }else {
                mCourseNotesListView.setVisibility(View.VISIBLE);
                mNoCourseNotes.setVisibility(View.GONE);
                mAdapter = new ExternalNoteAdapter(getContext(), R.layout.note_list_item, mCourseCode, BoredApplication.mCourseNotes);
                mCourseNotesListView.setAdapter(mAdapter);
            }
        }

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

    @Override
    public void onStop(){
        super.onStop();
        if(mFileNamesTask != null)
            mFileNamesTask.cancel(true);

        if(mCloudStorageTask != null)
            mCloudStorageTask.cancel(true);
    }


    private void getFileNames() {

        if (BoredApplication.mCloudStorage == null) {
            return;
        }

        try {
            mFilenames = CloudImageCRUD.listBucketContents(BoredApplication.mCloudStorage, APP_CLOUD_BUCKET_NAME);
        } catch (Exception e) {
            Log.d("TEST", e.getMessage());
        }
        

        for (String file : mFilenames) {
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


    private void createExternalNoteList() {

        DateTime dateTime = null;
        if(BoredApplication.mCourseNotes != null){
            BoredApplication.mCourseNotes.clear();
        }else{
            BoredApplication.mCourseNotes = new ArrayList<>();
        }

//        String courseCode = mSharedPrefs.getString("classCodePref", "");
        ArrayList<String> files = getMatchingStrings(mFilenames, mCourseCode + "/[a-z]+-[0-9]+.bmp");

        if(files == null){
            return;
        }

        ArrayList<String> remaining = new ArrayList<String>(files);
        for (String file : files) {
            if(remaining.contains(file)){
                String name = file.substring(mCourseCode.length() + 1, file.length() - 6);
                ArrayList<String> fileSegs = getMatchingStrings(files, mCourseCode + "/" + name + "-[0-9]+.bmp");
                remaining.removeAll(fileSegs);

                try {
                    dateTime = CloudImageCRUD.getFileDateTime(BoredApplication.mCloudStorage, APP_CLOUD_BUCKET_NAME, file);
                }catch(Exception e){e.printStackTrace();}

                BoredApplication.mCourseNotes.add(makeExternalNote(fileSegs, mCourseCode, dateTime));
            }
        }

        Log.d("TEST", "# of pics: " + Integer.toString(BoredApplication.mCourseNotes.size()));

    }

    private ExternalNote makeExternalNote(final ArrayList<String> filenames, String coursecode, DateTime dateTime) {


        return new ExternalNote(
                filenames.get(0).substring(coursecode.length() + 1, filenames.get(0).length() - 6),
                filenames,
                coursecode,
                dateTime);

    }


    private class InitCloudStorageTask extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void ... params) {

            try {

                BoredApplication.mCred = CredentialBuilder
                        .setup(getActivity().getApplicationContext(), R.raw.key, APP_CLOUD_ACCOUNT_ID)
                        .transporter(new NetHttpTransport())
                        .scope(CredentialBuilder.CredentialScope.DEVSTORAGE_READ_WRITE)
                        .build();

                BoredApplication.mCloudStorage = CloudStorage.build(APP_CLOUD_BUCKET_NAME, BoredApplication.mCred);

//                getFileNames();
//                createExternalNoteList();

                for (String name : mFilenames) {
                    Log.d("TEST", name);
                }

            } catch (Exception e) {
                Log.d("TEST", e.getMessage());
            }

            return null;
        }

    }

    private class FetchFileNamesTask extends AsyncTask <Void, Void, Void> {

        @Override
        public Void doInBackground(Void ... params){
            getFileNames();
            createExternalNoteList();

            return null;
        }
    }



}
