package me.chayut.SantaHelperLogic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.chayut.santaslittlehelper.R;

/**
 * Created by chayut on 1/05/16.
 */
public class SantaLocationAdapter  extends ArrayAdapter<SantaLocation> {

    Context mContext;
    private List<SantaLocation> mList;

    public SantaLocationAdapter (Context context, int textViewResourceId,
                           List<SantaLocation> objects) {
        super(context, textViewResourceId, objects);

        this.mContext = context;
        this.mList = objects;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_location, parent, false);

        TextView tvName = (TextView) rowView.findViewById(R.id.tvLocName);

        SantaLocation mResult = mList.get(position);

        tvName.setText(mResult.getName());

        return rowView;
    }

    public SantaLocation getItem(int position){

        return mList.get(position);
    }
}
