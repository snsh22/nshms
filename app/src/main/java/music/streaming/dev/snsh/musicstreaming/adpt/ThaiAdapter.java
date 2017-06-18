package music.streaming.dev.snsh.musicstreaming.adpt;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverThai;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class ThaiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int TYPE_DATA = 0;
    public final int TYPE_LOAD = 1;

    static Context context;
    List<DiscoverThai.Data> thaiDataList;
    public OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    public ThaiAdapter(Context context, List<DiscoverThai.Data> thaiDataList) {
        this.context = context;
        this.thaiDataList = thaiDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_DATA) {
            return new MovieHolder(inflater.inflate(R.layout.layout_grid, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.layout_load_more_grid, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_DATA) {
            ((MovieHolder) holder).bindData(thaiDataList.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if (thaiDataList.get(position).type == null) {
            return TYPE_DATA;
        } else {
            if (thaiDataList.get(position).type.equals("load")) {
                return TYPE_LOAD;
            } else {
                return TYPE_DATA;
            }
        }
    }

    @Override
    public int getItemCount() {
        return thaiDataList.size();
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tweetName, tweetText;
        CardView card_view;

        public MovieHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            tweetName = (TextView) itemView.findViewById(R.id.tweetName);
            tweetText = (TextView) itemView.findViewById(R.id.tweetText);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }

        void bindData(DiscoverThai.Data data) {
            Picasso.with(context)
                    .load(DOMAIN_TEST + data.album_image)
                    .placeholder(R.drawable.placeholder_song)
                    .into(img);
            tweetName.setText(data.song_name);
            tweetText.setText(data.album_name);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                card_view.setRadius(0);
            }
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}