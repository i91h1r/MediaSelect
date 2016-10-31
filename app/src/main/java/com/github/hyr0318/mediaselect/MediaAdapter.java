package com.github.hyr0318.mediaselect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.github.hyr0318.mediaselect_library.ui.Photo;
import java.io.File;
import java.util.List;

/**
 * Description:
 * 作者：hyr on 2016/10/31 16:57
 * 邮箱：2045446584@qq.com
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private Context mContext;

    private List<Photo> mediaList;


    public void setMediaList(List<Photo> mediaList) {
        this.mediaList = mediaList;
    }


    public MediaAdapter(Context context, List<Photo> mediaList) {
        this.mContext = context;

        this.mediaList = mediaList;

    }


    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.media_item, null, false);

        return new MediaViewHolder(inflate);
    }


    @Override public void onBindViewHolder(MediaViewHolder viewHolder, int position) {

        Photo photo = mediaList.get(position);


        if (null != photo) {

            switch (photo.getType()) {
                case 0://图片
                    viewHolder.mediaType.setVisibility(View.GONE);
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(viewHolder.mediaImg);
                    break;
                case 1://视频
                    viewHolder.mediaType.setVisibility(View.VISIBLE);
                    viewHolder.mediaType.setBackgroundResource(R.mipmap.icon_video);
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(viewHolder.mediaImg);
                    break;
                case 2://音频
                    viewHolder.mediaType.setVisibility(View.VISIBLE);
                    viewHolder.mediaType.setBackgroundResource(R.mipmap.icon_audio);
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.mipmap.audio_default_bg)
                        .error(R.mipmap.voice_default_icon)
                        .into(viewHolder.mediaImg);
                    break;

            }
        }

    }


    @Override public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }


    public class MediaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mediaImg;
        private final ImageView mediaType;


        public MediaViewHolder(View itemView) {
            super(itemView);

            mediaImg = (ImageView) itemView.findViewById(R.id.media_img);

            mediaType = (ImageView) itemView.findViewById(R.id.media_type);
        }
    }
}
