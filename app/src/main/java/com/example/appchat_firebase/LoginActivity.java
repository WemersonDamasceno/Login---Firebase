package com.example.appchat_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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



        tvCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
