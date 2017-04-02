package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.List;
import java.util.Map;

import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudImageCRUD;
import cpen391.team6.bored.Utility.CloudStorage;
import cpen391.team6.bored.Utility.CredentialBuilder;

/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */
public class CourseNotesFragment extends Fragment{

    public static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    public static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";
    public static String IMAGE_FULL_PATH = "any/path/for/the/image.webp"; // See Object Full Path explanation.

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.course_notes_fragment_layout, container, false);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {

                    Context context = getActivity().getApplicationContext();
                    Credential credential = CredentialBuilder
                            .setup(context, R.raw.key, APP_CLOUD_ACCOUNT_ID)
                            .transporter(new NetHttpTransport())
                            .scope(CredentialBuilder.CredentialScope.DEVSTORAGE_READ_WRITE)
                            .build();

                    CloudStorage cloudStorage = CloudStorage.build(APP_CLOUD_BUCKET_NAME, credential);

                    List<String> names = CloudImageCRUD.listBucketContents(cloudStorage, APP_CLOUD_BUCKET_NAME);

                    for(String name : names){
                        Log.d("TEST", name);
                    }

                }catch(Exception e){
                    Log.d("TEST", e.getMessage());
                }
            }

        });

        thread.start();

        return view;
    }
}
