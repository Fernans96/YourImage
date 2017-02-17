package eu.epitech.fernan_s.msa_m.yourimage.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.activity.ImageActivity;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;

/**
 * Created by matheo msa on 31/01/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context _context;
    private List<IThread> _data;
    private Map<Integer, IImage> _data1;

    public CardAdapter(List<IThread> treads) {
        _data = treads;
        _data1 = new ArrayMap<>();
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_cardview, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void LoadPictures(final int position, final CardAdapter.ViewHolder holder) {
        _data.get(position).getImages(new IImage.getImageCallback() {
            @Override
            public void onGetImageFinished(final List<IImage> lThread) {
                _data1.put(position, lThread.get(0));
                if (lThread.size() > 0) {
                    Handler handler = new Handler(_context.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String uri = _data1.get(position).getLink().toStringUrl();
                            final String extension = uri.substring(uri.lastIndexOf("."));
                            ImagesTools.LoadPictures(extension, _data1.get(position).getLink(), _context, holder.ImageContent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final CardAdapter.ViewHolder holder, final int position) {
        holder.TextTitle.setText(_data.get(position).getTitle());
        holder.TextDesc.setText(_data.get(position).getDesc());
        if (_data1.containsKey(position)) {
            String uri = _data1.get(position).getLink().toStringUrl();
            final String extension = uri.substring(uri.lastIndexOf("."));
            ImagesTools.LoadPictures(extension, _data1.get(position).getLink(), _context, holder.ImageContent);
        } else {
            LoadPictures(position, holder);
        }
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

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
}
