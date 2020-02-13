package com.example.appchat_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class RegisterActivity extends AppCompatActivity {
    final int GALERIA_IMAGENS = 1;
    final int TIRARFOTO = 3;
    final int PERMISSAO_REQUEST = 2;

    Button btEntrar;
    Uri selectedImage;
    Button btSelecionarFoto;
    ImageView imagemView;
    EditText editEmail;
    EditText editNome;
    EditText editSenha;





    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //Storage
    FirebaseStorage fireBaseStorage;
    StorageReference storageReference;
    //Autenticar
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //permissao do usuario
        checarPermissao();
        //Inicializando o banco de dados
        inicializandoFirebase();
        //inicializando o firebase fireBaseStorage
        fireBaseStorage = FirebaseStorage.getInstance();
        storageReference = fireBaseStorage.getReference();



        btSelecionarFoto = findViewById(R.id.btSelecionarFoto);
        btEntrar = findViewById(R.id.btEntrarNovo);
        editNome = findViewById(R.id.nomeNovo);
        editEmail = findViewById(R.id.emailNovo);
        editSenha = findViewById(R.id.senhaNovo);
        imagemView = findViewById(R.id.imgView);



        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        btSelecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
                btSelecionarFoto.setAlpha(0);
            }
        });











    }

    public void inicializandoFirebase() {
        FirebaseApp.initializeApp(RegisterActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
    private void checarPermissao() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }
    }
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALERIA_IMAGENS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //acessar galeria
        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {
            selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            assert selectedImage != null;
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            assert c != null;
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            imagemView.setImageBitmap(thumbnail);
        }
        //acessar camera
        if (requestCode == TIRARFOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagemView.setImageBitmap(imageBitmap);
            btSelecionarFoto.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }
    private void createUser() {
        String nome,email,senha;
        email = editEmail.getText().toString();
        senha = editSenha.getText().toString();
        nome = editNome.getText().toString();

        if (nome == null || nome.equals("") || email == null || email.equals("") || senha == null || senha.equals("")) {
            Toast.makeText(this, "Nome, email e senha devem ser preenchidos", Toast.LENGTH_LONG).show();
            return;
        }

        saveImgInFireBase();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //deu bom ?
                    Toast.makeText(RegisterActivity.this, "Sucesso ao autenticar", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Falha ao autenticar", Toast.LENGTH_SHORT).show();
            }
        });



    }
    //salvar imagem no banco de dados FireBase;
    private void saveImgInFireBase() {
        if (selectedImage != null) {
            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            storageReference = storageReference.child("images/"+ UUID.randomUUID().toString());
            storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //fazer algo com o id da imagem que é o uri
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setTitle("Uploaded " + (int) progress + "%");
                }
            });
        }
    }





}