package com.monuments.mnmts;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.monuments.mnmts.data.MonumentsContract;
import com.monuments.mnmts.sync.MonumentsSyncAdapter;

public class MonumentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MONUMENTS_LOADER = 0;

    private static final String[] MONUMENTS_COLUMNS = {
            MonumentsContract.MonumentsEntry._ID,
            MonumentsContract.MonumentsEntry.COLUMN_NIMI,
            MonumentsContract.MonumentsEntry.COLUMN_PAATOSPAIVA
    };

    static final int COL_MONUMENTS_ID = 0;
    static final int COL_MONUMENTS_NAME = 1;
    static final int COL_MONUMENTS_PAATOSPAIVA = 2;

    private MonumentsAdapter mMonumentsAdapter;

    public MonumentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.monumentsfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMonumentsAdapter = new MonumentsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mMonumentsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
        Intent intent = new Intent(getActivity(), DetailActivity.class)
                .setData(MonumentsContract.MonumentsEntry.buildMonumentsWithName(
                        cursor.getString(COL_MONUMENTS_NAME)
                ));
            startActivity(intent);
         }
        }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MONUMENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateArrayAdapter() {
        MonumentsSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateArrayAdapter();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = MonumentsContract.MonumentsEntry.COLUMN_NIMI + " ASC";
        Uri contentUri = MonumentsContract.MonumentsEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                                contentUri,
                                MONUMENTS_COLUMNS,
                                null,
                                null,
                                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMonumentsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMonumentsAdapter.swapCursor(null);
    }
}