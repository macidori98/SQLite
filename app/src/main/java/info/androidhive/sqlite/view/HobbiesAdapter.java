package info.androidhive.sqlite.view;



import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.model.Hobby;

public class HobbiesAdapter extends RecyclerView.Adapter<HobbiesAdapter.MyViewHolder> {

    private Context context;
    private List<Hobby> hobbiesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView hobby;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            hobby = view.findViewById(R.id.hobby);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public HobbiesAdapter(Context context, List<Hobby> hobbiesList) {
        this.context = context;
        this.hobbiesList = hobbiesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hobby_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Hobby hobby = hobbiesList.get(position);

        holder.hobby.setText(hobby.getHobby());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(hobby.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return hobbiesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
