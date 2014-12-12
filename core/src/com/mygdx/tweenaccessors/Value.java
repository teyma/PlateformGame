package com.mygdx.tweenaccessors;

/**
 * Simple Value class, which will just be a wrapper class for one float. 
 * We create this because only Objects can be tweened (primitives cannot).
 * @author kilobolt
 *
 */
public class Value {

	private float val = 1;

	public float getValue() {
		return val;
	}

	public void setValue(float newVal) {
		val = newVal;
	}

}
