package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.codekrypt.greendao.db.LocalNote;
import com.codekrypt.greendao.db.LocalNoteDao;
import com.google.api.client.util.DateTime;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudImageCRUD;
import cpen391.team6.bored.Utility.DataUtil;

/**
 * Created by neema on 2017-04-04.
 */
public class ExternalNoteAdapter extends ArrayAdapter<ExternalNote> {
    private Context mContext;
    private String mCourseCode;

    private Bitmap [] cachedBitmaps;
    private List<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder {
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteTopic;
        TextView mNoteDate;
        //        TextView mNoteCourseCode;
        IconTextView mNoteActions; // Do we need this??
    }

    public ExternalNoteAdapter(Context context, int resourceId, String courseCode, List<ExternalNote> notes) {
        super(context, resourceId, notes);
        this.mContext = context;
        this.mCourseCode = courseCode;
        this.notes = notes;

        cachedBitmaps = new Bitmap [notes.size()];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.note_image);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.mNoteTopic = (TextView) convertView.findViewById(R.id.note_topic);
            viewHolder.mNoteDate = (TextView) convertView.findViewById(R.id.note_date);
            viewHolder.mNoteActions = (IconTextView) convertView.findViewById(R.id.note_item_actions);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ExternalNote externalNote = (ExternalNote) note;
        viewHolder.mNoteImage.setTag(position);

        DateTime dateTime = externalNote.getDateTime();
        Date date = new Date(dateTime.getValue());

        DateFormat df = new SimpleDateFormat("MMM d, h:mm a");
        String formattedDate = df.format(date);
        String title = externalNote.getTitle();

        viewHolder.mNoteImage.setImageBitmap(null);
        viewHolder.mNoteTopic.setVisibility(View.GONE);
        viewHolder.mNoteTitle.setText(title);
        viewHolder.mNoteDate.setText(formattedDate);


        if(cachedBitmaps[position] == null) {
            new AsyncTask<Integer, Integer, Bitmap>() {

                @Override
                public Bitmap doInBackground(Integer... params) {

                    ArrayList<String> fileSegs = externalNote.getFileNames();
                    File cloudFile1 = null;
                    File cloudFile2 = null;
                    for (int i = 0; i < fileSegs.size(); i++) {
                        publishProgress(params[0]);
                        if (i == 0) {
                            cloudFile1 = getCloudFile(fileSegs.get(i));
                        } else {
                            cloudFile2 = getCloudFile(fileSegs.get(i));
                            combineFiles(cloudFile1, cloudFile2);
                        }
                    }
                    publishProgress(params[0]);
                    if(!isCancelled()) {
                        Bitmap bm = getBitmap(cloudFile1);
                        cachedBitmaps[position] = bm;
                        return bm;
                    }
                    return null;

                }

                @Override
                public void onProgressUpdate(Integer... tags) {

                    if (!tags[0].equals(viewHolder.mNoteImage.getTag())) {
                        cancel(true);
                    }
                }

                @Override
                public void onPostExecute(Bitmap result) {

                    loadBitmap(viewHolder.mNoteImage, result, 200);

                }

            }.execute((Integer) viewHolder.mNoteImage.getTag());
        }else{
            loadBitmap(viewHolder.mNoteImage, cachedBitmaps[position], 450);
        }

        // Need one for course code...

        viewHolder.mNoteActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, viewHolder.mNoteActions);

                popup.getMenuInflater().inflate(R.menu.note_action_popup_menu, popup.getMenu());

                popup.getMenu().findItem(R.id.delete_my_note).setVisible(false);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.load_my_note:


                                if(cachedBitmaps[position] != null){

                                }
//                                Bundle arguments = new Bundle();
//                                arguments.putString("load_note_path", externalNote.getBitmap());
//
//                                ((MainActivity) context).loadNote(arguments);
                                break;

                        }
                        return false;
                    }
                });
                /*Display the popup */
                popup.show();
            }
        });

        /* Load the image asynchronously from local storage */
//        Picasso.with(this.context)
//                .load(externalNote.getBitmap()) //Can we load bitmaps here?
//                .into(viewHolder.mNoteImage);


        //TODO: Do something with the local note here to fetch the imag  e and populate the image view


        return convertView;
    }


    public File getCloudFile(final String filename) {

        try {
            return CloudImageCRUD.readCloudImageSegment(mContext, BoredApplication.mCloudStorage, filename);

        } catch (Exception e) {
            Log.d("TEST", e.getMessage());
        }
        return null;
    }

    private void combineFiles(File cloudFile1, File cloudFile2) {
        try {

            Log.d("TEST", "Before: " + Long.toString(cloudFile1.length()));

            OutputStream os = new FileOutputStream(cloudFile1, true);
            InputStream is = new FileInputStream(cloudFile2);

            byte[] b = new byte[(int) cloudFile2.length()];
            is.read(b);
            os.write(b);

            os.close();
            is.close();

            Log.d("TEST", "After: " + Long.toString(cloudFile1.length()));

        } catch (Exception e) {
            Log.d("TEST", e.getMessage());
        }
    }

    private Bitmap getBitmap(File cloudFile) {

        Bitmap bitmap = null;
        try {
            InputStream is = new FileInputStream(cloudFile);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.d("TEST", e.getMessage());
        }

        return bitmap;
    }

    private void loadBitmap(ImageView imageView, Bitmap bm, int animDuration){


        imageView.setImageBitmap(bm);

        imageView.setAlpha(0f);
        imageView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        imageView.animate()
                .alpha(1f)
                .setDuration(animDuration)
                .setListener(null);

    }

//    private class FetchAndLoadImageTask extends AsyncTask<String, Void, Void>{
//
//        @Override
//        public Void doInBackground(String ... params){
//
//
//        }
//
//        @Override
//        public void onPostExecute(Void result){
//
//        }
//    }

}
