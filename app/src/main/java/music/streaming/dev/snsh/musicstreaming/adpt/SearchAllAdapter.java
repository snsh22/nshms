package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.SearchSong;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class SearchAllAdapter extends BaseQuickAdapter<SearchSong.Search_data, BaseViewHolder> {

    private final Activity activity;

    public SearchAllAdapter(ArrayList<SearchSong.Search_data> data, Activity activity) {
        super(R.layout.layout_search_songs, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchSong.Search_data item) {
        helper.setText(R.id.tweetName, item.getSong_name());
        Picasso.with(activity)
                .load(DOMAIN_TEST + item.getAlbum_image())
                .placeholder(R.drawable.placeholder_song)
                .into(((ImageView) helper.getView(R.id.img)));
        ((TextView) helper.getView(R.id.tweetText)).setText(item.getAlbum_name() + " - " + item.getArtist_name());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }

    }
}
