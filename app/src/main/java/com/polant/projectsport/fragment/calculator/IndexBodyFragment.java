package com.polant.projectsport.fragment.calculator;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.polant.projectsport.R;
import com.polant.projectsport.activity.ActivityOtherCalculators;
import com.polant.projectsport.data.Database;
import com.polant.projectsport.data.model.UserParametersInfo;

/**
 * Created by ����� on 17.10.2015.
 */
public class IndexBodyFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_index_body;

    private View view;
    private Database DB;

    //��������� ��� ����������� ���� ������� ����.
    private static final int MIN_BODY_INDEX = 18;       //����������� �����
    private static final int IDEAL_BODY_INDEX = 22;     //�������� ����������� ����� � ����
    private static final int MAX_BODY_INDEX = 25;       //������������ �����
    private static final int EXCESS_BODY_INDEX = 30;    //���������� ���
    private static final int OBESITY_BODY_1_INDEX = 35; //1 ������� ��������
    private static final int OBESITY_BODY_2_INDEX = 40; //2 ������� ��������
    //private static final int OBESITY_BODY_3_INDEX = 100;//3 ������� ��������


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //������� ������ ��, ������� ��������������� � ��������.
        DB = ((ActivityOtherCalculators) getActivity()).getDatabase();
        initButtonCalculateIndex();
    }

    //������������� ������ �������� ������� ����� ����.
    private void initButtonCalculateIndex(){
        Button btIndex = (Button) view.findViewById(R.id.buttonCalculateIndexBody);
        btIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertDialogChangeUserInfo();
            }
        });
    }

    //���������� AlertDialog ��� ������ ���������� �����, ���� ��� �������� �������.
    private void buildAlertDialogChangeUserInfo(){

        //���������� �������, � ������� ������������ ������ ���������� ��������� ���.
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //������� �� id �������, � ������ View, ����� ����� �������� ������ � ����.
        final View alertView = getActivity().getLayoutInflater().inflate(R.layout.alert_index_body, null);

        RadioButton radioFromDB = (RadioButton) alertView.findViewById(R.id.radioFromDB);
        //����� � ��������� ����� ��������� ����� ������.
        setOnRadioBtCheckedChange(radioFromDB, alertView);

        //���������� ������ �������.
        builder.setTitle(R.string.alertIndexBodyTitle)
                .setMessage(R.string.alertIndexBodyMessage)
                .setCancelable(true)
                .setIcon(R.drawable.alert_change_user_parameters_info_icon)
                .setView(alertView)
                .setPositiveButton(getString(R.string.alertIndexBodyPositiveBt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //�������� ��������� � ��������� �����.
                        setOnPositiveBtAlertClick(alertView);
                    }
                })
                .setNegativeButton(getString(R.string.alertIndexBodyNegativeBt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //���������� �������������� ����� ������ � ������� ��������������� �������.
    private void setOnRadioBtCheckedChange(RadioButton radioButton, final View alertView){
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText wEdit = (EditText) alertView.findViewById(R.id.editTextAlertIndexBodyWeight);
                EditText hEdit = (EditText) alertView.findViewById(R.id.editTextAlertIndexBodyHeight);
                if (isChecked) {
                    wEdit.setEnabled(false);
                    hEdit.setEnabled(false);
                } else {
                    wEdit.setEnabled(true);
                    hEdit.setEnabled(true);
                }
            }
        });
    }

    //���������� positive button � �������.
    private void setOnPositiveBtAlertClick(View alertView){
        EditText wText = (EditText) alertView.findViewById(R.id.editTextAlertIndexBodyWeight);
        EditText hText = (EditText) alertView.findViewById(R.id.editTextAlertIndexBodyHeight);
        RadioGroup group = (RadioGroup) alertView.findViewById(R.id.radioIndexBodyGroup);

        int idSelectedRadio = group.getCheckedRadioButtonId();

        float weight = 0;
        float height = 0;

        if (idSelectedRadio == R.id.radioFromDB){
            UserParametersInfo user = DB.getUserParametersInfo();
            weight = user.getWeight();
            height = user.getHeight();
        }
        else{
            if (!TextUtils.isEmpty(wText.getText()) && !TextUtils.isEmpty(hText.getText())){
                weight = Float.valueOf(wText.getText().toString());
                height = Float.valueOf(hText.getText().toString());
            }
            else {
                Toast.makeText(getActivity(), R.string.text_err_input_index_body, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
        double indexBody = UserParametersInfo.calculateIndexBody(weight, height);
        TextView index = (TextView) view.findViewById(R.id.textViewIndexResult);
        index.setText(String.valueOf(indexBody));

        setIndexDescription(indexBody);
    }

    //����� �� ����� ���������� �������� ����������� ������� �����.
    private void setIndexDescription(double indexBody) {
        TextView descriptionText = (TextView) view.findViewById(R.id.textViewIndexDescription);
        if (indexBody < MIN_BODY_INDEX){
            descriptionText.setText(getString(R.string.text_index_body_less_min));
        }
        else if (indexBody < IDEAL_BODY_INDEX - 1){
            descriptionText.setText(getString(R.string.text_index_body_min));
        }
        else if (indexBody > IDEAL_BODY_INDEX - 1 && indexBody < IDEAL_BODY_INDEX + 1){
            descriptionText.setText(getString(R.string.text_index_body_ideal));
        }
        else if (indexBody < MAX_BODY_INDEX){
            descriptionText.setText(getString(R.string.text_index_body_max));
        }
        else if (indexBody < EXCESS_BODY_INDEX){
            descriptionText.setText(getString(R.string.text_index_body_excess));
        }
        else if (indexBody < OBESITY_BODY_1_INDEX){
            descriptionText.setText(getString(R.string.text_index_body_obesity_1));
        }
        else if (indexBody < OBESITY_BODY_2_INDEX){
            descriptionText.setText(getString(R.string.text_index_body_obesity_2));
        }
        else{
            descriptionText.setText(getString(R.string.text_index_body_obesity_3));
        }
    }

}
