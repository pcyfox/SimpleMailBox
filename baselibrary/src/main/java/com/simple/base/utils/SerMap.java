package com.simple.base.utils;

import java.io.Serializable;
import java.util.Map;


public class SerMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<?, ?> map;

	public Map<?, ?> getMap() {
		return map;
	}

	public void setMap(Map<?, ?> map) {
		this.map = map;
	}

}
