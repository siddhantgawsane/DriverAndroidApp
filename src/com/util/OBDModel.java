package com.util;

import java.io.Serializable;

public class OBDModel implements Serializable{
	 public static final long serialVersionUID = 1L;
	public double load_pct, temp, rpm, vss, iat, maf, throttlepos, time, obdid;

	public double getLoad_pct() {
		return load_pct;
	}

	public void setLoad_pct(double load_pct) {
		this.load_pct = load_pct;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getRpm() {
		return rpm;
	}

	public void setRpm(double rpm) {
		this.rpm = rpm;
	}

	public double getVss() {
		return vss;
	}

	public void setVss(double vss) {
		this.vss = vss;
	}

	public double getIat() {
		return iat;
	}

	public void setIat(double iat) {
		this.iat = iat;
	}

	public double getMaf() {
		return maf;
	}

	public void setMaf(double maf) {
		this.maf = maf;
	}

	public double getThrottlepos() {
		return throttlepos;
	}

	public void setThrottlepos(double throttlepos) {
		this.throttlepos = throttlepos;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getObdid() {
		return obdid;
	}

	public void setObdid(double obdid) {
		this.obdid = obdid;
	}
	
}
