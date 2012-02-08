package ru.musicplayer.androidclient.activity;

import ru.musicplayer.androidclient.activity.R;
import android.content.Context;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.musicplayer.androidclient.model.EmptyResult;
import ru.musicplayer.androidclient.model.Model;
import ru.musicplayer.androidclient.model.Result;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 28/01/12
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
public class UploadOnScrollListView {
    private ListView myListView;
    private final int myDownloadStep;
    private final int myModelType;
    private Context myContext;
    private MusicApplication myApplication;
    private String mySearchString = "";
    private boolean isAppendable = false;
    private int myOffset = 0;

    public UploadOnScrollListView (int type, int downloadStep) {
        myListView = null;
        myDownloadStep = downloadStep;
        myModelType = type;
    }
    
    public void setListView (ListView listView) {
        if (myListView != null)
            return;
        myContext = listView.getContext();
        myApplication = (MusicApplication) listView.getContext().getApplicationContext();
        myListView = listView;
        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isAppendable && (totalItemCount - 2*visibleItemCount <= firstVisibleItem)) {
                    Result myResult;
                    try {
                        myResult = Request.search(mySearchString, myDownloadStep, myOffset, myModelType);
                    } catch (IOException e) {
                        isAppendable = false;
                        myApplication.showErrorMessage("Search", e.getMessage());
                        return;
                    }
                    if (myResult != null && !myResult.isEmpty()) {
                        Model[] additional = myResult.getModelsOfType(myModelType);
                        myOffset += additional.length;
                        OpenableArrayAdapter adapter = (OpenableArrayAdapter) myListView.getAdapter();
                        adapter.append(additional);
                    } else {
                        isAppendable = false;
                    }
                }
            }
        });
    }

    public void reset (int offset, String searchString, Model... objects) {
        if (objects == null || objects.length == 0) {
            myListView.setAdapter(new ArrayAdapter<Model>(myContext, R.layout.unplayable, new Model[] {new EmptyResult("No items were found.")}));
            myOffset = 0;
            isAppendable = false;
        } else {
            myListView.setAdapter(new SearchArrayAdapter(myContext, objects));
            myOffset = offset;
            isAppendable = true;
        }
        //isAppendable = (myOffset == 2*myDownloadStep);
        mySearchString = searchString;
    }

    public void reset () {
        myListView.setAdapter(new ArrayAdapter<Model>(myContext, R.layout.unplayable, new Model[] {new EmptyResult("No items were found.")}));
        isAppendable = false;
    }
}
