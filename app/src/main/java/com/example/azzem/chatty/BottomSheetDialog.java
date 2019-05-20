//package com.example.azzem.chatty;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomSheetDialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//public class BottomSheetDialog extends BottomSheetDialogFragment
//{
//    //member variable to bottomSheetListener.
//    private BottomSheetListener mListener;
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
//    {
//        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
//        //reference of the button in my dialog.
//        Button button1 = v.findViewById(R.id.b1);
//        Button button2 = v.findViewById(R.id.b2);
//        button1.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                mListener.onButtonClicked();
//                dismiss();
//            }
//        });
//
//        button2.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                mListener.onButtonClicked();
//                dismiss();
//            }
//        });
//        return v;
//    }
//
//    //Interface
//    public interface BottomSheetListener
//    {
//        //String from my dialog to my activity.
//        //This String contain which button is clicked and textform...!
//        void onButtonClicked();
//    }
//
//    //context is the activity which attach my dialog.
//    //When i open this dialog from mainActivity context will be the mainActivity.
//    @Override
//    public void onAttach(Context context)
//    {
//        super.onAttach(context);
//        //I want the mainActivity as listener to my dialog i assigned to context.
//        //And implement this interface to my main activity.
//
//        //When we try to open this bottomSheetDialog from activity and forgot tom implement
//        //this interface into this activity app crash and ai will get this error -->
//        //" must implement bottomSheetListener"
//        try{
//        mListener = (BottomSheetListener) context;}
//        catch(ClassCastException e)
//        {
//            //context.toString show the name of the mainActivity.
//            throw  new  ClassCastException(context.toString()
//            +" must implement BottomSheetListener");
//        }
//    }
//}
