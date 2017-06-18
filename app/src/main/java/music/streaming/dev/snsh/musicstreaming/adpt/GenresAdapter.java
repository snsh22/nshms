package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicType;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class GenresAdapter extends BaseQuickAdapter<PlaylistGenreMusicType.Data, BaseViewHolder> {

    private final Activity activity;

    public GenresAdapter(ArrayList<PlaylistGenreMusicType.Data> data, Activity activity) {
        super(R.layout.layout_geners, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PlaylistGenreMusicType.Data item) {
        helper.setText(R.id.tweetName, item.genere);
        Picasso.with(activity)
                .load(DOMAIN_TEST + item.genere_image)
                .placeholder(R.drawable.placeholder_song)
                .into(((ImageView) helper.getView(R.id.img)));
//        ((TextView) helper.getView(R.id.tweetText)).setText(" ");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }
    }
}
