package com.example.foodorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Interface.ItemClickListener;
import com.example.foodorder.Model.Food;
import com.example.foodorder.ViewHolder.FoodViewHolder;
import com.example.foodorder.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;

    DatabaseReference foodlist;

    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
//    search Functional

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar  materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
//        firebase
        database = FirebaseDatabase.getInstance();
        foodlist = database.getReference("Food");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//    get intent here

        if (getIntent()!= null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null){
            if (Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else{
                Toast.makeText(FoodList.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //    Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
//        materialSearchBar.setSpeechMode(false);
//        no need, becur we already define in at XML
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                when user type their text, we will change suggest list
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList){
                    if ( search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
//                when Search Bar is close
//                restore original adaper
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                
//                when search finish
//                show result of search adaper
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener((view, position1, isLongClick) -> {
//                    start new Activity
                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                    foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                    startActivity(foodDetail);
                        }
                );

            }
        };
        recyclerView.setAdapter(searchAdapter); //search  adapte for Recycler View is Search result

    }

    private void loadSuggest() {
        foodlist.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapShot:snapshot.getChildren()){
                            Food item = postSnapShot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class,
                foodlist.orderByChild("menuId").equalTo(categoryId)
//                select * from Food where MenuId = '
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getPrice()).into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
//                        Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
//                        start new activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey()); // send food id to new activitty
                        startActivity(foodDetail);
                    }
                });

            }
        };

//        Log.d("TAG", ""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}