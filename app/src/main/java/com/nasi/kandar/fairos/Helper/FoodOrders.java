package com.nasi.kandar.fairos.Helper;

import com.nasi.kandar.fairos.Model.Order;

import java.util.ArrayList;

public class FoodOrders {

    private static FoodOrders foodOrders = null;

    public ArrayList<Order> mainOrderArrayList;

    private FoodOrders(){
        mainOrderArrayList = new ArrayList<>();
    }

    public static FoodOrders getInstance(){

        if(foodOrders == null)
            foodOrders = new FoodOrders();

        return  foodOrders;

    }

}
