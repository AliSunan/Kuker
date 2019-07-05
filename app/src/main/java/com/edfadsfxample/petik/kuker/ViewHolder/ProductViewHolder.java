package com.edfadsfxample.petik.kuker.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edfadsfxample.petik.kuker.Interface.ItemClickListner;
import com.edfadsfxample.petik.kuker.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView tvProductName, tvProductDesc, tvProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        tvProductName = (TextView) itemView.findViewById(R.id.product_name);
        tvProductDesc = (TextView) itemView.findViewById(R.id.product_description);
        tvProductPrice = (TextView) itemView.findViewById(R.id.product_price);
    }


    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }


    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(),false);
    }
}
