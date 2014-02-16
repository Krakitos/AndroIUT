package com.iutdijon.androiut2.ftp.data;

/**
 * Enum�ration de la liste des commandes support�es par le client FTP int�gr�
 * @author Morgan Funtowicz
 *
 */
public final class FTPCommand {
	public static final int FTP_CD = 1 << 0;
	public static final int FTP_LIST = 1 << 1;
	public static final int FTP_GET = 1 << 2;
	public static final int FTP_PUT = 1 << 3;
	public static final int FTP_DELETE = 1 << 4;
	public static final int FTP_LOGIN = 1 << 5;
	public static final int FTP_LOGOUT = 1 << 6;
}
