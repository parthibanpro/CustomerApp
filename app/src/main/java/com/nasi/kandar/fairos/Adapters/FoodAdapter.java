package com.nasi.kandar.fairos.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nasi.kandar.fairos.Activity.FoodMenuActivity;
import com.nasi.kandar.fairos.Helper.FoodCart;
import com.nasi.kandar.fairos.Model.Food;
import com.nasi.kandar.fairos.Model.FoodCartQtyHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrithik.btp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Food> mFoodList;
    private Activity mActivity;
    boolean isAlreadyPresent = false;

    private static String TAG = "FoodAdapter";

    public FoodAdapter(Context context, ArrayList<Food> foodList, Activity activity){
        mContext = context;
        mFoodList = foodList;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_row_food_menu_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


            //holder.foodImage.setImageResource(mFoodList.get(position).getFoodImage());

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);
        Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mFoodList.get(position).getFoodImage()).into(holder.foodImage);
        holder.foodName.setText(mFoodList.get(position).getFoodName());
        holder.foodDesc.setText(mFoodList.get(position).getFoodDesc());
        holder.foodRating.setText("("+mFoodList.get(position).getFoodScore()+")");
        holder.foodPrice.setText("RM: "+mFoodList.get(position).getFoodPrice());
        holder.foodCal.setText("Calories: "+mFoodList.get(position).getFoodCal());


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(mContext, FoodDetailsActivity.class);
                intent.putExtra("food_selected", mFoodList.get(position));
                mActivity.startActivity(intent);*/

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = mActivity.getLayoutInflater().inflate(R.layout.custom_food_dialog, null);

                final ImageView dialogFoodImage = mView.findViewById(R.id.food_details_food_iv);
                final TextView dialogFoodRating = mView.findViewById(R.id.food_details_food_rating);
                final TextView dialogFoodDesc = mView.findViewById(R.id.food_details_food_desc_tv);
                final TextView dialogFoodName = mView.findViewById(R.id.food_details_name);
                final ImageView addIcon = mView.findViewById(R.id.food_details_food_add);
                final ImageView removeIcon = mView.findViewById(R.id.food_details_food_remove);
                final TextView foodItemCount = mView.findViewById(R.id.food_details_item_count);
                final Button addToCartBtn = mView.findViewById(R.id.add_to_cart_btn);



                addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int currCount = Integer.parseInt(foodItemCount.getText().toString());
                        if(currCount!=10){
                        currCount++;
                        foodItemCount.setText(String.valueOf(currCount));
                        } else {
                            Toast.makeText(mContext, "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                removeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int currCount = Integer.parseInt(foodItemCount.getText().toString());
                        if(currCount!=1){
                        currCount--;
                        foodItemCount.setText(String.valueOf(currCount));
                        } else {
                            Toast.makeText(mContext, "Minimum Limit Reached ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mFoodList.get(holder.getAdapterPosition()).getFoodImage()).into(dialogFoodImage);
                dialogFoodRating.setText(mFoodList.get(holder.getAdapterPosition()).getFoodPositiveReviewCount()+"/"+mFoodList.get(holder.getAdapterPosition()).getFoodTotalReviewCount());
                dialogFoodDesc.setText(mFoodList.get(holder.getAdapterPosition()).getFoodDesc());
                dialogFoodName.setText(mFoodList.get(holder.getAdapterPosition()).getFoodName());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                dialog.show();


                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int qty = Integer.parseInt(foodItemCount.getText().toString());
                        Food foodSelected = mFoodList.get(holder.getAdapterPosition());
                        Log.d(TAG, "Food Selected"+ mFoodList.get(holder.getAdapterPosition()).getFoodName());

                        FoodCart cart = FoodCart.getInstance();



                        for(int i=0; i<cart.foodCartList.size(); i++){
                            Log.d(TAG, cart.foodCartList.get(i).getFood().getFoodName()+" : "+cart.foodCartList.get(i).getQuantity());
                            Log.d(TAG, "Pos Selected: "+holder.getAdapterPosition());
                            Log.d(TAG,"1 "+ mFoodList.get(holder.getAdapterPosition()).getFoodCode());
                            Log.d(TAG, "2 "+cart.foodCartList.get(i).getFood().getFoodCode());

                            if(cart.foodCartList.get(i).getFood().getFoodName().equals(mFoodList.get(holder.getAdapterPosition()).getFoodName())){
                                int prevQty = cart.foodCartList.get(i).getQuantity();
                                int newQty = prevQty + qty;
                                cart.foodCartList.get(i).setQuantity(newQty);
                                isAlreadyPresent = true;
                                Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(!isAlreadyPresent){
                            FoodCartQtyHelper foodToBeAdded = new FoodCartQtyHelper(foodSelected, qty);
                            cart.foodCartList.add(foodToBeAdded);
                            Toast.makeText(mContext, "Added to cart!", Toast.LENGTH_SHORT).show();
                        }

                        // Display cart details
                        for(int i=0; i<cart.foodCartList.size(); i++){
                            Log.d(TAG, "Cart Details: "+cart.foodCartList.get(i).getFood().getFoodName()+" : "+cart.foodCartList.get(i).getQuantity());
                        }

                        FoodMenuActivity.updateCartItemCountIcon();

                        dialog.cancel();


                    }
                });

            }
        });

    }


    private void displayCart(){
        FoodCart cart = FoodCart.getInstance();

        for(int i=0; i<cart.foodCartList.size(); i++){
            Log.d(TAG, cart.foodCartList.get(i).getFood().getFoodName()+" : "+cart.foodCartList.get(i).getQuantity());
        }
    }


    @Override
    public int getItemCount() {
       return mFoodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout;
        CircleImageView foodImage;
        TextView foodName, foodDesc, foodRating, foodPrice,foodCal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.food_menu_single_row);
            this.foodImage = itemView.findViewById(R.id.food_menu_iv);
            this.foodName = itemView.findViewById(R.id.food_name_tv);
            this.foodDesc = itemView.findViewById(R.id.food_menu_desc_tv);
            this.foodPrice = itemView.findViewById(R.id.food_price_tv);
            this.foodCal = itemView.findViewById(R.id.food_cal_tv);
            this.foodRating = itemView.findViewById(R.id.food_menu_rating_tv);


        }
    }
}
