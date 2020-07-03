package com.icandothisallday2020.ex85firebasechatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class ChattingActivity extends AppCompatActivity {
    ListView listView;
    EditText et;
    ChatAdapter adapter;
    ArrayList<MItem> items=new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        //제목줄의 글씨 : 본인 닉네임
        getSupportActionBar().setTitle(G.nickName);

        et=findViewById(R.id.et_msg);
        listView=findViewById(R.id.listveiw);
        adapter=new ChatAdapter(this,items);
        listView.setAdapter(adapter);

        //Firebase DB의 "chat"이라는 이름의 자식노드의 채팅데이터들 저장
        //이름을 변경하면 여러 채팅방 제작 가능
        firebaseDatabase=FirebaseDatabase.getInstance();
        chatRef=firebaseDatabase.getReference("chat");

        //채팅멤세지의 변경내역에 반응하는 리스너 추가
        //ValueEventListener 는 값 변경할때마다  전체 데이터를 다시줌

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //추가된 메세지 데이터 하나의 스냅샷을 줌
                MItem item=dataSnapshot.getValue(MItem.class);

                //새로 추가된 아이템을 리스트에 추가
                items.add(item);
                adapter.notifyDataSetChanged();
                //리스트뷰의 커서 위치를 가장 마지막 아이템 포지션으로--포커스를 가장 최근 메세지로(맨 아래로)
                listView.setSelection(items.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void clickSend(View view) {
        //firebase DB에 저장할 데이터들(nickname,message,time,profile)
        String name=G.nickName;
        String message=et.getText().toString();
        String profileUrl=G.profileUrl;
        //메세지 작성 시간
        Calendar calendar=Calendar.getInstance();
        String APM;
        if (calendar.get(Calendar.MINUTE)==0) APM="AM";
        else APM="PM";
        @SuppressLint("WrongConstant") String time=calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+APM;
        //객체를 한번에 저장
        MItem item=new MItem(name,message,time,profileUrl);
        chatRef.push().setValue(item);//객체를 한번에 저장

        et.setText("");

        //메세지를 보내면 소프트 키패드가 들어가도록
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus()/*현재 포커스를 갖고 있는 놈*/.getWindowToken()/*토큰:권한"*/
                ,0/*flag:Googleing - 0: right now*/);
    }
}
