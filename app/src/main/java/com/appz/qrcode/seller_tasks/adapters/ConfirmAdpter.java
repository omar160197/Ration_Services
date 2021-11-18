package com.appz.qrcode.seller_tasks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appz.qrcode.R;
import com.appz.qrcode.seller_tasks.models.ChartItem;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class ConfirmAdpter extends RecyclerView.Adapter<ConfirmAdpter.VH> {
    private List<ChartItem> chartItems;
    private ConfirmOnCloseListener confirmOnCloseListener;

    public void addList(List<ChartItem> chartItems) {
        this.chartItems = chartItems;
        notifyDataSetChanged();
    }

    public void onClosrListerner(ConfirmOnCloseListener confirmOnCloseListener) {
        this.confirmOnCloseListener = confirmOnCloseListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.confirem_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.fillViews(chartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return chartItems.size();
    }

    class VH extends RecyclerView.ViewHolder {
        private ImageView img_item, img_close;
        private TextView txt_name, txt_points, txt_num_item;
        private View v;
        private double res = 0;

        public VH(@NonNull View itemView) {
            super(itemView);
            this.v = itemView;
            buildViews(itemView);
        }

        private void buildViews(View itemView) {
            img_item = itemView.findViewById(R.id.img_confirm);
            img_close = itemView.findViewById(R.id.img_close);
            txt_name = itemView.findViewById(R.id.txt_confirm_name);
            txt_num_item = itemView.findViewById(R.id.txt_confirm_num_item);
            txt_points = itemView.findViewById(R.id.txt_confirm_points);

        }

        private void fillViews(ChartItem chartItem) {
            if (chartItem.getImg_url() == "") {
                Glide.with(v.getContext())
                        .load(v.getResources().getDrawable(R.drawable.img_no))
                        .into(img_item);
            } else {
                Glide.with(v.getContext())
                        .load(chartItem.getImg_url())
                        .into(img_item);
            }


            txt_name.setText(chartItem.getName());
            txt_num_item.setText(chartItem.getNum_selected() + " item");


            res = chartItem.getNum_selected() * chartItem.getPoint();
            txt_points.setText(new DecimalFormat("##.##").format(res) + " point");

            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmOnCloseListener.onClose(res);

                    chartItems.remove(getAdapterPosition());
                    notifyDataSetChanged();

                }
            });

        }
    }


}
