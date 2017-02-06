package xyz.encryptany.encryptany;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Cory on 2/8/2017.
 */


public class OverlayRecyclerViewAdapter extends RecyclerView.Adapter<OverlayRecyclerViewAdapter.ViewHolder> {
    private String[] mDataset;

    public OverlayRecyclerViewAdapter(String[] myDataset) {
        this.mDataset = myDataset;
    }

    @Override
    public OverlayRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overlay_message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverlayRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return  mDataset.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView mTextView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = (TextView)view.findViewById(R.id.overlay_list_textview);
        }
        @Override
        public String toString() {
            return super.toString();
        }
    }
}