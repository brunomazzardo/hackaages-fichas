package ages.hacka.fichasapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ages.hacka.fichasapp.model.Aposta;
import ages.hacka.fichasapp.model.Ficha;
import ages.hacka.fichasapp.model.Jogada;
import ages.hacka.fichasapp.util.AdicionaAposta;

public class SalaJogoActivity extends AppCompatActivity {

    ImageView ficha10Imgg;
    ImageView ficha20Imgg;
    ImageView ficha50Imgg;
    ImageView ficha100Imgg;
    TextView aposta;
    TextView total;
    FirebaseUser user;
    Button apostarBtn;
    Button cancelarBtn;
    Button ganheiBtn;
    TextView log;
    TextView mesaMenu;
    String novaMsg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sala_jogo);

        ficha10Imgg = (ImageView) findViewById(R.id.ficha10Img);
        ficha20Imgg = (ImageView) findViewById(R.id.ficha20Img);
        ficha50Imgg = (ImageView) findViewById(R.id.ficha50Img);
        ficha100Imgg = (ImageView) findViewById(R.id.ficha100Img);
        aposta = findViewById(R.id.apostaNum);
        total = findViewById(R.id.totalNum);
        ganheiBtn = findViewById(R.id.ganheiBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        apostarBtn = findViewById(R.id.apostarBtn);
        cancelarBtn = findViewById(R.id.cancelarBtn);
        log = findViewById(R.id.log);
        mesaMenu = findViewById(R.id.mesaNum);


        final Ficha ficha10 = new Ficha(10);
        final Ficha ficha20 = new Ficha(20);
        final Ficha ficha50 = new Ficha(50);
        final Ficha ficha100 = new Ficha(100);
        final ArrayList<Ficha> fichaSet = new ArrayList<>();

        fichaSet.add(ficha10);
        fichaSet.add(ficha20);
        fichaSet.add(ficha50);
        fichaSet.add(ficha100);

        //Chamada da tela de compartilhar
        AlertDialog.Builder builder = new AlertDialog.Builder(SalaJogoActivity.this, R.style.CustomAlertDialog);
        View view1 = getLayoutInflater().inflate(R.layout.popup_compartilhar_sala, null);
        builder.setView(view1);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView tvNomeAdmin = view1.findViewById(R.id.tvNomeAdmin);
        EditText etCodigoCompartilhar = view1.findViewById(R.id.etCodigoCompartilhar);
        Button btnCompartilhar = view1.findViewById(R.id.btnCompartilhar);

        tvNomeAdmin.setText(getString(R.string.bem_vindo, user.getDisplayName().split(" ")));
        etCodigoCompartilhar.setText("61cb6fa63");
        etCodigoCompartilhar.setEnabled(false);

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Código da partida de poker: 61cb6fa63");
                startActivity(Intent.createChooser(sharingIntent, "Compartilhar via"));
            }
        });


        ganheiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int valorGanhou = Integer.parseInt(mesaMenu.getText().toString());
                mesaMenu.setText("" + 0);
                total.setText("" + (Integer.parseInt(total.getText().toString()) + valorGanhou));
                for (Ficha f : fichaSet) {
                    int apostaInt = Integer.parseInt(aposta.getText().toString());
                    int soma = apostaInt - (f.getValor() * f.getQuantidade());
                    String resultado = Integer.toString(soma);
                    aposta.setText(resultado);


                    f.setQuantidade(0);
                }
                Ficha ficha10 = new Ficha(10);
                Ficha ficha20 = new Ficha(20);
                Ficha ficha50 = new Ficha(50);
                Ficha ficha100 = new Ficha(100);
                List<Ficha> fichaSet = new ArrayList<>();
                fichaSet.add(ficha10);
                fichaSet.add(ficha20);
                fichaSet.add(ficha50);
                fichaSet.add(ficha100);
                Aposta aposta = new Aposta(fichaSet);
                Jogada jogada = new Jogada();
                jogada.setAposta(aposta);
                AdicionaAposta.adiciona(jogada);

            }
        });

        apostarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aposta apostaObject = new Aposta(fichaSet);
                Jogada jogada = new Jogada(user.getUid(), apostaObject, user.getDisplayName(), false);
                AdicionaAposta.adiciona(jogada);

                for (Ficha f : fichaSet) {
                    f.setQuantidade(0);
                }

                aposta.setText("0");
            }

        });

        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Ficha f : fichaSet) {
                    int apostaInt = Integer.parseInt(aposta.getText().toString());
                    int soma = apostaInt - (f.getValor() * f.getQuantidade());
                    String resultado = Integer.toString(soma);
                    aposta.setText(resultado);

                    int totalInt = Integer.parseInt(total.getText().toString());
                    int subtracao = totalInt + (f.getValor() * f.getQuantidade());
                    ;
                    String result = Integer.toString(subtracao);
                    total.setText(result);

                    f.setQuantidade(0);
                }
            }
        });

        ficha10Imgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(total.getText().toString()) < 10) return;
                aposter(10);
                ficha10.setQuantidade(ficha10.getQuantidade() + 1);
            }

        });

        ficha20Imgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(total.getText().toString()) < 20) return;
                aposter(20);
                ficha20.setQuantidade(ficha20.getQuantidade() + 1);
            }

        });

        ficha50Imgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(total.getText().toString()) < 50) return;
                aposter(50);
                ficha50.setQuantidade(ficha50.getQuantidade() + 1);
            }

        });

        ficha100Imgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(total.getText().toString()) < 100) return;
                aposter(100);
                ficha100.setQuantidade(ficha100.getQuantidade() + 1);
            }

        });


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("salas/-KxaHQa9r_igFHHhMy01/jogos/0/mao/jogadas/0");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nLog="";

                Jogada jogada = dataSnapshot.getValue(Jogada.class);

                if (jogada.calculaFicha() != 0){
                    nLog  = (log.getText().toString()) + "\n" + jogada.toString();
                    log.setText(nLog);
                    int valorAtual = Integer.parseInt(mesaMenu.getText().toString());
                    mesaMenu.setText("" + (valorAtual + jogada.calculaFicha()));
                }else{
                    if(jogada.getId()==null){
                        mesaMenu.setText("0");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getBaseContext(),"Failed to read value." + error.toException() ,Toast.LENGTH_LONG);
            }
        });
   }

    public void aposter(int num) {
//        Toast toast = Toast.makeText(getBaseContext(), num, Toast.LENGTH_LONG);
//        toast.show();

        int apostaInt = Integer.parseInt(aposta.getText().toString());
        int soma = apostaInt + num;
        String resultado = Integer.toString(soma);
        aposta.setText(resultado);

        int totalInt = Integer.parseInt(total.getText().toString());
        int subtracao = totalInt - num;
        String result = Integer.toString(subtracao);
        total.setText(result);
    }
}

