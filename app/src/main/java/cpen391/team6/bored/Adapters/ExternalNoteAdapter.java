package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class ExternalNoteAdapter extends ArrayAdapter<ExternalNote>{
    private Context mContext;
    private String mCourseCode;

    private List<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder {
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteDate;
//        TextView mNoteCourseCode;
        IconTextView mNoteActions; // Do we need this??
    }

    public  ExternalNoteAdapter(Context context, int resourceId, String courseCode, List<ExternalNote> notes) {
        super(context, resourceId, notes);
        this.mContext = context;
        this.mCourseCode = courseCode;
        this.notes = notes;
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
            viewHolder.mNoteActions = (IconTextView) convertView.findViewById(R.id.note_item_actions);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final ExternalNote externalNote = (ExternalNote) note;

        String title = externalNote.getFilename();
//        String courseCode = externalNote.getCourseCode();


        /* WE DON'T HAVE ANYTHING THAT REQUIRES THIS*/
//        if (topic.length() == 0) {
//            viewHolder.mNoteTopic.setVisibility(View.GONE);
//        }else{
//            viewHolder.mNoteTopic.setVisibility(View.VISIBLE);
//        }


        viewHolder.mNoteTitle.setText(title);

        // Need one for course code...

        viewHolder.mNoteActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, viewHolder.mNoteActions);

                popup.getMenuInflater().inflate(R.menu.note_action_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){

                            case R.id.load_my_note:

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


    private void getCourseNotes() {


    }



    private File cloudFile1;
    private File cloudFile2;


    public void getCloudFile(final String filename, final boolean first) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(first){
                        cloudFile1 = CloudImageCRUD.readCloudImageSegment(getActivity().getApplicationContext(), mCloudStorage, filename);

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
}
