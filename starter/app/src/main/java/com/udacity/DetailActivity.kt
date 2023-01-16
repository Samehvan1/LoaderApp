package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val args=intent.extras
        if(args!=null) {
            val fName = args.getString(getString(R.string.argsfnamekey))!!
            val fsize = args.getString(getString(R.string.argsfsizekey))!!
            val status = args.getString(getString(R.string.argsstatuskey))!!
            var fnameView:TextView= findViewById(R.id.filenametxt)
            var fsizeView:TextView= findViewById(R.id.filesizetxt)
            var statView:TextView= findViewById(R.id.filestatustxt)
            fnameView.setText(fName)
            fsizeView.setText(fsize)
            statView.setText(status)
        }
    }

    fun ok_clicked(view: View) {
        var intnt=Intent(this,MainActivity::class.java)
        startActivity(intnt)
        finish()
    }

}
