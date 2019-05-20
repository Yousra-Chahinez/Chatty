package com.example.azzem.chatty.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.azzem.chatty.MessageActivity;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>
{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1; //1 is important

    private String imageURL;
    private List<Messages> mMessageList;
    private DatabaseReference deleteRef, deleteRef2;
    private FirebaseAuth mAuth;
    private Context mContext;
    ClipboardManager clipboardManager;
    MessagesAdapter messagesAdapter;
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
        /*View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.massage_single_layout, parent, false);*/
        //return new MessagesViewHolder(v);
        mAuth = FirebaseAuth.getInstance();
        messagesAdapter = new MessagesAdapter(mMessageList, mContext, imageURL);
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
        private TextView messageText, timeText, textSeen;
        private CircleImageView  profileImage;
        private ImageView messageImage;

        public MessagesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            messageImage = itemView.findViewById(R.id.message_image_layout);
            timeText = itemView.findViewById(R.id.time_textView);
            textSeen = itemView.findViewById(R.id.text_seen);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, final int position)
    {
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        final String message_Type = c.getType();

        //Convert timestamp to time
        long timestamp = c.getTime();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String cal[] = calendar.getTime().toString().split(" ");
        final String time_of_messages = cal[1]+","+cal[2]+" "+cal[3].substring(0,5);
        Log.e("TIME IS ", calendar.getTime().toString());
        System.out.println("TIME IS sout "+ calendar.getTime().toString());
        messagesViewHolder.timeText.setText(time_of_messages);

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

        //Check for last message.
        if(position == mMessageList.size()-1)
        {
            //if seen == true.
            if(c.getSeen())
            {
                messagesViewHolder.textSeen.setText("seen");
            }
            else
            {
                messagesViewHolder.textSeen.setText("Delivered");
            }
        }
        else
        {
            messagesViewHolder.textSeen.setVisibility(View.GONE);
        }


        //And we will get that value as String
        //For the layout changes
        /*if(from_user.equals(current_user_id))
        {
            messagesViewHolder.messageText.setBackgroundColor(Color.WHITE);
            messagesViewHolder.messageText.setTextColor(Color.BLACK);
        }
        else //The other person who ih sending the message
        {
            messagesViewHolder.messageText.setBackgroundResource(R.drawable.background_right);
            messagesViewHolder.messageText.setTextColor(Color.WHITE);
        }*/
        if(message_Type.equals("text"))
        {
            messagesViewHolder.messageText.setText(c.getMessage());
            messagesViewHolder.messageImage.setVisibility(View.GONE);
        }
        else
        {
            messagesViewHolder.messageText.setVisibility(View.GONE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.image_maker).into(messagesViewHolder.messageImage);
        }

        //----------GET THE RECEIVER ID FROM USER ADAPTER-------------//
        SharedPreferences preferences = mContext.getSharedPreferences("Receiver_id", MODE_PRIVATE);
        final String receiver_id = preferences.getString("userid", "");
        //------------------------------------------------------------//

        messagesViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                new BottomSheet.Builder(mContext, R.style.BottomSheet_Dialog)
                               .sheet(R.menu.message_menu)
                               .grid()
                               .listener(new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        switch(which)
                        {
                            case R.id.delete:

                                    assert receiver_id != null;
                                    deleteRef = FirebaseDatabase.getInstance().getReference("messages").child(fuser.getUid())
                                                .child(receiver_id);
                                    deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                            {
                                                Messages messages1 = snapshot.getValue(Messages.class);
                                                assert messages1 != null;

                                                System.out.println("lahna nchoufou data snapshot" + dataSnapshot.getKey());
                                                System.out.println("lahna nchoufou  snapshot" + snapshot.getKey());
                                                deleteRef2 = deleteRef.child(Objects.requireNonNull(snapshot.getKey()));
                                            }
                                            deleteRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(mContext,"deleted", Toast.LENGTH_SHORT).show();
                                                        messagesViewHolder.messageText.setVisibility(View.GONE);
                                                        messagesViewHolder.timeText.setVisibility(View.GONE);
                                                        //messagesViewHolder.messageText.setFocusable(true);
                                                        messagesAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError)
                                        {
                                            Log.d(TAG, databaseError.getMessage());
                                        }
                                    });
                                    break;

                            case R.id.copy:
                                if (message_Type.equals("text"))
                                {
                                    clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (!clipboardManager.hasPrimaryClip())
                                    {
                                        //btn_paste.setEnabled(false);

                                        String text2 = messagesViewHolder.messageText.getText().toString();
                                        if (!text2.equals("")) {
                                            ClipData clipData = ClipData.newPlainText("text2", text2);
                                            clipboardManager.setPrimaryClip(clipData);
                                        }
                                        Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;

                            case R.id.edit:
                                String message_edit = messagesViewHolder.messageText.getText().toString();

                                SharedPreferences pref = mContext.getSharedPreferences("editing", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("edit", message_edit);
                                editor.apply();

                                Toast.makeText(mContext, "message" + message_edit, Toast.LENGTH_SHORT).show();

                                deleteRef = FirebaseDatabase.getInstance().getReference("messages").child(fuser.getUid())
                                        .child(receiver_id);
                                deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                        {
                                            Messages messages1 = snapshot.getValue(Messages.class);
                                            assert messages1 != null;
                                            deleteRef2 = deleteRef.child(Objects.requireNonNull(snapshot.getKey()));
                                        }
                                        deleteRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(mContext,"deleted", Toast.LENGTH_SHORT).show();
                                                    messagesViewHolder.messageText.setVisibility(View.GONE);
                                                    messagesViewHolder.timeText.setVisibility(View.GONE);
                                                    //messagesViewHolder.messageText.setFocusable(true);
                                                    messagesAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        Log.d(TAG, databaseError.getMessage());
                                    }
                                });
                                break;
                        }
                    }
                }).show();
                //----Send to UserAdapter.
                SharedPreferences preferences3 = mContext.getSharedPreferences("time", MODE_PRIVATE);
                SharedPreferences.Editor editor3 = preferences3.edit();
                editor3.putString("time", time_of_messages);
                editor3.apply();
                //------------------------
                return false;
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
