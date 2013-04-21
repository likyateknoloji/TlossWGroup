/*
 * TlosFaz2
 * com.likya.tlos.core.spc.helpers : ValidPlatforms.java
 * @author Serkan Taş
 * Tarih : 09.Kas.2008 23:40:05
 */

package com.likya.tlossw.utils;

public class ValidPlatforms {
	
	public static boolean isOSValid() {
		
		if (getCommand("") != null) {
			return true;
		}
		
		return false;
		
	}

		/**
	 * Windows'da bulunan farklı davranış biçimi nedeni ile, aşağıdaki davranış matrisi
	 * geçerlidir.
	 * 
	 *  Windows için komut davranış matrisi
	 *  
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *						|	Hatalı Jar	|	Hatalı İşleri Durdurma	| Hatasız İşleri Durdurma	|	İşin bitişini anlama	*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *	[0] = cmd			|				|							|							*
	 *  *	[1] = /c			|	Başarılı	|			Başarılı		|		Başarısız			*							*
	 *  *	[2] = jobCommand	|				|							|							*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *	[1] = "cmd"			|				|							|							*
	 *  *	[2] = ""			|	Başarısız	|			Başarılı		|		Başarılı			*		Başarısız			*
	 *  *	[3] = jobCommand	|				|							|							*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *	[1] = "cmd"			|				|							|							*
	 *  *	[2] = jobCommand	|	         	|			        		|		        			*		         			*
	 *  *	[3] = ""			|				|							|							*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *	[0] = jobCommand	|				|							|							*
	 *  *	[1] = ""			|	Başarılı	|			Başarılı		|		Başarısız			*							*
	 *  *	[2] = ""			|				|							|							*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 *  *	[0] = cmd			|				|							|							*
	 *  *	[1] = /k			|	Başarısız	|			Başarılı		|		Başarısız			*							*
	 *  *	[2] = jobCommand	|				|							|							*
	 *  *-----------------------------------------------------------------------------------------------*---------------------------*
	 */

	/**
	 * Windows'da bulunan farklı davranış biçimi nedeni ile, aşağıdaki
	 * davranış matrisi geçerlidir.
	 * 
	 * Windows için komut davranış matrisi
	 * 
	 * *----------------------------------------------------------------
	 * -------------------------------*---------------------------* * |
	 * Hatalı Jar | Hatalı İşleri Durdurma | Hatasız İşleri Durdurma |
	 * İşin bitişini anlama *
	 * *------------------------------------------
	 * ------------------------
	 * -----------------------------*---------------------------* * [0]
	 * = cmd | | | * * [1] = /c | Başarılı | Başarılı | Başarısız * * *
	 * [2] = jobCommand | | | *
	 * *----------------------------------------
	 * --------------------------
	 * -----------------------------*---------------------------* * [1]
	 * = "cmd" | | | * * [2] = "" | Başarısız | Başarılı | Başarılı *
	 * Başarısız * * [3] = jobCommand | | | *
	 * *--------------------------
	 * ----------------------------------------
	 * -----------------------------*---------------------------* * [1]
	 * = "cmd" | | | * * [2] = jobCommand | | | * * * [3] = "" | | | *
	 * *--
	 * ----------------------------------------------------------------
	 * -----------------------------*---------------------------* * [0]
	 * = jobCommand | | | * * [1] = "" | Başarılı | Başarılı | Başarısız
	 * * * * [2] = "" | | | *
	 * *------------------------------------------
	 * ------------------------
	 * -----------------------------*---------------------------* * [0]
	 * = cmd | | | * * [1] = /k | Başarısız | Başarılı | Başarısız * * *
	 * [2] = jobCommand | | | *
	 * *----------------------------------------
	 * --------------------------
	 * -----------------------------*---------------------------*
	 */
	
	// TODO Bu kısım değişmeli!
	/**
	 * Aslında aşağıdaki gibi bir liste olmamalı. Sadece deploy edilecek sisteme
	 * ait lisans olmalı
	 */
	public static String[] getCommand(String jobCommand) {

		String osName = System.getProperty("os.name");
		String[] cmd = new String[3];

		if (osName.equals("Windows Vista")) {
			
			/*
			 * cmd[0] = "cmd.exe"; cmd[1] = "/C"; cmd[2] = jobCommand;
			 */

			/*
			 * cmd[0] = "cmd.exe"; cmd[1] = "/K"; cmd[2] = jobCommand;
			 */

			/*
			 * cmd[0] = "cmd.exe"; cmd[1] = ""; cmd[2] = jobCommand;
			 */

			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = jobCommand;

			/*
			 * cmd[0] = "cmd.exe"; cmd[1] = jobCommand; cmd[2] = "";
			 */

		} else if (osName.equals("Windows NT")) {
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = jobCommand;
		} else if (osName.equals("Windows 95")) {
			cmd[0] = "command.com";
			cmd[1] = "/C";
			cmd[2] = jobCommand;
		} else if (osName.equals("Windows XP")) {
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = jobCommand;
		} else if (osName.equals("Windows 7")) {
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = jobCommand;
		} else if (osName.equals("HP-UX")) {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = jobCommand;
		} else if (osName.equals("HP-UX")) {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = jobCommand;
		} else if (osName.equals("SunOS")) {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = jobCommand;
		} else if(osName.equals("Mac OS X")) {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = jobCommand;
		} else {
//			System.out.println("----------->windows");
//			System.out.println("----------->" + osName.toLowerCase());
//			System.out.println("----------->" + osName.toLowerCase().indexOf("windows"));
			if (osName.toLowerCase().indexOf("windows") != -1) {
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				cmd[2] = jobCommand;
			} else {
				cmd[0] = "/bin/sh";
				cmd[1] = "-c";
				cmd[2] = jobCommand;
			}
			// TlosServer.getLogger().error(osName +
			// " sistemi desteklenmiyor !");
		}

		return cmd;
	}

}
