package com.kamesuta.mc.signpic.http;

import javax.annotation.Nonnull;

public abstract class Communicate implements ICommunicate {
	protected ICommunicateCallback callback;
	private Thread currentThread;

	protected void onDone(final @Nonnull  ICommunicateResponse response) {
		if (this.callback!=null)
			this.callback.onDone(response);
	}

	@Override
	public void setCallback(final @Nonnull ICommunicateCallback callback) {
		this.callback = callback;
	}

	protected  void setCurrent() {
		this.currentThread = Thread.currentThread();
	}

	protected void unsetCurrent() {
		this.currentThread = null;
	}

	@Override
	public void cancel() {
		if (this.currentThread!=null)
			this.currentThread.interrupt();
	}
}
