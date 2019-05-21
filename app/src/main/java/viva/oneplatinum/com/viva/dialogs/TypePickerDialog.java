package viva.oneplatinum.com.viva.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.interfaces.TypeSelectorListner;
import viva.oneplatinum.com.viva.models.CategoryInfo;
import viva.oneplatinum.com.viva.models.SubCategory;
import viva.oneplatinum.com.viva.parser.ParseUtility;

/**
 * Created by oneplatinum on 4/5/16.
 */
public class TypePickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {

    private OnDismissListner listner;
    VivaApplication mVivaApplication;
    Dialog customDailog;
    Context mContext;
    NumberPicker categoryPicker, subCategoryPicker;
    ArrayList<CategoryInfo> categoryInfoArrayList;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    String categoryData,typeData;
    String[] categorylist = {};
    String[] subCategorylist = {};
    TypeSelectorListner mTypeSelectorListner;
    Button doneButton;
    int categoryPosition,subCategoryPosition;
    int no;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryInfoArrayList = new ArrayList<CategoryInfo>();
        Bundle bundle = getArguments();

        categoryData = bundle.getString("categoryData");
        typeData= bundle.getString("type");

            try {
                JSONObject mJsonObject = new JSONObject(categoryData);
                categoryInfoArrayList = ParseUtility.parseCategoryDetail(mJsonObject);
                for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                    stringArrayList.add(categoryInfoArrayList.get(i).category_name);

                }

                categorylist = stringArrayList.toArray(new String[stringArrayList.size()]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mVivaApplication = (VivaApplication) mContext.getApplicationContext();
        mTypeSelectorListner = (TypeSelectorListner) mContext;
    }

    public void setDismissListner(OnDismissListner listner) {
        this.listner = listner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customDailog = new Dialog(mContext);
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.type_layout
                , null);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        categoryPicker = (NumberPicker) view.findViewById(R.id.categoryPicker);
        subCategoryPicker = (NumberPicker) view.findViewById(R.id.subCategoryPicker);
        categoryPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        subCategoryPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        categoryPicker.setMinValue(0);

        categoryPicker.setMaxValue(categorylist.length - 1);
        categoryPicker.setDisplayedValues(categorylist);
        categoryPicker.setWrapSelectorWheel(false);
        if(typeData!=null){
            for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                ArrayList<SubCategory> subCategoriesList = categoryInfoArrayList.get(i).subCategoriesList;
                for (int j = 0; j < subCategoriesList.size(); j++) {
                    if(subCategoriesList.get(j).sub_category_name.equals(typeData)){
                        categoryPosition=i;
                        subCategoryPosition=j;
                        Log.v("pickerData1", subCategoryPosition+","+categoryPosition );
                    }
                }
            }
        }
        categoryPicker.setValue(categoryPosition);
        categoryData = categorylist[categoryPicker.getValue()];
        Log.v("pickerData", "" + categoryPicker.getValue());
        for (int i = 0; i < categoryInfoArrayList.size(); i++) {
            if (categoryInfoArrayList.get(i).category_name.equals(categoryData)) {
                ArrayList<SubCategory> subCategoriesList = categoryInfoArrayList.get(i).subCategoriesList;
                ArrayList<String> subCategoryStringList = new ArrayList<String>();
                for (int j = 0; j < subCategoriesList.size(); j++) {

                    subCategoryStringList.add(subCategoriesList.get(j).sub_category_name);
                }
                subCategorylist = subCategoryStringList.toArray(new String[subCategoryStringList.size()]);
            }
        }

        subCategoryPicker.setMinValue(0);
        subCategoryPicker.setMaxValue(subCategorylist.length - 1);
        subCategoryPicker.setDisplayedValues(subCategorylist);
        subCategoryPicker.setWrapSelectorWheel(true);
        subCategoryPicker.setValue(subCategoryPosition);
        categoryPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v("pickerData", newVal + " " + oldVal);
                String categoryData = categorylist[newVal];
                for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                    if (categoryInfoArrayList.get(i).category_name.equals(categoryData)) {
                        ArrayList<SubCategory> subCategoriesList = categoryInfoArrayList.get(i).subCategoriesList;
                        ArrayList<String> subCategoryStringList = new ArrayList<String>();
                        for (int j = 0; j < subCategoriesList.size(); j++) {

                            subCategoryStringList.add(subCategoriesList.get(j).sub_category_name);
                        }
                        subCategorylist = subCategoryStringList.toArray(new String[subCategoryStringList.size()]);
                    }
                }
                subCategoryPicker.setDisplayedValues(null);
                subCategoryPicker.setMinValue(0);
                subCategoryPicker.setMaxValue(subCategorylist.length - 1);
                subCategoryPicker.setDisplayedValues(subCategorylist);
                subCategoryPicker.setWrapSelectorWheel(true);
//                subCategoryPicker.setValue(subCategoryPosition);
            }
        });
        subCategoryPicker.setOnValueChangedListener(this);
        subCategoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCategoryPicker.clearFocus();
                no = subCategoryPicker.getValue();
                String type = subCategorylist[no];
                mTypeSelectorListner.selectedType(type);
                dismiss();
            }
        });
        setNumberPickerTextColor(categoryPicker, Color.RED);
        setNumberPickerTextColor(subCategoryPicker, Color.RED);
        customDailog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        customDailog.setContentView(view);
        customDailog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        customDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(266));
        customDailog.show();
        Window window = customDailog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        customDailog.show();


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = subCategorylist[no];
                mTypeSelectorListner.selectedType(type);
                dismiss();
            }
        });
        return customDailog;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        no = newVal;
    }

    public interface OnDismissListner {
        public void onDismissDialog();
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("setNumberPickerTextColo", e);
                } catch (IllegalAccessException e) {
                    Log.w("setNumberPickerTextColo", e);
                } catch (IllegalArgumentException e) {
                    Log.w("setNumberPickerTextColo", e);
                }
            }
        }
        return false;
    }

    Dialog loadingDialog;

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(getActivity(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

}
