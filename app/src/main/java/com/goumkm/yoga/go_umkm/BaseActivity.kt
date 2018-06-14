package com.goumkm.yoga.go_umkm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Context.INPUT_METHOD_SERVICE
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser






open class BaseActivity : AppCompatActivity() {

    var mProgressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage("Loading")
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }
    fun showProgressDialog(teks : String) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(teks)
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            imm!!.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    override fun onStart() {
        super.onStart()


    }


    fun isLogin(): Boolean{
        mAuth = FirebaseAuth.getInstance();
        val currentUser = mAuth!!.getCurrentUser()
        if(currentUser != null){
           return true;
        }
        return false;
    }
    fun setSnackbar(teks : String, view : View){
        Snackbar.make(view,teks,Snackbar.LENGTH_SHORT).show()
    }
    fun setToast(teks : String){
        Toast.makeText(this,teks,Toast.LENGTH_SHORT).show()
    }



    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}
