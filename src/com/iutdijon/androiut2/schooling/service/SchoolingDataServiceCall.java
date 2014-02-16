package com.iutdijon.androiut2.schooling.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ListActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.schooling.adapters.SectionedAdapter;
import com.iutdijon.androiut2.schooling.data.Mark;
import com.iutdijon.androiut2.schooling.data.SchoolingData;
import com.iutdijon.androiut2.util.adapters.AbsencesAdapter;
import com.iutdijon.androiut2.util.adapters.MarksAdapter;
import com.iutdijon.androiut2.util.loaders.AndroIUTHTTPConnection;
import com.iutdijon.androiut2.util.loaders.GetAsyncServiceCall;

/**
 * Classe permettant de décharger toutes les requêtes réseaux sur les serveurs de l'IUT de l'IHM
 * @author Morgan Funtowicz
 *
 */
public class SchoolingDataServiceCall extends GetAsyncServiceCall<Void, Void, HashMap<String, ArrayList<SchoolingData>>> {

	public static final String FUNCTION_GET_MARKS = "getmarks";
	public static final String FUNCTION_GET_ABSENCES = "getabsences";
	
	private final String studentNum;
	private final String function;
	private final SectionedAdapter adapter;
	
	public SchoolingDataServiceCall(final ListActivity context, String id, String function) {
		super(context);

		adapter = new SectionedAdapter() {
			@Override
			protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
				TextView result = (TextView) convertView;
				
				if (convertView == null) {
					result = (TextView) context.getLayoutInflater().inflate(R.layout.header, null);
				}
//				Adapter adapter =  (Adapter) context.getListAdapter().getItem(index);
//				int average = 0;
				//+ " ("+String.valueOf(average)+")"
				result.setText(caption);
				
				return (result);
			}
		};
		
		if(!function.equals(FUNCTION_GET_ABSENCES) && !function.equals(FUNCTION_GET_MARKS)){
			throw new InvalidParameterException("The parameter function must be FUNCTION_GET_MARKS ou FUNCTION_GET_ABSENCES");
		}
		
		this.function = function;
		this.studentNum = id;
	}

	@Override
	protected HashMap<String, ArrayList<SchoolingData>> run(Void... params) {	
		HashMap schoolingData  = null;
		
		HttpPost post_request = new HttpPost(AndroIUTConstantes.WEBAPI_URL);
		
		ArrayList<NameValuePair> post_params = new ArrayList<NameValuePair>();
		post_params.add(new BasicNameValuePair("function", function));
		post_params.add(new BasicNameValuePair("id", studentNum));
		

		InputStream stream = null;
		try {
			post_request.setEntity(new UrlEncodedFormEntity(post_params));
			HttpClient client = AndroIUTHTTPConnection.getHTTPClient();
			HttpResponse response = client.execute(post_request);
			
			stream = response.getEntity().getContent();
			
			if(function == FUNCTION_GET_MARKS){
				schoolingData = new MarksAdapter().parse(stream);
			}else {
				schoolingData = new AbsencesAdapter().parse(stream);
			}
		} catch (Exception e){
			return null;
		}finally{
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return schoolingData;
	}
	@Override
	protected void onPostRun(HashMap<String, ArrayList<SchoolingData>> result) {
		super.onPostRun(result);
		if(getContext() instanceof ListActivity && result != null){
			Set<Entry<String, ArrayList<SchoolingData>>> entries = result.entrySet();
			
			for (Iterator<Entry<String, ArrayList<SchoolingData>>> iterator = entries.iterator(); iterator.hasNext();) {
				final Entry<String, ArrayList<SchoolingData>> entry = iterator.next();
				
				ArrayAdapter<SchoolingData> aAdapter = new ArrayAdapter<SchoolingData>(getContext(), android.R.layout.simple_list_item_1, entry.getValue()){
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View row;

	                    if (null == convertView) {
	                    	row = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.marks_list_item, null);
	                    } else {
	                    	row = convertView;
	                    }

	                    TextView tv = (TextView) row.findViewById(R.id.schooling_item_title);
	                    tv.setText(Html.fromHtml(getItem(position).toString()));
	                    	                    
	                    return row;
					}
				};
				
				//Calcul de la moyenne pondéré [somme(note*coef) / somme coef] pour la matière
				float average = 0.0f;
				float coef = 0.0f;
				final ArrayList<SchoolingData> datas = entry.getValue();
				
				for (Iterator<SchoolingData> it = datas.iterator(); it.hasNext();) {
					SchoolingData schoolingData = it.next();
					if(!(schoolingData instanceof Mark)){
						break;
					}else{
						Mark m = (Mark) schoolingData;
						average += Float.parseFloat(m.getMark())*Float.parseFloat(m.getCoef());
						coef += Float.parseFloat(m.getCoef());
					}
					
				}
				if(average != 0.0f){
					average /= coef;
					adapter.addSection(entry.getKey()+" ("+String.format("%.2f", average)+")", aAdapter);
				}else{
					adapter.addSection(entry.getKey(), aAdapter);
				}
			}
			
			((ListActivity) getContext()).setListAdapter(adapter);
		}
	}

}
