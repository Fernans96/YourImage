package eu.epitech.fernan_s.msa_m.yourimage.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.epitech.fernan_s.msa_m.yourimage.ImageActivity;
import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

/**
 * Created by matheo msa on 31/01/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    private Context _context;
    private ArrayList<IThread> _data;

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
                    _context.startActivity(intent);
                }
            });
        }
    }

    public CardAdapter(ArrayList<IThread> treads){
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
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int position) {
        holder.TextTitle.setText(_data.get(position).getTitle());
        holder.TextDesc.setText(_data.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
