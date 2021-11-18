package com.appz.qrcode.seller_tasks.adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appz.qrcode.R;
import com.appz.qrcode.seller_tasks.models.ItemModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.VH> implements Filterable {
    public static List<ItemModel> itemModels;
    private List<ItemModel> itemModels2;
    private StoreOnClickItem clickItem;
    private List<ItemModel> listClincFilter;


    public void setModel(List<ItemModel> itemModels) {

        StoreAdapter.itemModels = itemModels;
        this.itemModels2 = itemModels;
        this.listClincFilter = new ArrayList<>();


        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);
        return new VH(view);
    }

    public void onClickItem(StoreOnClickItem clickItem) {
        this.clickItem = clickItem;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.fillData(itemModels.get(position));

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                CharSequence charString = charSequence.toString();


                if (charString.equals(" ") || charString.equals("#")) {

                    listClincFilter = itemModels;
                    charString = "";
                } else {

                    List<ItemModel> filteredList = new ArrayList<>();
                    for (ItemModel model : itemModels2) {

                        if (model.getName().toLowerCase().contains(charString)) {

                            filteredList.add(model);
                        }
                    }
                    listClincFilter = filteredList;
                }
                FilterResults filterResults = new FilterResults();


                filterResults.values = listClincFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemModels = (List<ItemModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    class VH extends RecyclerView.ViewHolder {
        private ImageView img_store_item;
        private TextView name_store, all_num_units, point_store, num;
        private ImageButton btn_minis, btn_plus;
        private PopupMenu popupMenu;
        private View view;

        public VH(@NonNull final View itemView) {
            super(itemView);
            this.view = itemView;
            buildView(itemView);
            popupMenu = new PopupMenu(itemView.getContext(), view);
            popupMenu.inflate(R.menu.store_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.update:
                            clickItem.onUpdate(getAdapterPosition());
                            return true;

                        case R.id.delete:
                            if (!checkInternetConnection()) {
                                Toast.makeText(view.getContext(), "check internet connection ", Toast.LENGTH_SHORT).show();
                                return true;

                            }
                            clickItem.ondelete(getAdapterPosition());
                            itemModels.remove(getAdapterPosition());

                            notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    popupMenu.show();

                    return false;
                }
            });
        }

        private Boolean checkInternetConnection() {
            Boolean internetConnection = false;
            ConnectivityManager manager = (ConnectivityManager) view.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        void fillData(ItemModel model) {
            name_store.setText(model.getName());
            all_num_units.setText(model.getNumber_units() + "");
            point_store.setText(model.getPoint() + "");

            if (model.getImg_url() == "") {
                Glide.with(view.getContext())
                        .load(view.getResources().getDrawable(R.drawable.img_no))
                        .into(img_store_item);
            } else {
                Glide.with(view.getContext())
                        .load(model.getImg_url())
                        .into(img_store_item);
            }


        }

        private void buildView(View v) {
            img_store_item = v.findViewById(R.id.img_store);
            name_store = v.findViewById(R.id.txt_name_store);
            all_num_units = v.findViewById(R.id.txt_all_number_store);
            point_store = v.findViewById(R.id.txt_point_store);
            num = v.findViewById(R.id.txt_store_n);


            btn_minis = v.findViewById(R.id.btn_minus);
            btn_plus = v.findViewById(R.id.btn_plus);

            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickItem != null) {
                        clickItem.onClickPlus(getAdapterPosition(), num, all_num_units);
                    }

                }
            });
            btn_minis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickItem != null) {
                        clickItem.onClickMinus(getAdapterPosition(), num, all_num_units);
                    }
                }
            });

        }


    }
}
