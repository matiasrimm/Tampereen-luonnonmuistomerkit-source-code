package com.monuments.mnmts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.monuments.mnmts.data.MonumentsContract.MonumentsEntry;

public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private GoogleMap map;

    private TextView mNameView;
    private TextView mNumberView;
    private TextView mDateView;
    private TextView mFirstAdditionalView;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MONUMENTS_COLUMNS = {
        // columns to fill the views
        MonumentsEntry._ID,
        MonumentsEntry.COLUMN_EKAKOORDINAATTI,
        MonumentsEntry.COLUMN_TOKAKOORDINAATTI,
        MonumentsEntry.COLUMN_LISATIEDOT1,
        MonumentsEntry.COLUMN_LISATIEDOT2,
        MonumentsEntry.COLUMN_KOHTEENKUVAUS1,
        MonumentsEntry.COLUMN_KOHTEENKUVAUS2,
        MonumentsEntry.COLUMN_NIMI,
        MonumentsEntry.COLUMN_PAATOSNUMERO,
        MonumentsEntry.COLUMN_PAATOSPAIVA
    };

    static final int COL_MONUMENTS_ID = 0;
    static final int COL_MONUMENTS_KOORDINAATTI1 = 1;
    static final int COL_MONUMENTS_KOORDINAATTI2 = 2;
    static final int COL_MONUMENTS_LISATIEDOT1 = 3;
    static final int COL_MONUMENTS_LISATIEDOT2 = 4;
    static final int COL_MONUMENTS_KUVAUS1 = 5;
    static final int COL_MONUMENTS_KUVAUS2 = 6;
    static final int COL_MONUMENTS_NIMI = 7;
    static final int COL_MONUMENTS_PAATOSNUMERO = 8;
    static final int COL_MONUMENTS_PAATOSPAIVA = 9;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mNameView = (TextView) rootView.findViewById(R.id.nimi_text);
        mNumberView = (TextView) rootView.findViewById(R.id.paatosnumero_text);
        mDateView = (TextView) rootView.findViewById(R.id.paatospaiva_text);
        mFirstAdditionalView = (TextView) rootView.findViewById(R.id.ekatlisatiedot_text);


        // google maps
        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MONUMENTS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        Float nKoordinaatti = data.getFloat(COL_MONUMENTS_KOORDINAATTI1);
        Float eKoordinaatti = data.getFloat(COL_MONUMENTS_KOORDINAATTI2);

        String nameString = data.getString(COL_MONUMENTS_NIMI);
        String numberString = data.getString(COL_MONUMENTS_PAATOSNUMERO);
        String dateString = data.getString(COL_MONUMENTS_PAATOSPAIVA);

        String firstAddString = data.getString(COL_MONUMENTS_LISATIEDOT1);
        String secondAddString = data.getString(COL_MONUMENTS_LISATIEDOT2);
        String firstDescString = data.getString(COL_MONUMENTS_KUVAUS1);
        String secondDescString = data.getString(COL_MONUMENTS_KUVAUS2);

        if(numberString.equals("null")){numberString = "-";}
        if(dateString.equals("null")){dateString = "-";}
        if(firstAddString.equals("null")){firstAddString = "";}
        if(secondAddString.equals("null")){secondAddString = "";}
        if(firstDescString.equals("null")){firstDescString = "";}
        if(secondDescString.equals("null")){secondDescString = "";}

        mNameView.setText(nameString);
        mNumberView.setText(numberString);
        mDateView.setText(dateString);

        mFirstAdditionalView.setText(firstDescString + secondDescString + " " + firstAddString + secondAddString);

        LatLng SingleCoordinateObject = new LatLng(eKoordinaatti,nKoordinaatti);
        Marker SingleMarker = map.addMarker(new MarkerOptions().position(SingleCoordinateObject)
                .title(nameString));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SingleCoordinateObject, 10));
        SingleMarker.showInfoWindow();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}