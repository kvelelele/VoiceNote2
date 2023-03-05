package amir.voicenoded;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import amir.voicenoded.Database.Record;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<Record> listRecord;

    public MyAdapter(Context context) {
        this.context = context;
        listRecord = new ArrayList<>();
    }
//tv = record.getField
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

//        Это же в мтеоде сетДата
//        Record record = listRecord.get(position);
//        holder.recordName.setText(record.getTitle());
//        holder.date.setText(record.getDate());
//        holder.time.setText(record.getTime());
//        holder.duration.setText(record.getDuration());

        holder.setData(listRecord.get(position));

    }

    @Override
    public int getItemCount() {
        return listRecord.size();
    }

//    В ЭТОТ КЛАСС ДОБАВИТЬ ОБРАБОТКУ КНОПКИ ПЛЕЙ/ПАУЗА, В МЕТОД onBindViewHolder УКАЗАТЬ ВОСПРОИЗВЕДЕНИЕ ПО ТРУ/ФОЛС КАК НА КНОПКЕ МИКРФОН
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView recordName, date, time, duration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recordName = itemView.findViewById(R.id.tv_record_name);
            date = itemView.findViewById(R.id.tv_date);
            time = itemView.findViewById(R.id.tv_time);
            duration = itemView.findViewById(R.id.tv_duration);

        }
        public void setData(Record record){
            this.recordName.setText(record.getTitle());
            this.date.setText(record.getDate());
            this.time.setText(record.getTime());
            this.duration.setText(record.getDuration());

        }
    }

    public void updateAdapter(List<Record> newList) {
        listRecord.clear();
        listRecord.addAll(newList);
        notifyDataSetChanged();
    }



}
