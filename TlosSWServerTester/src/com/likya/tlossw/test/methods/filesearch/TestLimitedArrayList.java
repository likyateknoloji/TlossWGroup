package com.likya.tlossw.test.methods.filesearch;

import com.likya.tlossw.core.spc.helpers.LimitedArrayList;


public class TestLimitedArrayList {

	public static void main(String[] args) {

		LimitedArrayList<String> limitedArrayList = new LimitedArrayList<>();

		limitedArrayList.setMaxLength(3);

		limitedArrayList.add("1");
		limitedArrayList.add("2");
		limitedArrayList.add("3");
		limitedArrayList.add("4");
		limitedArrayList.add("5");
		limitedArrayList.add("6");

	}

}
