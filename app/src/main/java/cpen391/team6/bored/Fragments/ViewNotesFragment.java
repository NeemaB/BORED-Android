package cpen391.team6.bored.Fragments;

import android.app.Fragment;
//import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

//import com.google.appengine.tools.cloudstorage.GcsFileOptions;
//import com.google.appengine.tools.cloudstorage.GcsFilename;
//import com.google.appengine.tools.cloudstorage.GcsInputChannel;
//import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
//import com.google.appengine.tools.cloudstorage.GcsService;
//import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
//import com.google.appengine.tools.cloudstorage.RetryParams;
//import com.google.cloud.datastore.Datastore;
//import com.google.cloud.datastore.DatastoreOptions;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudImageCRUD;
import cpen391.team6.bored.Utility.CloudStorage;
import cpen391.team6.bored.Utility.CredentialBuilder;


/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */
public class ViewNotesFragment extends Fragment{

    public static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    public static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";
    public static String IMAGE_FULL_PATH = "any/path/for/the/image.webp"; // See Object Full Path explanation.

    private static final String PROJECT_ID_PROPERTY = "boredpupil-ceed0";
    private static final String APPLICATION_NAME_PROPERTY = "Bored";
    private static final String ACCOUNT_ID_PROPERTY = "ceed0.iam.gserviceaccount.com";
    private static final String PRIVATE_KEY = "4e5871a85d417a144cb3413750ca62f49edb78ca";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);

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
                    //Log.d("TEST", e.printStackTrace());
                    e.printStackTrace();
                }
            }

        });

        thread.start();

        return view;
    }
}
