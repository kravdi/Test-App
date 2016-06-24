package com.kravdi.applicationa.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kravdi.applicationa.R;
import com.kravdi.applicationa.adapters.LinksAdapter;
import com.kravdi.applicationa.dbutils.DataBaseHelper;
import com.kravdi.applicationa.models.Links;

import java.util.ArrayList;

public class HistorySectionFragment extends Fragment {

     public static final String ACTION_DATABASE_CHANGED = "com.kravdi.applicationa.DATABASE_CHANGED";


    private static HistorySectionFragment fragment;
    public static LinksAdapter adapter;
    public static ArrayList<Links> linksList = new ArrayList<>();
    private SQLiteDatabase database;
    private BroadcastReceiver databaseChangeReceiver;

    public HistorySectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HistorySectionFragment newInstance() {
        if (fragment == null)
            fragment = new HistorySectionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView linksRecycler = (RecyclerView) rootView.findViewById(R.id.links_list);

        adapter = new LinksAdapter(linksList, getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        linksRecycler.setLayoutManager(llm);
        linksRecycler.setAdapter(adapter);

        new GetLinks().execute("");

        databaseChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Cursor cursor = database.query(DataBaseHelper.LINKS_TABLE, null, null,
                        null, null, null, null);

                    linksList.clear();

                if (cursor.moveToFirst()) {
                    do {
                        Links link = cursorToLink(cursor);
                        linksList.add(link);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                adapter.notifyDataSetChanged();
            }
        };
        getActivity().registerReceiver(databaseChangeReceiver, new IntentFilter(ACTION_DATABASE_CHANGED));
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_by_state:
                new GetLinks().execute(DataBaseHelper.STATE_COLUMN);
                break;
            case R.id.sort_by_date:
                new GetLinks().execute(DataBaseHelper.TIME_COLUMN + " DESC");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Links cursorToLink(Cursor cursor) {
        Links link = new Links();
        link.setId(cursor.getInt(0));
        link.setLink(cursor.getString(1));
        link.setState(cursor.getInt(2));
        link.setDate(cursor.getLong(3));

        return link;
    }

    public class GetLinks extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(String... params) {

            if (params[0].isEmpty()) {
                params[0] = null;
            }

            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());
            database = dataBaseHelper.getWritableDatabase();

            Cursor cursor = database.query(DataBaseHelper.LINKS_TABLE, null, null,
                    null, null, null, params[0]);

            if (linksList.size() > 0)
                linksList.clear();

            if (cursor.moveToFirst()) {
                do {
                    Links link = cursorToLink(cursor);
                    linksList.add(link);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (result != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseChangeReceiver != null) {
            getActivity().unregisterReceiver(databaseChangeReceiver);
            databaseChangeReceiver = null;
        }
        database.close();
    }
}