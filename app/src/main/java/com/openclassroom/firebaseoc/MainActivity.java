package com.openclassroom.firebaseoc;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassroom.firebaseoc.databinding.ActivityMainBinding;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private String idToChange = "" ;
    private List<Product> products = new ArrayList<Product>();
    private Button buttonAdd ;
    private EditText textName ;
    private EditText textDesc ;
    private EditText textPrice ;
    private ListView listView ;
    BouchraAdapter addapter ;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productsCollection = db.collection("products");


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.add);

        textName = findViewById(R.id.textname);
        textDesc = findViewById(R.id.textdesc);
        textPrice = findViewById(R.id.textprice);
        listView =(ListView) findViewById(R.id.productlist);

//        ArrayAdapter addapter = new ArrayAdapter(this,R.layout.product_row,R.id.row,products);
        addapter = new BouchraAdapter(products);
        listView.setAdapter(addapter);
        buttonAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
            createProduct();
            }
        });
        loadProducts();
    }



    public void clearForm(){
        textName.setText("");
        textDesc.setText("");
        textPrice.setText("");
    }
    public void refreach(){
        loadProducts();
    }

    public void removeProduct(int id){
        this.products.remove(this.products.get(id)) ;
        refreach();
    }



    private void deleteProduct(String productId) {
        // Delete the product from Firestore
        DocumentReference productRef = productsCollection.document(productId);
        productRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Product deleted successfully
                    loadProducts(); // Refresh the product list after deletion
                } else {
                    // Handle deletion error
                }
            }
        });
    }



    public void toUpdateMode(Product product){
        textName.setText(product.getName());
        textDesc.setText(product.getDescription());
        textPrice.setText(product.getPrice());
        this.idToChange = product.getId();
        this.buttonAdd.setText("update");
        Toast.makeText(this,"id to change "+idToChange,Toast.LENGTH_LONG);
        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }




    public void update(){

        DocumentReference washingtonRef = db.collection("products").document(idToChange);

        Map<String,Object> updates = new HashMap<>();
        updates.put("name", textName.getText().toString());
        updates.put("description", textDesc.getText().toString());
        updates.put("price", textPrice.getText().toString());
        washingtonRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//          log something
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//          log something
                    }
                });

        refreach();

        clearForm();
        this.buttonAdd.setText("Add");
        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProduct();
            }
        });

    }





    private void loadProducts() {
        Toast.makeText(MainActivity.this,"start the method lead tada",Toast.LENGTH_LONG).show();

        productsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Toast.makeText(MainActivity.this,"Starting on cpmlete",Toast.LENGTH_LONG).show();

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Task succes",Toast.LENGTH_LONG).show();

                    products.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        Toast.makeText(MainActivity.this,"Result... loading",Toast.LENGTH_LONG).show();

                        // Convert each document to a Product object and add it to the list
                        String productId = document.getId();
                        Product product = document.toObject(Product.class);
                        product.setId(productId);
                        products.add(product);
                    }
                    // Update the RecyclerView
                    addapter.notifyDataSetChanged();
                } else {
                    // Handle errors
                }
            }
        });
        for(Product product : products){

            Toast.makeText(MainActivity.this,""+product,Toast.LENGTH_LONG).show();
        }
    }
    public void createProduct(){

        Map<String, Object> data = new HashMap<>();

        data.put("name", textName.getText().toString());
        data.put("description", textDesc.getText().toString());
        data.put("price", textPrice.getText().toString());

        db.collection("products")
                .add(data);
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//
//                   }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
        Toast.makeText(this,"data Added succesfully",Toast.LENGTH_LONG);
        clearForm();
        loadProducts();
    }
    class BouchraAdapter extends BaseAdapter {
        List<Product> listproduct ;
        public BouchraAdapter(List<Product> listproduct){
            this.listproduct = listproduct ;
        }
        @Override
        public int getCount() {
            return listproduct.size();
        }

        @Override
        public Object getItem(int position) {
            return this.listproduct.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.product_row,null);

            TextView id_text =(TextView) view.findViewById(R.id.id_product_row);

            Button deleteButton =(Button) view.findViewById(R.id.delete_button);
            Button updateButton =(Button) view.findViewById(R.id.update_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteProduct(listproduct.get(position).getId());
                }
            });
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toUpdateMode(listproduct.get(position));
                }
            });
            id_text.setText(""+listproduct.get(position).getId().substring(0,4));
            TextView name_text =(TextView) view.findViewById(R.id.name_product_row);
            name_text.setText(""+listproduct.get(position).getName());
            TextView desc_text =(TextView) view.findViewById(R.id.desc_product_row);
            desc_text.setText(""+listproduct.get(position).getDescription());
            TextView price_text =(TextView) view.findViewById(R.id.price_product_row);
            price_text.setText(""+listproduct.get(position).getPrice());
            return  view ;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                startActivity(new Intent(this,HomeActivity.class));

            } else {


                }
            }
        }
    }
