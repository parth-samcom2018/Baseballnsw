package com.nsw.baseballnsw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.nsw.baseballnsw.models.Event;
import com.nsw.baseballnsw.models.Media;
import com.nsw.baseballnsw.models.MediaAlbum;
import com.nsw.baseballnsw.models.MediaComment;
import com.nsw.baseballnsw.views.TextPoster;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class MediaDetailVC extends BaseVC {

    public static MediaAlbum mediaAlbum;
    public static int selectedMediaId;

    private Media selectedMedia;

    //VIEWS
    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private TextPoster textPoster;

    private SliderLayout slider;
    Dialog dialog;

    //Header
    private TextView firstTV;
    private TextView secondTV;
    private ImageView imageView;
    private Toolbar toolbars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail_vc);

        Toolbar toolbars = findViewById(R.id.toolbar_second);
        toolbars.setBackgroundColor(Color.BLACK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mediaAlbum.sortMediaAlbumsByDate(); //sort by oldest last, since api is useless
        try {
            selectedMedia = mediaAlbum.mediaModels.get(0); //DEFAULT TO FIRST
        }

        catch (NullPointerException e){
            e.printStackTrace();
            Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
        }

        if(selectedMediaId != 0)
        {
            Log.d("hq","passed selected media id:"+selectedMediaId);
            //passed some media for selection

            for(Media m : mediaAlbum.mediaModels)
            {
                if(m.mediaId == selectedMediaId)
                {

                    Log.d("hq","found selected media:"+selectedMediaId);
                    selectedMedia = m; //reference the model in the array
                    break;
                }
            }
            Log.d("hq","passed to see media:"+selectedMedia.url);
        }



        textPoster = findViewById(R.id.textposter);
        textPoster.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(textPoster.getText());
            }
        });

        listView = findViewById(R.id.list);
        listAdapter= new ArrayAdapter<Event>(this, R.layout.main_cell){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.main_cell, parent, false);
                }

                final MediaComment mc = selectedMedia.comments.get(position);


                TextView firstTV = convertView.findViewById(R.id.firstTV);
                String topString = "<font color='#e2441f'>"+mc.member+"</font>";
                firstTV.setText(Html.fromHtml(topString));

                TextView secondTV = convertView.findViewById(R.id.secondTV);
                secondTV.setText(mc.comment);

                TextView thirdTV = convertView.findViewById(R.id.thirdTV);
                thirdTV.setText(mc.getTimeAgo()+"");

                TextView usernameTV = convertView.findViewById(R.id.usernameTV);
                usernameTV.setText(mc.member);

                ImageView userIV = convertView.findViewById(R.id.imageView);
                Picasso p = Picasso.with(this.getContext());
                p.setIndicatorsEnabled(true);
                p.load(mc.memberAvatar)
                        .placeholder(R.drawable.icon)
                        //.fetch();
                        .into(userIV);

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        dialog = new Dialog(MediaDetailVC.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.my_notifications_comments);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView tvdata = dialog.findViewById(R.id.tvData);

                        tvdata.setText("" + mc.comment);

                        Button btn_no = dialog.findViewById(R.id.btn_no);
                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Button btnYes = dialog.findViewById(R.id.btn_yes);

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (DM.member.memberId == mc.memberId) {

                                    String auth = DM.getAuthString();

                                    DM.getApi().mediaCommentdelete(auth, mc.mediaCommentId, new Callback<Response>() {
                                        @Override
                                        public void success(Response response, Response response2) {
                                            Toast.makeText(MediaDetailVC.this, "Delete Comments", Toast.LENGTH_SHORT).show();
                                            refreshMedia();
                                            refreshLayout.setRefreshing(true);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(MediaDetailVC.this, "Cannot delete this comment!!", Toast.LENGTH_SHORT).show();
                                            refreshMedia();
                                            refreshLayout.setRefreshing(true);
                                        }
                                    });
                                }

                                else {
                                    Toast.makeText(MediaDetailVC.this, "You are authorized to delete this notification!!", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                        return true;
                    }
                });

                return convertView;
            }

            @Override
            public int getCount() {
                return selectedMedia.comments.size();
            }
        };
        listView.setAdapter(listAdapter);



        refreshLayout = findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshMedia();
            }
        });

        //HEADER
        firstTV = findViewById(R.id.firstTV);
        secondTV = findViewById(R.id.secondTV);
        imageView = findViewById(R.id.imageView);




        slider = findViewById(R.id.slider);
        slider.setDuration(10000);
        //   slider.setSliderTransformDuration(Integer.MAX_VALUE,null);
        slider.stopAutoCycle();


        slider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectedMedia = mediaAlbum.mediaModels.get(position);
                Log.d("hq","on page selected changed media!!!");
                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        modelToView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedMediaId = 0; //reset static..
    }

    private void modelToView()
    {

        setTitle(mediaAlbum.name);

        Media hackSelected = selectedMedia;

        firstTV.setText(mediaAlbum.name);
        secondTV.setText("Uploaded By:\n"+mediaAlbum.createdBy);

        Picasso p = Picasso.with(this);
        p.setIndicatorsEnabled(true);
        p.load(mediaAlbum.createdByAvatar)
                .placeholder(R.drawable.icon)
                //.fetch();
                .into(imageView);

        slider.removeAllSliders();
        for(final Media m : mediaAlbum.mediaModels)
        {
            //HACK needed becuase picasso does not take https
            m.url = DM.fromHTTPStoHTTP(m.url);

            if(m.url == "") continue; //skip empty ones
            final TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    //  .description("desc")
                    .image(m.url)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
            //.setOnSliderClickListener(this);

            //add your extra information
                    /*
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);
                            */

            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {

                    AlertDialog.Builder b = new AlertDialog.Builder(MediaDetailVC.this);

                    View v  = getLayoutInflater().inflate(R.layout.image_dialog,null);
                    ImageViewTouch iv = v.findViewById(R.id.imageView);

                    Picasso p = Picasso.with(MediaDetailVC.this);
                    p.setIndicatorsEnabled(true);
                    p.load(m.url)
                            .placeholder(R.drawable.icon)
                            //.fetch();
                            .into(iv);

                    b.setView(v);
                    b.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.show();



                }
            });
            slider.addSlider(textSliderView);
        }

        slider.setDuration(0);
                /*
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
                */


        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);


        //removing and adding slides to the slider fucks everything up cause it calls on page scrolled event, use this hack to perserve selection
        selectedMedia = hackSelected;

        if(selectedMedia !=null)
        {
            int index = 0;
            for (int i =0; i<mediaAlbum.mediaModels.size(); i++)
            {
                Media m = mediaAlbum.mediaModels.get(i);
                Log.d("hq","searching media:"+m.mediaId +" selected: "+selectedMedia.mediaId);
                if(selectedMedia.mediaId == m.mediaId)
                {
                    index = i;
                    break;
                }
            }


            Log.d("hq","selected slide index: "+index);
            try {
                slider.setCurrentPosition(index);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        listAdapter.notifyDataSetChanged();

    }




    private void postComment(String text)
    {
        if(text.length() == 0)
        {
            Toast.makeText(this,"You must enter text",Toast.LENGTH_LONG).show();
            return;
        }

        DM.hideKeyboard(this);


        final ProgressDialog pd = DM.getPD(this,"Posting Comment...");
        pd.show();

        /*DM.getApi().postMediaComments(DM.getAuthString(), selectedMedia.mediaId, text, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                Toast.makeText(MediaDetailVC.this,"Comment Posted!",Toast.LENGTH_LONG).show();
                textPoster.clearText();
                refreshMedia();
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(MediaDetailVC.this,"Comment failed "+error.getMessage(),Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });*/

        DM.getApi().postMediaComments(DM.getAuthString(), selectedMedia.mediaId, text, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                Toast.makeText(MediaDetailVC.this,"Comment Posted!",Toast.LENGTH_LONG).show();
                textPoster.clearText();
                refreshMedia();
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(MediaDetailVC.this,"Comment failed "+error.getMessage(),Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }

    private void refreshMedia()
    {
        final ProgressDialog pd = DM.getPD(this,"Refreshing Media...");
        pd.show();


        DM.getApi().getMediaAlbum(DM.getAuthString(), mediaAlbum.mediaAlbumId, new Callback<MediaAlbum>() {
            @Override
            public void success(MediaAlbum ma, Response response) {
                mediaAlbum = ma;
                mediaAlbum.sortMediaAlbumsByDate();

                for(Media m : ma.mediaModels)
                {
                    if(selectedMedia.mediaId == m.mediaId)
                    {
                        selectedMedia = m;
                        break;
                    }
                }

                modelToView();
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.edit) this.editAlbumAction();
        if(item.getItemId() == R.id.delete) this.deleteAlbumAction();
        return super.onOptionsItemSelected(item);
    }


    private void editAlbumAction()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        edittext.setText(mediaAlbum.name);
        String message = "Enter the new album name";
        alert.setMessage(message);
        alert.setTitle("Edit Album");
        alert.setView(edittext);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = edittext.getText().toString();
                Log.d("hq","update folder with name:"+name);

                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(MediaDetailVC.this, "Enter A name", Toast.LENGTH_LONG).show();
                    return;
                }

                mediaAlbum.name = name;




                /*DM.getApi().putMediaAlbum(DM.getAuthString(),name,mediaAlbum.albumDescription,mediaAlbum.mediaAlbumId, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(MediaDetailVC.this, "Album Updated!", Toast.LENGTH_LONG).show();
                        modelToView();
                        DM.hideKeyboard(MediaDetailVC.this);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(MediaDetailVC.this, "Could not update album:"+error.getMessage(), Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(MediaDetailVC.this);
                    }
                });*/

                DM.getApi().putMediaAlbums(DM.getAuthString(),name,mediaAlbum.albumDescription,mediaAlbum.mediaAlbumId, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(MediaDetailVC.this, "Album Updated!", Toast.LENGTH_LONG).show();
                        modelToView();
                        DM.hideKeyboard(MediaDetailVC.this);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(MediaDetailVC.this, "Could not update album:"+error.getMessage(), Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(MediaDetailVC.this);
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }

    private void deleteAlbumAction() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        String message = "Are you sure want to delete this item?";
        alert.setMessage(message);
        alert.setTitle("Delete Album image?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //Toast.makeText(MediaDetailVC.this, "" + selectedMedia.mediaId, Toast.LENGTH_SHORT).show();
                String auth = DM.getAuthString();

                DM.getApi().deleteMediaItem(auth, selectedMedia.mediaId, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(MediaDetailVC.this, "Successfully deleted media item", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(true);
                        //startActivity(new Intent(MediaDetailVC.this, GroupVC.class));
                        finish();
                        dialogInterface.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MediaDetailVC.this, "Media item cannot be deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

}
