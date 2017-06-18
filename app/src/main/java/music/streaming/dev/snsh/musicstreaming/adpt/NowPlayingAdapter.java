package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class NowPlayingAdapter extends BaseQuickAdapter<MediaItem, BaseViewHolder> {

    private final Activity activity;
    private ArrayList<MediaItem> data;
    private String songId;

    public NowPlayingAdapter(ArrayList<MediaItem> data, Activity activity) {
        super(R.layout.layout_now_playing, data);
        this.data = data;
        this.activity = activity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MediaItem item) {
        helper.setIsRecyclable(false);

        /*PlaylistManager playlistManager = App.getPlaylistManager();

        if (helper.getLayoutPosition() == playlistManager.getCurrentPosition())
            helper.setVisible(R.id.iv_now_playing, true);*/

        songId = data.get(helper.getLayoutPosition()).getSongId();

        Hawk.init(activity).build();
        int nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);
        if (helper.getLayoutPosition() == nowPlayingIndex) {
//            helper.setVisible(R.id.iv_now_playing, true);
            helper.setVisible(R.id.iv_now_playing_gif, true);
        }

        if (item.getAlbumImage().startsWith("/storage/"))
            Picasso.with(activity)
                    .load(new File(item.getAlbumImage()))
                    .placeholder(R.drawable.placeholder_song)
                    .into(((ImageView) helper.getView(R.id.img)));
        else
            Picasso.with(activity)
                    .load(item.getAlbumImage())
                    .placeholder(R.drawable.placeholder_song)
                    .into(((ImageView) helper.getView(R.id.img)));

        helper.setText(R.id.tweetName, item.getSongName());
        helper.setText(R.id.tweetText, item.getArtist() + " - " + item.getAlbum());

        helper.addOnClickListener(R.id.ib_song_detail);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }
    }


}
