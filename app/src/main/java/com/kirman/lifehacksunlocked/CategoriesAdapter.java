package com.kirman.lifehacksunlocked;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private List<CategoriesEntity> mCategoriesEntityList;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.category_name);
            imageView = (ImageView) v.findViewById(R.id.category_icon);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoriesAdapter(List<CategoriesEntity> categoriesEntityList, Context context) {
        this.mCategoriesEntityList = categoriesEntityList;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_row, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CategoriesEntity cat = mCategoriesEntityList.get(position);
        int iconID = cat.getIconID();
        holder.textView.setText(cat.getTitle());
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, iconID));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCategoriesEntityList.size();
    }



}
