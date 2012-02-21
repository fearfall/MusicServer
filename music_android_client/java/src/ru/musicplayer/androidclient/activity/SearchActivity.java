package ru.musicplayer.androidclient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.musicplayer.androidclient.model.Result;
import ru.musicplayer.androidclient.model.ResultCount;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends Activity {
    private final int offsetStep = 8;
    private UploadOnScrollListView myArtists;
    private UploadOnScrollListView myAlbums;
    private UploadOnScrollListView myTracks;
    private TabHost myTabHost;
    private EditText mySearchEdit;
    private MusicApplication myApplication;

    private View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
    
    private void setTabCaption (int k, final String caption) {
        TextView tv = (TextView)myTabHost.getTabWidget().getChildTabViewAt(k).findViewById(R.id.tabsText);
        tv.setText(caption);
    }

    private void setupTab(final View view, final String tag) {
        View tabView = createTabView(myTabHost.getContext(), tag);
        TabHost.TabSpec setContent = myTabHost.newTabSpec(tag).setIndicator(tabView).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return view;
            }
        });
        myTabHost.addTab(setContent);
    }

    private void initByList (int resourceId, UploadOnScrollListView uploadOnScrollListView, String tag) {
        ListView listView = (ListView)findViewById(resourceId);
        uploadOnScrollListView .setListView(listView);
        setupTab(listView, tag);
    }
    
     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        myApplication = ((MusicApplication)getApplication());
        myApplication.register(this);

        myTabHost = (TabHost) findViewById(android.R.id.tabhost);
        myTabHost.setup();

        myArtists = new UploadOnScrollListView(MusicApplication.ARTISTS, offsetStep);
        myAlbums = new UploadOnScrollListView(MusicApplication.ALBUMS, offsetStep);
        myTracks = new UploadOnScrollListView(MusicApplication.TRACKS, offsetStep);
        //myTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        initByList(R.id.list1, myArtists, "Artists");
        initByList(R.id.list2, myAlbums, "Albums");
        initByList(R.id.list3, myTracks, "Tracks");
        myTabHost.setCurrentTab(0);

        final InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
        
        final Button searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 2);
                getResultCount();
                onSearchClick();
            }
        });

        mySearchEdit = (EditText) findViewById(R.id.searchEditText);
        mySearchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (view.getId() != R.id.searchEditText)
                    return false;
                if (keyCode != 66) // = Enter
                    return false;
                if (keyEvent.getAction() != 0)
                    return false;
                //InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 2);
                searchButton.performClick();
                return true;
            }
        });
    }

    private void getResultCount () {
        ResultCount result;
        try {
            result = Request.getCount(mySearchEdit.getText().toString());
        } catch (IOException e) {
            myApplication.showErrorMessage("Get count", e.getMessage());
            return;
        }
        setTabCaption(0, "Artists (" + result.getArtistsSize() + ')');
        setTabCaption(1, "Albums (" + result.getAlbumsSize() + ')');
        setTabCaption(2, "Tracks (" + result.getTracksSize() + ')');
    }

    private void onSearchClick () {
        String searchString = mySearchEdit.getText().toString();
        Result myResult;
        try {
            myResult = Request.search(searchString, 3*offsetStep, 0, MusicApplication.ALL);
        } catch (IOException e) {
            myApplication.showErrorMessage("Search", e.getMessage());
            return;
        }
        if (myResult.isEmpty()) {
            myAlbums.reset();
            myArtists.reset();
            myTracks.reset();
        } else {
            myArtists.reset(myResult.getArtistsOffset(), searchString, myResult.getModelsOfType(MusicApplication.ARTISTS));
            myAlbums.reset(myResult.getAlbumsOffset(), searchString, myResult.getModelsOfType(MusicApplication.ALBUMS));
            myTracks.reset(myResult.getTracksOffset(), searchString, myResult.getModelsOfType(MusicApplication.TRACKS));
        }
        myTabHost.setCurrentTab(2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.search);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.remove(this);
    }
}
