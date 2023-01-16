package com.udacity.Util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

fun NotificationManager.sendNotify(context: Context,fName:String,flag :String,totSiz:Long){
    val chId=context.getString(R.string.channel_Id)
    //create the content intent
    val detailIntent= Intent(context,DetailActivity::class.java).apply {
        putExtra(context.getString(R.string.argsfnamekey), fName)
        putExtra(context.getString(R.string.argsfsizekey),totSiz.toString())
        putExtra(context.getString(R.string.argsstatuskey),flag)
    }

    //create pending intent
    val pendingIntent=PendingIntent.getActivity(context,1,detailIntent,PendingIntent.FLAG_UPDATE_CURRENT)

    val builder=NotificationCompat.Builder(context,chId)
        .setContentTitle(context.getString(R.string.notification_title))
        .setSmallIcon(R.drawable.down)
        .setContentText(fName+" has been "+flag)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    notify(1,builder.build())

}