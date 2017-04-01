package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codekrypt.greendao.db.LocalNote;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.DataUtil;

/**
 * Created by neema on 2017-03-30.
 */
public class LocalNoteAdapter extends ArrayAdapter<LocalNote> {

    private Context context;

    private List<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder {
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteTopic;
        TextView mNoteDate;
    }

    public LocalNoteAdapter(Context context, int resourceId, List<LocalNote> notes) {
        super(context, resourceId, notes);

        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.note_image);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.mNoteTopic = (TextView) convertView.findViewById(R.id.note_topic);
            viewHolder.mNoteDate = (TextView) convertView.findViewById(R.id.note_date);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        LocalNote localNote = (LocalNote) note;

        Date date = localNote.getDate();

        String formattedDate = DataUtil.getFormattedDate(date);

        //String date = localNote.getDate().toString();
        String title = localNote.getTitle();
        String topic = localNote.getTopic();

        if (topic.length() == 0) {
            viewHolder.mNoteTopic.setVisibility(View.GONE);
        }else{
            viewHolder.mNoteTopic.setVisibility(View.VISIBLE);
        }


        viewHolder.mNoteTitle.setText(title);
        viewHolder.mNoteTopic.setText(topic);
        viewHolder.mNoteDate.setText(formattedDate);

        Picasso.with(this.context)
                .load(new File(localNote.getFilePath()))
                .into(viewHolder.mNoteImage);

        //TODO: Do something with the local note here to fetch the imag  e and populate the image view


        return convertView;
    }

}
