package xyz.encryptany.encryptany;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;
import xyz.encryptany.encryptany.concrete.MessageFactory;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by Cory on 2/8/2017.
 */


public class OverlayRecyclerViewAdapter extends RecyclerView.Adapter<OverlayRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Message> mMsgs;
    private Format format;
    private static final String tag = "RecyclerViewAdapter";

    public OverlayRecyclerViewAdapter(ArrayList<Message> msg) {

        this.mMsgs = msg;
        this.format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }
    public OverlayRecyclerViewAdapter() {

        this.mMsgs = new ArrayList<>();
        this.format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }

    @Override
    public OverlayRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overlay_message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverlayRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(tag,"OnBindViewholder got text " + mMsgs.get(position).getMessage());
        holder.tvMsgSender.setText(mMsgs.get(position).getAuthor());
        holder.tvMsgTimestamp.setText(longToTS(mMsgs.get(position).getDate()));
        holder.tvMsgContent.setText(mMsgs.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return  mMsgs.size();
    }

    public void addMessage(Message msg) {
        Log.d(tag,"Adding message with text " + msg.getMessage());
        if (mMsgs.size() == 0)
            mMsgs.add(0, msg);
        else
            mMsgs.add(mMsgs.size(),msg);
        super.notifyDataSetChanged();
    }

    public void updateMessages(Message[] msg) {
        this.mMsgs.clear();
        for (int i = 0; i < msg.length; ++i)
            this.mMsgs.add(msg[i]);
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
    private String longToTS(long time)
    {
        Date date = new Date(time);
        return format.format(date);
    }

}