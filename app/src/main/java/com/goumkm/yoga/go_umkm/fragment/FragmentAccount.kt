package com.goumkm.yoga.go_umkm.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import com.goumkm.yoga.go_umkm.R
import kotlinx.android.synthetic.main.fragment_account.*
import com.google.firebase.auth.FirebaseUser
import com.goumkm.yoga.go_umkm.LoginActivity
import com.goumkm.yoga.go_umkm.adapter.list_with_image
import android.content.DialogInterface
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.EditProfileActivity
import com.goumkm.yoga.go_umkm.UmkmActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FragmentAccount : BaseFragment() {
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    internal lateinit var adapter: list_with_image
    internal lateinit var lv: ListView
    internal lateinit var nama: TextView
    internal lateinit var email: TextView
    internal var gambar = IntArray(3)
    internal var namaMenu = arrayOf("Edit Profil", "UMKM Saya", "Logout")

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase

    fun FragmentAccount() {

    }


    fun newInstance(): FragmentAccount{
        val args: Bundle = Bundle()
        val fragment = com.goumkm.yoga.go_umkm.fragment.FragmentAccount()
        fragment.arguments = args
        return fragment

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        // [END config_signin]

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth!!.getCurrentUser()

//        val btn =  view.findViewById<Button>(R.id.btn_logout)
        nama =  view.findViewById<TextView>(R.id.nama_akun)
        email =  view.findViewById<TextView>(R.id.email)
        val lv = view.findViewById<ListView>(R.id.list_view)

        gambar[0] = R.drawable.ic_account_circle_black_24dp
        gambar[1] = R.drawable.ic_store_black_24dp
        gambar[2] = R.drawable.baseline_exit_to_app_black_24dp


        adapter = list_with_image(context, namaMenu, gambar)
        lv.adapter = adapter
        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent: Intent
            if (i == 0) {
                val intent = Intent(context,EditProfileActivity::class.java)
                startActivity(intent)
            } else if (i == 1) {
                val intent = Intent(context,UmkmActivity::class.java)
                startActivity(intent)
            } else if (i == 2) {
               ShowDialog()
            }
        }

        nama.setText("-")
        email.setText("-")
        readData(mAuth!!.currentUser!!.uid)

        return view

    }

    fun readData(id_user : String){

        myRef = database.getReference("users").child(id_user);
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.value != null) {

                    nama.setText(dataSnapshot.child("nama").getValue(String::class.java))
                    email.setText(dataSnapshot.child("email").getValue(String::class.java))

                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

                Log.w("Error ", "Failed to read value.", error.toException())
            }
        })
    }

    private fun signOut() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google sign out
        mGoogleSignInClient!!.signOut();
        val inten = Intent(context,LoginActivity::class.java);
        startActivity(inten)
    }

    fun ShowDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle("Keluar")
        builder.setMessage("Apakah anda yakin untuk logout?")
        builder.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, id ->
            signOut()
        })
                .setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

        builder.create().show()
    }


}
