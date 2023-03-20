package comp5216.sydney.edu.au.healthproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GridviewAdapter extends BaseAdapter{
    private ArrayList<Food> list;
    private static HashMap<Integer,String> isSelected;
    private Context context;
    private LayoutInflater inflater = null;
    public GridviewAdapter(ArrayList<Food> list, Context context, String extra_data) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, String>();
        initDate(extra_data);
    }
    public void initDate(String extra_data){
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        SharedPreferences sp =context.getSharedPreferences(dateNowStr+"_food", MODE_PRIVATE);
        String data=sp.getString(extra_data+"Food_data",null);
        System.out.println(data);
        String[] food;
        if(data!=null) {
            for (int i = 0; i < list.size(); i++) {
                getIsSelected().put(i, "0");
            }
            food = data.split(",");
            for (int i = 0; i < list.size(); i++) {
                for(String key:food){
                    if(i==Integer.valueOf(key.split("-")[0])){
                        Tool.list.put(Integer.valueOf(key.split("-")[0]),Integer.valueOf(key.split("-")[1]));
                        getIsSelected().put(i, key.split("-")[1]);
                        break;
                    }

                }
            }
        }else{
            for (int i = 0; i < list.size(); i++) {
                getIsSelected().put(i, "0");
            }
        }
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gridviewitem, null);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.Power = (TextView) convertView.findViewById(R.id.item_power);
            holder.ID = (TextView) convertView.findViewById(R.id.item_ID);
            holder.cb = (SeekBar) convertView.findViewById(R.id.payment_seekbar);
            holder.percent = (TextView) convertView.findViewById(R.id.percent_text);
            ViewHolder finalHolder = holder;
            holder.cb.setProgress(Integer.parseInt(getIsSelected().get(position))/2);
            holder.percent.setText(getIsSelected().get(position)+"g");
            holder.cb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    finalHolder.percent.setText(String.valueOf(progress*2)+"g");
                    Tool.list.put(position,progress*2);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ID.setText(list.get(position).getId());
        holder.Power.setText(String.valueOf(list.get(position).getPower()));
        holder.name.setText(list.get(position).getName());

        return convertView;
    }
    static class ViewHolder{
        SeekBar cb;
        TextView name;
        TextView ID;
        TextView Power;
        TextView percent;
    }
    public static HashMap<Integer,String> getIsSelected() {
        return isSelected;
    }


}