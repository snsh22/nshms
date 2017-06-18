package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistName;

public class PersonalPlaylistNameAdapter2 extends BaseQuickAdapter<PersonalPlaylistName.Data, BaseViewHolder> {

    private final Activity activity;

    public PersonalPlaylistNameAdapter2(List<PersonalPlaylistName.Data> data, Activity activity) {
        super(R.layout.layout_personal_playlist_name, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalPlaylistName.Data item) {
        helper.setText(R.id.tweetName, item.playlist_name);
        helper.addOnClickListener(R.id.ib_details);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }
    }

}
