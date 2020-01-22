package neel.com.railmappingkl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import neel.com.railmappingkl.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);


    }

    private void renderWindowText(Marker marker,View view){

        String title = marker.getTitle();
        TextView titleTxt = view.findViewById(R.id.tv_custom_info_window_title);

        if(!title.equals("")){

            titleTxt.setText(title);

        }

        String snippet = marker.getSnippet();
        TextView snippetTxt = view.findViewById(R.id.tv_custom_info_window_snippet);

        if(!snippet.equals("")){

            snippetTxt.setText(snippet);

        }



    }


    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }
}
