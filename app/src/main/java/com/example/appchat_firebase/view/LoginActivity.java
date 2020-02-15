package com.example.appchat_firebase.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
Button btEntrar;
EditText editEmail;
EditText editSenha;
TextView tvCriarConta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btEntrar = findViewById(R.id.btEntrar);
        editEmail = findViewById(R.id.email);
        editSenha = findViewById(R.id.senha);
        tvCriarConta = findViewById(R.id.txtNãoTemConta);


        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String nome,email,senha;
                email = editEmail.getText().toString();
                senha = editSenha.getText().toString();

                verificarUserInFirebase(email,senha);




            }
        });



        tvCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verificarUserInFirebase(String email, String senha) {
        if (email == null || email.equals("") || senha == null || senha.equals("")) {
            Toast.makeText(LoginActivity.this, "Email e senha devem ser preenchidos", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(LoginActivity.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MessagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //deu bom
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //deu ruim
                Toast.makeText(LoginActivity.this, "Login deu ruim", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
