package com.goumkm.yoga.go_umkm

import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.io.PrintWriter
import java.util.ArrayList
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.goumkm.yoga.go_umkm.fragment.FragmentAccount
import android.support.design.widget.BottomNavigationView
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import com.goumkm.yoga.go_umkm.fragment.FragmentHome
import com.goumkm.yoga.go_umkm.fragment.FragmentUmkm


class MainActivity : BaseActivity() {
    private var dataset = "https://media.githubusercontent.com/media/ferortega/cf4j/master/datasets/MovieLens100K.txt"

    private val testItems = 0.2 // 20% test items
    private val testUsers = 0.2 // 20% test users
    private final val TAG = "coba"
    internal var myRef: DatabaseReference? = null;
    private val numberOfNeighbors = intArrayOf(50, 100, 150, 200, 250, 300, 350, 400, 450, 500)
    var kata = arrayOf<String>()
    val data = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // To store experiment results
        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("ratings")

        if (!isLogin()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        var frag: Fragment? = null
        frag = FragmentHome().newInstance();
        val fm = this.getSupportFragmentManager()
        addFragmentToActivity(fm,frag)

        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
            }
            val fm = this.getSupportFragmentManager()
            if(item.itemId == R.id.btn_home){
                var frag = FragmentHome().newInstance();
                addFragmentToActivity(fm,frag)
            }else if(item.itemId == R.id.btn_account){
                var frag = FragmentAccount().newInstance();
                addFragmentToActivity(fm,frag)
            }else if(item.itemId == R.id.btn_promosi){
                var frag = FragmentUmkm().newInstance();
                addFragmentToActivity(fm,frag)
            }


            true
        }



    }

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment) {

        val transaction = manager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()

    }

    override fun onBackPressed() {
        ShowDialog()
    }

    fun ShowDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Keluar")
        builder.setMessage("Apakah anda ingin keluar aplikasi?")
        builder.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, id ->
           finish()
            moveTaskToBack(true )
        })
                .setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

        builder.create().show()
    }

    fun readData(){
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.value != null) {

                    val datas = dataSnapshot.children
                    for (d in datas) {
                        val user = d.child("user").getValue().toString()
                        val item = d.child("item").getValue().toString()
                        val rating = d.child("rating").getValue().toString()

                        val dat = user + "::" + item + "::" + rating + "::0";
                        println("datanya = " + dat);
                        data.add(dat);
                    }
                    kata = data.toTypedArray()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
}
