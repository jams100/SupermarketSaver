package com.example.testwebscrape.AdapterHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testwebscrape.DataModel.Products;
import com.example.testwebscrape.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.GridViewHolder> {

    private GridLayoutManager mLayoutManager;
    ArrayList<Products> products;
    private static final String TAG = "MyActivity";

    private SaveAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShareClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(SaveAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
        TextView productDescription;
        TextView NewPrice;
        ImageView img;
        ProgressBar progressBar;
        ImageView imgLogo;
        ImageView share;
        ImageView delete;

        public GridViewHolder(@NonNull View itemView, final SaveAdapter.OnItemClickListener listener, int viewType) {
            super(itemView);

            productDescription=itemView.findViewById(R.id.save_product_description);
            NewPrice=itemView.findViewById(R.id.save_new_price);
            progressBar=itemView.findViewById(R.id.save_image_progress);
            img=itemView.findViewById(R.id.save_product_image);
            imgLogo=itemView.findViewById(R.id.save_website_logo);
            share=itemView.findViewById(R.id.save_share);
            delete=itemView.findViewById(R.id.save_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            //Click listener for sharing
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onShareClick(position);
                        }
                    }
                }
            });

            //Click listener for deleting
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public SaveAdapter(ArrayList<Products> prod, GridLayoutManager gridLayoutManager){
        products=prod;
        mLayoutManager=gridLayoutManager;
    }

    @NonNull
    @Override
    public SaveAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_list,parent,false);

        return new SaveAdapter.GridViewHolder(v,mListener,viewType);
    }

    @Override
    public void onBindViewHolder(final SaveAdapter.GridViewHolder holder, int position) {

        Products currentProduct=products.get(position);
        holder.NewPrice.setText(currentProduct.getPriceNew());
        holder.productDescription.setText(currentProduct.getProductDescription());
        //logoutput
        Log.i(TAG, "SaveAdapter.getView() — get item number " + position + currentProduct.getProductDescription());

        Picasso.get().load(currentProduct.getImageProduct()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });
        Picasso.get().load(currentProduct.getImageLogo()).into(holder.imgLogo);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}