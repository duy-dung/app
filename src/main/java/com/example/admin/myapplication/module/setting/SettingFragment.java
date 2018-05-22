package com.example.admin.myapplication.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.MyApplication;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.SettingParam;
import com.example.admin.myapplication.utils.Constrant;
import com.example.admin.myapplication.utils.LocaleHelper;
import com.example.admin.myapplication.utils.MyPerferences;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private MyPerferences myPerferences;




    private String mCurLanguage;
    private View btnSave;

    private Spinner spnNN,spnTDQ,spnTGC,spnCL,spnNAT;


    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private View mContentView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.frm_setting, container, false);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myPerferences = new MyPerferences(getContext());
        String language = myPerferences.getLanguage();
        mCurLanguage = language;
        btnSave = getActivity().findViewById(R.id.btn_save);

        spnCL =getActivity().findViewById(R.id.spn_clvd);
        spnNN =getActivity().findViewById(R.id.spn_nn);
        spnTDQ =getActivity().findViewById(R.id.spn_tdq);
        spnTGC =getActivity().findViewById(R.id.spn_tgc);
        spnNAT =getActivity().findViewById(R.id.spn_nat);
        setAdapter();

        btnSave.setOnClickListener(this);

    }

    private void setAdapter() {
        SettingParam param = myPerferences.getSetting();
        List<String> temp= Arrays.asList(getResources().getStringArray(R.array.nn));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item,temp); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnNN.setAdapter(spinnerArrayAdapter);
        spnNN.setSelection(param.getNn());
        //
        temp = Arrays.asList(getResources().getStringArray(R.array.cl));
        spinnerArrayAdapter =new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item,temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnCL.setAdapter(spinnerArrayAdapter);
        spnCL.setSelection(param.getCl());

        //
        temp = Arrays.asList(getResources().getStringArray(R.array.tdq));
        spinnerArrayAdapter =new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item,temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnTDQ.setAdapter(spinnerArrayAdapter);
        spnTDQ.setSelection(param.getTdq());
        //

        temp = Arrays.asList(getResources().getStringArray(R.array.tgc));
        spinnerArrayAdapter =new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item,temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnTGC.setAdapter(spinnerArrayAdapter);
        spnTGC.setSelection(param.getTgc());
        //
        temp = Arrays.asList(getResources().getStringArray(R.array.nat));
        spinnerArrayAdapter =new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item,temp);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnNAT.setAdapter(spinnerArrayAdapter);
        spnNAT.setSelection(param.getNat());

    }


    private void changeLanguage(String newLanguage) {
//

        MyApplication.updateAppLanguage(getActivity(), newLanguage);
        Intent i = getActivity().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    @Override
    public void onClick(View v) {
        SettingParam paramSaved = myPerferences.getSetting();
        SettingParam param =new SettingParam();
        param.setCl(spnCL.getSelectedItemPosition());
        param.setNn(spnNN.getSelectedItemPosition());
        param.setNat(spnNAT.getSelectedItemPosition());
        param.setTgc(spnTGC.getSelectedItemPosition());
        param.setTdq(spnTDQ.getSelectedItemPosition());
        myPerferences.saveSetting(param);
        if (spnNN.getSelectedItemPosition()==0) {
            mCurLanguage =Constrant.LANGUAGE_VI;
        }else mCurLanguage =Constrant.LANGUAGE_EN;
        if (paramSaved.getNn() == spnNN.getSelectedItemPosition()) {
            Toast.makeText(getContext(), getString(R.string.daluu), Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Title")
                .setMessage("Do you really want to change language?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.dongy, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        changeLanguage(mCurLanguage);
                        myPerferences.setLanguage(mCurLanguage);
                        myPerferences.setChangeLanguage(true);
                    }
                })
                .setNegativeButton(R.string.cancel, null).show();

    }
}
