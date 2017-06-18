package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistAdminPlaylist;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class PlaylistAdapter extends BaseQuickAdapter<PlaylistAdminPlaylist.Admin_playlist_data, BaseViewHolder> {

    private final Activity activity;

    public PlaylistAdapter(ArrayList<PlaylistAdminPlaylist.Admin_playlist_data> data, Activity activity) {
        super(R.layout.layout_playlist, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PlaylistAdminPlaylist.Admin_playlist_data item) {
        helper.setText(R.id.tweetName, item.playlist_name);

        Picasso.with(activity)
                .load(DOMAIN_TEST + item.playlist_photo)
                .placeholder(R.drawable.placeholder_song)
                .into(((ImageView) helper.getView(R.id.img)));

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < item.playlist_rating; i++) {
            stringBuilder.append("♥");
        }
        helper.setText(R.id.tweetText, "");
        helper.setText(R.id.tweetText, stringBuilder);
//        helper.setVisible(R.id.tweetText, false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }
    }

}
