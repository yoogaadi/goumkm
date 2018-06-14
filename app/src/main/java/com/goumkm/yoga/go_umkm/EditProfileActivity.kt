package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class EditProfileActivity : BaseActivity() {
    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    var namaUser: String? = null
    var emailUser : String? = null
    var namaProfil : EditText? = null
    var emailProfil : EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Edit Profil"
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val id_user = mAuth!!.currentUser!!.uid

        namaProfil = findViewById<EditText>(R.id.text_nama)
        emailProfil = findViewById<EditText>(R.id.text_email)
        val btnSimpan= findViewById<Button>(R.id.btn_simpan)

        readData(id_user);

        btnSimpan.setOnClickListener {
            showProgressDialog("Menyimpan data")
            myRef = database.getReference("users").child(id_user)
            myRef!!.child("nama").setValue(namaProfil!!.text.toString())
            hideProgressDialog()
            setToast("Data berhasil disimpan")
            hideProgressDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun readData(id_user : String){
        showProgressDialog("Mengunduh data")
        myRef = database.getReference("users").child(id_user);
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.value != null) {

                    namaUser = dataSnapshot.child("nama").getValue(String::class.java)
                    emailUser = dataSnapshot.child("email").getValue(String::class.java)
                    namaProfil!!.setText(namaUser)
                    emailProfil!!.setText(emailUser)

                    hideProgressDialog()

                }else{
                    hideProgressDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()
                Log.w("Error ", "Failed to read value.", error.toException())
            }
        })
    }
}
