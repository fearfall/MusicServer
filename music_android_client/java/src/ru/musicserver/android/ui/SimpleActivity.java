package ru.musicserver.android.ui;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.media.MediaPlayer;
import ru.musicserver.common.Album;
import ru.musicserver.common.Artist;
import ru.musicserver.common.MyProtocol;
import ru.musicserver.common.Track;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleActivity extends Activity {
    private TableLayout myLayout;

    public void addRow (TableRow row) {
        myLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_simple_layout);
        myLayout = (TableLayout)findViewById(R.id.my_layout);

        final Button button = (Button)findViewById(R.id.searchButton);

        button.setOnClickListener(new View.OnClickListener() {
            private Item myItem;

            private void addRowItem (Context context, TableRow row, Class c, Item item) {
                TextView textView = new TextView(context);
                if (c.isInstance(item))
                    textView.setText(item.toString());
                else
                    textView.setText("--");
                row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
            }

            private void addPlayButton (Context context, TableRow row, Item item) {
                if (!(item instanceof Track)) {
                    /*row.addView(new TextView(context), new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));*/
                    return;
                }
                myItem = item;
                Button playButton = new Button(context);
                playButton.setText(">");
                playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Uri q = Uri.parse(myItem.getUrl());
                        int qwa = R.raw.test;
                        //MediaPlayer player = MediaPlayer.create(SimpleActivity.this, R.raw.test);
                        MediaPlayer player = new MediaPlayer();
                        try {
                            player.setDataSource(SimpleActivity.this, Uri.parse(myItem.getUrl()));
                            //FileDescriptor fd = new FileDescriptor()
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            player.prepare();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        player.start();

                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                // finito :)

                            }
                        });

                    }
                });
                row.addView(playButton, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
            }

            public void onClick(View v) {
                EditText searchEdit = (EditText)findViewById(R.id.searchEditText);
                String searchString = searchEdit.getText().toString();
                List<Item> result = MyProtocol.searchRequest(searchString);
                if (result.isEmpty()) {
                    TextView textView = new TextView(v.getContext());
                    textView.setText("Nothing found :(");
                    TableRow row = new TableRow(v.getContext());
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    ((SimpleActivity)v.getContext()).addRow(row);
                    return;
                }

                //add captions
                TableRow row = new TableRow(v.getContext());
                for (Item.ItemType itemType: Item.ItemType.values()) {
                    TextView textView = new TextView(v.getContext());
                    textView.setText(itemType.toString());
                    textView.setTextColor(5);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                }
                ((SimpleActivity)v.getContext()).addRow(row);


                for (Item item: result) {
                    row = new TableRow(v.getContext());
                    addRowItem (v.getContext(), row, Artist.class, item);
                    addRowItem (v.getContext(), row, Album.class, item);
                    addRowItem (v.getContext(), row, Track.class, item);
                    addPlayButton(v.getContext(), row, item);

                    ((SimpleActivity)v.getContext()).addRow(row);
                }
            }
        });

    }
}
