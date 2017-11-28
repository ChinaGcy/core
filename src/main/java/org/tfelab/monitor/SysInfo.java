package org.tfelab.monitor;

import org.tfelab.json.JSON;
import org.tfelab.json.JSONable;

public abstract class SysInfo implements JSONable {
	@Override
	public String toJSON() {
		return JSON.toJson(this);
	}

	public abstract void probe();
}