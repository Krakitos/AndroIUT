package com.iutdijon.androiut2.ftp.adapters;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iutdijon.androiut2.R;

/**
 * Permet de faire le lien entre la représentation mémoire des fichiers présents dans le dossier actuel
 * vers la liste d'affichage de la vue.
 * Elle rajoute un fichier à la liste qui est le fichier ".." correspondant au fichier parent et permettant ainsi
 * de remonter dans l'arborescence du FTP.
 * @author Morgan Funtowicz
 *
 */
public class FTPFileListAdapter extends ArrayAdapter<FTPFile> {

	private final LayoutInflater inflater;
	private FTPFile[] files_with_back;
	
	public FTPFileListAdapter(Context context, int textViewResourceID, FTPFile[] files) {
		super(context, textViewResourceID, files);
		inflater = LayoutInflater.from(context);
		
		files_with_back = addBackFile(files);
		setNotifyOnChange(true);
	}

	/**
	 * Ajoute le fichier ".." à la liste des fichiers du dossier actuel
	 * @param tab Le tableau de fichiers sur le serveur
	 * @return un tableau de {@link FTPFile} contenant tab.length + 1 entrées
	 */
	private FTPFile[] addBackFile(FTPFile[] tab){
		FTPFile[] files = new FTPFile[tab.length+1];
		
		FTPFile gotoParent = new FTPFile();
		gotoParent.setName("..");
		gotoParent.setType(FTPFile.DIRECTORY_TYPE);
		
		files[0] = gotoParent;
		
		System.arraycopy(tab, 0, files, 1, tab.length);
		
		return files;
	}
	/**
	 * Permet de mettre à jour les données de la liste sans avoir à récréer un adaptateur.
	 * @param files Un tableau de {@link FTPFile} correspondant à la représentation du dossier nouvellement consulté
	 */
	public void updateFileList(FTPFile[] files){
		this.files_with_back = addBackFile(files);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return files_with_back.length;
	}

	@Override
	public FTPFile getItem(int position) {
		
		return files_with_back[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.ftp_list_item, null);
			
			holder.fileName = (TextView) convertView.findViewById(R.id.ftp_list_item_name);
			holder.type = (ImageView) convertView.findViewById(R.id.list_image);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
			holder.fileName.setText((files_with_back[position].getName()));
			holder.type.setBackgroundResource(files_with_back[position].isDirectory() ? R.drawable.collections_collection : R.drawable.collections_view_as_list);
		
		return convertView;
	}
	
	/**
	 * Classe agissant comme cache de l'affichage d'un élément de la liste
	 * afin d'améliorer les performances du service.
	 * @author Morgan Funtowicz
	 *
	 */
	private class ViewHolder{
		public ImageView type;
		public TextView fileName;
	}

}
