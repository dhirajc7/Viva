package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by D-MAX on 3/30/2016.
 */
public class CategoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public String category_id;
    public String category_name;
    public String sub_category_id;
    public ArrayList<SubCategory> subCategoriesList=new ArrayList<SubCategory>();

}