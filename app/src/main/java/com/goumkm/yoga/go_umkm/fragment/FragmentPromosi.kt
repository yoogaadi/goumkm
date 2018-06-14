package com.goumkm.yoga.go_umkm.fragment


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.LihatPromosi

import com.goumkm.yoga.go_umkm.R
import com.goumkm.yoga.go_umkm.adapter.*
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FragmentUmkm : Fragment() {

    var lv : NoScrollListView? = null;
    var adapter : list_promosi? = null
    var textKosong : TextView? = null;
    val nc = NumberControl()
    val sc = string_control()
    var loading : ProgressBar? = null;
    var layout : LinearLayout? = null

    var timestampSekarang : Int? = null


    var namaProduk: Array<String>? = null
    var idProduk:Array<String>? = null
    var gambarProduk:Array<String>? = null
    var deskripsiProduk:Array<String>? = null


    val strNama = ArrayList<String>()
    val strIdProduk = ArrayList<String>()
    val descProduk = ArrayList<String>()
    val gbrProduk = ArrayList<String>()


    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    fun FragmentUmkm() {

    }


    fun newInstance(): FragmentUmkm{
        val args: Bundle = Bundle()
        val fragment = com.goumkm.yoga.go_umkm.fragment.FragmentUmkm()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_umkm, container, false)
    try {
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        timestampSekarang = sc.getTimestamp("now").toInt();

        lv = view.findViewById(R.id.list_item)
        loading = view.findViewById(R.id.loading)
        textKosong = view.findViewById(R.id.text_kosong)
        layout = view.findViewById(R.id.layout_data)

        try {
            readData()
        } catch (e: Exception) {

        }
    }catch (e : Exception){

    }

        return view;
    }
    fun readData(){
        myRef = database.getReference("promosi")
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        if(timestampSekarang!! >= d.child("tanggal_mulai").value.toString().toInt() &&
                                timestampSekarang!! <= d.child("tanggal_selesai").value.toString().toInt()){
                            strNama.add(d.child("nama_promosi").value.toString())
                            var tanggal = sc.getWaktuByTimestamp(d.child("tanggal_mulai").value.toString().toInt())+" Sampai " +
                                    sc.getWaktuByTimestamp(d.child("tanggal_selesai").value.toString().toInt())
                            descProduk.add(tanggal)
                            strIdProduk.add(d.key)
                            gbrProduk.add(d.child("gambar_promosi").value.toString())
                        }

                    }
                    namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                    idProduk = strIdProduk.toArray(arrayOfNulls<String>(strIdProduk.size))
                    gambarProduk = gbrProduk.toArray(arrayOfNulls<String>(gbrProduk.size))
                    deskripsiProduk = descProduk.toArray(arrayOfNulls<String>(descProduk.size))
                    adapter = list_promosi(context, namaProduk,deskripsiProduk,gambarProduk);
                    lv!!.adapter = adapter
                    lv!!.visibility = ListView.VISIBLE
                    layout!!.visibility = LinearLayout.VISIBLE
                    loading!!.visibility = ProgressBar.GONE
                    lv!!.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(context,LihatPromosi::class.java)
                        intent.putExtra("id_promosi",idProduk!![position])
                        startActivity(intent)
                    }

                }else{
                    textKosong!!.visibility = LinearLayout.VISIBLE
                    loading!!.visibility = ProgressBar.GONE

                }



            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                textKosong!!.visibility = LinearLayout.VISIBLE
                textKosong!!.text= "Gagal Memuat Data"
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

}
