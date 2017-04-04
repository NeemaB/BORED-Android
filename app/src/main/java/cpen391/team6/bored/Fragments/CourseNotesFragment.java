package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
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

import java.io.File;
import java.io.FileInputStream;

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

public class CourseNotesFragment extends Fragment {

    private static final String APP_CLOUD_BUCKET_NAME = "boredpupil-ceed0.appspot.com";
    private static final String APP_CLOUD_ACCOUNT_ID = "bored-633@boredpupil-ceed0.iam.gserviceaccount.com";
    private static String IMAGE_FULL_PATH = "any/path/for/the/image.webp"; // See Object Full Path explanation.

    private List<String> notes;

    private Bitmap bitmap;
    private ByteBuffer buffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.course_notes_fragment_layout, container, false);

        notes = new ArrayList<String>();
        byte[] temp = new byte[6000 * 109];
        buffer = ByteBuffer.wrap(temp);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String test = preferences.getString("classCodePref", "");

        Log.d("TEST", "Classcode: "+test);

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

    @Override
    public void onResume() {
        super.onResume();

    }

}
