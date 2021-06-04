package nelson.aparecido.estudaki;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContatosActivity extends AppCompatActivity {

    View calendario, lupa, home, professor, perfil, btn_me_ajuda;
    private TextView txtChat;
    private GroupAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_lista_contatos);
        StatusBarUtil.setTransparent(this);
        txtChat = findViewById(R.id.txt_chat);


        RecyclerView recycler = findViewById(R.id.recycle);

        adapter = new GroupAdapter();
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        fetchUser();

        //Ir para tela Me Ajuda
        btn_me_ajuda = (View) findViewById(R.id.view_me_ajuda_batepapo);
        btn_me_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeAjudaActivity.class);
                startActivity(intent);
            }
        });


        /* BARRA DE TAREFA */
        calendario = findViewById(R.id.view_calendario);
        lupa = findViewById(R.id.view_lupa);
        home = findViewById(R.id.view_home);
        professor = findViewById(R.id.view_conversa_professor);
        perfil = findViewById(R.id.view_perfil);


        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                startActivity(intent);


            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);


            }
        });

        professor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ContatosActivity.class);
                startActivity(intent);


            }
        });


        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                startActivity(intent);


            }
        });

        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PesquisaActivity.class);
                startActivity(intent);


            }
        });
        /*  FIM BARRA DE TAREFA */
    }

    private void fetchUser() {

        FirebaseFirestore.getInstance().collection("Usuario")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot queryDocumentsSnapshots, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException e) {
                           if(e != null){
                               Log.e("teste", e.getMessage(), e);
                               return;
                           }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("Usuario").document(usuarioID);
                        final Usuario[] usuarioAtual = new Usuario[1];

                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot refAtual, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                                    usuarioAtual[0] = refAtual.toObject(Usuario.class);

                                    List<DocumentSnapshot> docs = queryDocumentsSnapshots.getDocuments();

                                for(DocumentSnapshot doc:docs){
                                    Usuario aux = doc.toObject(Usuario.class);
                                    if(((aux.getTurma()).equals(usuarioAtual[0].getTurma()))) {
                                        if(usuarioAtual[0].getOcupacao().equalsIgnoreCase("professor") || usuarioAtual[0].getOcupacao().equalsIgnoreCase("professora")) {
                                            txtChat.setText("Chat com alunos");
                                            if(aux.getOcupacao().equalsIgnoreCase("estudante")) {
                                                Usuario usuario = doc.toObject(Usuario.class);
                                                adapter.add(new UserItem(usuario));
                                            }
                                        }else{
                                            txtChat.setText("Chat com o professor");
                                            if(aux.getOcupacao().equalsIgnoreCase("professor") || aux.getOcupacao().equalsIgnoreCase("professora")){
                                            Usuario usuario = doc.toObject(Usuario.class);
                                            adapter.add(new UserItem(usuario));
                                            }
                                        }

                                    }
                                }
                            }
                        });

                    }
                });
    }
    private class UserItem extends Item<ViewHolder> {

        private final Usuario usuario;


        private UserItem(Usuario usuario){
            this.usuario = usuario;
        }

        @Override
        public void bind(@NonNull @NotNull ViewHolder viewHolder, int position) {
            TextView txtNome = viewHolder.itemView.findViewById(R.id.txtNomeContato);
            ImageView imgFoto  = viewHolder.itemView.findViewById(R.id.ivFotoContato);

            txtNome.setText(usuario.getNome());
            Picasso.get().load(usuario.getFotoPerfil()).into(imgFoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}
