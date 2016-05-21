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
 * Created by chayut on 27/03/16.
 */
@Deprecated
public class EndPointAdapter  extends ArrayAdapter<EndPoint> {

    Context mContext;
    private List<EndPoint> mList;

    public EndPointAdapter(Context context, int textViewResourceId,
                         List<EndPoint> objects) {
        super(context, textViewResourceId, objects);

        this.mContext = context;
        this.mList = objects;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_endpoint, parent, false);

        TextView tvName = (TextView) rowView.findViewById(R.id.tvEndpointName);
        TextView tvValue = (TextView) rowView.findViewById(R.id.tvEndPointValue);
        TextView tvType = (TextView) rowView.findViewById(R.id.tvEndPointType);


        EndPoint mResult = mList.get(position);

        tvName.setText(mResult.getName());
        tvValue.setText(mResult.getValue());

        if (mResult.getType() == EndPoint.TYPE_EMAIL)
        {
            tvType.setText("Email");
        }
        else if(mResult.getType() == EndPoint.TYPE_PHONE){
            tvType.setText("Phone");
        }



        return rowView;
    }

    public EndPoint getItem(int position){

        return mList.get(position);
    }

}
