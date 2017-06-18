package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistSong;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class PersonalPlaylistSongListAdapter extends BaseQuickAdapter<PersonalPlaylistSong.Data, BaseViewHolder> {

    private final Activity activity;

    public PersonalPlaylistSongListAdapter(List<PersonalPlaylistSong.Data> data, Activity activity) {
        super(R.layout.personal_playlist_song_list, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalPlaylistSong.Data item) {
        Picasso.with(activity)
                .load(DOMAIN_TEST + item.album_image)
                .placeholder(R.drawable.placeholder_song)
                .into(((ImageView) helper.getView(R.id.img)));
        helper.setText(R.id.tweetName, item.song_name);
        helper.setText(R.id.tweetText, item.artist_name + " - " + item.album_name);
        helper.addOnClickListener(R.id.ib_song_detail);
    }
}
