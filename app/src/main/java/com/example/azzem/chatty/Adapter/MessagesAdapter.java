package com.example.azzem.chatty.Adapter;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private String imageURL, conversationId, name;
    private List<Messages> mMessageList;
    private Context mContext;

    FirebaseUser fuser;
    FirebaseFirestore deleteMsgRef;

    public MessagesAdapter(List<Messages> mMessageList, Context mContext, String imageURL, String conversationId, String username)
    {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
        this.imageURL = imageURL;
        this.conversationId = conversationId;
        this.name = username;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        deleteMsgRef = FirebaseFirestore.getInstance();
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
        private TextView messageText, messageTime, usernameTextView;
        private CircleImageView profileImage;
        private ImageView messageImage;

        public MessagesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            messageImage = itemView.findViewById(R.id.message_image_layout);
            messageTime = itemView.findViewById(R.id.time_textView);
            usernameTextView = itemView.findViewById(R.id.receiverName);
            messageImage.setClipToOutline(true);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, final int position)
    {
        final Messages msg = mMessageList.get(position);

        System.out.println("nchoufou djibou wela aha l msg ya3ni "+msg.getMessage());

        messagesViewHolder.messageTime.setText(msg.getTime());

        if(imageURL.equals("default"))
        {
            String letter = String.valueOf(name.toUpperCase().charAt(0));
            int color = R.color.colorPrimaryDark;
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color);
            messagesViewHolder.profileImage.setBackground(drawable);
        }
        else
        {
            Picasso.get().load(imageURL).into(messagesViewHolder.profileImage);
        }

        if(msg.getType().equals("text"))
        {
            messagesViewHolder.messageText.setText(msg.getMessage());
            messagesViewHolder.messageImage.setVisibility(View.GONE);
        }
        else
        {
            messagesViewHolder.messageText.setVisibility(View.GONE);
            Picasso.get().load(msg.getMessage()).placeholder(R.drawable.image_maker).into(messagesViewHolder.messageImage);
        }

        messagesViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                System.out.println("le doc id "+conversationId);

                AlertDialog.Builder delete_history_dialog = new AlertDialog.Builder(mContext);

                delete_history_dialog.setTitle("Delete message");
                delete_history_dialog.setMessage("Are you sure you want to delete this message?");

                delete_history_dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteMsgRef.collection("Chats")
                                .document(conversationId)
                                .collection("Messages")
                                .document(msg.getDocumentId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            mMessageList.remove(position);
                                            notifyDataSetChanged();
                                            System.out.println("DELETING");
                                        }
                                        else
                                        {
                                            System.out.println("NOT DELETING");
                                        }
                                    }
                                });
                    }
                });

                delete_history_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                delete_history_dialog.create();
                delete_history_dialog.show();
                return true;
            }
        });

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
