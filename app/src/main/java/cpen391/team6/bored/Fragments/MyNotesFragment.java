package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.codekrypt.greendao.db.LocalNote;
import com.codekrypt.greendao.db.LocalNoteDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpen391.team6.bored.Adapters.LocalNoteAdapter;
import cpen391.team6.bored.Adapters.NoteImageAdapter;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-28.
 */
public class MyNotesFragment extends Fragment {

    private ListView mNotesListView;
    private List<LocalNote> mNotesList;
    private LocalNoteAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_notes_fragment_layout, container, false);


        mNotesListView = (ListView) view.findViewById(R.id.my_notes_list);

        LocalNoteDao localNoteDao = BoredApplication.getDaoSession().getLocalNoteDao();
        QueryBuilder<LocalNote> qb = localNoteDao.queryBuilder();
        mNotesList = qb.list();

        mAdapter = new LocalNoteAdapter(getActivity(), R.layout.note_list_item, mNotesList);


        mNotesListView.setAdapter(mAdapter);

        mNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Do nothing
            }
        });


        return view;

    }
}
