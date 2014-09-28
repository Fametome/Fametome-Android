package com.fametome.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.listener.OnFaceClickListener;
import com.fametome.object.Face;
import com.fametome.object.User;

public class OutboxFlashsListAutoCompleteAdapter extends BaseAdapter implements Filterable{

    private OnFaceClickListener faceClickListener;

	private List<Face> filteredFaces = null;

    private FacesFilter facesFilter = null;

    private LayoutInflater inflater = null;
	
	public OutboxFlashsListAutoCompleteAdapter(Context context){
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        facesFilter = new FacesFilter();
	}

	@Override
	public int getCount() {
		return filteredFaces.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		convertView = inflater.inflate(R.layout.item_outbox_auto_complete_list, null);
		
		TextView text = (TextView)convertView.findViewById(R.id.text);
		ImageView picture = (ImageView)convertView.findViewById(R.id.picture);
		
		text.setText(filteredFaces.get(position).getText());
		picture.setImageBitmap(filteredFaces.get(position).getPicture().getBitmap());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faceClickListener != null){
                    faceClickListener.onFaceClicked(filteredFaces.get(position));
                }
            }
        });
		
		return convertView;
	}

    public void setFaceClickListener(OnFaceClickListener faceClickListener){
        this.faceClickListener = faceClickListener;
    }

	@Override
	public Filter getFilter() {
		return facesFilter;
	}
	
	public class FacesFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint){
			
			FilterResults results = new FilterResults();

            if(constraint == null){
                constraint = "";
            }

			Log.i("FlashAutoCompleteAdapter", "(FacesFilter > performFiltering) constraint : " + constraint);
			
			filteredFaces = new ArrayList<Face>();
			
			for(int i = 0; i < User.getInstance().getFacesNumber(); i++){
				if(User.getInstance().getFace(i).getText().startsWith(constraint.toString())){
					filteredFaces.add(User.getInstance().getFace(i));
				}
			}
			
			results.values = filteredFaces;
			results.count = filteredFaces.size();
	
			return results;
	
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

			Log.i("FlashAutoCompleteAdapter", "(FacesFilter > publishResults) constraint : " + (constraint == null ? "" : constraint) + " || results number : " + results.count);

            filteredFaces = (List<Face>)results.values;
            notifyDataSetChanged();
		}
	}
		
}
