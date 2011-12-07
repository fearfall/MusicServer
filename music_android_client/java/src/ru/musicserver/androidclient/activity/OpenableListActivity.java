package ru.musicserver.androidclient.activity;

import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.musicserver.androidclient.model.EmptyResult;
import ru.musicserver.androidclient.model.Model;
import ru.musicserver.androidclient.model.Track;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/2/11
 * Time: 3:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OpenableListActivity extends ListActivity {
    protected ListView myListView;

    protected void showErrorMessage (String title, String message) {
        MainActivity.showErrorMessage(title, message, this);
    }

    protected void setAdapter (ArrayAdapter<Model> adapter) {
        myListView.setAdapter(adapter);
    }

    protected void setOnItemClickListener () {
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Model item = (Model)adapterView.getItemAtPosition(position);
                if (item instanceof Track) {
                    MainActivity.trackClicked((Track)item, OpenableListActivity.this);
                    return;
                }
                if (item instanceof EmptyResult)
                    return;

                ArrayAdapter<Model> adapter = (ArrayAdapter<Model>) adapterView.getAdapter();
                if (AdapterHelper.isOpened(adapter, position)) {
                    AdapterHelper.close(adapter, position);
                } else {
                    try {
                        AdapterHelper.open(adapter, position);
                    } catch (IOException e) {
                        showErrorMessage("Open list item", e.getMessage());
                        return;
                    }
                }
                setAdapter(adapter);
            }
        });
    }
}
