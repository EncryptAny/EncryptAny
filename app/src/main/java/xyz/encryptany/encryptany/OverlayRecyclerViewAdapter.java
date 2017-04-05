package xyz.encryptany.encryptany;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;

/**
 * Created by Cory on 2/8/2017.
 */


public class OverlayRecyclerViewAdapter extends RecyclerView.Adapter<OverlayRecyclerViewAdapter.ViewHolder> {
    private ArrayList<EncryptedMessage> mMsgs;

    public OverlayRecyclerViewAdapter(ArrayList<EncryptedMessage> enc_msg) {
        this.mMsgs = enc_msg;
    }
    public OverlayRecyclerViewAdapter() {
        this.mMsgs = new ArrayList<>();
    }

    @Override
    public OverlayRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overlay_message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverlayRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tvMsgSender.setText(mMsgs.get(position).getAuthor());
        holder.tvMsgTimestamp.setText(Long.toString(mMsgs.get(position).getDate()));
        holder.tvMsgContent.setText(mMsgs.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return  mMsgs.size();
    }

    public void addMessage(EncryptedMessage enc_msg) {
        mMsgs.add(enc_msg);
        super.notifyItemInserted(mMsgs.size() - 1);
    }

    public void updateMessages(ArrayList<EncryptedMessage> enc_msg) {
        this.mMsgs = enc_msg;
        super.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView tvMsgSender;
        public TextView tvMsgTimestamp;
        public TextView tvMsgContent;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvMsgSender = (TextView)view.findViewById(R.id.overlay_msg_sender);
            tvMsgTimestamp = (TextView)view.findViewById(R.id.overlay_msg_timestamp);
            tvMsgContent = (TextView)view.findViewById(R.id.overlay_msg_content);
        }
        @Override
        public String toString() {
            return super.toString();
        }
    }

}