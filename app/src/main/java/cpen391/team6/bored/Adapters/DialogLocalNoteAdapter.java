package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Date;
import java.util.List;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.DataUtil;

/**
 * Created by neema on 2017-04-03.
 */
public class DialogLocalNoteAdapter extends ArrayAdapter<LocalNote> {

    private Context context;

    private List<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder {
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteDate;
    }

    public DialogLocalNoteAdapter(Context context, int resourceId, List<LocalNote> notes) {
        super(context, resourceId, notes);

        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.note_image);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.mNoteDate = (TextView) convertView.findViewById(R.id.note_date);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final LocalNote localNote = (LocalNote) note;

        Date date = localNote.getDate();

        String formattedDate = DataUtil.getFormattedDate(date);

        //String date = localNote.getDate().toString();
        String title = localNote.getTitle();
//        String topic = localNote.getTopic();

//        if (topic.length() == 0) {
//            viewHolder.mNoteTopic.setVisibility(View.GONE);
//        }else{
//            viewHolder.mNoteTopic.setVisibility(View.VISIBLE);
//        }


        viewHolder.mNoteTitle.setText(title);
//        viewHolder.mNoteTopic.setText(topic);
        viewHolder.mNoteDate.setText(formattedDate);
        /* Load the image asynchronously from local storage */
        Picasso.with(this.context)
                .load(new File(localNote.getFilePath()))
                .into(viewHolder.mNoteImage);

        //TODO: Do something with the local note here to fetch the imag  e and populate the image view


        return convertView;
    }

}
