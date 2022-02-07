package com.nasi.kandar.fairos.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nasi.kandar.fairos.Helper.FoodCart;
import com.nasi.kandar.fairos.Helper.FoodDatabase;
import com.nasi.kandar.fairos.Model.Cuisine;
import com.nasi.kandar.fairos.Model.Food;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.nasi.kandar.fairos.Adapters.CuisineAdapter;
import com.nasi.kandar.fairos.Adapters.FoodAdapter;
import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FoodMenuActivity extends AppCompatActivity {

    private static  String TAG = "FoodMenuActivity";
    private static ArrayList<Food> localFoodDatabase;

    private static ArrayList<Food> foodList;
    private static ArrayList<Cuisine> cuisineArrayList;
    private static FoodAdapter mFoodAdapter;
    private static RecyclerView foodRecyclerView;

    private static RecyclerView cuisineRecyclerView;
    private static CuisineAdapter mCuisineAdapter;

    public static String cuisineSelected = "101";
    //private View rootView;

    private boolean isCuisineVisible = false;


    private static FirebaseFirestore firebaseFirestore;
    private static Query query;

    private static Context mContext;
    private static Activity mActivity;
    private ImageView foodCartIV;
    private static TextView cartItemCount;
    private ImageView orderHistoryIcon;
    private ImageView profileIcon;

    private Toolbar toolbar;
    String tableNo;

    String name = "";
    String image = "";
    String thumb_image = "";
    private Uri mainImageURI = null;
    private Uri thumbImageUri = null;
    //TODO  FAB double click error
    private String userId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51JmJm1AOzW35GkwU2a1KM6qK7wrcOAPnoOaeJ7bB6oH32Lq4xUTujkPdYz6Nzc72Ydyzi60GfctnazoH8Pt1kZjv00XdcsdWRv"
        );
        setTitle("Menu");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        profileIcon = findViewById(R.id.icon_profile);

        if(mAuth.getCurrentUser() != null) {

            userId = mAuth.getCurrentUser().getUid();

            Log.d(TAG, "UserId: "+userId);

            try {



                firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {

                                // setupBtn.setEnabled(false);

                                name = task.getResult().getString("name");
                                image = task.getResult().getString("image");
                                thumb_image = task.getResult().getString("thumb_image");



                                thumbImageUri = Uri.parse(thumb_image);
                                mainImageURI = Uri.parse(image);
                                // setupImage.setEnabled(false);


                                RequestOptions placeHolderRequest = new RequestOptions();
                                placeHolderRequest.placeholder(R.drawable.default_image);

                                // Loading thumb img first, then original image...
                                Glide.with(FoodMenuActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image)
                                        .thumbnail(Glide.with(FoodMenuActivity.this).load(thumb_image))
                                        .into(profileIcon);

                            }

                        } else {

                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(FoodMenuActivity.this, "FIRESTORE Retrieve Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } catch (Exception e){
                Log.d(TAG, "Exception: "+e.getMessage());
            }

        }else{
            Toast.makeText(this, "Please sign in first.", Toast.LENGTH_SHORT).show();
            this.finish();
        }


        mContext = FoodMenuActivity.this;
        mActivity = FoodMenuActivity.this;
        foodRecyclerView = findViewById(R.id.menu_rv);
        cuisineRecyclerView = findViewById(R.id.cuisine_rv);
        foodCartIV = findViewById(R.id.food_cart_icon_iv);
        cartItemCount = findViewById(R.id.cart_item_count_tv);
        toolbar = findViewById(R.id.food_menu_toolbar);
        orderHistoryIcon = findViewById(R.id.orders_menu_icon);

        orderHistoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodMenuActivity.this, UserFoodOrders.class));
            }
        });


        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodMenuActivity.this, MoreActivity.class));
            }
        });
        foodList = new ArrayList<>();
        cuisineArrayList = new ArrayList<>();
        localFoodDatabase = new ArrayList<>();

        //toolbar.inflateMenu(R.menu.food_menu_options);




       // addDataToDatabase();
        //addcuisines();


        // Food Recycler View
        getFoodListFromServer();
        //addcuisines();
        //addDataToFoodList(cuisineSelected);
        //Log.d(TAG, "Final Food List: "+foodList);




        // Cuisine Recycler View
        //mCuisineAdapter = new CuisineAdapter(FoodMenuActivity.this, cuisineArrayList, FoodMenuActivity.this);
        //LinearLayoutManager cuisineManager = new LinearLayoutManager(FoodMenuActivity.this, LinearLayoutManager.VERTICAL, false);
        //cuisineRecyclerView.setLayoutManager(cuisineManager);
        //cuisineRecyclerView.setAdapter(mCuisineAdapter);

        foodCartIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FoodMenuActivity.this,FoodCartActivity.class);
                startActivity(intent);
            }
        });



        // Floating Action Button
        final FloatingActionButton fab = findViewById(R.id.slide_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "FAB CLICKED", Toast.LENGTH_SHORT).show();

                if(isCuisineVisible) {
                    Log.d(TAG, "Cuisine visibility GONE called");
                    isCuisineVisible=false;
                    cuisineRecyclerView.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.right_arrow_icon);
                }else{
                    Log.d(TAG, "Cuisine visibility VISIBLE called");
                    isCuisineVisible=true;
                    cuisineRecyclerView.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.left_arrow_white);
                }
            }
        });



        FoodMenuActivity.updateCartItemCountIcon();

    }

    public static void updateCartItemCountIcon(){
        FoodCart cart = FoodCart.getInstance();
        int size = 0;

        for(int i=0; i<cart.foodCartList.size(); i++){
            size+= cart.foodCartList.get(i).getQuantity();
        }

        if(size == 0){
            cartItemCount.setVisibility(View.GONE);
        } else {
            cartItemCount.setVisibility(View.VISIBLE);
            cartItemCount.setText(String.valueOf(size));
        }
    }

    private void displayCart(){
        FoodCart cart = FoodCart.getInstance();

        for(int i=0; i<cart.foodCartList.size(); i++){
            Log.d(TAG, cart.foodCartList.get(i).getFood().getFoodName()+" : "+cart.foodCartList.get(i).getQuantity());
        }
    }

    public static void getFoodListFromServer(){

        Log.d(TAG, "get food list from server called");

        // Retriving food data from database...
        localFoodDatabase = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        query = firebaseFirestore.collection("Food").orderBy("foodScore", Query.Direction.DESCENDING);
        query.addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                Log.d(TAG, "Query Snapshot: "+queryDocumentSnapshots);

                if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){

                    for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                       String foodImage =  doc.getDocument().getString("foodImage");
                       String foodName = doc.getDocument().getString("foodName");
                       String foodDesc = doc.getDocument().getString("foodDesc");
                       //String foodRating = doc.getDocument().getString("foodRating");
                       String foodPrice =  doc.getDocument().getString("foodPrice");
                        String foodCal =  doc.getDocument().getString("foodCal");
                        String foodCuisineCode = doc.getDocument().getString("foodCuisineCode");
                       String foodTotalReviewCount = String.valueOf(doc.getDocument().get("foodTotalReviewCount"));
                       String foodScore =  String.valueOf(doc.getDocument().get("foodScore"));
                       String foodPositiveReviewCount =  String.valueOf(doc.getDocument().get("foodPositiveReviewCount"));
                       String foddDocID = doc.getDocument().getId();
                       String foodCode = String.valueOf(doc.getDocument().get("foodCode"));



                       // Food food = doc.getDocument().toObject(Food.class);
                        //food.setFoodDocID(doc.getDocument().getId());
                        localFoodDatabase.add(new Food(foodCode, foodImage,foodName,foodDesc,foodPrice, foodCal,foodCuisineCode,foodTotalReviewCount,foodScore,foodPositiveReviewCount,foddDocID));

                    }
                    Log.d(TAG, "Local Food DB: "+String.valueOf(localFoodDatabase));

                    FoodDatabase foodDatabase = FoodDatabase.getInstance();
                    foodDatabase.savedFoodDatabaseList = localFoodDatabase;

                    addDataToFoodList(cuisineSelected);
                    addcuisines();
                    Collections.sort(foodList, new Comparator<Food>() {
                        @Override
                        public int compare(Food f1, Food f2) {

                            if(Float.valueOf(f1.getFoodScore())> Float.valueOf(f2.getFoodScore()))
                                return -1;
                            else
                                return 1;
                        }
                    });

                    mFoodAdapter = new FoodAdapter(mContext, foodList, mActivity);

                    LinearLayoutManager foodManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    foodRecyclerView.setLayoutManager(foodManager);
                    foodRecyclerView.setAdapter(mFoodAdapter);


                }
            }
        });
    }

    private static void addDataToFoodList(String cuisineSelectedCode) {

        Log.d(TAG,"add data to food list called");

        foodList = new ArrayList<>();
        //addDataToDatabase();
        //getFoodListFromServer();

        FoodDatabase foodDatabase = FoodDatabase.getInstance();
        Log.d(TAG, "Saved DB: "+foodDatabase.savedFoodDatabaseList);
        localFoodDatabase =  foodDatabase.savedFoodDatabaseList;

        if(cuisineSelectedCode.equals("100")){
            foodList = localFoodDatabase;
        } else {
            for (int i = 0; i < localFoodDatabase.size(); i++) {
                if (cuisineSelectedCode.equals(localFoodDatabase.get(i).getFoodCuisineCode()))
                    foodList.add(localFoodDatabase.get(i));
            }
        }

        /*Collections.sort(foodList, new Comparator<Food>() {
            @Override
            public int compare(Food f1, Food f2) {

                if(f1.getFoodScore().compareTo(f2.getFoodScore()) > 0)
                    return 1;
                else
                    return 0;
            }
        });*/

        Log.d(TAG, "Updated Food List: "+String.valueOf(foodList));


        //Log.d(TAG, String.valueOf(foodRecyclerView));
        //Log.d(TAG, String.valueOf(mFoodAdapter));

    }

    public static void cuisineOnClick(Context context, String cuisineCode, Activity activity){

        Log.d(TAG, "Cuisine onClick called");

        //Toast.makeText(context, cuisineCode, Toast.LENGTH_SHORT).show();

        FoodMenuActivity foodMenuActivity = new FoodMenuActivity();
        foodMenuActivity.addDataToFoodList(cuisineCode);

        Log.d(TAG,"Adapter OnClick: "+ foodRecyclerView);
        Log.d(TAG,"Adapter OnClick: "+ mFoodAdapter);
        Collections.sort(foodList, new Comparator<Food>() {
            @Override
            public int compare(Food f1, Food f2) {

                if(Float.valueOf(f1.getFoodScore())> Float.valueOf(f2.getFoodScore()))
                    return -1;
                else
                    return 1;
            }
        });

        mFoodAdapter = new FoodAdapter(context, foodList, activity);
        foodRecyclerView.setAdapter(mFoodAdapter);
        mFoodAdapter.notifyDataSetChanged();


    }

    private static void addcuisines() {

        Log.d(TAG, "add Cuisines called.");

        cuisineArrayList.clear();

        cuisineArrayList.add(new Cuisine("101","Drinks"));
        cuisineArrayList.add(new Cuisine("102","Snacks"));
        cuisineArrayList.add(new Cuisine("103","Local"));
        cuisineArrayList.add(new Cuisine("104","Western"));
        cuisineArrayList.add(new Cuisine("105","Signature"));


         for(int i=0; i<cuisineArrayList.size(); i++){
            int count=0;
            for(int j=0; j<localFoodDatabase.size(); j++){

                if(cuisineArrayList.get(i).getCuisineCode().equals(localFoodDatabase.get(j).getFoodCuisineCode()))
                    count++;
            }
            cuisineArrayList.get(i).setCuisineName(cuisineArrayList.get(i).getCuisineName()+"\n("+count+")");
        }

        // Adding ALL Cuisine Section Here Manually....
        cuisineArrayList.add(new Cuisine("100","All\n("+localFoodDatabase.size()+")"));


        mCuisineAdapter = new CuisineAdapter(mContext, cuisineArrayList, mActivity);
        LinearLayoutManager cuisineManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        cuisineRecyclerView.setLayoutManager(cuisineManager);
        cuisineRecyclerView.setAdapter(mCuisineAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.my_profile_option_menu)
            startActivity(new Intent(this,SetupActivity.class));
        return true;
    }

    /* void addDataToDatabase(){
        localFoodDatabase = new ArrayList<>();
        localFoodDatabase.add(new Food(R.drawable.pizza, "Pizza", "Pizza is a savory dish of Italian origin, consisting of a usually round, flattened base of leavened wheat-based dough topped with tomatoes, cheese, and various other ingredients (anchovies, olives, meat, etc.) baked at a high temperature, traditionally in a wood-fired oven.", "4/5", "₹ 250","101"));
        localFoodDatabase.add(new Food(R.drawable.burger, "Burger", "A hamburger is a sandwich consisting of one or more cooked patties of ground meat, usually beef, placed inside a sliced bread roll or bun. The patty may be pan fried, grilled, or flame broiled. ... A hamburger topped with cheese is called a cheeseburger", "4.5/5", "₹ 50","101"));
        localFoodDatabase.add(new Food(R.drawable.pasta, "Pasta", "Pasta is a type of food made from a mixture of flour, eggs, and water that is formed into different shapes and then boiled. Spaghetti, macaroni, and noodles are types of pasta", "3.9/5", "₹ 100", "101"));
        localFoodDatabase.add(new Food(R.drawable.noodles, "Noodles", "A noodle is a piece of pasta, especially a long, skinny one. You can eat noodles with butter and cheese or sauce, or slurp them from a bowl of soup. Noodles are cut or rolled from a dough that contains some kind of flour — wheat, buckwheat, and rice flour are all commonly used", "3.7/5", "₹ 80", "104"));
        localFoodDatabase.add(new Food(R.drawable.french_fries, "French Fries", "French fries or simply fries or chips, are pieces of potato that have been deep-fried. ... These are deep-fried, very thin, salted slices of potato that are usually served at room temperature. French fries have numerous variants, from thick-cut to shoestring, crinkle, curly and many other names", "4.2/5", "₹ 100","101"));
        // localFoodDatabase.add(new Food(R.drawable.samosa, "Samosa", "A samosa is a fried or baked pastry with a savoury filling, such as spiced potatoes, onions, peas, meat, or lentils. It may take different forms, including triangular, cone, or half-moon shapes, depending on the region.","3.5/5","₹ 20", "101"));
        localFoodDatabase.add(new Food(R.drawable.chilli_potato, "Chilli Potato", "Chilli potato is a Indo chinese starter made with fried potatoes tossed in spicy, slightly sweet & sour chilli sauce. ","4.5/5","₹ 120", "101"));
        localFoodDatabase.add(new Food(R.drawable.pav_bhaji, "Pav Bhaji", "Pav bhaji has many variations in ingredients and garnishes, but is essentially a spiced mixture of mashed vegetables in a thick gravy, usually cooked on a flat griddle (tava) and served hot with a soft white bread roll.","4.5/5","₹ 220", "101"));
        localFoodDatabase.add(new Food(R.drawable.spring_roll, "Spring Roll", "A spring roll is a Chinese food consisting of a small roll of thin pastry filled with vegetables and sometimes meat, and then fried.","3.9/5","₹ 90", "101"));
        localFoodDatabase.add(new Food(R.drawable.oreo_shake, "Oreo Shake", "A beloved classic recipe used in restaurants the world over, this Oreo milk shake is the ideal sweet treat.","4.9/5","₹ 120", "105"));
        localFoodDatabase.add(new Food(R.drawable.banana_shake, "Banana Shake", " Banana milkshake recipe - Delicious, creamy and healthy banana shake you can whip up in minutes.","4.5/5","₹ 100", "105"));
        localFoodDatabase.add(new Food(R.drawable.choclate_shake, "Chocolate Shake", "","4.9/5","₹ 150", "105"));
        localFoodDatabase.add(new Food(R.drawable.coca_cola, "Coca Cola", "","4.5/5","₹ 40", "102"));
        localFoodDatabase.add(new Food(R.drawable.dosa, "Dosa ", "A dosa is a cooked flat thin layered rice batter, originating from the South India, made from a fermented batter. It is somewhat similar to a crepe in appearance.","4.9/5","₹ 150", "103"));
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
