package com.example.repasoandroid;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccesoFirebase{
    public static void grabarRuta(Ruta r){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rutas");
        myRef.push().setValue(r);
    }

    public static void pedirRutas(IRecuperarDatos callback){
        ArrayList<Ruta>rutas = new ArrayList<Ruta>();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://repasoandroid-ab3e7-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("rutas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> datos = dataSnapshot.getChildren();
                while (datos.iterator().hasNext()){
                    DataSnapshot d = datos.iterator().next();
                    Ruta r =d.getValue(Ruta.class);
                    rutas.add(r);
                }
                callback.recuperarRutas(rutas);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public interface IRecuperarDatos {
        void recuperarRutas(ArrayList<Ruta> rutas);
    }
}
