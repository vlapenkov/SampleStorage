package com.yst.sklad.tsd.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.services.SoapCallToWebService;
import com.yst.sklad.tsd.services.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapenkov on 25.09.2017.
 */
public class ProductOnRestsDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG="ProductOnRestsDialog";

    ListView lvData =null;
    String mProductId ;
    ArrayAdapter<StockAndRest> mAdapter;
    private List<StockAndRest> items= new   ArrayList<>();
    ProgressDialog loading ;

    private static class StockAndRest {
        public final String stock;
        public final String rest;

        public StockAndRest(String stock, String rest) {
            this.stock = stock;
            this.rest = rest;
        }
    }
        /*
        Адаптер для остатков по товару
         */
    private class StockAndRestAdapter extends ArrayAdapter<StockAndRest> {

        public StockAndRestAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StockAndRest cat = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(cat.stock);
            ((TextView) convertView.findViewById(android.R.id.text2))
                    .setText(cat.rest);
            return convertView;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);


        //items.add("Пусто");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.picture_of_product);
        View v = inflater.inflate(R.layout.dialog_productonrests, null);
        mProductId = getArguments().getString("productId");

        lvData = (ListView) v.findViewById(R.id.lvData);

       //  mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, items);

        mAdapter = new StockAndRestAdapter(getActivity());

        lvData.setAdapter(mAdapter);
        loading= new ProgressDialog(v.getContext());
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage(getString(R.string.stockcells_are_being_downloaded));

        return v;
    }


    public void onClick(View v) {

        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onResume() {
        super.onResume();

       new RestsGetter().execute(mProductId);
    }
        /*
        Асинхронная задача получения остатктов с web-сервиса
         */
   private class RestsGetter extends AsyncTask<String, String, Document> {

        protected Document doInBackground(String... args) {
            InputStream stream = SoapCallToWebService.getRestOfOneProduct(mProductId);
            XMLDOMParser parser = new XMLDOMParser();
            Document doc = parser.getDocument(stream);
            return doc;
        }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading.show();
            }

            protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            XMLDOMParser parser = new XMLDOMParser();

            NodeList nodeListProducts = result.getElementsByTagName("Products");
if (nodeListProducts.getLength()>0)
            for (int j = 0; j < nodeListProducts.getLength(); j++) {
                Element e = (Element) nodeListProducts.item(j);

                String productid = parser.getValue(e, "productid");
                String stockcell = parser.getValue(e, "stockcell");
                String rest = parser.getValue(e, "rest");
                String stockname = parser.getValue(e, "stockname");

                String strToAdd=stockname;
                if (stockname!=null && stockname!="")
                strToAdd=stockcell+" / "+stockname;

                items.add(new StockAndRest(strToAdd,rest));

            }
else
    items.add(new StockAndRest("Остатки по данному товару отсутствуют",""));

            mAdapter.notifyDataSetChanged();
               loading.dismiss();

    }

}
}
