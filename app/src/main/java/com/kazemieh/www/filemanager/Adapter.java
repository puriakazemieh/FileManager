package com.kazemieh.www.filemanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder> {
    Context context;
    List<DataModel> dataModels;

    static String na;
    public static String[] naa;
    public static boolean[] selection;
    boolean multiselect = false;

    Adapter(Context context, List<DataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.tv_filename.setText(dataModels.get(position).getNamefile());
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_filename;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_filename = itemView.findViewById(R.id.tv_Adapter_filename);
            cardView = itemView.findViewById(R.id.cr_Adapter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (multiselect) {

                        checkmultiselect();
                    } else {
                        //      storege/emulated/0/alarms
                        String s = MainActivity.addres + "/" + tv_filename.getText().toString();
                        File file = new File(s);
                        if (file.isFile()) {
                            Toast.makeText(context, "این یک فایل است", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivity.refresh(s, context);
                        }
                    }

                }
            });

            // {false,true,false}
            {
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    checkmultiselect();

                    na = tv_filename.getText().toString();

                    return true;
                }
            });

        }

        public void checkmultiselect() {

            if (selection[getAdapterPosition()]) {
                selection[getAdapterPosition()] = false;
            } else {
                selection[getAdapterPosition()] = true;
                naa[getAdapterPosition()]=tv_filename.getText().toString();
            }
            if (selection != null) {
                if (selection[getAdapterPosition()]) {
                    cardView.setBackgroundColor(Color.CYAN);
                } else {
                    cardView.setBackgroundColor(Color.WHITE);
                }
            }
            for (int i = 0; i < selection.length; i++) {
                if (selection[i]) {
                    multiselect = true;
                    break;
                } else {
                    multiselect = false;
                }
            }

            if (multiselect) {
                MainActivity.cl_MainActivity_ccdr.setVisibility(View.VISIBLE);
            } else {
                MainActivity.cl_MainActivity_ccdr.setVisibility(View.GONE);
            }

        }
    }


}
