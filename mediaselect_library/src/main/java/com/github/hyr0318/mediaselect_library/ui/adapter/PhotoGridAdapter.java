package com.github.hyr0318.mediaselect_library.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.event.OnItemCheckListener;
import com.github.hyr0318.mediaselect_library.event.OnPhotoClickListener;
import com.github.hyr0318.mediaselect_library.ui.MediaDirectory;
import com.github.hyr0318.mediaselect_library.ui.Photo;
import com.github.hyr0318.mediaselect_library.utils.MediaStoreHelper;
import java.io.File;
import java.util.List;

/**
 * Created by donglua on 15/5/31.
 */
public abstract class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private LayoutInflater inflater;

    private Context mContext;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;

    private boolean hasCamera = true;
    public int mediaType = 0;


    public PhotoGridAdapter(Context mContext, List<MediaDirectory> photoDirectories, int type) {
        this.photoDirectories = photoDirectories;
        this.mContext = mContext;
        this.mediaType = type;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA :ITEM_TYPE_PHOTO;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        return holder;
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            List<Photo> photos = getCurrentPhotos();
            final Photo photo;

            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
            switch (this.mediaType) {
                case 0://图片
                    holder.vedioIcon.setVisibility(View.GONE);
                    holder.vedioDuration.setVisibility(View.GONE);
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(holder.ivPhoto);
                    break;
                case 1://视频
                    holder.vedioIcon.setVisibility(View.VISIBLE);
                    holder.vedioDuration.setVisibility(View.VISIBLE);
                    holder.vedioDuration.setText(photo.getDuration());
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .thumbnail(0.1f)

                        .into(holder.ivPhoto);
                    break;
                case 2://音频
                    holder.vedioIcon.setVisibility(View.VISIBLE);
                    holder.vedioDuration.setVisibility(View.VISIBLE);
                    holder.vedioDuration.setText(photo.getDuration());
                    Glide.with(mContext)
                        .load(new File(photo.getPath()))
                        .centerCrop()
                        .placeholder(R.mipmap.audio_default_bg)
                        .thumbnail(0.1f)
                        .into(holder.ivPhoto);
                    break;

            }

            final boolean isChecked = isSelected(photo);

            holder.vSelected.setSelected(isChecked);
            holder.ivPhoto.setSelected(isChecked);
            holder.ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onLongClick(view, position);
                    }

                    return false;
                }
            });
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onClick(view, position, showCamera());
                    }
                }
            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isEnable = true;

                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked,
                            getSelectedPhotos().size());
                    }
                    if (isEnable) {
                        toggleSelection(photo, mediaType);
                        notifyItemChanged(position);
                    }
                    updateSelectCount(selectedPhotos.size());
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.drawable.camera);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount =
            photoDirectories.size() == 0 ? 0 :getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;
        private ImageView vedioIcon;
        private TextView vedioDuration;
        private View selectLayout;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            vedioIcon = (ImageView) itemView.findViewById(R.id.vedio_icon);
            vedioDuration = (TextView) itemView.findViewById(R.id.vedio_duration);
            selectLayout = (LinearLayout) itemView.findViewById(R.id.select_layout);

        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public List<Photo> getSelectedPhotoPaths() {
        return selectedPhotos;
    }


    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }


    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }


    public abstract void updateSelectCount(int size);

}
