package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;

public class DownloadAdapter extends BaseQuickAdapter<MediaItem, BaseViewHolder> {

    private final Activity activity;

    public DownloadAdapter(ArrayList<MediaItem> data, Activity activity) {
        super(R.layout.layout_download_songs, data);
        this.activity = activity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MediaItem item) {
        helper.setText(R.id.tweetName, item.getSongName());
        helper.setText(R.id.tweetText, item.getArtist() + " - " + item.getAlbum());
        Picasso.with(activity)
                .load(new File(item.getAlbumImage()))
                .placeholder(R.drawable.placeholder_song)
                .into(((ImageView) helper.getView(R.id.img)));

        helper.addOnClickListener(R.id.ib_song_detail);

//        ImageButton button = (ImageButton) helper.getView(R.id.ib_song_detail);
//        button.setColorFilter(Color.argb(0, 0, 0, 0));
    }
}
