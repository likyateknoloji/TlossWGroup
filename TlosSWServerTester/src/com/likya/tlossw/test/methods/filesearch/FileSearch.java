package com.likya.tlossw.test.methods.filesearch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Scanner;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.likya.tlossw.core.spc.helpers.LimitedArrayList;
import com.likya.tlossw.web.appmng.TraceBean;

public class FileSearch {
	
	/**
	 *  <xs:element name="content">
	 * 		<xs:complexType>
	 * 			<xs:simpleContent>
	 * 				<xs:extension base="xs:string">
	 * 					<xs:attribute name="logLineNumBack" use="optional" type="xs:positiveInteger"/>
	 * 					<xs:attribute name="logLineNumForward" use="optional" type="xs:positiveInteger"/>
	 * 				</xs:extension>
	 * 			</xs:simpleContent>
	 * 		</xs:complexType>
	 * 	</xs:element>
	 * 
	 */

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		String searchString = "/TlosProcessDataAll/RUN[@id='1143'";
		
		int logLineNumBack = 3;
		int logLineNumForward = 3;
		
		CircularFifoBuffer circularFifoBuffer = new CircularFifoBuffer(logLineNumBack + logLineNumForward + 1);
		System.out.println("****** Testing CircularFifoBuffer...");
		testMain(circularFifoBuffer, logLineNumForward, searchString);
		
		LimitedArrayList<Object> limitedArrayList = new LimitedArrayList<>(logLineNumBack + logLineNumForward + 1);
		System.out.println("****** Testing LimitedArrayList...");
		testMain(limitedArrayList, logLineNumForward, searchString);
	}
	
	public static void testMain(AbstractCollection<Object> collection, int logLineNumForward, String searchString) {
		
		boolean isFound = false;

		long startTime = System.currentTimeMillis();
		
		isFound = reverseFind(collection, logLineNumForward, searchString);
		logTimeInfo("     > Reverse Find Süre : ", startTime);
		
		Object myObjArray[] = collection.toArray();
		
		for(int j = myObjArray.length - 1; j >= 0; j--){
			System.out.println(myObjArray[j].toString());
		}
		
		System.out.println("* *");
		
		startTime = System.currentTimeMillis();
		
		isFound = find(collection, logLineNumForward, searchString);
		
		for(Object text : collection.toArray()) {
			System.out.println(text.toString());
		}
		
		logTimeInfo("     > Find Süre : ", startTime);
		
		System.out.println("Sonuç : " + isFound);
		
	}

	public static void logTimeInfo(String header, long timeInfo) {
		if(timeInfo == 0) {
			System.out.println(header);
		} else {
			System.out.println(header + TraceBean.dateDiffWithNow(timeInfo) + "ms");		
		}
	}
	
	public static boolean reverseFind(AbstractCollection<Object> collection, int logLineNumForward, String searchString) {

		
		// Testing only
		File f = new File("/Users/serkan/programlar/dev/workspace/Tester/tlosSTrace.log");
		
		
		boolean result = false;
		Scanner in = null;
		try {
			// in = new Scanner(new FileReader(f));
			ReverseLineInputStream reverseLineInputStream = new ReverseLineInputStream(f);
			in = new Scanner(reverseLineInputStream);
			
			int fwCounter = 0;
			
			while (in.hasNextLine() && (fwCounter < logLineNumForward) ) {
				String myLine = in.nextLine(); 
				// System.out.println(myLine);
				
				if(!result) {
					result = myLine.indexOf(searchString) >= 0;
				} else {
					fwCounter ++;
				}
				collection.add(myLine);
			}
			
			System.out.println();			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}
		
		System.out.printf("Result of searching for %s in %s was %b\n", searchString, f.getName(), result);
		
		return result;
	}

	
	public static boolean find(AbstractCollection<Object> collection, int logLineNumForward, String searchString) {

		// Testing only
		File f = new File("/Users/serkan/programlar/dev/workspace/Tester/tlosSTrace.log");
		
		boolean result = false;
		Scanner in = null;
		try {
			in = new Scanner(new FileReader(f));
			while (in.hasNextLine() && !result) {
				String myLine = in.nextLine(); 
				// System.out.println(myLine);
				result = myLine.indexOf(searchString) >= 0;
				collection.add(myLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}
		
		System.out.printf("Result of searching for %s in %s was %b\n", searchString, f.getName(), result);
		
		return result;
	}
}
