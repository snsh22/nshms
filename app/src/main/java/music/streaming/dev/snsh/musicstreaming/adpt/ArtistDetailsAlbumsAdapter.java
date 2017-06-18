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
import music.streaming.dev.snsh.musicstreaming.dto.ArtistDetails;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class ArtistDetailsAlbumsAdapter extends BaseQuickAdapter<ArtistDetails.Albums_data, BaseViewHolder> {

    private final Activity activity;
    private String artistName;

    public ArtistDetailsAlbumsAdapter(ArrayList<ArtistDetails.Albums_data> data, Activity activity, String artistName) {
        super(R.layout.layout_albums, data);
        this.activity = activity;
        this.artistName = artistName;
    }

    @Override
    protected void convert(BaseViewHolder helper, ArtistDetails.Albums_data item) {
        helper.setText(R.id.tweetName, item.album_name);
        Picasso.with(activity)
                .load(DOMAIN_TEST + item.album_image)
                .placeholder(R.drawable.placeholder_artist)
                .into(((ImageView) helper.getView(R.id.img)));
        ((TextView) helper.getView(R.id.tweetText)).setText(artistName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }

    }

}
