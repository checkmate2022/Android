package com.example.avatwin.Fragment.Schedule


import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ScheduleDialogFragment(id:Int): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?)
    : View? {
        val view=inflater.inflate(com.example.avatwin.R.layout.dialog_schedule_detail,container,false)
        val bundle=arguments

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        }

    fun getInstance():ScheduleDialogFragment{
        return ScheduleDialogFragment(1)
    }
}