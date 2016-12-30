package com.kamesuta.mc.signpic.http;

import javax.annotation.Nullable;

public  class  CommunicateResponse implements ICommunicateResponse {
	protected  boolean  isSuccess;
	protected  Throwable error;

	public @Nullable CommunicateResponse(final  boolean isSuccess,  final Throwable error) {
		this.isSuccess = isSuccess;
		this.error = error;
	}

	@Override
	public  boolean  isSuccess() {
		return this.isSuccess;
	}

	@Override
	public @Nullable Throwable getError() {
		return this.error;
	}
}