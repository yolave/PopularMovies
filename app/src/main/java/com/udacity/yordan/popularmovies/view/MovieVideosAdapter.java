package com.udacity.yordan.popularmovies.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.yordan.popularmovies.R;
import com.udacity.yordan.popularmovies.json.ResultMovieVideo;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yordan on 3/18/2017.
 */
public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.AdapterViewHolder> {

    private List<ResultMovieVideo> list;

    public MovieVideosAdapter(List<ResultMovieVideo> list){
        this.list = list;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_videos,parent,false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        final ResultMovieVideo item = list.get(position);

        holder.mTvVideoName.setText(item.getName());
        final String key = item.getKey();
        Picasso
                .with(holder.mIvThumbnail.getContext())
                .load(NetworkUtils.generateYoutubeImageThumbUrl(key))
                .into(holder.mIvThumbnail);
        holder.mBtnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + key));
                        try {
                            v.getContext().startActivity(appIntent);
                        }
                        catch (ActivityNotFoundException ex) {
                            v.getContext().startActivity(webIntent);
                        }
            }
        });
        holder.mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + key);
                v.getContext().startActivity(Intent.createChooser(shareIntent, "Share video using"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_video_name)
        TextView mTvVideoName;
        @BindView(R.id.btn_watch)
        Button mBtnWatch;
        @BindView(R.id.iv_video_thumbnail)
        ImageView mIvThumbnail;
//        @BindView(R.id.iv_share)
//        ImageView mIvShare;
        @BindView(R.id.btn_share)
        Button mBtnShare;

        AdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
