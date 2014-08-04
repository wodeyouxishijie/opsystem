package com.googlecode.psiprobe.test;

import com.googlecode.psiprobe.Tomcat80ContainerAdaptor;

public class TestInstanceMain {

	public static void main(String[] args) throws Exception {
		Object o = Class.forName("com.googlecode.psiprobe.Tomcat80ContainerAdaptor").newInstance();
		System.out.println(o instanceof Tomcat80ContainerAdaptor);
	}

}
