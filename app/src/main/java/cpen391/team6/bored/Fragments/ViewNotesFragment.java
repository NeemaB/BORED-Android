package cpen391.team6.bored.Fragments;

import android.app.Fragment;
//import android.os.AsyncTask;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
<<<<<<< Updated upstream:app/src/main/java/cpen391/team6/bored/Fragments/ViewNotesFragment.java
//import android.util.Log;
=======
import android.support.annotation.NonNull;
>>>>>>> Stashed changes:app/src/main/java/cpen391/team6/bored/Fragments/CourseNotesFragment.java
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

<<<<<<< Updated upstream:app/src/main/java/cpen391/team6/bored/Fragments/ViewNotesFragment.java
import java.net.URLEncoder;
import java.util.Collections;
=======
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
>>>>>>> Stashed changes:app/src/main/java/cpen391/team6/bored/Fragments/CourseNotesFragment.java
import java.util.List;

import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudImageCRUD;
import cpen391.team6.bored.Utility.CloudStorage;
import cpen391.team6.bored.Utility.CredentialBuilder;


/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */

public class CourseNotesFragment extends Fragment {

    private static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    private static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";
    private static String IMAGE_FULL_PATH = "any/path/for/the/image.webp"; // See Object Full Path explanation.

    private List<String> notes;

    private Bitmap bitmap;
    private ByteBuffer buffer;

    private static final String PROJECT_ID_PROPERTY = "boredpupil-ceed0";
    private static final String APPLICATION_NAME_PROPERTY = "Bored";
    private static final String ACCOUNT_ID_PROPERTY = "ceed0.iam.gserviceaccount.com";
    private static final String PRIVATE_KEY = "4e5871a85d417a144cb3413750ca62f49edb78ca";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);

        notes = new ArrayList<String>();
        byte[] temp = new byte[6000 * 109];
        buffer = ByteBuffer.wrap(temp);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Context context = getActivity().getApplicationContext();
                    Credential credential = CredentialBuilder
                            .setup(context, R.raw.key, APP_CLOUD_ACCOUNT_ID)
                            .transporter(new NetHttpTransport())
                            .scope(CredentialBuilder.CredentialScope.DEVSTORAGE_READ_WRITE)
                            .build();

                    CloudStorage cloudStorage = CloudStorage.build(APP_CLOUD_BUCKET_NAME, credential);

                    notes = CloudImageCRUD.listBucketContents(cloudStorage, APP_CLOUD_BUCKET_NAME);

                    File bm1 = CloudImageCRUD.readCloudImageSegment(context, cloudStorage, "AAAA/fdds-0.bmp");
                    File bm2 = CloudImageCRUD.readCloudImageSegment(context, cloudStorage, "AAAA/fdds-1.bmp");

                    Log.d("TEST", Long.toString(bm1.length()));
                    Log.d("TEST", Long.toString(bm2.length()));

                    OutputStream os = new FileOutputStream(bm1, true);
                    InputStream is = new FileInputStream(bm2);
                    byte[] b = new byte[(int)bm2.length()];
                    int test = is.read(b);
                    os.write(b);

                    os.close();
                    is.close();

                    is = new FileInputStream(bm1);
                    bitmap = BitmapFactory.decodeStream(is);

                    Log.d("TEST", Long.toString(bm1.length()));
                    Log.d("TEST", Long.toString(bm2.length()));

                    for (String name : notes) {
                        Log.d("TEST", name);
                    }


                } catch (Exception e) {
                    Log.d("TEST", e.getMessage());
                }
            }
        });

        thread.start();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        while(bitmap == null){
            //
            // Log.d("TEST", "Waiting");
        }

        ImageView mImg = (ImageView) getView().findViewById(R.id.imageView);
        mImg.setImageBitmap(bitmap);

    }
}
