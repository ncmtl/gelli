package com.dkanada.gramophone.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.gramophone.R;
import com.dkanada.gramophone.interfaces.MediaCallback;
import com.dkanada.gramophone.model.Playlist;
import com.dkanada.gramophone.model.Song;
import com.dkanada.gramophone.util.PlaylistUtil;
import com.dkanada.gramophone.util.QueryUtil;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialog extends DialogFragment {

    @NonNull
    public static AddToPlaylistDialog create(Song song) {
        List<Song> list = new ArrayList<>();
        list.add(song);
        return create(list);
    }

    @NonNull
    public static AddToPlaylistDialog create(List<Song> songs) {
        AddToPlaylistDialog dialog = new AddToPlaylistDialog();

        Bundle args = new Bundle();
        args.putParcelableArrayList("songs", new ArrayList<>(songs));

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<Playlist> playlists = new ArrayList<>();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.action_add_to_playlist)
                .items(getActivity().getResources().getString(R.string.action_new_playlist))
                .itemsCallback((materialDialog, view, i, charSequence) -> {
                    final List<Song> songs = getArguments().getParcelableArrayList("songs");
                    if (songs == null) return;

                    if (i == 0) {
                        materialDialog.dismiss();
                        CreatePlaylistDialog.create(songs).show(getActivity().getSupportFragmentManager(), "ADD_TO_PLAYLIST");
                    } else {
                        materialDialog.dismiss();
                        PlaylistUtil.addItems(songs, playlists.get(i - 1).id);
                    }
                })
                .build();

        QueryUtil.getPlaylists(new MediaCallback() {
            @Override
            public void onLoadMedia(List<?> media) {
                playlists.addAll((List<Playlist>) media);

                CharSequence[] names = new CharSequence[playlists.size() + 1];
                names[0] = getActivity().getResources().getString(R.string.action_new_playlist);
                for (int i = 0; i < playlists.size(); i++) {
                    names[i + 1] = playlists.get(i).name;
                }

                dialog.setItems(names);
            }
        });

        return dialog;
    }
}
