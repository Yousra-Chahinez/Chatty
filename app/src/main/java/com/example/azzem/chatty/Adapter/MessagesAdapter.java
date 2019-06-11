package com.example.azzem.chatty.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.MODE_PRIVATE;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>
{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private String imageURL;
    private List<Messages> mMessageList;
    private Context mContext;
    private ClipboardManager clipboardManager;

    FirebaseUser fuser;

    public MessagesAdapter(List<Messages> mMessageList, Context mContext, String imageURL)
    {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessagesAdapter.MessagesViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessagesAdapter.MessagesViewHolder(view);
        }
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        private TextView messageText;
        private CircleImageView  profileImage;
        private ImageView messageImage;

        public MessagesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            messageImage = itemView.findViewById(R.id.message_image_layout);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, final int position)
    {
        Messages msg = mMessageList.get(position);
        System.out.println("nchoufou djibou wela aha l msg ya3ni "+msg.getMessage());

        if(imageURL.equals("default"))
        {
//            SharedPreferences preferences = mContext.getSharedPreferences("Name of the user", MODE_PRIVATE);
//            String name = preferences.getString("username", "");
//            assert name != null;
//            String letter = String.valueOf(name.charAt(0));
//            int color = R.color.gradient_end_color;
//            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color);
//            messagesViewHolder.profileImage.setBackground(drawable);
        }
        else
        {
            Picasso.get().load(imageURL).into(messagesViewHolder.profileImage);
        }

        if(msg.getType().equals("text"))
        {
            messagesViewHolder.messageText.setText(msg.getMessage());
            //messagesViewHolder.messageImage.setVisibility(View.GONE);
        }
        else
        {
            messagesViewHolder.messageText.setVisibility(View.GONE);
            Picasso.get().load(msg.getMessage()).placeholder(R.drawable.image_maker).into(messagesViewHolder.messageImage);
        }

        //----------GET THE RECEIVER ID FROM USER ADAPTER-------------//
        SharedPreferences preferences = mContext.getSharedPreferences("Receiver_id", MODE_PRIVATE);
        final String receiver_id = preferences.getString("userid", "");
        //------------------------------------------------------------//

    }

    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mMessageList.get(position).getFrom().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
