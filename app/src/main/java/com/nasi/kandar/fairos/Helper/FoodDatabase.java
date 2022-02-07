package com.nasi.kandar.fairos.Helper;

import com.nasi.kandar.fairos.Model.Food;

import java.util.ArrayList;


/*
* Singleton Class to store all food from database once app is launched.

* 13/10/19
* */

public class FoodDatabase {

    private static FoodDatabase foodDatabase = null;

    public ArrayList<Food> savedFoodDatabaseList;

    private FoodDatabase(){
        savedFoodDatabaseList = new ArrayList<>();
    }

    public static FoodDatabase getInstance(){

        if(foodDatabase == null)
            foodDatabase = new FoodDatabase();

        return  foodDatabase;

    }
}
