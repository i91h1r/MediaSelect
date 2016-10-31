package com.github.hyr0318.mediaselect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.github.hyr0318.mediaselect_library.Constans.Constans;
import com.github.hyr0318.mediaselect_library.Constans.MediaType;
import com.github.hyr0318.mediaselect_library.ui.MediaSelectActivity;
import com.github.hyr0318.mediaselect_library.ui.Photo;
import com.github.hyr0318.mediaselect_library.widget.SelectDialog;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements SelectDialog.OnTakeAudioClickListener, SelectDialog.OnTakePicClickListener,
    SelectDialog.OnTakeVideoClickListener, SelectDialog.OnCancleClickListener {
    List<Photo> mediaList ;
    private SelectDialog selectDialog;
    private MediaAdapter mediaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.media);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mediaAdapter = new MediaAdapter(getApplicationContext(), mediaList);


        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mediaAdapter);
    }


    public void choose(View view) {

        selectDialog = new SelectDialog(this);

        selectDialog.setOnCancleClickListener(this);
        selectDialog.setOnTakePicClickListener(this);
        selectDialog.setOnTakeAudioClickListener(this);
        selectDialog.setOnTakeVideoClickListener(this);
        selectDialog.show();
    }


    @Override public void onTakeAudioClick() {
        MediaSelectActivity.openActivity(this, MediaType.AUDIO_SELECT_TYPE, 1000, mediaList,
            Constans.REQUEST_CODE);
    }


    @Override public void onTakePicClick() {
        MediaSelectActivity.openActivity(this, MediaType.PHOTO_SELECT_TYPE, 1000, mediaList,
            Constans.REQUEST_CODE);
    }


    @Override public void onTakeVideoClick() {
        MediaSelectActivity.openActivity(this, MediaType.VIDEO_SELECT_TYPE, 1000, mediaList,
            Constans.REQUEST_CODE);
    }


    @Override public void onCancleClick() {
        selectDialog.dismiss();
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constans.REQUEST_CODE){
            mediaList = (List<Photo>) data.getSerializableExtra(Constans.RESULT_LIST);

            mediaAdapter.setMediaList(mediaList);

            mediaAdapter.notifyDataSetChanged();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
