package ru.musicserver.androidclient.activity;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.*;
import ru.musicserver.androidclient.model.*;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleActivity extends ListActivity {
    private MusicPlayerServiceInterface myPlayer;
    private Result myResult = null;
    private ListView myResultView;

    private final String singleTab = "  ";

    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
		public void onServiceDisconnected(ComponentName className) {
			myPlayer = null;
		}
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myPlayer = MusicPlayerServiceInterface.Stub.asInterface(iBinder);
        }
    };

    private void setAdapter (ArrayAdapter<Model> adapter) {
        myResultView.setAdapter(adapter);
    }

    private void addToAdapter (ArrayAdapter<Model> adapter, Model[] models, String parentShift) {
        for (Model model: models) {
            model.setShift(parentShift + singleTab);
            adapter.add(model);
        }
    }

    private boolean isOpened (ArrayAdapter<Model> adapter, int position) {
        Model item = adapter.getItem(position);
        boolean opened = false;
        if (position != adapter.getCount()-1) {
            Model nextItem = adapter.getItem(position+1);
            if (nextItem.getShift().length() > item.getShift().length())
                opened = true;
        }
        return opened;
    }

    private void close (ArrayAdapter<Model> adapter, int position) {
        boolean hasChild = true;
        int childPosition = position + 1;
        while (hasChild) {
            adapter.remove(adapter.getItem(childPosition));
            if (!isOpened(adapter, position))
                hasChild = false;
        }
    }

    private void open (ArrayAdapter<Model> adapter, int position) {
        Model item = adapter.getItem(position);
        String newShift = item.getShift() + singleTab;
        int childPosition = position + 1;

        Model trueItem = item;
        if (!(item instanceof ModelContainer)) {
            String type = (item instanceof Artist) ? "artist" : "album";
            try {
                trueItem = Request.get(type, item.getMbid());
            } catch (IOException e) {
                showMessage(e.getMessage());
                return;
            }
        }
        for (Model child: trueItem.getContent()) {
            child.setShift(newShift);
            adapter.insert(child, childPosition);
            ++childPosition;
        }
    }

     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        myResultView = getListView();
        this.bindService(new Intent(SimpleActivity.this, MusicPlayerService.class), myServiceConnection, Context.BIND_AUTO_CREATE);

        final Button button = (Button)findViewById(R.id.searchButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchEdit = (EditText)findViewById(R.id.searchEditText);
                String searchString = searchEdit.getText().toString();

                try {
                    myResult = Request.search(searchString);
                } catch (IOException e) {
                    showMessage(e.getMessage());
                    return;
                }

                ArrayAdapter<Model> adapter = new ArrayAdapter<Model>(v.getContext(), R.layout.unplayable);

                if (myResult.isEmpty()) {
                    adapter.add(new EmptyResult());
                } else {
                    adapter.add(new ModelContainer("Artist", myResult.getArtists()));
                    addToAdapter(adapter, myResult.getArtists(), "");
                    adapter.add(new ModelContainer("Album", myResult.getAlbums()));
                    addToAdapter(adapter, myResult.getAlbums(), "");
                    adapter.add(new ModelContainer("Track", myResult.getTracks()));
                    addToAdapter(adapter, myResult.getTracks(), "");
                }
                setAdapter(adapter);
            }
        });

        myResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showMessage("test");
                Model item = (Model)adapterView.getItemAtPosition(position);
                if (item instanceof Track) {
                    try {
                        myPlayer.play(((Track) item).getName(), ((Track) item).getUrl(), item.getMbid());


                        /*if (myPlayer.getPlayingTrackUrl()!=null && myPlayer.getPlayingTrackUrl().equals(((Track) item).getUrl())) {
                            myPlayer.stop();
                        } else {
                            myPlayer.play(((Track) item).getName(), ((Track) item).getUrl());
                        }  */
                    } catch (RemoteException e) {
                        Log.e(getString(R.string.app_name), e.getMessage());
                        showMessage(e.getMessage());
                    }
                    return;
                }
                if (item instanceof EmptyResult)
                    return;

                ArrayAdapter<Model> adapter = (ArrayAdapter<Model>) adapterView.getAdapter();
                if (isOpened(adapter, position)) {
                    close(adapter, position);
                } else {
                    open(adapter, position);
                }
                setAdapter(adapter);
            }
        });
    }

    private void showMessage (String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
