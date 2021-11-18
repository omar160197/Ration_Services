package com.appz.qrcode.seller_tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appz.qrcode.R;
import com.appz.qrcode.helperUi.AllFinal;
import com.appz.qrcode.seller_tasks.adapters.StoreAdapter;
import com.appz.qrcode.seller_tasks.adapters.StoreOnClickItem;
import com.appz.qrcode.seller_tasks.models.ChartItem;
import com.appz.qrcode.seller_tasks.models.ItemModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreActivity extends AppCompatActivity implements StoreOnClickItem {
    // var
    public static List<ItemModel> itemModelList;
    public static Map<String, ChartItem> chartItemList;
    //selling process
    public double num_of_clint_points;
    public String clientiddd;
    // ui
    private RecyclerView recyclerView_store;
    private TextView txt_num_item, txt_all_points;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private StoreAdapter adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private double point = 0;
    private int item = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        reference = database.getReference(AllFinal.STORE_ITEMS);

/////////////////////////////////////////////////
        Intent intent = getIntent();
        num_of_clint_points = intent.getDoubleExtra("Client_Points", 0);
        clientiddd = intent.getStringExtra("Client_Id");
/////////////////////////////////////////////////

        buildUI();
    }


    private void getDataOfRecycle() {
        showProgress(true);
        reference.child(mAuth.getCurrentUser().getUid())
                .child(AllFinal.ITEMS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    showProgress(false);
                    Toast.makeText(StoreActivity.this, "no item in a store try again later", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
                itemModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ItemModel itemModel = snapshot.getValue(ItemModel.class);

                    itemModel.setId(snapshot.getKey());

                    itemModelList.add(itemModel);
                }

                adapter.setModel(itemModelList);
                recyclerView_store.setAdapter(adapter);

                showProgress(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buildUI() {
        progressDialog = new ProgressDialog(StoreActivity.this);
        progressDialog.setMessage("Wait Please");

        txt_all_points = findViewById(R.id.txt_item_price);
        txt_num_item = findViewById(R.id.txt_item_num);
//        searchView = findViewById(R.id.search_store);
        recyclerView_store = findViewById(R.id.rec_store);
        recyclerView_store.setHasFixedSize(true);
        recyclerView_store.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        itemModelList = new ArrayList<>();
        adapter = new StoreAdapter();
        adapter.onClickItem(this);
        chartItemList = new HashMap<>();


        getDataOfRecycle();
        //searchViewListerers();
    }


    private void searchViewListerers() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapter.getFilter().filter(s);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("#");

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.getFilter().filter("#");
                return false;
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////
    private Boolean checkInternetConnection() {
        Boolean internetConnection = false;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();

        if (connection != null) {
            if (connection.getType() == ConnectivityManager.TYPE_WIFI)
                return internetConnection = true;
            else if (connection.getType() == ConnectivityManager.TYPE_MOBILE)
                return internetConnection = true;
            else
                return internetConnection = false;
        }

        return internetConnection;
    }

    public void gotoChart(View view) {
        if (!checkInternetConnection()) {
            Toast.makeText(this, "check internet connection ", Toast.LENGTH_SHORT).show();
            return;

        }


        if (point <= 0) {
            Toast.makeText(this, "select items first and try again  ", Toast.LENGTH_SHORT).show();
            return;
        }


        if (num_of_clint_points < point) {
            Toast.makeText(this, "your points is not enough ", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
            intent.putExtra(AllFinal.ALL_POINT, point);
            intent.putExtra("clientt_id", clientiddd);
            intent.putExtra("client_points_before_sell", num_of_clint_points);
            startActivity(intent);
            finish();
        }
    }

    private void showProgress(boolean s) {
        if (s == true) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClickPlus(final int pos, TextView view, TextView v2) {
        int it = Integer.parseInt(v2.getText().toString());
        if (it <= 0) {
            Toast.makeText(this, "sorry " + StoreAdapter.itemModels.get(pos).getName() + " not avaliable", Toast.LENGTH_SHORT).show();
            return;
        }

        int a = Integer.parseInt(view.getText().toString());
        a++;

        view.setText(a + "");
        item++;
        txt_num_item.setText(item + "");
        double d = StoreAdapter.itemModels.get(pos).getPoint();
        point += d;
        txt_all_points.setText(new DecimalFormat("##.##").format(point) + " point");
        it--;
        v2.setText(it + "");
        ChartItem chartItem = new ChartItem(StoreAdapter.itemModels.get(pos).getImg_url(),
                StoreAdapter.itemModels.get(pos).getId(), StoreAdapter.itemModels.get(pos).getName(), a, d);


        chartItemList.put(chartItem.getId(), chartItem);


    }

    @Override
    public void onClickMinus(final int pos, TextView view, TextView v2) {
        final int y = StoreAdapter.itemModels.get(pos).getNumber_units();
        int it = Integer.parseInt(v2.getText().toString());
        int a = Integer.parseInt(view.getText().toString());
        a--;
        if (a < 0) {
            a = 0;
            chartItemList.remove(StoreAdapter.itemModels.get(pos).getId());
            return;

        }


        view.setText(a + "");
        item--;
        if (item < 0)
            item = 0;
        txt_num_item.setText(item + "");
        double d = StoreAdapter.itemModels.get(pos).getPoint();
        point -= d;
        if (point < 0)
            point = 0.0;
        txt_all_points.setText(new DecimalFormat("##.##").format(point) + " point");
        it++;
        if (it > y) {
            it = y;
        }
        v2.setText(it + "");

        ChartItem chartItem = new ChartItem(StoreAdapter.itemModels.get(pos).getImg_url(),
                StoreAdapter.itemModels.get(pos).getId(), StoreAdapter.itemModels.get(pos).getName(), a, d);
        chartItemList.put(chartItem.getId(), chartItem);

    }

    @Override
    public void onUpdate(int pos) {
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", itemModelList.get(pos));
        bundle.putBoolean(AllFinal.UPDATE, true);
        intent.putExtra("bundle1", "dssd");
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    public void ondelete(int pos) {
        if (!checkInternetConnection()) {
            Toast.makeText(this, "check internet connection ", Toast.LENGTH_SHORT).show();
            return;

        }
        Log.d("wwwwwww", StoreAdapter.itemModels.get(pos).getId());


        reference.child(mAuth.getCurrentUser().getUid())
                .child(AllFinal.ITEMS).child(itemModelList.get(pos).getId())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(StoreActivity.this, "item deleted", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
