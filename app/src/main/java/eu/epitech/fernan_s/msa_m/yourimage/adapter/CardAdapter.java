package eu.epitech.fernan_s.msa_m.yourimage.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.activity.ImageActivity;
import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

/**
 * Created by matheo msa on 31/01/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    private Context _context;
    private List<IThread> _data;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TextTitle, TextDesc;
        private ImageView ImageContent, ImageProfil;

        public ViewHolder(View v) {
            super(v);
            _context = v.getContext();
            TextTitle = (TextView) v.findViewById(R.id.image_title_card);
            TextDesc = (TextView) v.findViewById(R.id.image_desc_card);
            ImageContent = (ImageView) v.findViewById(R.id.image_card);
            ImageProfil = (ImageView) v.findViewById(R.id.profil_image_card);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(_context, ImageActivity.class);
                    Gson gson = new Gson();

                    intent.putExtra("thread", gson.toJson(_data.get(getAdapterPosition())));
                    _context.startActivity(intent);
                }
            });
        }
    }

    public CardAdapter(List<IThread> treads){
        _data = treads;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_cardview, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CardAdapter.ViewHolder holder, int position) {
        holder.TextTitle.setText(_data.get(position).getTitle());
        holder.TextDesc.setText(_data.get(position).getDesc());
        _data.get(position).getImages(new IImage.getImageCallback() {
            @Override
            public void onGetImageFinished(final List<IImage> lThread) {
                if (lThread.size() > 0) {
                    Handler handler = new Handler(_context.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String uri = lThread.get(0).getLink();
                            final String extension = uri.substring(uri.lastIndexOf("."));
                            if (extension.equals(".gif"))
                                Glide
                                        .with(_context)
                                        .load(lThread.get(0).getLink())
                                        .asGif()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.interrogation_karai)
                                        .into(holder.ImageContent);
                            else {
                                Glide
                                        .with(_context)
                                        .load(lThread.get(0).getLink())
                                        .placeholder(R.drawable.interrogation_karai)
                                        .into(holder.ImageContent);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }
}
