package com.nasi.kandar.fairos.Adapters;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nasi.kandar.fairos.Activity.FoodMenuActivity;
import com.nasi.kandar.fairos.Fragments.MenuFragment;
import com.nasi.kandar.fairos.Model.Cuisine;
import com.example.hrithik.btp.R;

import java.util.ArrayList;

public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.ViewHolder>{

    private static final String TAG = "CuisineAdapter";
    private Context mContext;
    private ArrayList<Cuisine> mCuisineList;
    private Activity mActivity;

    public CuisineAdapter(Context context, ArrayList<Cuisine> cuisineList, Activity activity){
        mContext = context;
        mCuisineList = cuisineList;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_row_cuisine_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"Cuisine selected: "+ MenuFragment.cuisineSelected);

        holder.cuisineName.setText(mCuisineList.get(position).getCuisineName());

        if(mCuisineList.get(position).getCuisineCode().trim().toLowerCase().equals(FoodMenuActivity.cuisineSelected)){
            holder.layout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(),R.color.blue, null));
        } else {
            holder.layout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(),R.color.colorPrimaryDark, null));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FoodMenuActivity.cuisineSelected = mCuisineList.get(position).getCuisineCode();
                notifyDataSetChanged();
                FoodMenuActivity.cuisineOnClick(mContext ,mCuisineList.get(position).getCuisineCode(), mActivity);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mCuisineList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout;
        TextView cuisineName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.cuisine_single_row_layout);
            this.cuisineName = itemView.findViewById(R.id.cuisine_name_single_row);
        }
    }


}


