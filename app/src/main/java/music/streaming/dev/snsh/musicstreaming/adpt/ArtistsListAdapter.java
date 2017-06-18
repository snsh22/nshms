package music.streaming.dev.snsh.musicstreaming.adpt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.ArtistsListModel;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class ArtistsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_DATA = 0;
    public final int TYPE_LOAD = 1;
    public final int TYPE_FOOTER = 2;

    static Context context;
    List<ArtistsListModel.Data> artistsDataList;
    public OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

     /*isLoading - to set the remote loading and complete status to fix back to back load more call
     isMoreDataAvailable - to set whether more data from server available or not.
     It will prevent useless load more request even after all the server data loaded*/


    public ArtistsListAdapter(Context context, List<ArtistsListModel.Data> artistsDataList) {
        this.context = context;
        this.artistsDataList = artistsDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_DATA) {
            return new MovieHolder(inflater.inflate(R.layout.layout_artists_new, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new LoadHolder(inflater.inflate(R.layout.layout_footer, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.layout_load_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_DATA) {
            ((MovieHolder) holder).bindData(artistsDataList.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if (artistsDataList.get(position).type == null) {
            return TYPE_DATA;
        } else {
            if (artistsDataList.get(position).type.equals("load")) {
                return TYPE_LOAD;
            } else if (artistsDataList.get(position).type.equals("footer")) {
                return TYPE_FOOTER;
            } else {
                return TYPE_DATA;
            }
        }
    }

    @Override
    public int getItemCount() {
        return artistsDataList.size();
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView iv_artist;
        TextView tweetName;

        public MovieHolder(View itemView) {
            super(itemView);
            iv_artist = (ImageView) itemView.findViewById(R.id.iv_artist);
            tweetName = (TextView) itemView.findViewById(R.id.tweetName);
        }

        void bindData(ArtistsListModel.Data artistsModel) {
            Picasso.with(context)
                    .load(DOMAIN_TEST + artistsModel.artist_photo)
                    .placeholder(R.drawable.placeholder_artist)
                    .transform(new CropCircleTransformation())
                    .into(iv_artist);
            tweetName.setText(artistsModel.artist_name);
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
