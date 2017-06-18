package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistData;

public class PersonalPlaylistAdapter extends BaseQuickAdapter<PersonalPlaylistData, BaseViewHolder> {

    private final Activity activity;

    public PersonalPlaylistAdapter(ArrayList<PersonalPlaylistData> data, Activity activity) {
        super(R.layout.layout_personal_playlist, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalPlaylistData item) {
//        helper.setText(R.id.tweetName, item.getGenere());
        Picasso.with(activity)
                .load(item.getImg())
                .into(((ImageView) helper.getView(R.id.img)));
//        ((TextView) helper.getView(R.id.tweetText)).setText(" ");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }
    }
}
