package com.icandothisallday2020.ex85firebasechatting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView ivProfile;
    EditText etName;

    Uri imgUri;//프로필 이미지 Uri 참조변수
    boolean isChanged=false;//프로필 이미지 변경여부


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivProfile=findViewById(R.id.iv_profile);
        etName=findViewById(R.id.et_name);

        //이미 저장되어있는 정보들 읽어오기
        loadData();
        if(!G.nickName.equals("(알 수 없는 사용자)")){//null 이면 == 비교 가능  G == null
            //저장된 것이 있다면
            etName.setText(G.nickName);
            Glide.with(this).load(G.profileUrl).into(ivProfile);
        }
    }

    public void clickCIV(View view) {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
            imgUri=data.getData();
            if(imgUri!=null){
                Glide.with(this).load(imgUri).into(ivProfile);

                //profile image 를 변경했으니
                isChanged=true;
            }
        }
    }

    //데이터를 저장하는 메소드
    void saveData(){
        //프로필이미지와 채팅명을 Firebase DB에 저장
        G.nickName=etName.getText().toString();
//        G.profileUri=imgUri.toString();//Uri 전체경로를 저장
        
        //First, Save a profile image file on Firebase Storage
        //업로드할 파일명이 겹치지 않게 날짜를 이용하여 파일명 지정
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName=sdf.format(new Date())+".png";
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        final StorageReference imgRef=firebaseStorage.getReference("profiles/"+fileName);

        //이미지 업로드
        UploadTask task=imgRef.putFile(imgUri);//task :이미지 업로드에는 시간이 걸리기 때문에 별도의 스레드에서 작업
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //firebase 실시간 DB에 저장할 저장소에 업로드된 실제 인터넷 경로 URL 알아내기
                imgRef.getDownloadUrl()/*이 메소드의 반환값 :Task*/.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {//parameter:다운로드 uri
                        //다운로드 URL 을 G.profileUri 에 저장
                        G.profileUrl =uri.toString();
                        
                        //Firebase 에 저장된 이미지 파일의 경로를 DB에 저장(다른 사용들도 보이도록)
                        // &내 디바이스에도 저장(다음에 다시 앱을 열었을때 내 디바이스에서 가져오도록)
                        
                        //1.Firebase DB 에 저장 [ G.nickName, G.profileUri(http 주소) ]
                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                        DatabaseReference profileRef=firebaseDatabase.getReference("profiles");
                        //nickName 을 Key 값으로 지정한 노드에 이미지 URL을 값으로 지정
                        profileRef.child(G.nickName).setValue(G.profileUrl);
                        //"profiles"이라는 이름의 자식노드 참조객체
                        
                        //2.SharedPreference 를 이용하여 저장  [ G.nickName, G.profileUri ]
                        SharedPreferences pref=getSharedPreferences("account",MODE_PRIVATE);
                        SharedPreferences.Editor editor=pref.edit();//작업 시작
                        editor.putString("nickName",G.nickName);
                        editor.putString("profileUrl",G.profileUrl);
                        editor.commit();//작업 끝
                        Toast.makeText(MainActivity.this, "프로필을 저장", Toast.LENGTH_SHORT).show();
                        
                        //모든 저장이 완료되었으므로 채팅화면으로 이동
                        Intent intent=new Intent(MainActivity.this,ChattingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }

    //디바이스에 저장된 정보 읽어오기
    void loadData(){
        SharedPreferences pref=getSharedPreferences("account",MODE_PRIVATE);
        G.nickName=pref.getString("nickName","(알 수 없는 사용자)");
        G.profileUrl=pref.getString("profileUrl","");
    }

    public void clickBtn(View view) {
        //데이터가 변경되었을때만 --프로필이미지와 채팅명을 Firebase DB에 다시 저장
        if(isChanged) {
            saveData();
        }
        else {
            Intent intent=new Intent(this,ChattingActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
