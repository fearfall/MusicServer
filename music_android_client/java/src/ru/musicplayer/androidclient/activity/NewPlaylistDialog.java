package ru.musicplayer.androidclient.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import ru.musicplayer.androidclient.model.Playlist;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 07/02/12
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class NewPlaylistDialog {

    public static Dialog create (final MusicApplication myApplication, Context mContext, final InputMethodManager imm)    {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.new_playlist_dialog);
        dialog.setTitle("New Playlist");

        final EditText edit = (EditText) dialog.findViewById(R.id.editText);

        final Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit.getText().toString();
                if (myApplication.newPlaylist(name))
                    dialog.hide();
            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (view.getId() != R.id.editText)
                    return false;
                if (keyCode != 66) // = Enter
                    return false;
                if (keyEvent.getAction() != 0)
                    return false;
                //InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 2);
                ok.performClick();
                return true;
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                edit.setText("");
            }
        });

        return dialog;
    }

}
